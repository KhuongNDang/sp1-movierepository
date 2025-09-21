package app.daos;

import app.entities.Genre;
import app.entities.Movie;
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

    // Get all movies in a genre by name
    public List<Movie> getMoviesByGenreName(String genreName) {
        return em.createQuery(
                        "SELECT m FROM Movie m JOIN m.genres g WHERE g.name = :genreName",
                        Movie.class
                )
                .setParameter("genreName", genreName)
                .getResultList();
    }

    public Genre findByName(String name) {
        List<Genre> genres = em.createQuery(
                        "SELECT g FROM Genre g WHERE g.name = :name", Genre.class)
                .setParameter("name", name)
                .getResultList();
        return genres.isEmpty() ? null : genres.get(0);
    }

}
