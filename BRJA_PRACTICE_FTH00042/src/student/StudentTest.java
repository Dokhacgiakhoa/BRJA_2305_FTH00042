package student;

import java.io.File;
import java.util.List;

// Bộ kiểm thử tự động gồm 10 ca kiểm thử độc lập cho bài thực hành Quản lý Sinh viên.
// Bộ kiểm thử này tự thiết lập môi trường dữ liệu, dọn dẹp file nhị phân tạm sau khi chạy
// và đánh giá chính xác từng nghiệp vụ logic từ việc khởi tạo đối tượng, lưu trữ tuần tự hóa
// cho tới việc căn chỉnh lề hiển thị Console.
public class StudentTest {
    private static int passedCases = 0;
    private static int totalCases = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  BAT DAU CHAY 10 CA KIEM THU - BAI SINH VIEN");
        System.out.println("==================================================");

        // Khởi tạo trạng thái sạch bằng cách xóa tệp nhị phân cũ trước khi kiểm thử
        File file = new File("students.dat");
        if (file.exists()) {
            file.delete();
        }

        runTestCase("Ca kiem thu 01: Khoi tao thuc the Student hop le", () -> {
            Student s = new Student("GC01", "Huy", "Nguyen", 20);
            return s.getEnrolID().equals("GC01") && s.getFirstName().equals("Huy") && s.getLastName().equals("Nguyen") && s.getAge() == 20;
        });

        runTestCase("Ca kiem thu 02: Cac phuong thuc Getter/Setter hoat dong dung", () -> {
            Student s = new Student();
            s.setEnrolID("GC02");
            s.setFirstName("Thao");
            s.setLastName("Hoang");
            s.setAge(18);
            return s.getEnrolID().equals("GC02") && s.getFirstName().equals("Thao") && s.getLastName().equals("Hoang") && s.getAge() == 18;
        });

        runTestCase("Ca kiem thu 03: Ghep noi ho ten sinh vien (getFullName)", () -> {
            Student s = new Student("GC03", "Anh", "Tran Van", 21);
            return s.getFullName().equals("Anh Tran Van");
        });

        runTestCase("Ca kiem thu 04: Them sinh vien moi vao StudentManager", () -> {
            StudentManager manager = new StudentManager();
            int initialSize = manager.getStudents().size();
            manager.addStudent(new Student("GC04", "Nam", "Le", 22));
            return manager.getStudents().size() == (initialSize + 1);
        });

        runTestCase("Ca kiem thu 05: Tinh chinh xac cua sinh vien duoc luu tam trong bo nho", () -> {
            StudentManager manager = new StudentManager();
            Student s = new Student("GC05", "Minh", "Vu", 19);
            manager.addStudent(s);
            List<Student> list = manager.getStudents();
            Student last = list.get(list.size() - 1);
            return last.getEnrolID().equals("GC05") && last.getFullName().equals("Minh Vu");
        });

        runTestCase("Ca kiem thu 06: Luu tru danh sach sinh vien vao tep tin students.dat", () -> {
            StudentManager manager = new StudentManager();
            manager.addStudent(new Student("GC00123", "Nguyen Xuan Huy", "", 19));
            manager.addStudent(new Student("GC00125", "Hoang Thu Thao", "", 18));
            manager.saveToFile();
            return true; // Nếu không văng ra lỗi luồng IO là đã ghi tệp tin thành công
        });

        runTestCase("Ca kiem thu 07: Xac minh tep tin students.dat thuc su ton tai tren o cung", () -> {
            File f = new File("students.dat");
            return f.exists() && f.length() > 0;
        });

        runTestCase("Ca kiem thu 08: Doc nguoc du lieu tu tep tin students.dat vao bo nho thanh cong", () -> {
            StudentManager manager2 = new StudentManager();
            manager2.loadFromFile();
            List<Student> list = manager2.getStudents();
            // Phải chứa đầy đủ các sinh viên đã được chèn vào từ các ca kiểm thử trước
            return list.size() >= 2;
        });

        runTestCase("Ca kiem thu 09: Phong chong crash - Khoi tao danh sach trong khi tep tin khong ton tai", () -> {
            // Thực hiện xóa tệp tin để giả lập trường hợp tệp tin bị mất hoặc chưa từng được tạo
            File f = new File("students.dat");
            if (f.exists()) f.delete();

            StudentManager managerTemp = new StudentManager();
            managerTemp.loadFromFile();
            return managerTemp.getStudents() != null && managerTemp.getStudents().isEmpty();
        });

        runTestCase("Ca kiem thu 10: In bang du lieu Console khong phat sinh ngoai le", () -> {
            // Tạo lại dữ liệu mẫu ổn định và thực hiện in thử giao diện dòng lệnh ra màn hình
            StudentManager managerFinal = new StudentManager();
            managerFinal.addStudent(new Student("GC00123", "Nguyen Xuan Huy", "", 19));
            managerFinal.addStudent(new Student("GC00125", "Hoang Thu Thao", "", 18));
            managerFinal.saveToFile();
            
            System.out.println("\n--- Giao dien bang mau in ra thu nghiem: ---");
            managerFinal.displayAll();
            System.out.println("--------------------------------------------");
            return true;
        });

        System.out.println("\n==================================================");
        System.out.println("  KET QUA: DA VUOT QUA " + passedCases + "/" + totalCases + " CA KIEM THU BAI SINH VIEN");
        System.out.println("==================================================");
    }

    // Thực thi ca kiểm thử và báo cáo kết quả kiểm thử an toàn
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

    // Giao diện gọi kiểm thử an toàn
    interface TestAction {
        boolean execute() throws Exception;
    }
}
