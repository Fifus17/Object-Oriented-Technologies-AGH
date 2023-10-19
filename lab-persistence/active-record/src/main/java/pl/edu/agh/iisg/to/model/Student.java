package pl.edu.agh.iisg.to.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import pl.edu.agh.iisg.to.executor.QueryExecutor;

public class Student {
    private final int id;

    private final String firstName;

    private final String lastName;

    private final int indexNumber;

    Student(final int id, final String firstName, final String lastName, final int indexNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.indexNumber = indexNumber;
    }

    public static Optional<Student> create(final String firstName, final String lastName, final int indexNumber) {
        String sql = "INSERT INTO student (first_name, last_name, index_number) VALUES (?, ?, ?);";

        // it is important to maintain the correct order of the variables
        Object[] args = {
                firstName,
                lastName,
                indexNumber
        };

        try {
            int id = QueryExecutor.createAndObtainId(sql, args);
            return Student.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();

    }

    public static Optional<Student> findByIndexNumber(final int indexNumber) {
        String sql = "SELECT * FROM student WHERE index_number = (?);";
        return find(indexNumber, sql);
    }

    public static Optional<Student> findById(final int id) {
        String sql = "SELECT * FROM student WHERE id = (?)";
        return find(id, sql);
    }

    private static Optional<Student> find(int value, String sql) {
        Object[] args = {value};
        try (ResultSet rs = QueryExecutor.read(sql, args)) {
            if (rs.next()) {
                return Optional.of(new Student(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("index_number")
                ));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Map<Course, Float> createReport() {
        String sql = "SELECT course.id as course_id, name as course_name, grade FROM grade JOIN course on course_id = course.id WHERE student_id = ?";
        Object[] args = {
                this.id()
        };

        try (ResultSet rs = QueryExecutor.read(sql, args)) {
            Map<Course, Float> map = new HashMap<>();
            float sum = 0f;
            int noGrades = 0;
            Course oldCourse = new Course(-1, "");
            while (rs.next()) {
                Course course = new Course(rs.getInt("course_id"), rs.getString("course_name"));
                float grade = rs.getFloat("grade");
                if(oldCourse.id() == -1) oldCourse = new Course(rs.getInt("course_id"), rs.getString("course_name"));
                if (course.id() == oldCourse.id()) {
                    sum += grade;
                    noGrades++;
                }
                else {
                    map.put(oldCourse, sum/noGrades);
                    sum = 0 + grade;
                    noGrades = 1;
                }
                oldCourse = new Course(rs.getInt("course_id"), rs.getString("course_name"));
            }
            map.put(oldCourse, sum/noGrades);
            return map;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    public int id() {
        return id;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public int indexNumber() {
        return indexNumber;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String FIRST_NAME = "first_name";

        public static final String LAST_NAME = "last_name";

        public static final String INDEX_NUMBER = "index_number";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Student student = (Student) o;

        if (id != student.id)
            return false;
        if (indexNumber != student.indexNumber)
            return false;
        if (!firstName.equals(student.firstName))
            return false;
        return lastName.equals(student.lastName);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + indexNumber;
        return result;
    }
}
