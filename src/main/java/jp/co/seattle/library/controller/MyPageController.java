package jp.co.seattle.library.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.seattle.library.dto.UserInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.UsersService;

@Controller //APIの入り口
public class MyPageController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private BooksService booksService;

    final static Logger logger = LoggerFactory.getLogger(MyPageController.class);

    /**
     * マイページボタンからマイページ画面に戻るページ
     * @param locale ロケール情報
     * @param model
     * @param userId
     * @return マイページ画面
     */
    @RequestMapping(value = "/mypage", method = RequestMethod.POST)
    public String transitionMyPage(Locale locale,
            @RequestParam("userId") Integer userId,
            Model model) {
        
        //ユーザー情報取得して表示
        UserInfo userInfo = usersService.returnUserInfo(userId);
        model.addAttribute("userInfo", userInfo);

        //貸出中の本を表示
        model.addAttribute("bookList", booksService.getLendingBookList(userId));


        return "myPage";
    }

}
