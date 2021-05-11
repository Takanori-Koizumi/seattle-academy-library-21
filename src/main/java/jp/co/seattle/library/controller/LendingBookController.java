package jp.co.seattle.library.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;

/**
 * 貸出コントローラー
 */
@Controller //APIの入り口
public class LendingBookController {
    final static Logger logger = LoggerFactory.getLogger(LendingBookController.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BooksService booksService;
    
    /**
     * 対象書籍を貸出する
     *
     * @param locale ロケール情報
     * @param bookId 書籍ID
     * @param model モデル情報
     * @return 遷移先画面名
     */
    
    @Transactional
    @RequestMapping(value = "/rentBook", method = RequestMethod.POST)
    public String rentBook(
            Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        logger.info("Welcome delete! The client locale is {}.", locale);


        //貸し出しの可能か判定

        String beforebollowCheck = booksService.bollowCheck(bookId);

        if (beforebollowCheck.equals("貸出可")) {
            booksService.bollowSystem(bookId);
        }

        String afterbollowCheck = booksService.bollowCheck(bookId);
        model.addAttribute("rendCheck", afterbollowCheck);


        //登録した書籍の詳細情報を表示するように実装

        BookDetailsInfo bookDetailsInfo = booksService.getBookInfo(bookId);
        model.addAttribute("bookDetailsInfo", bookDetailsInfo);


        return "details";

    }

    /**
     * 対象書籍を返却する
     *
     * @param locale ロケール情報
     * @param bookId 書籍ID
     * @param model モデル情報
     * @return 遷移先画面名
     */

    @Transactional
    @RequestMapping(value = "/returnBook", method = RequestMethod.POST)
    public String returnBook(
            Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        logger.info("Welcome delete! The client locale is {}.", locale);

        //貸出可能か判定

        String beforebollowCheck = booksService.bollowCheck(bookId);

        if (beforebollowCheck.equals("貸出中")) {
            booksService.returnSystem(bookId);
        }

        String afterbollowCheck = booksService.bollowCheck(bookId);

        model.addAttribute("rendCheck", afterbollowCheck);

        //登録した書籍の詳細情報を表示するように実装

        BookDetailsInfo bookDetailsInfo = booksService.getBookInfo(bookId);
        model.addAttribute("bookDetailsInfo", bookDetailsInfo);

        return "details";

    }
}
