package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.QStudentDTO;
import study.querydsl.dto.StudentDTO;
import study.querydsl.dto.StudentInfoDTO;
import study.querydsl.entity.Club;
import study.querydsl.entity.QClub;
import study.querydsl.entity.QStudent;
import study.querydsl.entity.Student;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.jpa.JPAExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QClub.club;
import static study.querydsl.entity.QStudent.student;

@Transactional
@SpringBootTest
class QuerydslApplicationTests {
	@Autowired
	EntityManager em;
	@PersistenceUnit
	EntityManagerFactory emf;
	JPAQueryFactory queryFactory;

	Student cMania, cLover, bMania, bLover;
	Club programming, basket;
	List<Long> studentIds = new ArrayList<>();
	int size;

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
		size = studentIds.size();
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

		Assertions.assertThatThrownBy(selectStudent::fetchOne)
				.hasCauseInstanceOf(NonUniqueResultException.class);

		Student fetchFirst = selectStudent.fetchFirst();
		assertThat(fetch.get(0)).isEqualTo(fetchFirst);

		long count = selectStudent.fetchCount();
		assertThat(count >= size).isTrue();
	}

	/**
	 * ?????? ??????
	 * 1. ?????? ?????? ????????????(desc)
	 * 2. ?????? ?????? ????????????(asc)
	 * ???, ?????? ????????? ????????? ???????????? ??????(nulls last)
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

	@Test
	public void paging() {
		List<Student> result = queryFactory
				.selectFrom(student)
				.orderBy(student.name.desc())
				.offset(1)
				.limit(2)
				.fetch();

		assertThat(result.size()).isEqualTo(2);
	}

	@Test
	public void pagingWithTotal() {
		QueryResults<Student> fetchResults = queryFactory
				.selectFrom(student)
				.offset(2).limit(2)
				.fetchResults();
		List<Student> pagedStudents = fetchResults.getResults();
		assertThat(pagedStudents.size() == 2).isTrue();
		assertThat(fetchResults.getLimit() == 2).isTrue();
		assertThat(fetchResults.getOffset() == 2).isTrue();
		assertThat(fetchResults.getTotal() >= size).isTrue();
	}

	@Test
	public void statistics() {
		Tuple tuple = queryFactory.select(
				student.count(),
						student.age.sum(),
						student.age.avg(),
						student.age.max(),
						student.age.min())
				.from(student)
				.fetchOne();
		assertThat(tuple.get(student.count()) >= size).isTrue();
		assertThat(tuple.get(student.age.avg()) > tuple.get(student.age.min())
				&& tuple.get(student.age.avg()) < tuple.get(student.age.max())).isTrue();
	}

	/**
	 * ?????? ????????? ??? ?????? ?????? ?????? ?????????
	 */
	@Test
	public void getAgeAvgGroupByClub() {
		List<Tuple> tuples = queryFactory
				.select(club.name, student.age.avg())
				.from(student)
				.join(student.club, club)
				.where(student.id.in(studentIds))
				.groupBy(club.id)
				.having(club.name.isNotEmpty())
				.orderBy(club.name.desc())
				.fetch();

		Tuple t1 = tuples.get(0), t2 = tuples.get(1);

		assertThat(t1.get(club.name)).isEqualTo(programming.getName());
		assertThat(t1.get(student.age.avg())).isEqualTo(20.5); // (20 + 21) / 2

		assertThat(t2.get(club.name)).isEqualTo(basket.getName());
		assertThat(t2.get(student.age.avg())).isEqualTo(20.5); // (20 + 21) / 2
	}

	/**
	 * programming club??? ????????? ?????? ?????? ??????
	 */
	@Test
	public void join() {
		List<Student> students = queryFactory
				.selectFrom(student)
				.join(student.club, club)
				.where(club.id.eq(programming.getId()))
				.fetch();

		assertThat(students).contains(cMania, cLover);
	}

	/**
	 * ????????? ????????? ???????????? ?????? student ??????
	 */
	@Test
	public void thetaJoin() {
		em.persist(new Student(basket.getName(), 2, 21, basket));
		em.persist(new Student(programming.getName(), 2, 21, programming));

		List<Student> students = queryFactory
				.select(student)
				.from(student, club)
				.where(student.name.eq(club.name))
				.fetch();

		assertThat(students)
				.extracting("name")
				.containsExactly(basket.getName(), programming.getName());
	}

	/**
	 * Student??? Club JOIN, Club??? programming??? Club??? ??????, Student??? ?????? ??????
	 */
	@Test
	public void joinOn() {
		List<Tuple> tuples = queryFactory
				.select(student, club)
				.from(student)
				.leftJoin(student.club, club)
				.on(club.id.eq(programming.getId()))
				.fetch();

		for (Tuple t : tuples) {
			System.out.println(t);
		}

		assertThat(tuples.size() >= size);
	}

	/**
	 * ????????? ????????? ???????????? ?????? student ??????
	 * join ... on ??????
	 */
	@Test
	public void thetaJoinOn() {
		em.persist(new Student(basket.getName(), 2, 21, basket));
		em.persist(new Student(programming.getName(), 2, 21, programming));

		List<Tuple> students = queryFactory
				.select(student, club)
				.from(student)
				.join(club)
				.on(student.name.eq(club.name))
				.where(club.id.in(programming.getId(), basket.getId()))
				.fetch();

		for (Tuple t : students) {
			System.out.println("t = " + t);
		}

		List<String> names = students.stream().map(s -> s.get(0, Student.class).getName()).collect(Collectors.toList());
		assertThat(names).containsOnly(basket.getName(), programming.getName());
	}

	/**
	 * fetch join??? ?????? ????????? ???
	 * Student??? ??????????????? club??? loading ????????????
	 */
	@Test
	public void findStudent() {
		em.flush();
		em.clear();

		Student findStudent = queryFactory.selectFrom(student)
				.where(student.id.eq(cMania.getId()))
				.fetchOne();

		boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findStudent.getClub());
		assertThat(loaded).isFalse();
	}

	/**
	 * fetch join??? ?????? ???
	 * Student??? ??????????????? club??? loading ????????????
	 */
	@Test
	public void fetchClubFromStudent() {
		em.flush();
		em.clear();

		Student findStudent = queryFactory.selectFrom(student)
				.join(student.club).fetchJoin()
				.where(student.id.eq(cMania.getId()))
				.fetchOne();

		boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findStudent.getClub());
		assertThat(loaded).isTrue();
	}

	/**
	 * ????????? ?????? ?????? ????????? ??????
	 */
	@Test
	public void maxAgeSubQUery() {
		int maxAge = getMaxAge();
		QStudent subSt = new QStudent("subSt");
		List<Student> students = queryFactory
				.selectFrom(student)
				.where(student.age.eq(
						select(subSt.age.max())
						.from(subSt)))
				.fetch();
		assertThat(students).extracting("age").containsOnly(maxAge);
	}

	private int getMaxAge() {
		return queryFactory
				.select(student.age.max())
				.from(student)
				.fetchOne();
	}

    /**
     * ????????? ????????? ??????????????? ?????? ??????
     */
	@Test
	public void selectSubQuery() {
        List<Tuple> tuples = queryFactory.select(student.name,
                        student.age.subtract(
                                select(student.age.max())
										.from(student)
                        ))
                .from(student)
                .fetch();

        for (Tuple tuple : tuples) {
            System.out.println("tuple = " + tuple);
        }
    }

	@Test
	public void basicCase() {
        List<String> result = queryFactory.select(student.grade
                        .when(1).then("?????????")
                        .when(2).then("2??????")
                        .when(3).then("3??????")
                        .when(4).then("?????????")
                        .otherwise("??????"))
                .from(student)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

	@Test
	public void complexCase() {
		List<String> result = queryFactory.select(new CaseBuilder()
						.when(student.grade.between(1, 2)).then("?????????")
						.when(student.grade.between(3, 4)).then("???")
						.otherwise(""))
				.from(student)
				.fetch();
		for (String s : result) {
			System.out.println("s = " + s);
		}
	}

	@Test
	public void constant() {
		List<Tuple> tuples = queryFactory
				.select(student.name, Expressions.constant("test"))
				.from(student)
				.fetch();

		for (Tuple t : tuples) {
			System.out.println("t = " + t);
		}
	}

	@Test
	public void concat() {
		String info = queryFactory
				.select(student.name.concat("_").concat(student.age.stringValue()))
				.from(student)
				.where(student.id.eq(cMania.getId()))
				.fetchOne();

		assertThat(info).isEqualTo(cMania.getName() + "_" + cMania.getAge());
	}

	@Test
	public void tupleProjection() {
		Tuple tuple = queryFactory.select(student.name, student.age)
				.from(student)
				.where(student.id.eq(cMania.getId()))
				.fetchOne();

		assertThat(tuple.get(student.name)).isEqualTo(cMania.getName());
		assertThat(tuple.get(student.age)).isEqualTo(cMania.getAge());
	}

	@Test
	public void findStudentDTOByJPQL() {
		StudentDTO dto = em.createQuery("select new study.querydsl.dto.StudentDTO(s.name, s.age) " +
						"from Student s where s.id = :id", StudentDTO.class)
				.setParameter("id", cMania.getId())
				.getSingleResult();

		assertThat(dto.getName()).isEqualTo(cMania.getName());
		assertThat(dto.getAge()).isEqualTo(cMania.getAge());
	}

	@Test
	public void findStudentDTOBySetter() {
		StudentDTO dto = queryFactory.select(
				Projections.bean(StudentDTO.class, student.age, student.name)
				).from(student)
				.where(student.id.eq(cMania.getId()))
				.fetchOne();

		assertThat(dto.getName()).isEqualTo(cMania.getName());
		assertThat(dto.getAge()).isEqualTo(cMania.getAge());
	}

	@Test
	public void findStudentDTOByField() {
		StudentDTO dto = queryFactory.select(
						Projections.fields(StudentDTO.class, student.age, student.name)
				).from(student)
				.where(student.id.eq(cMania.getId()))
				.fetchOne();

		assertThat(dto.getName()).isEqualTo(cMania.getName());
		assertThat(dto.getAge()).isEqualTo(cMania.getAge());
	}

	@Test
	public void findStudentDTOByConstructor() {
		StudentDTO dto = queryFactory.select(
						Projections.constructor(StudentDTO.class, student.name, student.age)
				).from(student)
				.where(student.id.eq(cMania.getId()))
				.fetchOne();

		assertThat(dto.getName()).isEqualTo(cMania.getName());
		assertThat(dto.getAge()).isEqualTo(cMania.getAge());
	}

	@Test
	public void findStudentInfoDTOByField() {
		StudentInfoDTO dto = queryFactory.select(
						Projections.fields(StudentInfoDTO.class,
								student.name.as("studentName"),
								ExpressionUtils.as(
										JPAExpressions.select(student.age.max())
												.from(student), "age")
						)
				).from(student)
				.where(student.id.eq(cMania.getId()))
				.fetchOne();

		assertThat(dto.getStudentName()).isEqualTo(cMania.getName());
		assertThat(dto.getAge() >= cMania.getAge()).isTrue();
	}

	@Test
	public void findStudentDTOByQueryProjection() {
		StudentDTO s = queryFactory
				.select(new QStudentDTO(student.name, student.age))
				.from(student)
				.where(student.id.eq(cMania.getId()))
				.fetchOne();

		assertThat(s.getName()).isEqualTo(cMania.getName());
		assertThat(s.getAge()).isEqualTo(cMania.getAge());
	}

	@Test
	public void findByNameAndAgeByBuilder() {
		String name = cMania.getName();
		Integer age = cMania.getAge();

		List<Student> students = dynamicSearchStudent(name, age);
		assertThat(students).extracting("name").containsOnly(cMania.getName());
		assertThat(students).extracting("age").containsOnly(cMania.getAge());
	}

	private List<Student> dynamicSearchStudent(String nameCond, Integer ageCond) {
		BooleanBuilder builder = new BooleanBuilder();

		if (nameCond != null) {
			builder.and(student.name.eq(nameCond));
		}

		if (ageCond != null) {
			//??????????????? ???????????????
			builder.and(student.age.eq(ageCond));
		}

		return queryFactory
				.selectFrom(student)
				.where(builder)
				.fetch();
	}

	@Test
	public void findByNameAndAgeByDynamicWhere() {
		String name = cMania.getName();
		Integer age = cMania.getAge();

		List<Student> students = dynamicSearchStudentByWhere(name, age);
		assertThat(students).extracting("name").containsOnly(cMania.getName());
		assertThat(students).extracting("age").containsOnly(cMania.getAge());
	}

	private List<Student> dynamicSearchStudentByWhere(String nameCond, Integer ageCond) {
		return queryFactory.selectFrom(student)
				.where(nameEq(nameCond), ageEq(ageCond))
				.fetch();
	}

	private BooleanExpression nameEq(String name) {
		return name == null ? null : student.name.eq(name);
	}

	private BooleanExpression ageEq(Integer age) {
		return age == null ? null : student.age.eq(age);
	}

	@Test
	public void toNextGrade() {
		long count = queryFactory.update(student)
				.set(student.age, student.age.add(1))
				.set(student.grade, student.grade.add(1))
				.where(student.grade.ne(4))
				.execute();

		em.flush();
		em.clear();

		List<Student> students = queryFactory.selectFrom(QStudent.student)
				.where(student.grade.ne(4))
				.fetch();
		for (Student s : students) {
			System.out.println("s = " + s);
		}
	}

	@Test
	public void bulkDelete() {
		long count = queryFactory.delete(student)
				.where(student.grade.eq(4))
				.execute();
	}

	@Test
	public void getUpperName() {
		List<String> names = queryFactory
//				.select(student.name.upper())
				.select(Expressions.stringTemplate("function('upper', {0})", student.name))
				.from(student)
				.fetch();
		for (String name : names) {
			System.out.println("name = " + name);
		}
	}
}
