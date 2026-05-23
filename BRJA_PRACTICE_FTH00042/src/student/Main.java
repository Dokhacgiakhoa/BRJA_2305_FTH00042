package student;

import java.util.Scanner;

// Class chạy chính của chương trình quản lý sinh viên (giao diện Console)
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentManager manager = new StudentManager();

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
                    System.out.println("Cảm ơn bạn đã sử dụng chương trình. Tạm biệt!");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn từ 1 đến 4.");
            }
            System.out.println();
        } while (choice != 4);
    }

    private static void showMenu() {
        System.out.println("================ STUDENT MANAGEMENT ===============");
        System.out.println("1. Add new student");
        System.out.println("2. Save");
        System.out.println("3. Display all students");
        System.out.println("4. Exit");
        System.out.println("===================================================");
    }

    private static void addNewStudent() {
        System.out.println("\n--- Thêm Sinh viên Mới ---");
        String enrolID = getStringInput("Nhập mã sinh viên (EnrolID): ");
        while (enrolID.trim().isEmpty()) {
            System.out.print("Mã sinh viên không được để trống! Nhập lại: ");
            enrolID = scanner.nextLine();
        }

        String firstName = getStringInput("Nhập Tên (First Name): ");
        while (firstName.trim().isEmpty()) {
            System.out.print("Tên không được để trống! Nhập lại: ");
            firstName = scanner.nextLine();
        }

        String lastName = getStringInput("Nhập Họ đệm (Last Name): ");
        while (lastName.trim().isEmpty()) {
            System.out.print("Họ đệm không được để trống! Nhập lại: ");
            lastName = scanner.nextLine();
        }

        int age = getAgeInput();

        Student s = new Student(enrolID, firstName, lastName, age);
        manager.addStudent(s);
        System.out.println("Thêm thành công sinh viên " + s.getFullName() + " vào danh sách tạm.");
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Vui lòng nhập một số nguyên hợp lệ!");
            }
        }
    }

    private static int getAgeInput() {
        while (true) {
            int age = getIntInput("Nhập Tuổi (Age): ");
            if (age > 0 && age < 150) {
                return age;
            }
            System.out.println("Lỗi: Tuổi sinh viên phải nằm trong khoảng từ 1 đến 150!");
        }
    }
}
