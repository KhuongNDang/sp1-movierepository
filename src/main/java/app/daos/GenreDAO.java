package app.daos;

import app.entities.Genre;
import jakarta.persistence.EntityManager;
import java.util.List;

public class GenreDAO implements IDAO<Genre, Integer> {

    private final EntityManager em;

    public GenreDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Genre create(Genre genre) {
        em.persist(genre);
        return genre;
    }

    @Override
    public Genre getById(Integer id) {
        return em.find(Genre.class, id);
    }

    @Override
    public List<Genre> getAll() {
        return em.createQuery("SELECT g FROM Genre g", Genre.class).getResultList();
    }

    @Override
    public Genre update(Genre genre) {
        return em.merge(genre);
    }

    @Override
    public boolean delete(Integer id) {
        Genre genre = getById(id);
        if (genre != null) {
            em.remove(genre);
            return true;
        }
        return false;
    }
}
