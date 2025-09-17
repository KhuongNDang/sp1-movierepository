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
            MovieService movieService = new MovieService();

            // 2️⃣ Fetch a movie from TMDb (example: Fight Club = 550)
            MovieDTO movieDTO = movieService.fetchMovie(550);

            // 3️⃣ Convert DTO -> Entity
            Movie movieEntity = movieService.convertToEntity(movieDTO);

            // 4️⃣ Persist movie into PostgreSQL
            movieDAO.save(movieEntity);

            System.out.println("✅ Movie persisted: " + movieEntity.getTitle());

            // Optional: print actors & directors
            System.out.println("Actors:");
            movieEntity.getActors().forEach(a -> System.out.println(a.getName() + " as " + a.getCharacter()));

            System.out.println("Directors:");
            movieEntity.getDirectors().forEach(d -> System.out.println(d.getName()));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
        }
    }
}
