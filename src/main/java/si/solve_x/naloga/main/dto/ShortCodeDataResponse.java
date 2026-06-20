package si.solve_x.naloga.main.dto;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ShortCodeDataResponse {
    private Instant createdAt;
    private long clickCount;
}
