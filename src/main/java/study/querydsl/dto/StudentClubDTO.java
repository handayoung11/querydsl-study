package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class StudentClubDTO {
    private Long studentId;
    private String studentName;
    private int age;
    private Long clubId;
    private String clubName;

    @QueryProjection
    public StudentClubDTO(Long studentId, String studentName, int age, Long clubId, String clubName) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.age = age;
        this.clubId = clubId;
        this.clubName = clubName;
    }
}
