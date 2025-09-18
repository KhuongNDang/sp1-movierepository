package app.daos;

import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManager;

import java.util.List;

public class GenreDAO {
    private final EntityManager em;

    public GenreDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Genre genre) {
        em.merge(genre);
    }

    public Genre find(int id) {
        return em.find(Genre.class, id);
    }

    public Genre update(Genre genre) {
        em.getTransaction().begin();
        Genre updated = em.merge(genre);  // merge opdaterer entity
        em.getTransaction().commit();
        return updated;
    }
    public void delete(int id) {
        Genre genre = find(id);
        if (genre != null) {
            em.remove(genre);
        }
    }

    public List<Genre> findAll() {
        return em.createQuery("SELECT g FROM Genre g", Genre.class).getResultList();
    }
}
