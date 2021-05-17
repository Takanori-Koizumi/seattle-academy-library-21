package jp.co.seattle.library.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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


/**
 * Handles requests for the application home page.
 */
@Controller //APIの入り口
public class AddBooksController {
    final static Logger logger = LoggerFactory.getLogger(AddBooksController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private ThumbnailService thumbnailService;

    @RequestMapping(value = "/addBook", method = RequestMethod.GET) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String login(Model model) {
        return "addBook";
    }

    /**
     * 書籍情報を登録する
     * @param locale ロケール情報
     * @param title 書籍名
     * @param author 著者名
     * @param publisher 出版社
     * @param publishDtea 出版日
     * @param file サムネイルファイル
     * @param model モデル
     * @return 遷移先画面
     */
    @Transactional
    @RequestMapping(value = "/insertBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String insertBook(Locale locale,
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
                return "addBook";
            }
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
                model.addAttribute("bookInfo", bookInfo);
                return "addBook";
            }

        // 書籍情報を新規登録する
        booksService.registBook(bookInfo);

        model.addAttribute("resultMessage", "登録完了");

        // TODO 登録した書籍の詳細情報を表示するように実装

        int registBookId = booksService.getlatestBookId();

        booksService.addLending(registBookId);

        BookDetailsInfo bookDetailsInfo = booksService.getBookInfo(registBookId);
        model.addAttribute("bookDetailsInfo", bookDetailsInfo);

        String bollowCheck = booksService.bollowCheck(registBookId);
        model.addAttribute("rendCheck", bollowCheck);

        //  詳細画面に遷移する
        return "details";
    } catch (DataIntegrityViolationException e) {
        model.addAttribute("StringError", "255文字以内で入力してください");

        BookDetailsInfo bookInfo = new BookDetailsInfo();
        bookInfo.setTitle(title);
        bookInfo.setAuthor(author);
        bookInfo.setPublisher(publisher);
        bookInfo.setIsbn(isbn);
        bookInfo.setDescription(description);

        String thumbnail = file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {
                // サムネイル画像をアップロード
                String fileName = thumbnailService.uploadThumbnail(thumbnail, file);
                // URLを取得
                String thumbnailUrl = thumbnailService.getURL(fileName);

                bookInfo.setThumbnailName(fileName);
                bookInfo.setThumbnailUrl(thumbnailUrl);

            } catch (Exception ex) {

                // 異常終了時の処理
                logger.error("サムネイルアップロードでエラー発生", e);
                model.addAttribute("bookInfo", bookInfo);
                return "addBook";
            }
        }

        model.addAttribute("bookInfo", bookInfo);

        return "editBook";
    } catch (Exception e) {
        return "editBook";
    }

    }

}
