package student;

import java.io.Serializable;

// Class định nghĩa đối tượng Sinh viên (có Serializable để lưu file nhị phân)
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String enrolID;
    private String firstName;
    private String lastName;
    private int age;

    public Student() {
    }

    public Student(String enrolID, String firstName, String lastName, int age) {
        this.enrolID = enrolID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

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

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
