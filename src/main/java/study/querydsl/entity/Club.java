package study.querydsl.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity @Data
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Club {

    @Id @GeneratedValue
    private Long id;
    private String name;

    @ToString.Exclude
    @OneToMany(mappedBy = "club")
    private List<Student> students = new ArrayList<>();

    public Club(String name) {
        this.name = name;
    }
}
