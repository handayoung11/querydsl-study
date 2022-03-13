package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Student;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static study.querydsl.entity.QStudent.student;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentJpaRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Transactional
    public void save(Student student) {
        em.persist(student);
    }

    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(em.find(Student.class, id));
    }

    public List<Student> findAll() {
        return em.createQuery("select s from Student s")
                .getResultList();
    }

    public List<Student> findAll_DSL() {
        return queryFactory.selectFrom(student).fetch();
    }

    public List<Student> findByName(String name) {
        return em.createQuery("select s from Student s where s.name = :name")
                .setParameter("name", name)
                .getResultList();
    }

    public List<Student> findByName_DSL(String name) {
        return queryFactory.selectFrom(student)
                .where(student.name.eq(name))
                .fetch();
    }
}
