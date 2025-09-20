package app.daos;

import app.entities.Director;
import jakarta.persistence.EntityManager;
import java.util.List;

public class DirectorDAO implements IDAO<Director, Integer> {

    private final EntityManager em;

    public DirectorDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Director create(Director director) {
        em.persist(director);
        return director;
    }

    @Override
    public Director getById(Integer id) {
        return em.find(Director.class, id);
    }

    @Override
    public List<Director> getAll() {
        return em.createQuery("SELECT d FROM Director d", Director.class).getResultList();
    }

    @Override
    public Director update(Director director) {
        return em.merge(director);
    }

    @Override
    public boolean delete(Integer id) {
        Director director = getById(id);
        if (director != null) {
            em.remove(director);
            return true;
        }
        return false;
    }
}
