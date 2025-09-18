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

    @Column(columnDefinition = "TEXT")
    private String overview;

    private String releaseDate;

    private int runtime;




    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
    name = "movie_genre",
    joinColumns = @JoinColumn(name = "movie_id"),
    inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;


    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private List<Actor> actors;



    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "movie_director",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    private List<Director> directors;

}
