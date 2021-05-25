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
 * お気に入りコントローラー
 */
@Controller //APIの入り口
public class FavoriteBookController {
    final static Logger logger = LoggerFactory.getLogger(FavoriteBookController.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BooksService booksService;

    /**
     * 書籍のお気に入り機能
     *
     * @param locale ロケール情報
     * @param userId ユーザーID
     * @param bookId 書籍ID
     * @param model モデル情報
     * @return 遷移先画面名
     */

    @Transactional
    @RequestMapping(value = "/favoriteBook", method = RequestMethod.POST)
    public String favoriteBook(
            Locale locale,
            @RequestParam("userId") Integer userId,
            @RequestParam("bookId") Integer bookId,
            Model model) {

        logger.info("Welcome delete! The client locale is {}.", locale);

        int favoriteCheck = booksService.favoriteCheck(userId, bookId);

        if (favoriteCheck == 0) {
            booksService.addFavorite(userId, bookId);
        }

        if (favoriteCheck == 1) {
            booksService.removeFavorite(userId, bookId);
        }

        BookDetailsInfo bookDetailsInfo = booksService.getBookInfo(bookId);
        model.addAttribute("bookDetailsInfo", bookDetailsInfo);

        String bollowCheck = booksService.bollowCheck(bookId);
        model.addAttribute("rendCheck", bollowCheck);

        model.addAttribute("favoriteCheck", booksService.favoriteCheck(userId, bookId));

        return "details";

    }
}
