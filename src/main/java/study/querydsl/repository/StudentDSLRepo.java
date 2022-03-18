package study.querydsl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.querydsl.dto.StudentClubDTO;
import study.querydsl.dto.StudentSearchCondition;

import java.util.List;

public interface StudentDSLRepo {
    List<StudentClubDTO> search(StudentSearchCondition condition);
    Page<StudentClubDTO> searchAndPageWithCount(StudentSearchCondition condition, Pageable pageable);
    Page<StudentClubDTO> searchAndPageWithoutCount(StudentSearchCondition condition, Pageable pageable);
}
