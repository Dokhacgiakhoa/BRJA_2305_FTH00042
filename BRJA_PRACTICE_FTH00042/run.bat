@echo off
chcp 65001 > nul
echo ==================================================
echo   BIÊN DỊCH VÀ CHẠY BÀI THỰC HÀNH STUDENT
echo ==================================================

rem Tạo thư mục chứa file class đã biên dịch nếu chưa tồn tại
if not exist "bin" mkdir bin

echo 1. Đang biên dịch mã nguồn Java...
javac -encoding UTF-8 -d bin src/student/*.java
if %errorlevel% neq 0 (
    echo [LỖI] Biên dịch mã nguồn thất bại!
    pause
    exit /b %errorlevel%
)
echo [OK] Biên dịch thành công.

echo 2. Đang khởi chạy chương trình...
echo --------------------------------------------------
java -Dfile.encoding=UTF-8 -cp bin student.Main
echo --------------------------------------------------
pause
