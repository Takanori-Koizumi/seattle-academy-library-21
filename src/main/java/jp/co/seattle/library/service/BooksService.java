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

                "select * from books order by title asc",
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

        String sql = "INSERT INTO books (title, author,publisher,publish_date,thumbnail_url,thumbnail_name,reg_date,upd_date,isbn,description"
                + ") VALUES ('"
                + bookInfo.getTitle() + "','" + bookInfo.getAuthor() + "','" + bookInfo.getPublisher() + "','"
                + bookInfo.getPublishDate() + "','"
                + bookInfo.getThumbnailUrl() + "','"
                + bookInfo.getThumbnailName() + "',"
                + "sysdate(),"
                + "sysdate(),'"
                + bookInfo.getIsbn() + "','"
                + bookInfo.getDescription() + "')";

        jdbcTemplate.update(sql);
    }

    /**
     * 最新の本のIDを取得する
     */

    public int getlatestBookId() {
        String sql = "select max(id) from books ;";
        int latestBookId = jdbcTemplate.queryForObject(sql, Integer.class);
        return latestBookId;
    }

    /**
    * 書籍削除機能
    */
    public void deleteSystem(int bookId) {
        String sql = "delete from books where id=" + bookId + ";";
        jdbcTemplate.update(sql);

    }

    /**
     * 書籍編集機能
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
     * 書籍検索機能
     */

    public List<BookInfo> getSearchBookList(String searchTitle) {

        // TODO 取得したい情報を取得するようにSQLを修正
        List<BookInfo> getedBookList = jdbcTemplate.query(

                "select * from books where title like '%" + searchTitle + "%'",
                new BookInfoRowMapper());

        return getedBookList;
    }

}
