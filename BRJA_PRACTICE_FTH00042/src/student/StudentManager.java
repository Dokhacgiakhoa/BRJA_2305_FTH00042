package student;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Lớp chịu trách nhiệm quản lý danh sách sinh viên trong bộ nhớ tạm và đồng bộ dữ liệu
// xuống tệp nhị phân students.dat. Lớp này thực hiện các tính năng như thêm mới sinh viên,
// lưu trữ bền vững đối tượng và định dạng in danh sách ra màn hình Console.
public class StudentManager {
    // Danh sách lưu trữ tạm thời các sinh viên trong bộ nhớ RAM
    private List<Student> students;
    
    // Tên tệp vật lý lưu trữ dữ liệu nhị phân của danh sách sinh viên
    private static final String FILE_NAME = "students.dat";

    // Khởi tạo trình quản lý sinh viên và tự động nạp dữ liệu cũ từ tệp lên bộ nhớ
    public StudentManager() {
        this.students = new ArrayList<>();
        loadFromFile();
    }

    // Thêm sinh viên mới vào danh sách tạm thời trong bộ nhớ
    public void addStudent(Student s) {
        students.add(s);
    }

    // Lấy về danh sách sinh viên hiện tại trong bộ nhớ
    public List<Student> getStudents() {
        return students;
    }

    // Thực hiện kỹ thuật tuần tự hóa ghi toàn bộ danh sách đối tượng sinh viên xuống file nhị phân students.dat
    public void saveToFile() {
        // Áp dụng try-with-resources để luồng ObjectOutputStream tự động đóng sau khi ghi dữ liệu xong
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(students);
            System.out.println("Lưu thành công " + students.size() + " sinh viên vào tệp nhị phân " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("Gặp lỗi trong quá trình ghi dữ liệu nhị phân ra tệp tin: " + e.getMessage());
        }
    }

    // Đọc ngược dòng dữ liệu nhị phân từ tệp tin students.dat và chuyển đổi ngược thành danh sách đối tượng (Deserialization)
    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        File file = new File(FILE_NAME);
        // Nếu tệp chưa tồn tại (ví dụ ứng dụng chạy lần đầu tiên), khởi tạo danh sách trống để tránh lỗi hệ thống
        if (!file.exists()) {
            students = new ArrayList<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            students = (List<Student>) ois.readObject();
        } catch (FileNotFoundException e) {
            students = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Gặp sự cố khi cố gắng đọc dữ liệu nhị phân từ tệp: " + e.getMessage());
            students = new ArrayList<>(); // Gán danh sách trống phòng ngừa lỗi dữ liệu bị hỏng
        }
    }

    // Hiển thị danh sách toàn bộ sinh viên ra màn hình Console, căn chỉnh lề cột chuẩn xác theo yêu cầu đề bài
    public void displayAll() {
        loadFromFile(); // Luôn đọc lại tệp tin để đảm bảo dữ liệu hiển thị là dữ liệu mới nhất
        if (students.isEmpty()) {
            System.out.println("Hiện tại chưa có dữ liệu sinh viên nào trong hệ thống.");
            return;
        }

        // Thực hiện căn lề theo định dạng cụ thể: cột EnrolID rộng 16 ký tự, cột Full Name rộng 43 ký tự, cột Age rộng 7 ký tự
        System.out.printf("%-16s %-43s %-7s%n", "EnrolID", "Full Name", "Age");
        System.out.printf("%-16s %-43s %-7s%n", "----------------", "-------------------------------------------", "-------");
        for (Student s : students) {
            System.out.printf("%-16s %-43s %-7d%n", s.getEnrolID(), s.getFullName(), s.getAge());
        }
    }
}
