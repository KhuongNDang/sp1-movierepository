package app.entities;

import app.entities.Movie;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Actor {

    @Id
    private int id;

    private String name;

    @ManyToMany(mappedBy = "actors")
    private List<Movie> movies = new ArrayList<>();

    // getters and setters
    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    // other getters/setters
}
