package book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Lớp xử lý dữ liệu (DAO) thực hiện các câu lệnh SQL với bảng books
public class BookDAO {

    // Thêm sách mới
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, author, release_date, content) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getReleaseDate());
            pstmt.setString(4, book.getContent());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm sách mới: " + e.getMessage());
            return false;
        }
    }

    // Lấy danh sách tất cả sách
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, release_date, content FROM books ORDER BY id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Book book = new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("release_date"),
                    rs.getString("content")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách sách: " + e.getMessage());
        }
        return books;
    }

    // Lấy thông tin một cuốn sách theo ID
    public Book getBookById(int id) {
        String sql = "SELECT id, title, author, release_date, content FROM books WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("release_date"),
                        rs.getString("content")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin sách bằng ID: " + e.getMessage());
        }
        return null;
    }

    // Tìm kiếm sách theo tiêu đề (truy vấn LIKE)
    public List<Book> searchBooksByName(String name) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, release_date, content FROM books WHERE title LIKE ? ORDER BY id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + name + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("release_date"),
                        rs.getString("content")
                    );
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm sách theo tên: " + e.getMessage());
        }
        return books;
    }

    // Cập nhật thông tin sách
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, release_date = ?, content = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getReleaseDate());
            pstmt.setString(4, book.getContent());
            pstmt.setInt(5, book.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật sách: " + e.getMessage());
            return false;
        }
    }

    // Xóa sách theo ID
    public boolean deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa sách: " + e.getMessage());
            return false;
        }
    }
}
