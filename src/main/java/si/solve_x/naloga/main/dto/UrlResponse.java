package si.solve_x.naloga.main.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class UrlResponse {
    Long id;
    String originalUrl;
    String code;
    String shortUrl;
    Instant createdAt;
    Instant expiresAt;
    long clickCount;
}
