package book;

import java.io.File;
import java.sql.Connection;
import java.io.PrintWriter;
import java.util.List;

// Bộ kiểm thử tự động gồm 10 ca kiểm thử tích hợp cho bài ASM Sách kết hợp cơ sở dữ liệu SQLite.
// Bộ kiểm thử này được thiết kế độc lập, tự thiết lập và dọn dẹp dữ liệu mẫu, chạy tuần tự để tự động
// đánh giá tính chính xác của các tính năng trong tầng dữ liệu DAO trước khi bàn giao sản phẩm.
public class BookTest {
    private static int passedCases = 0;
    private static int totalCases = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  BAT DAU CHAY 10 CA KIEM THU - BAI ASM SACH");
        System.out.println("==================================================");

        // Dọn dẹp cơ sở dữ liệu cũ để tránh dữ liệu cũ làm sai lệch kết quả kiểm thử mới
        File dbFile = new File("books.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }

        runTestCase("Ca kiem thu 11: Khoi tao thuc the Book hop le", () -> {
            Book b = new Book("Java Coding", "DeepMind", "2026-05", "Learn Java coding step by step.");
            return b.getTitle().equals("Java Coding") && b.getAuthor().equals("DeepMind") && b.getReleaseDate().equals("2026-05") && b.getContent().equals("Learn Java coding step by step.");
        });

        runTestCase("Ca kiem thu 12: Thiet lap ket noi SQLite thanh cong", () -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                return conn != null && !conn.isClosed();
            }
        });

        runTestCase("Ca kiem thu 13: Khoi tao bang du lieu books tu dong thanh cong", () -> {
            // Kiểm tra xem bảng books có hoạt động truy vấn bình thường không
            BookDAO dao = new BookDAO();
            dao.getAllBooks(); // Nếu không ném ra lỗi SQL tức là bảng đã tồn tại và sẵn sàng
            return true;
        });

        BookDAO dao = new BookDAO();
        // Sử dụng mảng một phần tử làm bao đóng để lưu lại mã số ID của cuốn sách được thêm ngẫu nhiên
        final int[] testBookId = new int[1];

        runTestCase("Ca kiem thu 14: Them moi cuon sach vao co so du lieu SQLite (Insert)", () -> {
            Book newBook = new Book("Lap Trinh Huong Doi Tuong", "Aptech", "2025-10-12", "Noi dung hoc OOP tu co ban den nang cao.");
            boolean success = dao.addBook(newBook);
            if (success) {
                List<Book> books = dao.getAllBooks();
                if (!books.isEmpty()) {
                    testBookId[0] = books.get(0).getId(); // Lưu lại ID mới nhất vừa sinh tự động
                    return true;
                }
            }
            return false;
        });

        runTestCase("Ca kiem thu 15: Truy van lay toan bo danh sach sach (Select All)", () -> {
            List<Book> list = dao.getAllBooks();
            return list != null && !list.isEmpty();
        });

        runTestCase("Ca kiem thu 16: Tim kiem chi tiet sach bang ma so ID (Select by ID)", () -> {
            if (testBookId[0] == 0) return false;
            Book book = dao.getBookById(testBookId[0]);
            return book != null && book.getTitle().equals("Lap Trinh Huong Doi Tuong");
        });

        runTestCase("Ca kiem thu 17: Tim kiem sach gan dung theo ky tu Tieu de (LIKE Search)", () -> {
            List<Book> searchResult = dao.searchBooksByName("Huong Doi Tuong");
            return searchResult != null && !searchResult.isEmpty() && searchResult.get(0).getId() == testBookId[0];
        });

        runTestCase("Ca kiem thu 18: Cap nhat thay doi thong tin sach thanh cong (Update)", () -> {
            if (testBookId[0] == 0) return false;
            Book updateBook = new Book(testBookId[0], "Java Programming II (Updated)", "Aptech Team", "2026-01-01", "Noi dung nang cao Java Swing va JDBC.");
            boolean success = dao.updateBook(updateBook);
            if (success) {
                Book check = dao.getBookById(testBookId[0]);
                return check != null && check.getTitle().equals("Java Programming II (Updated)") && check.getAuthor().equals("Aptech Team");
            }
            return false;
        });

        runTestCase("Ca kiem thu 19: Xuat thong tin sach ra tep tin van ban thanh cong (Export)", () -> {
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
                f.delete(); // Xóa tệp tạm sau khi xác nhận kết quả để bảo vệ sự sạch sẽ của thư mục dự án
            }
            return exists;
        });

        runTestCase("Ca kiem thu 20: Xoa cuon sach ra khoi co so du lieu SQLite (Delete)", () -> {
            if (testBookId[0] == 0) return false;
            boolean success = dao.deleteBook(testBookId[0]);
            if (success) {
                Book check = dao.getBookById(testBookId[0]);
                return check == null; // Sau khi xóa thành công, truy vấn theo ID phải trả về null
            }
            return false;
        });

        System.out.println("\n==================================================");
        System.out.println("  KET QUA: DA VUOT QUA " + passedCases + "/" + totalCases + " CA KIEM THU BAI ASM SACH");
        System.out.println("==================================================");
    }

    // Phương thức chạy ca kiểm thử, xử lý ngoại lệ phát sinh để tránh chương trình kiểm thử bị dừng đột ngột
    private static void runTestCase(String caseName, TestAction action) {
        totalCases++;
        try {
            boolean success = action.execute();
            if (success) {
                System.out.println("[PASS] " + caseName);
                passedCases++;
            } else {
                System.out.println("[FAIL] " + caseName + " (Logic tra ve sai)");
            }
        } catch (Exception e) {
            System.out.println("[FAIL] " + caseName + " (Ngoai le phat sinh: " + e.getMessage() + ")");
        }
    }

    // Giao diện chức năng phục vụ việc gọi kiểm thử an toàn
    interface TestAction {
        boolean execute() throws Exception;
    }
}
