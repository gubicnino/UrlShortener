package si.solve_x.naloga.main.service.implementation;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import si.solve_x.naloga.main.dto.CreateUrlRequest;
import si.solve_x.naloga.main.dto.UrlResponse;
import si.solve_x.naloga.main.repository.UrlRepository;
import si.solve_x.naloga.main.service.UrlService;
import si.solve_x.naloga.main.vao.Url;

@Service
@AllArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;

    private static String encodeBase62(long number) {
        final String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        while(number > 0) {
            sb.append(characters.charAt((int) (number % characters.length())));
            number /= characters.length();
        }
        return sb.reverse().toString();
    }
    @Override
    public UrlResponse create(CreateUrlRequest request) {
        Url url = new Url();
        url.setOriginalUrl(request.getOriginalUrl());
        urlRepository.save(url);
        String shortCode = encodeBase62(url.getId());
        url.setShortCode(shortCode);
        urlRepository.save(url);
        return UrlResponse.builder()
                .id(url.getId())
                .originalUrl(url.getOriginalUrl())
                .shortCode(url.getShortCode())
                .shortUrl("http://localhost:8080/" + url.getShortCode())
                .createdAt(url.getCreatedAt())
                .clickCount(url.getClickCount())
                .build();
    }

    @Override
    public UrlResponse getByShortCode(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode).orElse(null);
        return UrlResponse.builder()
                .id(url.getId())
                .originalUrl(url.getOriginalUrl())
                .shortCode(url.getShortCode())
                .shortUrl("http://localhost:8080/" + url.getShortCode())
                .createdAt(url.getCreatedAt())
                .clickCount(url.getClickCount())
                .build();
    }

    @Override
    public String resolveAndTrack(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Short code not found"));
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
        return url.getOriginalUrl();
    }
}
