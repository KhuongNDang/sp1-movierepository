package app;

import app.config.HibernateConfig;
import app.daos.MovieDAO;
import app.entities.Actor;
import app.entities.Movie;
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

            int[] movieIds = {550, 718930}; // TMDb IDs

            em.getTransaction().begin();

            // Save movies
            for (int tmdbId : movieIds) {
                MovieDTO dto = movieService.fetchMovie(tmdbId);
                Movie movie = movieService.convertToEntity(dto, em);

                if (em.find(Movie.class, movie.getId()) == null) {
                    em.persist(movie);
                    System.out.println("Saved movie: " + movie.getTitle());
                }
            }

            // Update title
            movieDAO.updateTitle(550, "KING KHUONG");

            // Delete movie
            movieService.deleteMovie(718930);
            System.out.println("Deleted movie 718930.");

            em.getTransaction().commit();

            // Verify actor
            em.clear();
            Actor brad = em.find(Actor.class, 287);
            if (brad != null) {
                System.out.println("Brad Pitt is still in movies:");
                brad.getMovies().forEach(m -> System.out.println("- " + m.getTitle()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
            emf.close();
        }
    }
}
