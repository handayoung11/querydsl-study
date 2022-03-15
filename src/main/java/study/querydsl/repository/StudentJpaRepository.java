package study.querydsl.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.QStudentClubDTO;
import study.querydsl.dto.StudentClubDTO;
import study.querydsl.dto.StudentSearchCondition;
import study.querydsl.entity.Student;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;
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

    public List<StudentClubDTO> searchByBuilder(StudentSearchCondition condition) {

        BooleanBuilder builder = new BooleanBuilder();
        if (hasText(condition.getStudentName())) {
            builder.and(student.name.eq(condition.getStudentName()));
        }
        if (hasText(condition.getClubName())) {
            builder.and(student.club.name.eq(condition.getClubName()));
        }
        if (condition.getMinAge() != null) {
            builder.and(student.age.goe(condition.getMinAge()));
        }
        if (condition.getMaxAge() != null) {
            builder.and(student.age.loe(condition.getMaxAge()));
        }

        return queryFactory
                .select(new QStudentClubDTO(
                        student.id.as("studentId"),
                        student.name.as("studentName"),
                        student.age,
                        student.club.id.as("clubId"),
                        student.club.name.as("clubName")
                )).from(student)
                .where(builder)
                .leftJoin(student.club)
                .fetch();
    }

    public List<StudentClubDTO> searchStudentClubDTO(StudentSearchCondition condition) {

        return queryFactory
                .select(new QStudentClubDTO(
                        student.id.as("studentId"),
                        student.name.as("studentName"),
                        student.age,
                        student.club.id.as("clubId"),
                        student.club.name.as("clubName")
                )).from(student)
                .where(
                        studentNameEq(condition.getStudentName()),
                        clubNameEq(condition.getClubName()),
                        ageGoe(condition.getMinAge()),
                        ageLoe(condition.getMaxAge())
                )
                .leftJoin(student.club)
                .fetch();
    }

    public List<Student> searchStudent(StudentSearchCondition condition) {
        return queryFactory
                .selectFrom(student)
                .where(
                        studentNameEq(condition.getStudentName()),
                        clubNameEq(condition.getClubName()),
                        ageGoe(condition.getMinAge()),
                        ageLoe(condition.getMaxAge())
                )
                .leftJoin(student.club)
                .fetch();
    }

    private BooleanExpression ageBetween(Integer minAge, Integer maxAge) {
        return ageGoe(minAge).and(ageLoe(maxAge));
    }

    private BooleanExpression studentNameEq(String studentName) {
        return hasText(studentName) ? student.name.eq(studentName) : null;
    }

    private BooleanExpression clubNameEq(String clubName) {
        return hasText(clubName) ? student.club.name.eq(clubName) : null;
    }

    private BooleanExpression ageGoe(Integer minAge) {
        return minAge != null ? student.age.goe(minAge) : null;
    }

    private BooleanExpression ageLoe(Integer maxAge) {
        return maxAge != null ? student.age.loe(maxAge) : null;
    }
}
