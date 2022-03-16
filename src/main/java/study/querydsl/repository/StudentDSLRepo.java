package study.querydsl.repository;

import study.querydsl.dto.StudentClubDTO;
import study.querydsl.dto.StudentSearchCondition;

import java.util.List;

public interface StudentDSLRepo {
    List<StudentClubDTO> search(StudentSearchCondition condition);
}
