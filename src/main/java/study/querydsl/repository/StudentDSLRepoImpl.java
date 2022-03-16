package study.querydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import study.querydsl.dto.QStudentClubDTO;
import study.querydsl.dto.StudentClubDTO;
import study.querydsl.dto.StudentSearchCondition;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entity.QStudent.student;

@RequiredArgsConstructor
public class StudentDSLRepoImpl implements StudentDSLRepo {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<StudentClubDTO> search(StudentSearchCondition condition) {

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
