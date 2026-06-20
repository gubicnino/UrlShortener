package si.solve_x.naloga.main.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UrlResponse {
    String code;
    String shortUrl;
}
