package app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Director {
    @Id
    private int id;
    private String name;
}
