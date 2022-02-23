package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Club;
import study.querydsl.entity.QClub;
import study.querydsl.entity.Student;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QStudent.student;

@Transactional
@SpringBootTest
class QuerydslApplicationTests {
	@Autowired
	EntityManager em;
	JPAQueryFactory queryFactory;
	Student cMania, cLover, bMania, bLover;
	Club programming, basket;
	List<Long> studentIds = new ArrayList<>();

	@BeforeEach
	public void before() {
		queryFactory =  new JPAQueryFactory(em);
		programming = new Club("programming");
		basket = new Club("basket");
		em.persist(programming);
		em.persist(basket);

		cMania = new Student("code-mania", 1, 20, programming);
		cLover = new Student("code-lover", 2, 21, programming);
		bMania = new Student("basket-mania", 1, 20, basket);
		bLover = new Student("basket-lover", 2, 21, basket);
		em.persist(cMania);
		em.persist(cLover);
		em.persist(bMania);
		em.persist(bLover);

		studentIds.add(cMania.getId());
		studentIds.add(cLover.getId());
		studentIds.add(bMania.getId());
		studentIds.add(bLover.getId());
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

	@Test
	public void fetchTest() {
		JPAQuery<Student> selectStudent = queryFactory
				.selectFrom(student);

		List<Student> fetch = selectStudent.fetch();
		assertThat(fetch.size() == 4).isTrue();

		Assertions.assertThatThrownBy(() -> selectStudent.fetchOne())
				.hasCauseInstanceOf(NonUniqueResultException.class);

		Student fetchFirst = selectStudent.fetchFirst();
		assertThat(fetch.get(0)).isEqualTo(fetchFirst);

		QueryResults<Student> fetchResults = selectStudent.offset(2).limit(2).fetchResults();
		List<Student> pagedStudents = fetchResults.getResults();
		assertThat(pagedStudents.size() == 2).isTrue();
		assertThat(fetchResults.getLimit() == 2).isTrue();
		assertThat(fetchResults.getOffset() == 2).isTrue();
		assertThat(fetchResults.getTotal() >= 4).isTrue();

		long count = selectStudent.fetchCount();
		assertThat(count >= 4).isTrue();
	}

	/**
	 * 회원 정렬
	 * 1. 회원 나이 내림차순(desc)
	 * 2. 회원 이름 올림차순(asc)
	 * 단, 회원 이름이 없으면 마지막에 출력(nulls last)
	 */
	@Test
	public void sort() {
		Student s1 = new Student(null, 4, 26, null);
		Student s2 = new Student("student2", 4, 26, null);
		Student s3 = new Student("student3", 4, 26, null);
		em.persist(s1);
		em.persist(s2);
		em.persist(s3);

		System.out.println("id: " + s1.getId());
		System.out.println("id: " + s2.getId());
		System.out.println("id: " + s3.getId());
		List<Student> students = queryFactory.selectFrom(student)
				.where(student.id.in(s1.getId(), s2.getId(), s3.getId()))
				.orderBy(student.age.desc(), student.name.asc().nullsLast())
				.fetch();

		Student first = students.get(0);
		Student second = students.get(1);
		Student third = students.get(2);

		assertThat(first.getName()).isEqualTo("student2");
		assertThat(second.getName()).isEqualTo("student3");
		assertThat(third.getName()).isEqualTo(null);
	}
}
