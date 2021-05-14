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
</head>
<body class="wrapper">
    <header>
        <div class="left">
            <img class="mark" src="resources/img/logo.png" />
            <div class="logo">Seattle Library</div>
        </div>
        <div class="right">
            <ul>
                <li><a href="<%=request.getContextPath()%>/home" class="menu">Home</a></li>
                <li><a href="<%=request.getContextPath()%>/">ログアウト</a></li>
            </ul>
        </div>
    </header>
    <main>
        <h1>Home</h1>
        <a href="<%=request.getContextPath()%>/addBook" class="btn_add_book">書籍の追加</a> <a href="<%=request.getContextPath()%>/bulkRegistration" class="btn_bulk_book">一括登録</a> <a href="<%=request.getContextPath()%>/bulkDelete" class="btn_bulkDelete" id="bulkDelete">一括削除</a>
        <form id="form5" action="<%=request.getContextPath()%>/searchBooks" method="post" enctype="multipart/form-data" id="data_upload_form">
            <div class="radio">
                <input type="radio" name="radio" class="radio-input" id="radio-01" value="part-match" checked> <label for="radio-01">部分一致</label><br><input type="radio" name="radio" class="radio-input" id="radio-02" value="perfect-match" > <label for="radio-02">完全一致</label>
            </div>
            <div class="search_box">
                <c:if test="${!empty searchedTitle}">
                    <input id="sbox5" type="text" name="searchTitle" value="${searchedTitle}">
                </c:if>
                <c:if test="${empty searchedTitle}">
                    <input id="sbox5" type="text" name="searchTitle" autocomplete="off" placeholder="タイトルを入力">
                </c:if>
                <button id="sbtn5" type="submit">検索</button>
            </div>
        </form>
        <div class="content_body">
            <c:if test="${!empty resultMessage}">
                <div class="error_msg">${resultMessage}</div>
            </c:if>
            <div>
                <div class="booklist">
                    <c:forEach var="bookInfo" items="${bookList}">
                        <div class="books">
                            <form method="post" class="book_thumnail" action="<%=request.getContextPath()%>/details">
                                <a href="javascript:void(0)" onclick="this.parentNode.submit();"> <c:if test="${bookInfo.thumbnail=='null'}">
                                        <img class="book_noimg" src="resources/img/noImg.png">
                                    </c:if> <c:if test="${bookInfo.thumbnail != 'null'}">
                                        <img class="book_noimg" src="${bookInfo.thumbnail}">
                                    </c:if>
                                </a> <input type="hidden" name="bookId" value="${bookInfo.bookId}">
                            </form>
                            <ul>
                                <c:if test="${not empty bookInfo}">
                                    <li class="book_title">${bookInfo.title}</li>
                                    <li class="book_author">${bookInfo.author}(著)</li>
                                    <li class="book_publisher">出版社:${bookInfo.publisher}</li>
                                    <li class="book_publish_date">出版日:${bookInfo.publishDate}</li>
                                </c:if>
                            </ul>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </main>
</body>
</html>
