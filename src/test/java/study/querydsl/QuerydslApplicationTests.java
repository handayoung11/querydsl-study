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

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class QuerydslApplicationTests {
	@Autowired
	EntityManager em;
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
	void contextLoads() {
		Club club = new Club("programming");
		em.persist(club);

		JPAQueryFactory jpa = new JPAQueryFactory(em);

		Club result = jpa.selectFrom(QClub.club)
				.where(QClub.club.id.eq(club.getId()))
				.fetchOne();

		assertThat(result).isEqualTo(club);
		assertThat(result.getId()).isEqualTo(club.getId());
	}

}
