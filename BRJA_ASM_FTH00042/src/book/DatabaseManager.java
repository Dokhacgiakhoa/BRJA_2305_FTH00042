package book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Lớp chịu trách nhiệm quản lý kết nối và tự động khởi tạo cấu trúc cơ sở dữ liệu SQLite cho hệ thống.
// Đảm bảo rằng ứng dụng luôn có một kênh giao tiếp an toàn và ổn định với tệp cơ sở dữ liệu books.db.
public class DatabaseManager {
    // Đường dẫn kết nối JDBC SQLite tới tệp cơ sở dữ liệu vật lý
    private static final String DB_URL = "jdbc:sqlite:books.db";

    static {
        try {
            // Thực hiện nạp lớp Driver SQLite vào bộ nhớ khi lớp DatabaseManager được nạp lần đầu tiên.
            // Điều này đảm bảo thư viện JDBC SQLite sẵn sàng hoạt động trước khi bất kỳ kết nối nào được tạo ra.
            Class.forName("org.sqlite.JDBC");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy SQLite JDBC Driver trong thư mục thư viện (classpath): " + e.getMessage());
        }
    }

    // Phương thức tĩnh dùng để thiết lập kết nối kết nối mới tới cơ sở dữ liệu SQLite.
    // Các lớp nghiệp vụ DAO sẽ gọi phương thức này mỗi khi cần truy vấn hoặc cập nhật dữ liệu.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Phương thức tự động khởi tạo cấu trúc bảng dữ liệu 'books' nếu hệ thống chạy lần đầu và bảng chưa được tạo.
    // Giúp chương trình hoạt động độc lập và tự động thiết lập môi trường dữ liệu chuẩn xác mà không cần tạo bảng thủ công bên ngoài.
    private static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS books (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "title TEXT NOT NULL, " +
                     "author TEXT, " +
                     "release_date TEXT, " +
                     "content TEXT" +
                     ");";
                     
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            // Thực thi câu lệnh SQL để đảm bảo sự tồn tại của bảng books
            stmt.execute(sql);
            System.out.println("Cơ sở dữ liệu SQLite đã được kết nối và kiểm tra bảng dữ liệu thành công.");
        } catch (SQLException e) {
            System.err.println("Gặp lỗi nghiêm trọng trong quá trình khởi tạo cấu trúc cơ sở dữ liệu: " + e.getMessage());
        }
    }
}
