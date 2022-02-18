package study.querydsl.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data @Entity
@Setter(AccessLevel.NONE)
public class TestEntity {
    @Id @GeneratedValue
    private Long id;

}
