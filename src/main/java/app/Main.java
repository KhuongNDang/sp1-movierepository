package app;

import app.daos.MovieDAO;
import app.dtos.MovieDTO;
import app.entities.Movie;
import app.services.MovieService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            // 1️⃣ Create EntityManager
            emf = Persistence.createEntityManagerFactory("default");
            em = emf.createEntityManager();

            MovieDAO movieDAO = new MovieDAO(em);
            MovieService movieService = new MovieService(em);

            // 2️⃣ Begin transaction
            em.getTransaction().begin();

            // 3️⃣ Fetch and persist first movie (Fight Club)
            MovieDTO movieDTO1 = movieService.fetchMovie(550);
            Movie movie1 = movieService.convertToEntity(movieDTO1, em); // pass EM to reuse entities
            movieDAO.save(movie1); // uses merge internally
            System.out.println("✅ Movie persisted: " + movie1.getTitle());

            // 4️⃣ Fetch and persist second movie (Pulp Fiction)
            MovieDTO movieDTO2 = movieService.fetchMovie(787);
            Movie movie2 = movieService.convertToEntity(movieDTO2, em); // reuse same EM
            movieDAO.save(movie2); // merge again
            System.out.println("✅ Movie persisted: " + movie2.getTitle());

            // 5️⃣ Commit transaction
            em.getTransaction().commit();

            // Optional: print actors & directors for both movies
            printMovieDetails(movie1);
            printMovieDetails(movie2);

        } catch (Exception e) {
            e.printStackTrace();
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
        }
    }

    private static void printMovieDetails(Movie movie) {
        System.out.println("\nMovie: " + movie.getTitle());
        System.out.println("Actors:");
        movie.getActors().forEach(a -> System.out.println(a.getName()));
        System.out.println("Directors:");
        movie.getDirectors().forEach(d -> System.out.println(d.getName()));
    }
}
