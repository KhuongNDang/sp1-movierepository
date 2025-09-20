package app.daos;

import app.entities.Actor;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ActorDAO implements IDAO<Actor, Integer> {

    private final EntityManager em;

    public ActorDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Actor create(Actor actor) {
        em.persist(actor);
        return actor;
    }

    @Override
    public Actor getById(Integer id) {
        return em.find(Actor.class, id);
    }

    @Override
    public List<Actor> getAll() {
        return em.createQuery("SELECT a FROM Actor a", Actor.class).getResultList();
    }

    @Override
    public Actor update(Actor actor) {
        return em.merge(actor);
    }

    @Override
    public boolean delete(Integer id) {
        Actor actor = getById(id);
        if (actor != null) {
            em.remove(actor);
            return true;
        }
        return false;
    }
}
