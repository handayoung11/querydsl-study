package study.querydsl.dto;

import lombok.Data;

@Data
public class StudentSearchCondition {

    private String studentName, clubName;
    private Integer minAge, maxAge;
}
