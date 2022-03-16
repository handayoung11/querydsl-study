package study.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.querydsl.entity.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long>, StudentDSLRepo {
    List<Student> findByName(String name);
}
