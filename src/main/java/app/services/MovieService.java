package app.services;

import app.dtos.MovieDTO;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MovieService {

    private static final String API_KEY = System.getenv("api_key"); // Your TMDb API key
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EntityManager em;

    public MovieService(EntityManager em) {
        this.em = em;
    }

    // Fetch a movie from TMDb
    public MovieDTO fetchMovie(int movieId) throws Exception {
        String urlString = BASE_URL + movieId + "?api_key=" + API_KEY + "&append_to_response=credits";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String json = reader.lines().collect(Collectors.joining());
        reader.close();

        return objectMapper.readValue(json, MovieDTO.class);
    }

    // Convert DTO -> Entity with shared Actor/Director/Genre
    public Movie convertToEntity(MovieDTO dto, EntityManager em) {
        Movie movie = new Movie();
        movie.setId(dto.getId());
        movie.setTitle(dto.getTitle());
        movie.setOverview(dto.getOverview());
        movie.setReleaseDate(dto.getRelease_date());
        movie.setRuntime(dto.getRuntime());



    // Persist the movie DTO to database
    public void saveMovieFromDto(MovieDTO dto) {
        em.getTransaction().begin();

        Movie movie = convertToEntity(dto, em);
        em.merge(movie); // merge inserts new or updates existing
        em.getTransaction().commit();
    }



    // Reuse existing Actor or create new
    private Actor getOrCreateActor(int id, String name) {
        Actor actor = em.find(Actor.class, id);
        if (actor == null) {
            actor = new Actor();
            actor.setId(id);
            actor.setName(name);
            em.persist(actor); // persist immediately
        }
        return actor;
    }

    // Reuse existing Director or create new
    private Director getOrCreateDirector(int id, String name) {
        Director director = em.find(Director.class, id);
        if (director == null) {
            director = new Director();
            director.setId(id);
            director.setName(name);
            em.persist(director);
        }
        return director;
    }

    public void deleteMovie(int movieId) {
        Movie movie = em.find(Movie.class, movieId);
        if (movie == null) return;

        // Remove associations in join tables
        movie.getActors().clear();
        movie.getDirectors().clear();

        // Then delete the movie
        em.remove(movie);
    }

    // Reuse existing Genre or create new
    private Genre getOrCreateGenre(int id, String name) {
        Genre genre = em.find(Genre.class, id);
        if (genre == null) {
            genre = new Genre();
            genre.setId(id);
            genre.setName(name);
            em.persist(genre);
        }
        return genre;
    }
}
