package app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class MovieDTO {

    private int id;
    private String title;
    @JsonProperty("original_title")
    private String originalTitle;

    private String overview;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

    private double popularity;

    @JsonProperty("vote_average")
    private double voteAverage;

    @JsonProperty("vote_count")
    private int voteCount;

    @JsonProperty("spoken_language")
    private String spokenLanguage;

    @JsonProperty("original_language")
    private String originalLanguage;

    private List<GenreDTO> genres;

    private List<ActorDTO> actors;

    public String getReleaseYear() {
        if (releaseDate != null) {
            return String.valueOf(releaseDate.getYear());
        }
        return "Release Year unknown";
    }
}
