package jp.co.seattle.library.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.seattle.library.service.BooksService;

/**
 * Handles requests for the application home page.
 */
@Controller //APIの入り口
public class HomeController {
    final static Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private BooksService booksService;

    /**
     * Homeボタンからホーム画面に戻るページ
     * @param model
     * @param userId ユーザーID
     * @return
     */
    @RequestMapping(value = "/home", method = RequestMethod.POST)
    public String transitionHome(Model model,
            @RequestParam("userId") Integer userId) {

        model.addAttribute("userId", userId);
        model.addAttribute("bookList", booksService.getBookList());
        return "home";
    }

    /**
     * 検索機能
     * @param locale ロケール情報
     * @param searchTitle 検索名
     * @param model
     * @return
     */
    @Transactional
    @RequestMapping(value = "/searchBooks", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String searchBooks(Locale locale,
            @RequestParam("searchTitle") String searchTitle,
            @RequestParam("radio") String matchCheck,
            Model model) {

        model.addAttribute("bookList", booksService.getSearchBookList(searchTitle, matchCheck));
        model.addAttribute("searchedTitle", searchTitle);
        model.addAttribute("allShow", "一覧表示");

        return "home";
    }

    //    /**
    //     * 書籍を一覧表示する
    //     *
    //     * @param locale ロケール情報
    //     * @param model モデル情報
    //     * @return 遷移先画面名
    //     */
    //    @Transactional
    //    @RequestMapping(value = "/allShow", method = RequestMethod.GET)
    //    public String bulkDelete(
    //            Locale locale,
    //            Model model) {
    //        logger.info("Welcome delete! The client locale is {}.", locale);
    //        model.addAttribute("bookList", booksService.getBookList());
    //        return "home";
    //
    //    }

}
