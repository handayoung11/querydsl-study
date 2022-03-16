package study.querydsl.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Student;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class StudentRepositoryTest {
    @Autowired
    EntityManager em;

    @Autowired
    StudentRepository studentRepository;

    @Test
    public void basicTest() {
        Student student = new Student("code-mania", 1, 20, null);
        studentRepository.save(student);

        Student findStudent = studentRepository.findById(student.getId())
                .orElseThrow(NullPointerException::new);
        assertThat(student).isEqualTo(findStudent);

        List<Student> students = studentRepository.findAll();
        assertThat(students).contains(student);

        List<Student> studentsByName = studentRepository.findByName(student.getName());
        assertThat(studentsByName).contains(student);
    }
}
