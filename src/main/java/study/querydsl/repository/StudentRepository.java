package study.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import study.querydsl.entity.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long>, StudentDSLRepo, QuerydslPredicateExecutor<Student> {
    List<Student> findByName(String name);
}
