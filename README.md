## CẤU TRÚC THƯ MỤC BÀI LÀM
Thư mục dự án được tổ chức khoa học và phân chia rõ ràng thành hai phần dự án độc lập tương ứng với hai đề thi:

```text
BRJA_2305_FTH00042/
│
├── BRJA_PRACTICE_FTH00042/        # Phần 1: Dự án Quản lý Sinh viên (Console)
│   ├── src/student/               # Thư mục chứa mã nguồn Java
│   │   ├── Student.java           # Lớp thực thể đối tượng Sinh viên (Serializable)
│   │   ├── StudentManager.java    # Xử lý luồng IO ghi/đọc nhị phân và hiển thị bảng Console
│   │   ├── Main.java              # Lớp chứa hàm main khởi chạy chương trình Menu tương tác
│   │   └── StudentTest.java       # Bộ 10 ca kiểm thử tự động tự kiểm chứng nghiệp vụ
│   └── run.bat                    # Script chạy nhanh chương trình trên hệ điều hành Windows
│
├── BRJA_ASM_FTH00042/             # Phần 2: Dự án Quản lý Sách nâng cao (Swing GUI & SQLite)
│   ├── src/book/                  # Thư mục chứa mã nguồn Java
│   │   ├── Book.java              # Lớp thực thể đối tượng Sách
│   │   ├── DatabaseManager.java   # Quản lý Driver và cấu trúc bảng SQLite tự động
│   │   ├── BookDAO.java           # Thực hiện các truy vấn dữ liệu CRUD SQLite bằng PreparedStatement
│   │   ├── AutoScrollReader.java  # Hộp thoại đọc sách tối màu hỗ trợ Timer tự động cuộn
│   │   ├── BookGUI.java           # Lớp giao diện Swing chính quản trị thư viện sách
│   │   └── BookTest.java          # Bộ 10 ca kiểm thử tự động kết nối CSDL và các hàm DAO
│   ├── lib/                       # Chứa thư viện liên kết SQLite JDBC Driver (.jar)
│   └── run.bat                    # Script chạy nhanh chương trình trên hệ điều hành Windows
│
├── .gitignore                     # Cấu hình bỏ qua các tệp tin biên dịch dư thừa khi lưu trữ
└── README.md                      # Tệp tin báo cáo nộp bài thi này
```

## HƯỚNG DẪN CHẠY VÀ CHẤM BÀI CHI TIẾT

### 1. Phần 1: Chương trình Quản lý Sinh viên (Console)
Ứng dụng thực hiện quản lý danh sách sinh viên tạm thời trong bộ nhớ và lưu trữ bền vững lâu dài bằng kỹ thuật tuần tự hóa đối tượng ra file nhị phân `students.dat`.

* **Cách chạy nhanh (Khuyến nghị)**:
  1. Mở thư mục `BRJA_PRACTICE_FTH00042`.
  2. Kích đúp vào file `run.bat` để chương trình tự động biên dịch và khởi chạy trên cửa sổ Command Prompt của Windows.
  
* **Cách biên dịch và khởi chạy thủ công bằng Terminal**:
  ```powershell
  cd BRJA_PRACTICE_FTH00042
  javac -encoding UTF-8 -d bin src/student/*.java
  java "-Dfile.encoding=UTF-8" -cp bin student.Main
  ```
  *(Lưu ý: Tham số `-encoding UTF-8` và `-Dfile.encoding=UTF-8` là bắt buộc để hỗ trợ hiển thị tiếng Việt có dấu chuẩn xác trên cửa sổ dòng lệnh)*

* **Chạy bộ kiểm thử tự động bài Sinh viên**:
  Để chấm điểm nhanh tính chính xác của 10 ca kiểm thử nghiệp vụ bài sinh viên:
  ```powershell
  java "-Dfile.encoding=UTF-8" -cp bin student.StudentTest
  ```
  Kết quả mong đợi: Chương trình sẽ hiển thị đầy đủ thông tin vượt qua `10/10` ca kiểm thử từ Case 01 đến Case 10 và in giao diện bảng sinh viên mẫu.

### 2. Phần 2: Chương trình Quản lý Sách nâng cao (Swing GUI & SQLite)
Ứng dụng quản trị sách toàn diện tích hợp giao diện đồ họa người dùng hiện đại Swing (sắp xếp GridBagLayout thông minh, màu sắc chủ đạo hài hòa dễ nhìn) kết hợp cơ sở dữ liệu SQLite cục bộ để quản lý thông tin.

* **Cách chạy nhanh (Khuyến nghị)**:
  1. Mở thư mục `BRJA_ASM_FTH00042`.
  2. Kích đúp vào file `run.bat` để tự động liên kết thư viện driver SQLite trong thư mục `lib/` và khởi chạy giao diện Swing.

* **Cách biên dịch và khởi chạy thủ công bằng Terminal**:
  ```powershell
  cd BRJA_ASM_FTH00042
  javac -encoding UTF-8 -cp "lib/*" -d bin src/book/*.java
  java "-Dfile.encoding=UTF-8" -cp "bin;lib/*" book.BookGUI
  ```

* **Chạy bộ kiểm thử tự động bài Sách**:
  Bộ kiểm thử thực hiện chạy tuần tự 10 ca kiểm thử để kiểm tra kết nối database SQLite thực tế và toàn bộ logic CRUD của tầng dữ liệu DAO:
  ```powershell
  java "-Dfile.encoding=UTF-8" -cp "bin;lib/*" book.BookTest
  ```
  Kết quả mong đợi: Chương trình dọn dẹp database cũ, tự động khởi tạo lại cấu trúc bảng mới, vượt qua `10/10` ca kiểm thử từ Case 11 đến Case 20 (thêm mới, sửa, xóa, tìm kiếm gần đúng bằng LIKE, xuất file đơn lẻ, xuất danh mục hàng loạt).

## TÓM TẮT CÁC CÔNG NGHỆ VÀ KỸ THUẬT ĐÃ ÁP DỤNG
Qua việc hoàn thành bài thi này, em đã áp dụng thành công các kiến thức cốt lõi của ngôn ngữ lập trình Java Core bao gồm:
1. **Lập trình hướng đối tượng (OOP)**: Thiết kế các thực thể đóng gói thuộc tính khoa học, thiết lập quan hệ cấu trúc dữ liệu rõ ràng.
2. **Luồng vào ra dữ liệu (Java IO)**: Sử dụng luồng nhị phân `ObjectOutputStream`/`ObjectInputStream` để tuần tự hóa dữ liệu đối tượng; sử dụng các lớp ghi văn bản `PrintWriter` và `FileWriter` để định dạng xuất báo cáo văn bản chuyên nghiệp.
3. **Thao tác cơ sở dữ liệu SQLite**: Sử dụng JDBC Driver kết nối cơ sở dữ liệu dạng tệp cục bộ SQLite, thực hiện truy vấn CRUD nâng cao, bảo mật thông tin bằng cách gán tham số qua `PreparedStatement` để phòng chống SQL Injection.
4. **Lập trình giao diện Swing nâng cao**: Xây dựng Layout linh hoạt (BorderLayout, GridBagLayout), đồng bộ giao diện theo Look and Feel hệ thống, cập nhật JScrollBar bằng `Timer` định kỳ nhằm tránh xung đột luồng và nâng cao trải nghiệm người dùng với chế độ đọc sách ban đêm.
5. **Kiểm tra và ràng buộc dữ liệu đầu vào**: Xử lý ngoại lệ `NumberFormatException`, lọc bỏ khoảng trắng thừa bằng `.trim()`, dùng biểu thức chính quy (Regex) để xử lý ký tự cấm đặt tên file của hệ điều hành Windows.
