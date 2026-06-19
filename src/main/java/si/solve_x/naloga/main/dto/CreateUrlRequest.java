package si.solve_x.naloga.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.Instant;

@Data
public class CreateUrlRequest {

    @NotBlank
    private String originalUrl;
}
