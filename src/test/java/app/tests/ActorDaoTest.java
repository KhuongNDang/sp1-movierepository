package app.tests;

import app.entities.Actor;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActorDaoTest extends BaseTest {

    @Test
    void testSaveAndFindActor() {
        Actor actor = new Actor();
        actor.setId(1);
        actor.setName("Brad Pitt");

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(actor);
        tx.commit();

        // Fetch actor from DB
        Actor found = em.find(Actor.class, 1);
        assertNotNull(found);
        assertEquals("Brad Pitt", found.getName());
    }
}
