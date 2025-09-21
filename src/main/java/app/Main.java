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

import java.util.List;

public class Main {

    public static void main(String[] args) {


        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();


        try {
            MovieDAO movieDAO = new MovieDAO(em);
            GenreDAO genreDAO = new GenreDAO(em);
            ActorDAO actorDAO = new ActorDAO(em);
            DirectorDAO directorDAO = new DirectorDAO(em);

            MovieService movieService = new MovieService(movieDAO, genreDAO, actorDAO, directorDAO);

            em.getTransaction().begin();


            // Fetch Danish movies from the last 5 years
            movieService.fetchRecentDanishMovies(5);

            System.out.println("-----Finished saving recent Danish movies-----");

            System.out.println();
            System.out.println();

            System.out.println("------Showing information about movies-----");
            movieService.printMovieById(980026);
            movieService.printMovieById(859585);

            System.out.println();
            System.out.println();

            // All movies in the "Drama" genre
            List<Movie> dramaMovies = genreDAO.getMoviesByGenreName("Drama");
            dramaMovies.forEach(System.out::println);

            System.out.println();
            System.out.println();

            // search for "king" → matches "The Lion King", "King Kong", etc.
            List<Movie> results = movieDAO.searchByTitle("king");
            results.forEach(System.out::println);


            // Average rating
            Double avgRating = movieDAO.getAverageRating();
            System.out.printf("Average rating of all movies: %.2f%n%n", avgRating);

            // Top 10 highest rated
            System.out.println("Top 10 highest rated movies:");
            printMovies(movieDAO.getTop10HighestRated());

            // Top 10 lowest rated
            System.out.println("\nTop 10 lowest rated movies:");
            printMovies(movieDAO.getTop10LowestRated());

            // Top 10 most popular
            System.out.println("\nTop 10 most popular movies:");
            printMovies(movieDAO.getTop10MostPopular());

            // Update movie
            Movie m = movieDAO.getById(676685);
            m.setTitle("Updated Title");
            movieDAO.updateMovieInDatabase(m);

            // Delete movie
            boolean deleted = movieDAO.deleteMovieFromDatabase(1232827);
            System.out.println("Deleted from DB? " + deleted);

            em.getTransaction().commit();

        } catch (Exception e) {
            throw new RuntimeException(e);
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
