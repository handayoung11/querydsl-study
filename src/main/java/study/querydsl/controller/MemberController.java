package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.StudentClubDTO;
import study.querydsl.dto.StudentSearchCondition;
import study.querydsl.repository.StudentJpaRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final StudentJpaRepository studentJpaRepository;

    @GetMapping("/v1/members")
    public List<StudentClubDTO> searchStudentV1(StudentSearchCondition condition) {
        return studentJpaRepository.searchStudentClubDTO(condition);
    }
}
