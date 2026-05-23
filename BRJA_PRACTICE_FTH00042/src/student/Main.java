package student;

import java.util.Scanner;

// Lớp khởi chạy chính của chương trình quản lý sinh viên hiển thị giao diện dòng lệnh Console.
// Tích hợp luồng điều khiển chương trình thông qua hệ thống Menu tương tác trực quan, đồng thời
// xử lý chặt chẽ việc kiểm tra tính hợp lệ của dữ liệu đầu vào ngăn chặn mọi lỗi nhập liệu có thể gây crash chương trình.
public class Main {
    // Đối tượng Scanner toàn cục dùng để nhận dữ liệu nhập vào từ bàn phím người dùng
    private static final Scanner scanner = new Scanner(System.in);
    
    // Đối tượng quản lý dữ liệu sinh viên chịu trách nhiệm thực thi lưu/đọc dữ liệu nhị phân
    private static final StudentManager manager = new StudentManager();

    // Điểm khởi chạy chính của chương trình Java Core
    public static void main(String[] args) {
        int choice;
        do {
            showMenu();
            choice = getIntInput("Nhập lựa chọn của bạn (1-4): ");
            switch (choice) {
                case 1:
                    addNewStudent();
                    break;
                case 2:
                    manager.saveToFile();
                    break;
                case 3:
                    manager.displayAll();
                    break;
                case 4:
                    System.out.println("Cảm ơn bạn đã sử dụng chương trình quản lý sinh viên. Tạm biệt!");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn lại một số nguyên từ 1 đến 4.");
            }
            System.out.println();
        } while (choice != 4);
    }

    // Hiển thị danh sách Menu các chức năng yêu cầu của chương trình
    private static void showMenu() {
        System.out.println("================ STUDENT MANAGEMENT ===============");
        System.out.println("1. Add new student");
        System.out.println("2. Save");
        System.out.println("3. Display all students");
        System.out.println("4. Exit");
        System.out.println("===================================================");
    }

    // Thực hiện thu thập thông tin và thêm một sinh viên mới vào danh sách tạm
    private static void addNewStudent() {
        System.out.println("\n--- Thêm Sinh viên Mới ---");
        
        // Nhập và kiểm tra bắt buộc đối với mã sinh viên (EnrolID) không được để trống
        String enrolID = getStringInput("Nhập mã sinh viên (EnrolID): ");
        while (enrolID.trim().isEmpty()) {
            System.out.print("Lỗi: Mã sinh viên bắt buộc phải nhập và không được để trống! Nhập lại: ");
            enrolID = scanner.nextLine();
        }

        // Nhập và kiểm tra bắt buộc đối với trường Tên (First Name)
        String firstName = getStringInput("Nhập Tên (First Name): ");
        while (firstName.trim().isEmpty()) {
            System.out.print("Lỗi: Tên sinh viên không được bỏ trống! Nhập lại: ");
            firstName = scanner.nextLine();
        }

        // Nhập và kiểm tra bắt buộc đối với trường Họ đệm (Last Name)
        String lastName = getStringInput("Nhập Họ đệm (Last Name): ");
        while (lastName.trim().isEmpty()) {
            System.out.print("Lỗi: Họ đệm sinh viên không được bỏ trống! Nhập lại: ");
            lastName = scanner.nextLine();
        }

        // Nhập và xác thực số tuổi hợp lệ của sinh viên
        int age = getAgeInput();

        Student s = new Student(enrolID, firstName, lastName, age);
        manager.addStudent(s);
        System.out.println("Thêm thành công sinh viên " + s.getFullName() + " vào danh sách bộ nhớ tạm.");
    }

    // Tiện ích lấy chuỗi ký tự từ Console
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    // Tiện ích lấy số nguyên từ bàn phím an toàn, phòng chống lỗi nhập ký tự chữ gây sập ứng dụng
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                // Bắt buộc loại bỏ khoảng trắng thừa hai đầu chuỗi trước khi chuyển đổi kiểu số
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                // Xử lý ngoại lệ NumberFormatException khi người dùng vô tình nhập chữ thay vì số nguyên
                System.out.println("Lỗi cú pháp: Giá trị nhập vào bắt buộc phải là một số nguyên hợp lệ!");
            }
        }
    }

    // Thực hiện nhập tuổi của sinh viên và kiểm tra điều kiện ràng buộc nghiệp vụ (tuổi từ 1 đến 149)
    private static int getAgeInput() {
        while (true) {
            int age = getIntInput("Nhập Tuổi (Age): ");
            if (age > 0 && age < 150) {
                return age;
            }
            System.out.println("Lỗi nghiệp vụ: Số tuổi của sinh viên phải là số dương lớn hơn 0 và nhỏ hơn 150!");
        }
    }
}
