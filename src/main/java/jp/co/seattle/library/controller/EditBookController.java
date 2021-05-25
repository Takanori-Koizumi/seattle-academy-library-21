package jp.co.seattle.library.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.ThumbnailService;

@Controller //APIの入り口
public class EditBookController {
    final static Logger logger = LoggerFactory.getLogger(EditBookController.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BooksService booksService;
    @Autowired
    private ThumbnailService thumbnailService;
    @Autowired
    private BooksService bookdService;

    @RequestMapping(value = "/editBookReturn", method = RequestMethod.POST) //value＝actionで指定したパラメータ
    public String returnEdit(Locale locale, @RequestParam("bookId") Integer bookId, Model model) {

        model.addAttribute("bookInfo", bookdService.getBookInfo(bookId));
        return "editBook";
    }

    /**
     * 対象書籍を編集する
     *
     * @param locale ロケール情報
     * @param bookId 書籍ID
     * @param title 書籍名
     * @param author 著者名
     * @param publisher 出版社
     * @param publishDtea 出版日
     * @param file サムネイルファイル
     * @parm isbn ISBN
     * @pram description 説明文
     * @param model モデル
     * @return 遷移先画面
     */
    @Transactional
    @RequestMapping(value = "/editBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String editBook(
            Locale locale,
            @RequestParam("bookId") Integer bookId,
            @RequestParam("userId") Integer userId,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("publisher") String publisher,
            @RequestParam("publishDate") String publishDate,
            @RequestParam("thumbnail") MultipartFile file,
            @RequestParam("isbn") String isbn,
            @RequestParam("description") String description,
            Model model) {
        logger.info("Welcome insertBooks.java! The client locale is {}.", locale);

        try {
        
        // パラメータで受け取った書籍情報をDtoに格納する。
        BookDetailsInfo bookInfo = new BookDetailsInfo();
        bookInfo.setBookId(bookId);
        bookInfo.setTitle(title);
        bookInfo.setAuthor(author);
        bookInfo.setPublisher(publisher);
        bookInfo.setIsbn(isbn);
        bookInfo.setDescription(description);

        // クライアントのファイルシステムにある元のファイル名を設定する
        String thumbnail = file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {
                // サムネイル画像をアップロード
                String fileName = thumbnailService.uploadThumbnail(thumbnail, file);
                // URLを取得
                String thumbnailUrl = thumbnailService.getURL(fileName);

                bookInfo.setThumbnailName(fileName);
                bookInfo.setThumbnailUrl(thumbnailUrl);

            } catch (Exception e) {

                // 異常終了時の処理
                logger.error("サムネイルアップロードでエラー発生", e);
                model.addAttribute("bookDetailsInfo", bookInfo);
                return "editBook";
            }
        }

        if (file.isEmpty()) {
            BookDetailsInfo bookDetailsInfo = booksService.getBookInfo(bookId);
            bookInfo.setThumbnailName(bookDetailsInfo.getThumbnailName());
            bookInfo.setThumbnailUrl(bookDetailsInfo.getThumbnailUrl());
        }

        // 出版日のバリデーションチェック

        boolean isValidDate = publishDate.matches("^[0-9]+$");
        boolean isVaildCheck = false;

        if (!isValidDate) {
            model.addAttribute("dateError", "出版日は半角数字のYYYYMMDD形式で入力してください");
            isVaildCheck = true;
        }
        try {
            // 日付チェック
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            sdf.setLenient(false);
            sdf.parse(publishDate);
            bookInfo.setPublishDate(publishDate);

        } catch (ParseException e) {
            model.addAttribute("dateError", "出版日は半角数字のYYYYMMDD形式で入力してください");
            isVaildCheck = true;
        }

        //ISBNのバリデーションチェック

        if (!StringUtils.isEmpty(isbn)) {
            boolean isValidIsbn = isbn.matches("^[0-9]+$");
            int isbnNum = String.valueOf(isbn).length();
            if (!isValidIsbn || !(isbnNum == 10 || isbnNum == 13)) {
                model.addAttribute("isbnError", "ISBNの桁数または半角数字が正しくありません");
                isVaildCheck = true;
            }
        }

        if (isVaildCheck) {

            model.addAttribute("bookInfo", bookdService.getBookInfo(bookId));

            return "editBook";
        }

        // 編集した書籍情報を編集する

        if (!file.isEmpty()) {
            booksService.editSystem(bookInfo);
        } else {
            booksService.thumbnailNullEditSystem(bookInfo);
        }

        model.addAttribute("resultMessage", "編集完了");


        BookDetailsInfo bookDetailsInfo = booksService.getBookInfo(bookId);
        model.addAttribute("bookDetailsInfo", bookDetailsInfo);

        String bollowCheck = booksService.bollowCheck(bookId);
        model.addAttribute("rendCheck", bollowCheck);

        model.addAttribute("favoriteCheck", booksService.favoriteCheck(userId, bookId));

        //  詳細画面に遷移する
        return "details";
    } catch (DataIntegrityViolationException e) {
        model.addAttribute("StringError", "255文字以内で入力してください");
        model.addAttribute("bookInfo", bookdService.getBookInfo(bookId));
        return "editBook";
    }catch(Exception e) {
        model.addAttribute("bookInfo", bookdService.getBookInfo(bookId));
        return"editBook";
    }
    }
}
