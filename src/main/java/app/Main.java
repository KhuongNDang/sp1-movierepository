package app;

import app.config.HibernateConfig;
import app.daos.MovieDAO;
import app.entities.Movie;
import app.entities.Actor;
import app.services.MovieService;
import app.dtos.MovieDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        try {
            MovieService movieService = new MovieService(em);
            MovieDAO movieDAO = new MovieDAO(em);

            // Example TMDb movie IDs (Fight Club and Ocean's Eleven)
            int[] movieIds = {550, 11};

            em.getTransaction().begin();

            for (int tmdbId : movieIds) {
                System.out.println("Fetching movie with TMDb ID: " + tmdbId);
                MovieDTO dto = movieService.fetchMovie(tmdbId);

                // Convert DTO -> Entity
                Movie movie = movieService.convertToEntity(dto, em);

                // Persist movie
                Movie existingMovie = em.find(Movie.class, movie.getId());
                if (existingMovie == null) {
                    em.persist(movie);
                    System.out.println("Saved movie: " + movie.getTitle());
                } else {
                    System.out.println("Movie already exists: " + existingMovie.getTitle());
                }
            }

            em.getTransaction().commit();

            // Verify actor (e.g., Brad Pitt, ID 287) is in multiple movies
            Actor brad = em.find(Actor.class, 287);
            if (brad != null) {
                System.out.println("Brad Pitt is in movies:");
                brad.getMovies().forEach(m -> System.out.println("- " + m.getTitle()));
            } else {
                System.out.println("Brad Pitt not found in DB");
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
            emf.close();
        }
    }
}
