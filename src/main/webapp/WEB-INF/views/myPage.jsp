<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%@ page contentType="text/html; charset=utf8"%>
<%@ page import="java.util.*"%>
<html>
<head>
<title>ホーム｜シアトルライブラリ｜シアトルコンサルティング株式会社</title>
<link href="<c:url value="/resources/css/reset.css" />" rel="stylesheet" type="text/css">
<link href="https://fonts.googleapis.com/css?family=Noto+Sans+JP" rel="stylesheet">
<link href="<c:url value="/resources/css/default.css" />" rel="stylesheet" type="text/css">
<link href="https://use.fontawesome.com/releases/v5.6.1/css/all.css" rel="stylesheet">
<link href="<c:url value="/resources/css/home.css" />" rel="stylesheet" type="text/css">
<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script src="resources/js/book.js" /></script>
<script src="resources/js/user.js" /></script>
</head>
<body class="wrapper">
    <header>
        <div class="left">
            <img class="mark" src="resources/img/logo.png" />
            <div class="logo">Seattle Library</div>
        </div>
        <div class="right">
            <ul>
                <li>
                    <form method="post" action="mypage">
                        <button id="mypage" type="submit" class="menu">マイページ</button>
                        <input class="userId" type="hidden" name="userId" value="${userId}">
                    </form>
                </li>
                <li>
                    <form method="post" action="home">
                        <button id="mypage" type="submit" class="menu">Home</button>
                        <input class="userId" type="hidden" name="userId" value="${userId}">
                    </form>
                </li>
                <li><a href="<%=request.getContextPath()%>/">ログアウト</a></li>
            </ul>
        </div>
    </header>
    <main>
        <h1 class="">マイページ</h1>
        <div class="mypage_body content_body">
            <div class="user-contents">
                <ul class="user_box">
                    <c:if test="${not empty userInfo}">
                        <li class="user_id">ユーザーID:${userInfo.userId}</li>
                        <li class="user_email">メールアドレス:${userInfo.email}</li>
                        <li class="usr_password">パスワード:${userInfo.password}</li>
                    </c:if>
                </ul>
            </div>
            <h2 class="booklist_title">借りている本</h2>
            <div class="booklist">
                <c:forEach var="bookInfo" items="${lendingBookList}">
                    <div class="books">
                        <form method="post" class="book_thumnail" action="<%=request.getContextPath()%>/details">
                            <a href="javascript:void(0)" onclick="this.parentNode.submit();"> <c:if test="${bookInfo.thumbnail=='null'}">
                                    <img class="book_noimg" src="resources/img/noImg.png">
                                </c:if> <c:if test="${bookInfo.thumbnail != 'null'}">
                                    <img class="book_noimg" src="${bookInfo.thumbnail}">
                                </c:if>
                            </a> <input type="hidden" name="bookId" value="${bookInfo.bookId}"><input class="userId" type="hidden" name="userId" value="${userId}">
                        </form>
                        <ul class="home_list">
                            <c:if test="${not empty bookInfo}">
                                <li class="book_title">${bookInfo.title}</li>
                                <li class="book_author"><p class="book_auther_list_1">${bookInfo.author}</p>
                                    <p class="book_auther_list_2">(著)</p></li>
                                <li class="book_publisher">出版社:${bookInfo.publisher}</li>
                                <li class="book_publish_date">出版日:${bookInfo.publishDate}</li>
                            </c:if>
                        </ul>
                    </div>
                </c:forEach>
            </div>
            <h2 class="booklist_title">お気に入りの本</h2>
            <div class="booklist">
                <c:forEach var="bookInfo" items="${favoriteBookList}">
                    <div class="books">
                        <form method="post" class="book_thumnail" action="<%=request.getContextPath()%>/details">
                            <a href="javascript:void(0)" onclick="this.parentNode.submit();"> <c:if test="${bookInfo.thumbnail=='null'}">
                                    <img class="book_noimg" src="resources/img/noImg.png">
                                </c:if> <c:if test="${bookInfo.thumbnail != 'null'}">
                                    <img class="book_noimg" src="${bookInfo.thumbnail}">
                                </c:if>
                            </a> <input type="hidden" name="bookId" value="${bookInfo.bookId}"><input class="userId" type="hidden" name="userId" value="${userId}">
                        </form>
                        <ul class="home_list">
                            <c:if test="${not empty bookInfo}">
                                <li class="book_title">${bookInfo.title}</li>
                                <li class="book_author"><p class="book_auther_list_1">${bookInfo.author}</p>
                                    <p class="book_auther_list_2">(著)</p></li>
                                <li class="book_publisher">出版社:${bookInfo.publisher}</li>
                                <li class="book_publish_date">出版日:${bookInfo.publishDate}</li>
                            </c:if>
                        </ul>
                    </div>
                </c:forEach>
            </div>
        </div>
    </main>
    <div class="loader">
        <div class="loader-inner">
            <div class="line line1"></div>
            <div class="line line2"></div>
            <div class="line line3"></div>
        </div>
    </div>
</body>
</html>