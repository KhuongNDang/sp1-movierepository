package app.services;

import app.dtos.*;
import app.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class MovieService {

    private static final String API_KEY = System.getenv("TMDB_API_KEY"); // Set environment variable
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    // Convert DTO -> Entity
    public Movie convertToEntity(MovieDTO dto) {
        Movie movie = new Movie();
        movie.setId(dto.getId());
        movie.setTitle(dto.getTitle());
        movie.setOverview(dto.getOverview());
        movie.setReleaseDate(dto.getRelease_date());
        movie.setRuntime(dto.getRuntime());

        // Genres
        if (dto.getGenres() != null) {
            movie.setGenres(dto.getGenres().stream().map(g -> {
                Genre genre = new Genre();
                genre.setId(g.getId());
                genre.setName(g.getName());
                return genre;
            }).toList());
        }

        // Actors
        if (dto.getCredits() != null && dto.getCredits().getCast() != null) {
            movie.setActors(dto.getCredits().getCast().stream().map(a -> {
                Actor actor = new Actor();
                actor.setId(a.getId());
                actor.setName(a.getName());
                return actor;
            }).toList());
        }

        // Directors
        if (dto.getCredits() != null && dto.getCredits().getCrew() != null) {
            movie.setDirectors(dto.getCredits().getCrew().stream()
                    .filter(c -> c.getJob().equals("Director"))
                    .map(c -> {
                        Director director = new Director();
                        director.setId(c.getId());
                        director.setName(c.getName());
                        return director;
                    }).toList());
        }

        return movie;
    }
}
