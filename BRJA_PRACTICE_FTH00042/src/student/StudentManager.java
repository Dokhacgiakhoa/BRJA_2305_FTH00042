package student;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Quản lý danh sách sinh viên: thêm, đọc/ghi file, hiển thị
public class StudentManager {
    private List<Student> students;
    private static final String FILE_NAME = "students.dat";

    public StudentManager() {
        this.students = new ArrayList<>();
        loadFromFile();
    }

    // Thêm sinh viên mới vào danh sách tạm
    public void addStudent(Student s) {
        students.add(s);
    }

    public List<Student> getStudents() {
        return students;
    }

    // Ghi toàn bộ danh sách sinh viên hiện tại vào file nhị phân (students.dat)
    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(students);
            System.out.println("Lưu thành công " + students.size() + " sinh viên vào file " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi dữ liệu ra file: " + e.getMessage());
        }
    }

    // Đọc danh sách sinh viên từ file nhị phân
    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            students = new ArrayList<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            students = (List<Student>) ois.readObject();
        } catch (FileNotFoundException e) {
            students = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Lỗi khi đọc dữ liệu từ file: " + e.getMessage());
            students = new ArrayList<>();
        }
    }

    // In danh sách sinh viên ra console theo bảng
    public void displayAll() {
        loadFromFile(); // Đọc lại file trước khi in để đảm bảo data mới nhất
        if (students.isEmpty()) {
            System.out.println("Không có sinh viên nào trong hệ thống.");
            return;
        }

        // Căn lề theo mẫu yêu cầu: EnrolID 16, Full Name 43, Age 7
        System.out.printf("%-16s %-43s %-7s%n", "EnrolID", "Full Name", "Age");
        System.out.printf("%-16s %-43s %-7s%n", "----------------", "-------------------------------------------", "-------");
        for (Student s : students) {
            System.out.printf("%-16s %-43s %-7d%n", s.getEnrolID(), s.getFullName(), s.getAge());
        }
    }
}
