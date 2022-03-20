package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.StudentClubDTO;
import study.querydsl.dto.StudentSearchCondition;
import study.querydsl.repository.StudentJpaRepository;
import study.querydsl.repository.StudentRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final StudentJpaRepository studentJpaRepository;
    private final StudentRepository studentRepository;

    @GetMapping("/v1/members")
    public List<StudentClubDTO> searchStudentV1(StudentSearchCondition condition) {
        return studentJpaRepository.searchStudentClubDTO(condition);
    }

    @GetMapping("/v2/members")
    public Page<StudentClubDTO> searchStudentV2(StudentSearchCondition condition, Pageable pageable) {
        return studentRepository.searchAndPageWithCount(condition, pageable);
    }

    @GetMapping("/v3/members")
    public Page<StudentClubDTO> searchStudentV3(StudentSearchCondition condition, Pageable pageable) {
        return studentRepository.searchAndPageWithoutCount(condition, pageable);
    }
}
