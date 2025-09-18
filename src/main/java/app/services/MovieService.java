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
import java.util.stream.Collectors;

public class MovieService {

    private static final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EntityManager em;

    public MovieService(EntityManager em) {
        this.em = em;
    }

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

    public Movie convertToEntity(MovieDTO dto, EntityManager em) {
        Movie movie = new Movie();
        movie.setId(dto.getId());
        movie.setTitle(dto.getTitle());
        movie.setOverview(dto.getOverview());
        movie.setReleaseDate(dto.getRelease_date());
        movie.setRuntime(dto.getRuntime());

        // Genres
        if (dto.getGenres() != null) {
            movie.setGenres(dto.getGenres().stream()
                    .map(g -> {
                        Genre genre = em.find(Genre.class, g.getId());
                        if (genre == null) {
                            genre = new Genre();
                            genre.setId(g.getId());
                            genre.setName(g.getName());
                        }
                        return genre;
                    })
                    .collect(Collectors.toCollection(ArrayList::new)));
        }

        // Actors
        if (dto.getCredits() != null && dto.getCredits().getCast() != null) {
            movie.setActors(dto.getCredits().getCast().stream()
                    .map(a -> getOrCreateActor(a.getId(), a.getName()))
                    .collect(Collectors.toCollection(ArrayList::new)));
        }

        // Directors
        if (dto.getCredits() != null && dto.getCredits().getCrew() != null) {
            movie.setDirectors(dto.getCredits().getCrew().stream()
                    .filter(c -> "Director".equals(c.getJob()))
                    .map(c -> getOrCreateDirector(c.getId(), c.getName()))
                    .collect(Collectors.toCollection(ArrayList::new)));
        }

        return movie;
    }

    private Actor getOrCreateActor(int id, String name) {
        Actor actor = em.find(Actor.class, id);
        if (actor == null) {
            actor = new Actor();
            actor.setId(id);
            actor.setName(name);
            em.persist(actor);
        }
        return actor;
    }

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

        // Remove associations
        movie.getActors().forEach(a -> a.getMovies().remove(movie));
        movie.getDirectors().forEach(d -> d.getMovies().remove(movie));
        movie.getActors().clear();
        movie.getDirectors().clear();

        em.remove(movie);
    }
}
