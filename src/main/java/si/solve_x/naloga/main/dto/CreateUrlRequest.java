package si.solve_x.naloga.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateUrlRequest {

    @NotBlank(message = "URL must not be blank")
    @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
    private String url;
}
