package jp.co.seattle.library.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.ThumbnailService;

@Controller //APIの入り口
public class bulkRegistController {

    final static Logger logger = LoggerFactory.getLogger(bulkRegistController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private ThumbnailService thumbnailService;

    /**
     * 一括登録画面遷移
     * @param model
     * @return 遷移先画面
     */

    @RequestMapping(value = "/bulkRegistration", method = RequestMethod.GET) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String bulkRegistReturn(Model model) {
        return "bulkRegistration";
    }

    /**
     * 書籍情報を一括登録する
     * @param locale ロケール情報
     * @param title 書籍名
     * @param author 著者名
     * @param publisher 出版社
     * @param publishDtea 出版日
     * @param model モデル
     * @return 遷移先画面
     */
    @Transactional
    @RequestMapping(value = "/bulkRegist", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String bulkRegist(Locale locale,
            @RequestParam("csvFile") MultipartFile file,
            Model model) {
        logger.info("Welcome insertBooks.java! The client locale is {}.", locale);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));) {



            ArrayList<BookDetailsInfo> bookInfos = new ArrayList<BookDetailsInfo>();
            ArrayList<String> errorMessages = new ArrayList<String>();

            String line;
            int rowCount = 1;

            // 1行ずつCSVファイルを読み込む
            while ((line = br.readLine()) != null) {
                String[] bookData = line.split(",", -1); // 行をタブ区切りで配列に変換


                // パラメータで受け取った書籍情報をDtoに格納する。
                BookDetailsInfo bookInfo = new BookDetailsInfo();
                boolean isVaildCheck = false;

                bookInfo.setDescription(bookData[5]);

                //必須項目のバリデーションチェック

                if (StringUtils.isEmpty(bookData[0]) || StringUtils.isEmpty(bookData[1]) ||
                        StringUtils.isEmpty(bookData[2]) || StringUtils.isEmpty(bookData[3])) {
                    isVaildCheck = true;
                }

                bookInfo.setTitle(bookData[0]);
                bookInfo.setAuthor(bookData[1]);
                bookInfo.setPublisher(bookData[2]);


                // 出版日のバリデーションチェック

                boolean isValidDate = bookData[3].matches("^[0-9]+$");


                if (!isValidDate) {
                    model.addAttribute("dateError", "出版日は半角数字のYYYYMMDD形式で入力してください");
                    isVaildCheck = true;
                }
                try {
                    // 日付チェック
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    sdf.setLenient(false);
                    sdf.parse(bookData[3]);
                    bookInfo.setPublishDate(bookData[3]);

                } catch (ParseException e) {
                    model.addAttribute("dateError", "出版日は半角数字のYYYYMMDD形式で入力してください");
                    isVaildCheck = true;
                }

                //ISBNのバリデーションチェック

                if (!StringUtils.isEmpty(bookData[4])) {
                    boolean isValidIsbn = bookData[4].matches("^[0-9]+$");
                    int isbnNum = String.valueOf(bookData[4]).length();
                    if (!isValidIsbn || !(isbnNum == 10 || isbnNum == 13)) {
                        model.addAttribute("isbnError", "ISBNの桁数または半角数字が正しくありません");
                        isVaildCheck = true;
                    }
                    bookInfo.setIsbn(bookData[4]);

                }

                //1データごとのバリデーションチェック
                if (isVaildCheck) {
                    errorMessages.add(rowCount + "行目の書籍情報登録でバリデーションエラー ");
                }

                
                bookInfos.add(bookInfo);

                rowCount++;

            }

            //エラーメッセージ判定
            if (!CollectionUtils.isEmpty(errorMessages)) {
                model.addAttribute("csvErrors", errorMessages);
                return "bulkRegistration";
            }

            // 書籍情報を新規登録する

            for (BookDetailsInfo book : bookInfos) {
                booksService.registBook(book);
                int registBookId = booksService.getlatestBookId();
                booksService.addLending(registBookId);
            }

            model.addAttribute("resultMessage", "登録完了");

            //  詳細画面に遷移する
            return "bulkRegistration";

        } catch (IOException e) {
            model.addAttribute("catchError", "エラーが発生しました");
            return "bulkRegistration";
        } catch (Exception e) {
            model.addAttribute("catchError", "エラーが発生しました");
            return "bulkRegistration";
        }

    }
}
