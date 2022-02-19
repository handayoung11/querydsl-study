package study.querydsl.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class StudentTest {
    @Autowired
    EntityManager em;

    @Test
    public void entityTest() {
        Club programming = new Club("programming");
        em.persist(programming);

        Student mania = new Student("code-mania", 1, 20, programming),
                lover = new Student("code-lover", 2, 21, programming);
        em.persist(mania);
        em.persist(lover);

        em.flush();
        em.clear();

        List<Student> students = em.createQuery(
                "select s from Student s where s.id in :ids", Student.class
                ).setParameter("ids", Arrays.asList(mania.getId(), lover.getId()))
                .getResultList();
        assertThat(students.size()).isEqualTo(2);
        assertThat(students.get(0).getClub()).isEqualTo(students.get(1).getClub());
        assertThat(students.get(0).getClub().getId()).isEqualTo(programming.getId());
    }
}
