package student;

import java.io.Serializable;

// Lớp định nghĩa thực thể Sinh viên trong hệ thống quản lý sinh viên.
// Lớp này triển khai giao diện Serializable để cho phép môi trường Java Core thực hiện
// kỹ thuật tuần tự hóa đối tượng (Binary Serialization), ghi trực tiếp trạng thái bộ nhớ của thực thể
// ra tệp tin nhị phân students.dat và đọc ngược dữ liệu vào bộ nhớ.
public class Student implements Serializable {
    // Mã số định danh tương thích phiên bản lớp khi tuần tự hóa dữ liệu nhị phân
    private static final long serialVersionUID = 1L;

    // Các thuộc tính cơ bản của một sinh viên theo đặc tả yêu cầu của đề bài
    private String enrolID;
    private String firstName;
    private String lastName;
    private int age;

    // Phương thức khởi tạo mặc định không tham số
    public Student() {
    }

    // Phương thức khởi tạo đầy đủ tham số để nhanh chóng thiết lập thông tin cho sinh viên mới
    public Student(String enrolID, String firstName, String lastName, int age) {
        this.enrolID = enrolID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    // Các phương thức lấy và thiết lập giá trị cho các trường thuộc tính của sinh viên
    public String getEnrolID() {
        return enrolID;
    }

    public void setEnrolID(String enrolID) {
        this.enrolID = enrolID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // Ghép họ và tên sinh viên một cách tự nhiên để hiển thị lên bảng danh sách
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
