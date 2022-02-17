package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.QTestEntity;
import study.querydsl.entity.TestEntity;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class QuerydslApplicationTests {
	@Autowired
	EntityManager em;

	@Test
	void contextLoads() {
		TestEntity t = new TestEntity();
		em.persist(t);

		JPAQueryFactory jpa = new JPAQueryFactory(em);

		TestEntity result = jpa.selectFrom(QTestEntity.testEntity)
				.where(QTestEntity.testEntity.id.eq(t.getId()))
				.fetchOne();

		assertThat(result).isEqualTo(t);
		assertThat(result.getId()).isEqualTo(t.getId());
	}

}
