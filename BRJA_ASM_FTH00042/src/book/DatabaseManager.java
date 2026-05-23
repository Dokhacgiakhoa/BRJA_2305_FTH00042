package book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Quản lý kết nối và khởi tạo database SQLite
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:books.db";

    static {
        try {
            // Nạp driver SQLite
            Class.forName("org.sqlite.JDBC");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy SQLite JDBC Driver trong classpath: " + e.getMessage());
        }
    }

    // Kết nối đến SQLite
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Tự động tạo bảng books nếu chưa có
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
            stmt.execute(sql);
            System.out.println("Cơ sở dữ liệu SQLite đã được khởi tạo thành công.");
        } catch (SQLException e) {
            System.err.println("Lỗi khi khởi tạo cơ sở dữ liệu: " + e.getMessage());
        }
    }
}
