package app.daos;

import app.entities.Movie;
import jakarta.persistence.EntityManager;

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
}
