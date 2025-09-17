package app.dtos;

import app.dtos.ActorDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditsDTO {
    private List<ActorDTO> cast; // actors
    private List<CrewDTO> crew;  // directors (filter by job="Director")
}
