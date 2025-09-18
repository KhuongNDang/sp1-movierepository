package app.services;

import app.dtos.*;
import app.entities.*;
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

    private static final String API_KEY = System.getenv("API_KEY"); // Your TMDb API key
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

    // Convert DTO -> Entity with shared Actor/Director
    public Movie convertToEntity(MovieDTO dto, EntityManager em) {
        Movie movie = new Movie();
        movie.setId(dto.getId());
        movie.setTitle(dto.getTitle());
        movie.setOverview(dto.getOverview());
        movie.setReleaseDate(dto.getRelease_date());
        movie.setRuntime(dto.getRuntime());

        // --- Genres ---
        if (dto.getGenres() != null) {
            List<Genre> genres = new ArrayList<>();
            for (var g : dto.getGenres()) {
                Genre genre = em.find(Genre.class, g.getId());
                if (genre == null) {
                    genre = new Genre();
                    genre.setId(g.getId());
                    genre.setName(g.getName());
                    em.persist(genre);
                }
                genres.add(genre);
            }
            movie.setGenres(genres); // mutable list
        } else {
            movie.setGenres(new ArrayList<>());
        }

        // --- Actors ---
        if (dto.getCredits() != null && dto.getCredits().getCast() != null) {
            List<Actor> actors = new ArrayList<>();
            for (var a : dto.getCredits().getCast()) {
                Actor actor = em.find(Actor.class, a.getId());
                if (actor == null) {
                    actor = new Actor();
                    actor.setId(a.getId());
                    actor.setName(a.getName());
                    em.persist(actor);
                }
                actors.add(actor);
            }
            movie.setActors(actors); // mutable list
        } else {
            movie.setActors(new ArrayList<>());
        }

        // --- Directors ---
        if (dto.getCredits() != null && dto.getCredits().getCrew() != null) {
            List<Director> directors = new ArrayList<>();
            for (var c : dto.getCredits().getCrew()) {
                if (!"Director".equals(c.getJob())) continue;

                Director director = em.find(Director.class, c.getId());
                if (director == null) {
                    director = new Director();
                    director.setId(c.getId());
                    director.setName(c.getName());
                    em.persist(director);
                }
                directors.add(director);
            }
            movie.setDirectors(directors); // mutable list
        } else {
            movie.setDirectors(new ArrayList<>());
        }

        return movie;
    }




    // Reuse existing Actor or create new
    private Actor getOrCreateActor(int id, String name) {
        Actor actor = em.find(Actor.class, id);
        if (actor == null) {
            actor = new Actor();
            actor.setId(id);
            actor.setName(name);
            em.persist(actor); // persist immediately so Hibernate knows about it
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

}
