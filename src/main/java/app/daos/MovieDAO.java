package app.daos;

import app.entities.Movie;
import jakarta.persistence.EntityManager;
import java.util.List;

public class MovieDAO implements IDAO<Movie, Integer> {

    private final EntityManager em;

    public MovieDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Movie create(Movie movie) {
        em.persist(movie);
        return movie;
    }

    @Override
    public Movie getById(Integer id) {
        return em.find(Movie.class, id);
    }

    @Override
    public List<Movie> getAll() {
        return em.createQuery("SELECT m FROM Movie m", Movie.class).getResultList();
    }

    @Override
    public Movie update(Movie movie) {
        return em.merge(movie);
    }

    @Override
    public boolean delete(Integer id) {
        Movie movie = getById(id);
        if (movie != null) {
            em.remove(movie);
            return true;
        }
        return false;
    }

    public List<Movie> searchByTitle(String searchTerm) {
        return em.createQuery(
                        "SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(:searchTerm)",
                        Movie.class
                )
                .setParameter("searchTerm", "%" + searchTerm + "%")  // wrap with % for substring search
                .getResultList();
    }

}
