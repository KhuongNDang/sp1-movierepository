package app.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Director {

    @Id
    private int id;

    private String name;

    // Inverse side
    @ManyToMany(mappedBy = "directors")
    private List<Movie> movies = new ArrayList<>();
}
