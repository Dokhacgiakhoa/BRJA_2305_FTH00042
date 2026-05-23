@echo off
chcp 65001 > nul
echo ==================================================
echo   BIÊN DỊCH VÀ CHẠY BÀI ASSIGNMENT BOOK MANAGEMENT
echo ==================================================

rem Tạo thư mục chứa file class đã biên dịch nếu chưa tồn tại
if not exist "bin" mkdir bin

echo 1. Đang biên dịch mã nguồn Java với thư viện SQLite...
javac -encoding UTF-8 -cp "lib/*" -d bin src/book/*.java
if %errorlevel% neq 0 (
    echo [LỖI] Biên dịch mã nguồn thất bại!
    pause
    exit /b %errorlevel%
)
echo [OK] Biên dịch thành công.

echo 2. Đang khởi chạy chương trình giao diện Swing...
echo (Vui lòng đợi vài giây để cửa sổ hiển thị...)
echo --------------------------------------------------
java -Dfile.encoding=UTF-8 -cp "bin;lib/*" book.BookGUI
echo --------------------------------------------------
pause
