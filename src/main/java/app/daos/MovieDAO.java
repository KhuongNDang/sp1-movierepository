package app.daos;

import app.entities.Movie;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.lang.Double;

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


    // Top 10 highest rated
    public List<Movie> getTop10HighestRated() {
        return em.createQuery(
                        "SELECT m FROM app.entities.Movie m ORDER BY m.voteAverage DESC", Movie.class)
                .setMaxResults(10)
                .getResultList();
    }

    // Top 10 lowest rated
    public List<Movie> getTop10LowestRated() {
        return em.createQuery(
                        "SELECT m FROM app.entities.Movie m ORDER BY m.voteAverage ASC", Movie.class)
                .setMaxResults(10)
                .getResultList();
    }

    // Most popular
    public List<Movie> getTop10MostPopular() {
        return em.createQuery(
                        "SELECT m FROM app.entities.Movie m ORDER BY m.popularity DESC", Movie.class)
                .setMaxResults(10)
                .getResultList();
    }

    // Average rating
    public Double getAverageRating() {
        return em.createQuery(
                        "SELECT AVG(m.voteAverage) FROM app.entities.Movie m", Double.class)
                .getSingleResult();
    }



    public List<Movie> searchByTitle(String searchTerm) {
        return em.createQuery(
                        "SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(:searchTerm)",
                        Movie.class
                )
                .setParameter("searchTerm", "%" + searchTerm + "%")  // wrap with % for substring search
                .getResultList();
    }


    public Movie updateMovieInDatabase(Movie movie) {
        return em.merge(movie);
    }

    public boolean deleteMovieFromDatabase(Integer id) {
        Movie movie = em.find(Movie.class, id);
        if (movie != null) {
            em.remove(movie);
            return true;
        }
        return false;
    }
}
