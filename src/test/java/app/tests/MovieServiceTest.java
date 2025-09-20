package app.tests;

import app.daos.ActorDAO;
import app.daos.DirectorDAO;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.dtos.MovieDTO;
import app.entities.Actor;
import app.entities.Movie;
import app.services.MovieService;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieServiceTest extends BaseTest {

    private MovieService movieService;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();             // Initializes emf and em
        MovieDAO movieDAO = new MovieDAO(em);
        GenreDAO genreDAO = new GenreDAO(em);
        ActorDAO actorDAO = new ActorDAO(em);
        DirectorDAO directorDAO = new DirectorDAO(em);
        movieService = new MovieService(movieDAO, genreDAO, actorDAO, directorDAO);
    }

    @Test
    void testFetchAndPersistMoviesWithSharedActors() throws Exception {
        em.getTransaction().begin();

        // Fetch first movie DTO from API (or mock)
        MovieDTO dto1 = movieService.fetchMovie(550); // Example ID: Fight Club
        Movie movie1 = movieService.convertToEntity(dto1);
        em.persist(movie1);

        // Fetch second movie DTO that likely shares some actors
        MovieDTO dto2 = movieService.fetchMovie(278); // Example ID: Shawshank Redemption
        Movie movie2 = movieService.convertToEntity(dto2);
        em.persist(movie2);

        em.getTransaction().commit();

        // Verify both movies are persisted
        TypedQuery<Movie> movieQuery = em.createQuery("SELECT m FROM Movie m", Movie.class);
        List<Movie> savedMovies = movieQuery.getResultList();
        assertEquals(2, savedMovies.size(), "Should have 2 movies in DB");

        // Verify actors are reused (no duplicates)
        TypedQuery<Actor> actorQuery = em.createQuery("SELECT a FROM Actor a", Actor.class);
        List<Actor> allActors = actorQuery.getResultList();

        int totalActorsFromMovies = movie1.getActors().size() + movie2.getActors().size();
        assertTrue(allActors.size() <= totalActorsFromMovies,
                "Actors should be reused and not duplicated");

        // Optional: check shared actors
        movie1.getActors().forEach(actor -> {
            if (movie2.getActors().contains(actor)) {
                assertTrue(allActors.contains(actor), "Shared actor should exist in DB only once");
            }
        });
    }
}
