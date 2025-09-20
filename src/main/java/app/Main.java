package app;

import app.config.HibernateConfig;
import app.daos.ActorDAO;
import app.daos.DirectorDAO;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.entities.Movie;
import app.services.MovieService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class Main {

    public static void main(String[] args) {


        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();
/*
        try {
            MovieDAO movieDAO = new MovieDAO(em);

            // Fetch all movies
            List<Movie> movies = movieDAO.getAll();

            // Print them
            movies.forEach(System.out::println);
        } finally {
            em.close();
            HibernateConfig.getEntityManagerFactory().close();
        }
*/


        try {
            MovieDAO movieDAO = new MovieDAO(em);

            // 1️⃣ Average rating
            Double avgRating = movieDAO.getAverageRating();
            System.out.printf("Average rating of all movies: %.2f%n%n", avgRating);

            // 2️⃣ Top 10 highest rated
            System.out.println("Top 10 highest rated movies:");
            printMovies(movieDAO.getTop10HighestRated());

            // 3️⃣ Top 10 lowest rated
            System.out.println("\nTop 10 lowest rated movies:");
            printMovies(movieDAO.getTop10LowestRated());

            // 4️⃣ Top 10 most popular
            System.out.println("\nTop 10 most popular movies:");
            printMovies(movieDAO.getTop10MostPopular());

        } finally {
            em.close();
            HibernateConfig.getEntityManagerFactory().close();
        }
    }



    /** Helper method to print a list of movies neatly */

    private static void printMovies(List<Movie> movies) {
        int rank = 1;
        for (Movie m : movies) {
            System.out.printf("%2d. %-40s | Rating: %.1f | Popularity: %.2f | Released: %s%n",
                    rank++, m.getTitle(), m.getVoteAverage(), m.getPopularity(), m.getReleaseDate());
        }
    }
}



//INITIATING THE DB WITH DATA FROM THE API
/*
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
*/






