package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentDTO {
    private String name;
    private int age;

    @QueryProjection
    public StudentDTO(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
