package jp.co.seattle.library.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.rowMapper.BookDetailsInfoRowMapper;
import jp.co.seattle.library.rowMapper.BookInfoRowMapper;

/**
 * 書籍サービス
 * 
 *  booksテーブルに関する処理を実装する
 */
@Service
public class BooksService {
    final static Logger logger = LoggerFactory.getLogger(BooksService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 書籍リストを取得する
     *
     * @return 書籍リスト
     */
    public List<BookInfo> getBookList() {

        // TODO 取得したい情報を取得するようにSQLを修正
        List<BookInfo> getedBookList = jdbcTemplate.query(

                "select id,title,author,publisher,publish_date,thumbnail_url from books order by title asc",
                new BookInfoRowMapper());

        return getedBookList;
    }

    /**
     * 書籍IDに紐づく書籍詳細情報を取得する
     *
     * @param bookId 書籍ID
     * @return 書籍情報
     */
    public BookDetailsInfo getBookInfo(int bookId) {

        // JSPに渡すデータを設定する
        String sql = "SELECT * FROM books where id ="
                + bookId;

        BookDetailsInfo bookDetailsInfo = jdbcTemplate.queryForObject(sql, new BookDetailsInfoRowMapper());

        return bookDetailsInfo;
    }

    /**
     * 書籍を登録する
     *
     * @param bookInfo 書籍情報
     */
    public void registBook(BookDetailsInfo bookInfo) {

        String sql = "INSERT INTO books (title,author,publisher,publish_date,thumbnail_url,thumbnail_name,reg_date,upd_date,isbn,description"
                + ") VALUES ('"
                + bookInfo.getTitle() + "','" + bookInfo.getAuthor() + "','" + bookInfo.getPublisher() + "','"
                + bookInfo.getPublishDate() + "','"
                + bookInfo.getThumbnailUrl() + "','"
                + bookInfo.getThumbnailName() + "',"
                + "sysdate(),"
                + "sysdate(),'"
                + bookInfo.getIsbn() + "','"
                + bookInfo.getDescription() + "');";
          

        jdbcTemplate.update(sql);

    }

    /**
     * 最新の本のIDを取得する
     * @param bookId 書籍ID
     * @return 最新の書籍ID
     */

    public int getlatestBookId() {
        String sql = "select max(id) from books ;";
        int latestBookId = jdbcTemplate.queryForObject(sql, Integer.class);
        return latestBookId;
    }

    /**
    * 書籍削除機能
    * @param bookId 書籍ID
    */
    public void deleteSystem(int bookId) {
        String sql = "delete from books where id=" + bookId + ";";
        jdbcTemplate.update(sql);

    }

    /**
     * 書籍一括削除機能
     */
    public void bulkDeleteSystem() {
        String sql = "delete from books";
        jdbcTemplate.update(sql);

        String sql2 = "delete from lending";
        jdbcTemplate.update(sql2);

    }

    /**
     * 貸出レコード削除
     * @param bookId 書籍ID
     */

    public void lendingDelete(int bookId) {
        String sql = "delete from lending where bookId=" + bookId;
        jdbcTemplate.update(sql);
    }


    /**
     * 書籍編集機能(画像変更あり)
     * @param bookInfo 書籍情報
     */

    public void editSystem(BookDetailsInfo bookInfo) {
        String sql = "update books set title='" + bookInfo.getTitle() + "',author='" + bookInfo.getAuthor()
                + "',publisher='" + bookInfo.getPublisher()
                + "',publish_date='" + bookInfo.getPublishDate()
                + "',thumbnail_url='" + bookInfo.getThumbnailUrl() + "',thumbnail_name='" + bookInfo.getThumbnailName()
                + "',upd_date=sysdate(),isbn='" + bookInfo.getIsbn() + "',description='" +
                bookInfo.getDescription() + "' where id=" + bookInfo.getBookId() + ";";

        jdbcTemplate.update(sql);
    }

    /**
     * 書籍編集機能(画像変更なし)
     * @param bookInfo 書籍情報
     */
    public void thumbnailNullEditSystem(BookDetailsInfo bookInfo) {
        String sql = "update books set title='" + bookInfo.getTitle() + "',author='" + bookInfo.getAuthor()
                + "',publisher='" + bookInfo.getPublisher()
                + "',publish_date='" + bookInfo.getPublishDate()
                + "',upd_date=sysdate(),isbn='" + bookInfo.getIsbn() + "',description='" +
                bookInfo.getDescription() + "' where id=" + bookInfo.getBookId() + ";";
        jdbcTemplate.update(sql);
    }


    /**
     * 書籍検索機能
     * @param searchTitle 検索タイトル
     * @param matchCheck　検索方法(部分か完全)
     * @return 書籍リスト
     */

    public List<BookInfo> getSearchBookList(String searchTitle, String matchCheck) {

        if (matchCheck.equals("part-match")) {
            List<BookInfo> getedBookList = jdbcTemplate.query(

                    "select * from books where title like '%" + searchTitle + "%'",
                    new BookInfoRowMapper());
            return getedBookList;
        }

        List<BookInfo> getedBookList = jdbcTemplate.query(

                "select * from books where title ='" + searchTitle + "'",
                new BookInfoRowMapper());
        return getedBookList;

    }


    /**
     * 貸出機能
     * @param bookId 書籍ID
     * @param userId ユーザーID
     */

    public void bollowSystem(int userId, int bookId) {
        String sql = "UPDATE lending SET LENDCHECK='貸出中' , userid=" + userId + " where bookId=" + bookId;
        jdbcTemplate.update(sql);
    }

    /**
     * 返却機能
     * @param bookId 書籍ID
     * @param userId ユーザーID
     */

    public void returnSystem(int userId, int bookId) {
        String sql = "UPDATE lending SET LENDCHECK='貸出可',userid=0 where bookId=" + bookId;
        jdbcTemplate.update(sql);
    }

    /**
     * 貸し出し判定
     * @param bookId 書籍ID
     * @return 書籍ステータス
     */

    public String bollowCheck(int bookId) {
        String sql = "select LENDCHECK from lending where bookId=" + bookId;
        String bollowStatus = jdbcTemplate.queryForObject(sql, String.class);
        return bollowStatus;
    }

    /**
     * 借りている本のユーザーIDを取得
     * @param bookId 書籍ID
     * @return　ユーザーID
     */
    public int bollowUserId(int bookId) {
        String sql = "select USERID from lending where bookId=" + bookId;
        int bollowUserId = jdbcTemplate.queryForObject(sql, Integer.class);
        return bollowUserId;
    }

    /**
     * 貸出テーブルに追加
     * @param bookId 書籍ID
     */

    public void addLending(int bookId) {
        String sql = "INSERT INTO lending(bookId) VALUES(" + bookId + ")";
        jdbcTemplate.update(sql);
    }


    /**
     * 貸出中の本を取得(ユーザーごと)
     * @param userId ユーザーID
     * @return 書籍リスト
     */
    public List<BookInfo> getLendingBookList(int userId) {

        List<BookInfo> getedBookList = jdbcTemplate.query(

                "select books.id,title,author,publisher,publish_date,thumbnail_url from lending join books on lending.bookid=books.id where userid="
                        + userId,
                new BookInfoRowMapper());

        return getedBookList;
    }

    /**
     * お気に入りチェック
     * @param userId ユーザーID
     * @param bookId 書籍ID
     * @return　お気に入りチェック
     */
    public int favoriteCheck(int userId, int bookId) {
        String sql = "select count(*) from likes where BOOKID=" + bookId + " and USERID=" + userId;
        int favoriteCheck = jdbcTemplate.queryForObject(sql, Integer.class);
        return favoriteCheck;
    }

    /**
     * お気に入りテーブルに追加
     * @param userId ユーザーID
     * @param bookId 書籍ID
     */
    public void addFavorite(int userId, int bookId) {
        String sql = "insert into likes (BOOKID,USERID) values (" + bookId + "," + userId + ")";
        jdbcTemplate.update(sql);
    }

    /**
    * お気に入りテーブルから削除
    * @param userId ユーザーID
    * @param bookId 書籍ID
    */
    public void removeFavorite(int userId, int bookId) {
        String sql = "delete from likes where BOOKID=" + bookId + " and USERID=" + userId;
        jdbcTemplate.update(sql);
    }

    /**
     * お気に入りの本を取得(ユーザーごと)
     * @param userId ユーザーID
     * @return 書籍リスト
     */
    public List<BookInfo> getFavoriteBookList(int userId) {

        List<BookInfo> getedBookList = jdbcTemplate.query(

                "select books.id,title,author,publisher,publish_date,thumbnail_url from likes join books on likes.bookid=books.id where userid="
                        + userId + " order by title asc",
                new BookInfoRowMapper());

        return getedBookList;
    }


}
