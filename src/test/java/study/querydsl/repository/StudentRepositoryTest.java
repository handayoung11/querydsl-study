package study.querydsl.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.StudentClubDTO;
import study.querydsl.dto.StudentSearchCondition;
import study.querydsl.entity.Club;
import study.querydsl.entity.QStudent;
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
    Club programming, basket;
    Student cMania, cLover, bMania, bLover;

    @BeforeEach
    public void data() {
        programming = new Club("programming");
        basket = new Club("basket");
        em.persist(programming);
        em.persist(basket);

        cMania = new Student("code-mania", 1, 20, programming);
        cLover = new Student("code-lover", 2, 21, programming);
        bMania = new Student("basket-mania", 1, 20, basket);
        bLover = new Student("basket-lover", 2, 21, basket);
        em.persist(cMania);
        em.persist(cLover);
        em.persist(bMania);
        em.persist(bLover);
    }

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


    @Test
    public void searchTest() {
        StudentSearchCondition condition = new StudentSearchCondition();
        condition.setMaxAge(20);
        condition.setClubName(programming.getName());

//        List<StudentClubDTO> searchStudentClubDTORes = studentRepository.search(condition);
//        assertThat(searchStudentClubDTORes).extracting("studentId").contains(cMania.getId());

        PageRequest pageRequest = PageRequest.of(0, 3);
        Page<StudentClubDTO> result = studentRepository.searchAndPageWithCount(new StudentSearchCondition(), pageRequest);
        assertThat(result.getSize()).isEqualTo(3);
    }

    @Test
    public void findAllWithPredicate() {
        QStudent student = QStudent.student;
        Iterable<Student> students = studentRepository.findAll(student.age.between(20, 25).and(student.name.contains("code")));
        for (Student s : students) {
            System.out.println("s = " + s);
        }
        assertThat(students).extracting("name").contains("code");
    }
}
