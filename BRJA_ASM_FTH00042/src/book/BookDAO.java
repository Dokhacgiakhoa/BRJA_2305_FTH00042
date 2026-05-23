package book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Lớp Data Access Object (DAO) chịu trách nhiệm thực thi toàn bộ các thao tác truy vấn
// dữ liệu CRUD (Thêm, Đọc, Sửa, Xóa) trực tiếp với bảng dữ liệu books trong cơ sở dữ liệu SQLite.
// Lớp này sử dụng PreparedStatement để phòng chống tấn công SQL Injection một cách triệt để
// và áp dụng mô hình tự động đóng tài nguyên hệ thống (try-with-resources) để tránh lỗi rò rỉ kết nối.
public class BookDAO {

    // Phương thức thêm mới thông tin một cuốn sách vào cơ sở dữ liệu SQLite
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, author, release_date, content) VALUES (?, ?, ?, ?)";
        // Sử dụng cơ chế try-with-resources để tự đóng Connection và PreparedStatement sau khi thực thi xong câu lệnh SQL
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Gán dữ liệu tương ứng vào các vị trí dấu hỏi chấm nhằm tránh lỗi cú pháp SQL và lỗi bảo mật SQL Injection
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getReleaseDate());
            pstmt.setString(4, book.getContent());
            
            // Thực thi câu lệnh chèn dữ liệu và nhận số lượng dòng chịu ảnh hưởng trong bảng
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Gặp lỗi khi cố gắng thêm sách mới vào cơ sở dữ liệu: " + e.getMessage());
            return false;
        }
    }

    // Phương thức lấy về toàn bộ danh sách sách hiện có trong cơ sở dữ liệu, sắp xếp theo mã ID giảm dần
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, release_date, content FROM books ORDER BY id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            // Duyệt qua tập kết quả ResultSet để nạp thông tin vào danh sách đối tượng sách
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
            System.err.println("Gặp lỗi khi tải danh sách sách từ cơ sở dữ liệu: " + e.getMessage());
        }
        return books;
    }

    // Phương thức truy vấn thông tin chi tiết của một cuốn sách cụ thể bằng mã định danh ID
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
            System.err.println("Gặp lỗi khi tìm kiếm thông tin cuốn sách bằng mã ID: " + e.getMessage());
        }
        return null;
    }

    // Phương thức tìm kiếm danh sách sách dựa theo từ khóa gần đúng của tiêu đề sách (sử dụng toán tử LIKE trong SQL)
    public List<Book> searchBooksByName(String name) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, release_date, content FROM books WHERE title LIKE ? ORDER BY id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Thêm các ký tự phần trăm để thực hiện tìm kiếm chuỗi chứa từ khóa ở bất kỳ vị trí nào
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
            System.err.println("Gặp lỗi trong quá trình tìm kiếm sách theo tên: " + e.getMessage());
        }
        return books;
    }

    // Phương thức cập nhật các thông tin thay đổi của một cuốn sách đã tồn tại trong hệ thống
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
            System.err.println("Gặp lỗi khi cố gắng cập nhật thông tin sách: " + e.getMessage());
            return false;
        }
    }

    // Phương thức xóa bỏ hoàn toàn thông tin một cuốn sách ra khỏi cơ sở dữ liệu dựa trên mã số ID
    public boolean deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Gặp lỗi khi cố gắng thực thi xóa cuốn sách khỏi hệ thống: " + e.getMessage());
            return false;
        }
    }
}
