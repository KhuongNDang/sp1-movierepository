package app.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class Movie {
    @Id
    private int id;
    private String title;
    private String overview;
    private String releaseDate;
    private int runtime;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Actor> actors;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Director> directors;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Genre> genres;
}
