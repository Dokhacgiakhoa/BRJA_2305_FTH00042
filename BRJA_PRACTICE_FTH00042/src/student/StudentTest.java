package student;

import java.io.File;
import java.util.List;

// File chạy test tự động 10 ca kiểm thử cho bài Student
public class StudentTest {
    private static int passedCases = 0;
    private static int totalCases = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  BẮT ĐẦU CHẠY 10 TEST CASES - BÀI PRACTICE STUDENT");
        System.out.println("==================================================");

        // Xóa file dữ liệu cũ trước khi test để tránh ảnh hưởng kết quả
        File file = new File("students.dat");
        if (file.exists()) {
            file.delete();
        }

        runTestCase("Case 01: Khởi tạo thực thể Student hợp lệ", () -> {
            Student s = new Student("GC01", "Huy", "Nguyen", 20);
            return s.getEnrolID().equals("GC01") && s.getFirstName().equals("Huy") && s.getLastName().equals("Nguyen") && s.getAge() == 20;
        });

        runTestCase("Case 02: Các phương thức Getter/Setter hoạt động đúng", () -> {
            Student s = new Student();
            s.setEnrolID("GC02");
            s.setFirstName("Thao");
            s.setLastName("Hoang");
            s.setAge(18);
            return s.getEnrolID().equals("GC02") && s.getFirstName().equals("Thao") && s.getLastName().equals("Hoang") && s.getAge() == 18;
        });

        runTestCase("Case 03: Ghép nối họ tên sinh viên (getFullName)", () -> {
            Student s = new Student("GC03", "Anh", "Tran Van", 21);
            return s.getFullName().equals("Anh Tran Van");
        });

        runTestCase("Case 04: Thêm sinh viên mới vào StudentManager", () -> {
            StudentManager manager = new StudentManager();
            int initialSize = manager.getStudents().size();
            manager.addStudent(new Student("GC04", "Nam", "Le", 22));
            return manager.getStudents().size() == (initialSize + 1);
        });

        runTestCase("Case 05: Tính chính xác của sinh viên được lưu tạm trong bộ nhớ", () -> {
            StudentManager manager = new StudentManager();
            Student s = new Student("GC05", "Minh", "Vu", 19);
            manager.addStudent(s);
            List<Student> list = manager.getStudents();
            Student last = list.get(list.size() - 1);
            return last.getEnrolID().equals("GC05") && last.getFullName().equals("Minh Vu");
        });

        runTestCase("Case 06: Lưu trữ danh sách sinh viên vào tệp students.dat", () -> {
            StudentManager manager = new StudentManager();
            manager.addStudent(new Student("GC00123", "Nguyễn Xuân Huy", "", 19));
            manager.addStudent(new Student("GC00125", "Hoàng Thu Thảo", "", 18));
            manager.saveToFile();
            return true; // If no exception occurs
        });

        runTestCase("Case 07: Xác minh tệp tin students.dat thực sự được ghi ra đĩa", () -> {
            File f = new File("students.dat");
            return f.exists() && f.length() > 0;
        });

        runTestCase("Case 08: Đọc ngược dữ liệu từ tệp students.dat vào bộ nhớ thành công", () -> {
            StudentManager manager2 = new StudentManager();
            manager2.loadFromFile();
            List<Student> list = manager2.getStudents();
            // Phải chứa ít nhất 2 sinh viên đã ghi ở Case 06
            return list.size() >= 2;
        });

        runTestCase("Case 09: Phòng chống crash - Khởi tạo danh sách trống khi tệp không tồn tại", () -> {
            // Xóa file đi để test việc khởi tạo danh sách trống
            File f = new File("students.dat");
            if (f.exists()) f.delete();

            StudentManager managerTemp = new StudentManager();
            managerTemp.loadFromFile();
            return managerTemp.getStudents() != null && managerTemp.getStudents().isEmpty();
        });

        runTestCase("Case 10: In bảng dữ liệu Console không phát sinh ngoại lệ", () -> {
            // Khởi tạo lại dữ liệu mẫu và in thử
            StudentManager managerFinal = new StudentManager();
            managerFinal.addStudent(new Student("GC00123", "Nguyễn Xuân Huy", "", 19));
            managerFinal.addStudent(new Student("GC00125", "Hoàng Thu Thảo", "", 18));
            managerFinal.saveToFile();
            
            System.out.println("\n--- Giao diện bảng mẫu in ra thử nghiệm: ---");
            managerFinal.displayAll();
            System.out.println("--------------------------------------------");
            return true;
        });

        System.out.println("\n==================================================");
        System.out.println("  KẾT QUẢ: ĐÃ VƯỢT QUA " + passedCases + "/" + totalCases + " TEST CASES BÀI STUDENT");
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
