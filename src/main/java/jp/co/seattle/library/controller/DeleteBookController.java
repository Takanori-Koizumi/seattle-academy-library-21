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
 * 削除コントローラー
 */
@Controller //APIの入り口
public class DeleteBookController {
    final static Logger logger = LoggerFactory.getLogger(DeleteBookController.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BooksService booksService;
    /**
     * 対象書籍を削除する
     *
     * @param locale ロケール情報
     * @param bookId 書籍ID
     * @param model モデル情報
     * @return 遷移先画面名
     */
    @Transactional
    @RequestMapping(value = "/deleteBook", method = RequestMethod.POST)
    public String deleteBook(
            Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        logger.info("Welcome delete! The client locale is {}.", locale);


        String beforeBollowCheck = booksService.bollowCheck(bookId);

        if (beforeBollowCheck.equals("貸出中")) {

            model.addAttribute("bookDeleteError", "貸出中のため削除できません");

            model.addAttribute("rendCheck", beforeBollowCheck);

            int registBookId = booksService.getlatestBookId();
            BookDetailsInfo bookDetailsInfo = booksService.getBookInfo(registBookId);
            model.addAttribute("bookDetailsInfo", bookDetailsInfo);

            return "details";
        }

        booksService.lendingDelete(bookId);
        booksService.deleteSystem(bookId);

        model.addAttribute("bookList", booksService.getBookList());
        return "home";

    }

    /**
     * 書籍を一括削除する
     *
     * @param locale ロケール情報
     * @param model モデル情報
     * @return 遷移先画面名
     */
    @Transactional
    @RequestMapping(value = "/bulkDelete", method = RequestMethod.GET)
    public String bulkDelete(
            Locale locale,
            Model model) {
        logger.info("Welcome delete! The client locale is {}.", locale);
        booksService.bulkDeleteSystem();
        model.addAttribute("bookList", booksService.getBookList());
        return "home";

    }

}
