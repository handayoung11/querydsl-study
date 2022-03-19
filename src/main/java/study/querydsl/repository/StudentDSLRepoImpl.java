package study.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
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
        return searchByCondition(condition)
                .fetch();
    }

    @Override
    public Page<StudentClubDTO> searchAndPageWithCount(StudentSearchCondition condition, Pageable pageable) {
        QueryResults<StudentClubDTO> result = searchByCondition(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    @Override
    public Page<StudentClubDTO> searchAndPageWithoutCount(StudentSearchCondition condition, Pageable pageable) {
        List<StudentClubDTO> content = searchByCondition(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(student.count())
                .from(student)
                .where(
                        studentNameEq(condition.getStudentName()),
                        clubNameEq(condition.getClubName()),
                        ageGoe(condition.getMinAge()),
                        ageLoe(condition.getMaxAge())
                );
//        return new PageImpl<>(content, pageable, total);
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private JPAQuery<StudentClubDTO> searchByCondition(StudentSearchCondition condition) {
        return queryFactory
                .select(new QStudentClubDTO(
                        student.id.as("studentId"),
                        student.name.as("studentName"),
                        student.age,
                        student.club.id.as("clubId"),
                        student.club.name.as("clubName")
                )).from(student)
                .leftJoin(student.club)
                .where(
                        studentNameEq(condition.getStudentName()),
                        clubNameEq(condition.getClubName()),
                        ageGoe(condition.getMinAge()),
                        ageLoe(condition.getMaxAge())
                );
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
