package study.querydsl.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.StudentClubDTO;
import study.querydsl.dto.StudentSearchCondition;
import study.querydsl.entity.Club;
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

    @Test
    public void searchTest() {
        Club programming = new Club("programming");
        Club basket = new Club("basket");
        em.persist(programming);
        em.persist(basket);

        Student cMania = new Student("code-mania", 1, 20, programming);
        Student cLover = new Student("code-lover", 2, 21, programming);
        Student bMania = new Student("basket-mania", 1, 20, basket);
        Student bLover = new Student("basket-lover", 2, 21, basket);
        em.persist(cMania);
        em.persist(cLover);
        em.persist(bMania);
        em.persist(bLover);

        StudentSearchCondition condition = new StudentSearchCondition();
        condition.setMaxAge(20);
        condition.setClubName(programming.getName());

        List<StudentClubDTO> searchByBuilderRes = studentJpaRepository.searchByBuilder(condition);
        assertThat(searchByBuilderRes).extracting("studentId").contains(cMania.getId());

        List<StudentClubDTO> searchStudentClubDTORes = studentJpaRepository.searchStudentClubDTO(condition);
        assertThat(searchStudentClubDTORes).extracting("studentId").contains(cMania.getId());
    }
}