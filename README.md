# Tài Liệu Hướng Dẫn & Giới Thiệu Dự Án (Java Core & SQLite)

Chào mừng bạn đến với kho lưu trữ mã nguồn của 2 bài tập Java thực tế. Dự án này bao gồm hai chương trình độc lập được thiết kế tối ưu, có chú thích tiếng Việt rõ ràng, ngắn gọn và đi kèm các bộ kiểm thử tự động (10/10 Test Cases) để tự kiểm chứng chất lượng.

---

## 📂 Cấu Trúc Thư Mục Dự Án

```text
BRJA_2305_FTH00042/
│
├── BRJA_PRACTICE_FTH00042/        # 1. Dự án Quản lý Sinh viên (Console)
│   ├── src/student/               # Thư mục mã nguồn chính
│   │   ├── Student.java           # Định nghĩa đối tượng sinh viên (Serializable)
│   │   ├── StudentManager.java    # Logic lưu trữ dữ liệu nhị phân (.dat) và in bảng
│   │   ├── Main.java              # Cửa sổ Menu tương tác Console chính
│   │   └── StudentTest.java       # Bộ 10 ca kiểm thử tự động bài Student
│   └── run.bat                    # Script tự động biên dịch và khởi chạy trên Windows
│
├── BRJA_ASM_FTH00042/             # 2. Dự án Quản lý Sách (SQLite & Swing GUI)
│   ├── src/book/                  # Thư mục mã nguồn chính
│   │   ├── Book.java              # Định nghĩa đối tượng Sách (Book)
│   │   ├── DatabaseManager.java   # Tự động nạp SQLite driver và tạo bảng tự động
│   │   ├── BookDAO.java           # Lớp Data Access Object thực hiện các truy vấn CRUD SQL
│   │   ├── AutoScrollReader.java  # Giao diện đọc sách hỗ trợ cuộn tự động (Timer)
│   │   ├── BookGUI.java           # Giao diện quản trị Swing chính (Modern Design)
│   │   └── BookTest.java          # Bộ 10 ca kiểm thử tự động bài Book với SQLite
│   ├── lib/                       # Chứa thư viện SQLite JDBC Driver (.jar)
│   └── run.bat                    # Script tự động biên dịch và khởi chạy trên Windows
│
└── .gitignore                     # Cấu hình bỏ qua các file thừa (bin, DB, dat, PDF) khi đẩy Git
```

---

## 🛠️ Yêu Cầu Hệ Thống

Để chạy các ứng dụng trong dự án, máy tính của bạn cần cài đặt sẵn:
- **Java Development Kit (JDK)**: Phiên bản 8 trở lên (khuyến nghị JDK 17 hoặc 21).
- **Môi trường Windows**: Có hỗ trợ chạy file script `.bat` (Command Prompt hoặc PowerShell).
- **Hỗ trợ bảng mã UTF-8**: Để hiển thị tiếng Việt có dấu chuẩn xác trên cửa sổ Console.

---

## 1. 🎓 Chương Trình Quản Lý Sinh Viên (Student Management)

Ứng dụng chạy trực tiếp trên giao diện dòng lệnh (Console), quản lý danh sách sinh viên tạm thời trong bộ nhớ và lưu trữ bền vững bằng kỹ thuật **Binary Serialization (Tuần tự hóa đối tượng)** ra file vật lý `students.dat`.

### ✨ Các Tính Năng Chính
- **Add new student**: Thêm sinh viên mới, tự động kiểm tra tính hợp lệ dữ liệu (không được để trống, tuổi phải nằm trong khoảng 1 - 150, phòng chống lỗi nhập chữ thay cho số).
- **Save**: Lưu toàn bộ danh sách sinh viên hiện có từ bộ nhớ tạm vào file nhị phân `students.dat` trên đĩa cứng.
- **Display all students**: Tải lại dữ liệu mới nhất từ file nhị phân và in ra bảng danh sách được căn chỉnh cột chính xác theo đúng khuôn mẫu chuẩn của đề bài.

### 🚀 Hướng Dẫn Chạy Chương Trình
1. Mở thư mục `BRJA_PRACTICE_FTH00042`.
2. Kích đúp (Double-click) vào file `run.bat` để biên dịch tự động và khởi chạy ứng dụng.
3. *Cách chạy thủ công bằng terminal*:
   ```powershell
   cd BRJA_PRACTICE_FTH00042
   javac -encoding UTF-8 -d bin src/student/*.java
   java "-Dfile.encoding=UTF-8" -cp bin student.Main
   ```

### 🧪 Bộ Kiểm Thử Tự Động (StudentTest)
Để chạy kiểm tra 10 ca kiểm thử tự động xem lớp học có hoạt động chính xác theo nghiệp vụ yêu cầu hay không:
```powershell
java "-Dfile.encoding=UTF-8" -cp bin student.StudentTest
```

---

## 2. 📚 Chương Trình Quản Lý Sách Cao Cấp (Book Management)

Ứng dụng quản trị thư viện sách toàn diện tích hợp giao diện đồ họa **Java Swing hiện đại** (sắp xếp layout thông minh, sử dụng bảng màu HSL hài hòa, dễ nhìn) kết hợp cơ sở dữ liệu quan hệ **SQLite** để lưu trữ.

### ✨ Các Tính Năng Nổi Bật
- **CRUD nâng cao**: Thêm mới, Cập nhật thông tin chi tiết, Xóa bỏ sách trực tiếp trên bảng hiển thị trực quan (có hộp thoại xác nhận an toàn trước khi xóa).
- **Tìm kiếm đa năng**: Hỗ trợ tìm kiếm nhanh không phân biệt hoa thường theo **Mã sách (ID)** hoặc **Từ khóa Tiêu đề** (truy vấn `LIKE` SQL).
- **Giao diện đọc sách cuộn tự động (Auto Scroll Reader)**:
  - Sử dụng giao diện đọc chế độ tối (Dark Mode) giúp bảo vệ mắt.
  - Tích hợp thanh trượt (Slider) cho phép điều chỉnh tốc độ cuộn từ chậm đến nhanh.
  - Có nút bắt đầu (Start) / dừng lại (Stop) linh hoạt và tự động thông báo khi đọc hết cuốn sách.
- **Xuất báo cáo văn bản chuyên nghiệp (Export)**:
  - **Xuất đơn lẻ**: Xuất thông tin cuốn sách đang chọn thành file `.txt` có định dạng rõ ràng.
  - **Xuất hàng loạt**: Tự động tạo thư mục `/exported_books/` xuất tất cả các cuốn sách thành các file riêng biệt, đồng thời tạo ra một file tổng hợp danh mục toàn bộ sách thư viện (`all_books_summary.txt`) được căn lề cột hoàn hảo.

### 🚀 Hướng Dẫn Chạy Chương Trình
1. Mở thư mục `BRJA_ASM_FTH00042`.
2. Kích đúp vào file `run.bat` để biên dịch kèm nạp thư viện SQLite trong thư mục `lib/` và khởi chạy giao diện Swing.
3. *Cách chạy thủ công bằng terminal*:
   ```powershell
   cd BRJA_ASM_FTH00042
   javac -encoding UTF-8 -cp "lib/*" -d bin src/book/*.java
   java "-Dfile.encoding=UTF-8" -cp "bin;lib/*" book.BookGUI
   ```

### 🧪 Bộ Kiểm Thử Tự Động (BookTest)
Bộ kiểm thử tích hợp tự động thiết lập và dọn dẹp môi trường sạch, chạy tuần tự 10 ca kiểm thử kết nối DB SQLite và các chức năng của DAO:
```powershell
java "-Dfile.encoding=UTF-8" -cp "bin;lib/*" book.BookTest
```

---

## 📜 Lưu Ý Về Việc Đẩy Git
Tệp tin cấu hình `.gitignore` ở thư mục gốc đã được thiết kế sẵn sàng và tối ưu. Khi bạn chạy lệnh `git commit`, hệ thống sẽ:
1. **Chỉ đẩy lên các file mã nguồn có giá trị** (`.java`, các thư viện `.jar` cần thiết, tệp `.bat` khởi chạy ứng dụng và file `README.md` này).
2. **Tự động loại bỏ hoàn toàn các file rác phát sinh**: Thư mục biên dịch `bin/`, file cơ sở dữ liệu `books.db`, file nhị phân `students.dat`, các file đề bài hướng dẫn PDF gốc và các file cài đặt cấu hình riêng của IDE cá nhân (`.idea/`, `.vscode/`).