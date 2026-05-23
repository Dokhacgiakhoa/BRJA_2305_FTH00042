package book;

// Lớp biểu diễn thông tin chi tiết của một cuốn sách trong hệ thống quản lý thư viện.
// Đây là một lớp thực thể (Entity hay POJO) tuân thủ mô hình đóng gói dữ liệu JavaBean,
// giúp luân chuyển thông tin của sách một cách nhất quán giữa tầng giao diện người dùng (GUI)
// và tầng truy xuất cơ sở dữ liệu (DAO).
public class Book {
    // Mã định danh duy nhất của cuốn sách, trường này tự động tăng trong cơ sở dữ liệu SQLite
    private int id;
    
    // Tiêu đề hoặc tên của cuốn sách
    private String title;
    
    // Tên tác giả viết cuốn sách
    private String author;
    
    // Ngày cuốn sách được xuất bản hoặc phát hành ra thị trường
    private String releaseDate;
    
    // Nội dung chi tiết của cuốn sách, dùng để hiển thị trên giao diện đọc sách tự động
    private String content;

    // Phương thức khởi tạo mặc định không tham số, giúp tạo một đối tượng sách trống
    public Book() {
    }

    // Phương thức khởi tạo mới một đối tượng sách khi chưa có mã số ID (dùng khi thêm mới sách vào hệ thống)
    public Book(String title, String author, String releaseDate, String content) {
        this.title = title;
        this.author = author;
        this.releaseDate = releaseDate;
        this.content = content;
    }

    // Phương thức khởi tạo đầy đủ thông tin sách bao gồm cả mã số ID đã được cấp phát từ database
    public Book(int id, String title, String author, String releaseDate, String content) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.releaseDate = releaseDate;
        this.content = content;
    }

    // Các phương thức Getter và Setter giúp truy xuất và cập nhật thông tin cho các thuộc tính của lớp
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
