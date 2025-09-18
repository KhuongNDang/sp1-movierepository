package app.daos;

import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManager;

import java.util.List;

public class MovieDAO {
    private final EntityManager em;

    public MovieDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Movie movie) {
        em.merge(movie);
    }

    public Movie find(int id) {
        return em.find(Movie.class, id);
    }

    public void delete(int id) {
        Movie movie = find(id);
        if (movie != null) {
            em.remove(movie);
        }
    }

    public List<Genre> findAll() {
        return em.createQuery("SELECT m FROM Movie m", Genre.class).getResultList();
    }

    public Movie findByTitle(String title) {
        return em.createQuery("SELECT m FROM Movie m WHERE m.title = :title", Movie.class)
                .setParameter("title", title)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

}
