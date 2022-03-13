package study.querydsl.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Student;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StudentJpaRepositoryTest {
    @Autowired
    EntityManager em;
    @Autowired
    StudentJpaRepository studentJpaRepository;

    @Test
    public void basicTest() {
        Student student = new Student("code-mania", 1, 20, null);
        studentJpaRepository.save(student);

        Student findStudent = studentJpaRepository.findById(student.getId())
                .orElseThrow(NullPointerException::new);
        assertThat(student).isEqualTo(findStudent);

        List<Student> students = studentJpaRepository.findAll();
        assertThat(students).contains(student);

        List<Student> studentsByName = studentJpaRepository.findByName(student.getName());
        assertThat(studentsByName).contains(student);
    }

    @Test
    public void basicDSLTest() {
        Student student = new Student("code-mania", 1, 20, null);
        studentJpaRepository.save(student);

        List<Student> students = studentJpaRepository.findAll_DSL();
        assertThat(students).contains(student);

        List<Student> studentsByName = studentJpaRepository.findByName_DSL(student.getName());
        assertThat(studentsByName).contains(student);
    }
}