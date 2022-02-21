package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Club;
import study.querydsl.entity.QClub;
import study.querydsl.entity.Student;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QStudent.student;

@Transactional
@SpringBootTest
class QuerydslApplicationTests {
	@Autowired
	EntityManager em;
	JPAQueryFactory queryFactory;

	@BeforeEach
	public void before() {
		queryFactory =  new JPAQueryFactory(em);
		Club programming = new Club("programming"),
				basket = new Club("basket");
		em.persist(programming);
		em.persist(basket);

		Student cMania = new Student("code-mania", 1, 20, programming),
				cLover = new Student("code-lover", 2, 21, programming),
				bMania = new Student("basket-mania", 1, 20, programming),
				bLover = new Student("basket-lover", 2, 21, programming);
		em.persist(cMania);
		em.persist(cLover);
		em.persist(bMania);
		em.persist(bLover);
	}

	@Test
	public void selectClub() {
		Club club = new Club("programming");
		em.persist(club);

		JPAQueryFactory jpa = new JPAQueryFactory(em);

		Club result = jpa.selectFrom(QClub.club)
				.where(QClub.club.id.eq(club.getId()))
				.fetchOne();

		assertThat(result).isEqualTo(club);
		assertThat(result.getId()).isEqualTo(club.getId());
	}

	@Test
	public void JPQLTest() {
		List<Student> codeMania = em.createQuery("select s from Student s where s.name = :name", Student.class)
				.setParameter("name", "code-mania")
				.getResultList();

		assertThat(codeMania.size() > 0).isTrue();
	}

	@Test
	public void dslTest() {
		List<Student> codeMania = queryFactory.select(student)
				.from(student)
				.where(student.name.eq("code-mania"))
				.fetch();

		assertThat(codeMania.size() > 0).isTrue();
	}

	@Test
	void search() {
		List<Student> students = queryFactory.selectFrom(student)
				.where(student.grade.eq(2)
						.and(student.name.eq("code-lover")))
				.fetch();
		assertThat(students.get(0).getName()).isEqualTo("code-lover");
	}

	@Test
	void searchAndParam() {
		List<Student> students = queryFactory.selectFrom(student)
				.where(student.grade.eq(2),
						student.name.eq("code-lover"))
				.fetch();
		assertThat(students.get(0).getName()).isEqualTo("code-lover");
	}
}
