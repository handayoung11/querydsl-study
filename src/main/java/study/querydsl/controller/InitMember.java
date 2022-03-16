package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Club;
import study.querydsl.entity.Student;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
@Profile("local")
@RequiredArgsConstructor
public class InitMember {

    private final InitMemberService initMemberService;

    @PostConstruct
    public void init() {
        initMemberService.init();
    }

    @Component
    static class InitMemberService {
        @PersistenceContext
        private EntityManager em;

        @Transactional
        public void init() {
            Club basket = new Club("basket");
            Club programming = new Club("programming");
            em.persist(basket);
            em.persist(programming);

            for (int i = 0; i < 100; i++) {
                Club selectedClub = i % 2 == 0 ? basket : programming;
                em.persist(new Student("student" + i, i % 4 + 1, i, selectedClub));
            }
        }
    }
}
