package app;

import app.config.HibernateConfig;
import app.entities.Movie;
import app.entities.Actor;
import app.entities.Director;
import app.services.MovieService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        try {
            MovieService movieService = new MovieService(em);

            // Example TMDb movie IDs (Fight Club and Ocean's Eleven)
            int[] movieIds = {550, 787};

            // --- Persist movies ---
            em.getTransaction().begin();

            Movie movie1 = movieService.convertToEntity(movieService.fetchMovie(movieId1), em);
            Movie movie2 = movieService.convertToEntity(movieService.fetchMovie(movieId2), em);

            em.persist(movie1);
            em.persist(movie2);

            em.getTransaction().commit();

            System.out.println("Movies saved successfully.");

            // --- Verify actors/directors linked ---
            Actor brad = em.find(Actor.class, 287); // Brad Pitt
            if (brad != null) {
                System.out.println("Brad Pitt is in movies:");
                brad.getMovies().forEach(m -> System.out.println("- " + m.getTitle()));
            }

            // --- Delete one movie ---
            em.getTransaction().begin();
            System.out.println("\nDeleting movie: " + movie1.getTitle());
            movieService.deleteMovie(movie1.getId());
            em.getTransaction().commit();

            System.out.println("Movie deleted successfully.");

            // --- Check remaining actors/directors ---
            brad = em.find(Actor.class, 287);
            if (brad != null) {
                System.out.println("Brad Pitt is still in movies:");
                brad.getMovies().forEach(m -> System.out.println("- " + m.getTitle()));
            } else {
                System.out.println("Brad Pitt no longer exists in DB.");
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
