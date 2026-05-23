package book;

import java.io.File;
import java.sql.Connection;
import java.io.PrintWriter;
import java.util.List;

// Bộ kiểm thử tự động 10 ca kiểm thử cho bài ASM Book (SQLite)
public class BookTest {
    private static int passedCases = 0;
    private static int totalCases = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  BẮT ĐẦU CHẠY 10 TEST CASES - BÀI ASM BOOK (SQLITE)");
        System.out.println("==================================================");

        // Xóa file database cũ đi để tránh lẫn dữ liệu cũ khi test
        File dbFile = new File("books.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }

        runTestCase("Case 11: Khởi tạo thực thể Book hợp lệ", () -> {
            Book b = new Book("Java Coding", "DeepMind", "2026-05", "Learn Java coding step by step.");
            return b.getTitle().equals("Java Coding") && b.getAuthor().equals("DeepMind") && b.getReleaseDate().equals("2026-05") && b.getContent().equals("Learn Java coding step by step.");
        });

        runTestCase("Case 12: Thiết lập kết nối SQLite thành công", () -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                return conn != null && !conn.isClosed();
            }
        });

        runTestCase("Case 13: Khởi tạo bảng dữ liệu 'books' tự động thành công", () -> {
            // Kiểm tra xem bảng books có truy vấn được không
            BookDAO dao = new BookDAO();
            dao.getAllBooks(); // Nếu không văng ngoại lệ SQL là bảng đã được tạo
            return true;
        });

        BookDAO dao = new BookDAO();
        // Mảng 1 phần tử dùng để lưu trữ lại ID của cuốn sách vừa thêm
        final int[] testBookId = new int[1];

        runTestCase("Case 14: Thêm sách mới vào CSDL SQLite (Insert)", () -> {
            Book newBook = new Book("Lập Trình Hướng Đối Tượng", "Aptech", "2025-10-12", "Nội dung học OOP từ cơ bản đến nâng cao.");
            boolean success = dao.addBook(newBook);
            if (success) {
                List<Book> books = dao.getAllBooks();
                if (!books.isEmpty()) {
                    testBookId[0] = books.get(0).getId(); // Store latest ID
                    return true;
                }
            }
            return false;
        });

        runTestCase("Case 15: Truy vấn danh sách sách (Select All)", () -> {
            List<Book> list = dao.getAllBooks();
            return list != null && !list.isEmpty();
        });

        runTestCase("Case 16: Tìm kiếm chi tiết sách theo ID (Select by ID)", () -> {
            if (testBookId[0] == 0) return false;
            Book book = dao.getBookById(testBookId[0]);
            return book != null && book.getTitle().equals("Lập Trình Hướng Đối Tượng");
        });

        runTestCase("Case 17: Tìm kiếm sách theo từ khóa Tiêu đề (LIKE Search)", () -> {
            List<Book> searchResult = dao.searchBooksByName("Hướng Đối Tượng");
            return searchResult != null && !searchResult.isEmpty() && searchResult.get(0).getId() == testBookId[0];
        });

        runTestCase("Case 18: Cập nhật thông tin sách thành công (Update)", () -> {
            if (testBookId[0] == 0) return false;
            Book updateBook = new Book(testBookId[0], "Java Programming II (Updated)", "Aptech Team", "2026-01-01", "Nội dung nâng cao Java Swing và JDBC.");
            boolean success = dao.updateBook(updateBook);
            if (success) {
                Book check = dao.getBookById(testBookId[0]);
                return check != null && check.getTitle().equals("Java Programming II (Updated)") && check.getAuthor().equals("Aptech Team");
            }
            return false;
        });

        runTestCase("Case 19: Xuất thông tin sách ra tệp tin văn bản (Export)", () -> {
            if (testBookId[0] == 0) return false;
            Book book = dao.getBookById(testBookId[0]);
            if (book == null) return false;
            
            String fileName = "test_export_book.txt";
            File f = new File(fileName);
            if (f.exists()) f.delete();

            try (PrintWriter writer = new PrintWriter(f)) {
                writer.println("Title: " + book.getTitle());
                writer.println("Content: " + book.getContent());
            }
            
            boolean exists = f.exists() && f.length() > 0;
            if (exists) {
                f.delete(); // Dọn dẹp: xóa file test sau khi chạy xong
            }
            return exists;
        });

        runTestCase("Case 20: Xóa sách ra khỏi CSDL SQLite thành công (Delete)", () -> {
            if (testBookId[0] == 0) return false;
            boolean success = dao.deleteBook(testBookId[0]);
            if (success) {
                Book check = dao.getBookById(testBookId[0]);
                return check == null; // Phải là null sau khi xóa thành công
            }
            return false;
        });

        System.out.println("\n==================================================");
        System.out.println("  KẾT QUẢ: ĐÃ VƯỢT QUA " + passedCases + "/" + totalCases + " TEST CASES BÀI ASM BOOK");
        System.out.println("==================================================");
    }

    private static void runTestCase(String caseName, TestAction action) {
        totalCases++;
        try {
            boolean success = action.execute();
            if (success) {
                System.out.println("[PASS] " + caseName);
                passedCases++;
            } else {
                System.out.println("[FAIL] " + caseName + " (Logic trả về sai)");
            }
        } catch (Exception e) {
            System.out.println("[FAIL] " + caseName + " (Ngoại lệ phát sinh: " + e.getMessage() + ")");
        }
    }

    interface TestAction {
        boolean execute() throws Exception;
    }
}
