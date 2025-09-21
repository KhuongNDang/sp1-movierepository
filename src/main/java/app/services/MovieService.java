package app.services;

import app.dtos.MovieDTO;
import app.daos.*;
import app.entities.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MovieService {

    private static final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MovieDAO movieDAO;
    private final GenreDAO genreDAO;
    private final ActorDAO actorDAO;
    private final DirectorDAO directorDAO;

    public MovieService(MovieDAO movieDAO, GenreDAO genreDAO, ActorDAO actorDAO, DirectorDAO directorDAO) {
        this.movieDAO = movieDAO;
        this.genreDAO = genreDAO;
        this.actorDAO = actorDAO;
        this.directorDAO = directorDAO;
    }

    /** Fetch full movie details from TMDb */
    public MovieDTO fetchMovie(int movieId) throws Exception {
        URL url = new URL(BASE_URL + movieId + "?api_key=" + API_KEY + "&append_to_response=credits");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String json = reader.lines().collect(Collectors.joining());
        reader.close();

        return objectMapper.readValue(json, MovieDTO.class);
    }

    /** Fetch recent Danish movies from the last N years */
    public void fetchRecentDanishMovies(int yearsBack) throws Exception {
        int currentYear = LocalDate.now().getYear();
        int startYear = currentYear - yearsBack + 1;

        int page = 1;
        int totalPages = 1;

        do {
            String urlString = "https://api.themoviedb.org/3/discover/movie" +
                    "?api_key=" + API_KEY +
                    "&with_origin_country=DK" +
                    "&primary_release_date.gte=" + startYear + "-01-01" +
                    "&page=" + page;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = reader.lines().collect(Collectors.joining());
            reader.close();

            JsonNode root = objectMapper.readTree(json);
            totalPages = root.get("total_pages").asInt();
            JsonNode results = root.get("results");

            if (results != null && results.isArray()) {
                for (JsonNode node : results) {
                    int movieId = node.get("id").asInt();

                    // Skip duplicates
                    if (movieDAO.getById(movieId) != null) continue;

                    // Fetch full details
                    MovieDTO dto = fetchMovie(movieId);
                    Movie movie = convertToEntity(dto);

                    if (movie == null) {
                        System.out.println("Skipped incomplete or non-Danish movie ID: " + movieId);
                        continue;
                    }

                    movieDAO.create(movie);
                    System.out.println("Saved: " + movie.getTitle());
                }
            }

            page++;
        } while (page <= totalPages);
    }

    public void printMovieById(int id) {
        Movie movie = movieDAO.getById(id);
        if (movie == null) {
            System.out.println("Movie not found with ID " + id);
            return;
        }
        System.out.println(movie);
    }


    /** Convert TMDb DTO -> Movie entity, skip invalid or non-Danish movies */
    public Movie convertToEntity(MovieDTO dto) {

        Movie movie = new Movie();
        movie.setId(dto.getId());
        movie.setTitle(dto.getTitle());
        movie.setOverview(dto.getOverview());
        movie.setReleaseDate(dto.getRelease_date());
        movie.setRuntime(dto.getRuntime() != null ? dto.getRuntime() : 0);
        movie.setVoteAverage(dto.getVote_average());
        movie.setPopularity(dto.getPopularity());

        // Genres
        if (dto.getGenres() != null) {
            movie.setGenres(dto.getGenres().stream()
                    .map(g -> getOrCreateGenre(g.getId(), g.getName()))
                    .collect(Collectors.toList()));
        }

        // Actors
        if (dto.getCredits() != null && dto.getCredits().getCast() != null) {
            movie.setActors(dto.getCredits().getCast().stream()
                    .map(a -> getOrCreateActor(a.getId(), a.getName()))
                    .collect(Collectors.toList()));
        }

        // Directors
        if (dto.getCredits() != null && dto.getCredits().getCrew() != null) {
            movie.setDirectors(dto.getCredits().getCrew().stream()
                    .filter(c -> "Director".equals(c.getJob()))
                    .map(c -> getOrCreateDirector(c.getId(), c.getName()))
                    .collect(Collectors.toList()));
        }

        return movie;
    }



    private Genre getOrCreateGenre(int id, String name) {
        Genre genre = genreDAO.getById(id);
        if (genre == null) {
            genre = new Genre();
            genre.setId(id);
            genre.setName(name);
            genreDAO.create(genre);
        }
        return genre;
    }

    private Actor getOrCreateActor(int id, String name) {
        Actor actor = actorDAO.getById(id);
        if (actor == null) {
            actor = new Actor();
            actor.setId(id);
            actor.setName(name);
            actorDAO.create(actor);
        }
        return actor;
    }

    private Director getOrCreateDirector(int id, String name) {
        Director director = directorDAO.getById(id);
        if (director == null) {
            director = new Director();
            director.setId(id);
            director.setName(name);
            directorDAO.create(director);
        }
        return director;
    }

    /** Delete movie and remove all associations */
    public void deleteMovie(int movieId) {
        Movie movie = movieDAO.getById(movieId);
        if (movie == null) return;

        movie.getActors().forEach(a -> a.getMovies().remove(movie));
        movie.getDirectors().forEach(d -> d.getMovies().remove(movie));
        movie.getActors().clear();
        movie.getDirectors().clear();

        movieDAO.delete(movieId);
    }
}
