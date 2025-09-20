package app;

import app.config.HibernateConfig;
import app.daos.ActorDAO;
import app.daos.DirectorDAO;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.services.MovieService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            // Initialize Hibernate
            emf = HibernateConfig.getEntityManagerFactory();
            em = emf.createEntityManager();

            // Create DAOs
            MovieDAO movieDAO = new MovieDAO(em);
            GenreDAO genreDAO = new GenreDAO(em);
            ActorDAO actorDAO = new ActorDAO(em);
            DirectorDAO directorDAO = new DirectorDAO(em);

            // Create service with DAOs
            MovieService movieService = new MovieService(movieDAO, genreDAO, actorDAO, directorDAO);

            // Start transaction
            em.getTransaction().begin();

            // Fetch Danish movies from the last 5 years
            movieService.fetchRecentDanishMovies(5);

            // Commit changes to DB
            em.getTransaction().commit();

            System.out.println("-----Finished saving recent Danish movies-----");

            System.out.println();
            System.out.println();

            System.out.println("------Showing information about movies-----");
            movieService.printMovieById(980026);
            movieService.printMovieById(859585);


        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
        }
    }
}
