package app.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Director {

    @Id
    private int id;

    private String name;

    @ManyToMany(mappedBy = "directors")
    private List<Movie> movies = new ArrayList<>();
}
