package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTO {
    private int id;
    private String title;
    private String overview;
    private String release_date;
    private int runtime;
    private List<GenreDTO> genres;
    private CreditsDTO credits;
}
