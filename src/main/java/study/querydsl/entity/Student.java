package study.querydsl.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity @Data
@Setter(AccessLevel.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Student {

    @Id @GeneratedValue
    private Long id;
    private String name;
    private int grade;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    Club club;

    public Student(String name, int grade, int age, Club club) {
        this.name = name;
        this.grade = grade;
        if(club != null) {
            joinClub(club);
        }
    }

    public void joinClub(Club club) {
        this.club = club;
        club.getStudents().add(this);
    }
}
