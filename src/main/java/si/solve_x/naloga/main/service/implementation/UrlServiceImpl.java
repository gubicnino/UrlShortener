package si.solve_x.naloga.main.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import si.solve_x.naloga.main.dto.CreateUrlRequest;
import si.solve_x.naloga.main.dto.ShortCodeDataResponse;
import si.solve_x.naloga.main.dto.UrlResponse;
import si.solve_x.naloga.main.repository.UrlRepository;
import si.solve_x.naloga.main.service.UrlService;
import si.solve_x.naloga.main.entity.Url;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;

    @Value("${app.base-url}")
    private String baseUrl;

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
        url.setOriginalUrl(request.getUrl());
        urlRepository.save(url);
        String code = encodeBase62(url.getId());
        url.setCode(code);
        urlRepository.save(url);
        return UrlResponse.builder()
                .code(url.getCode())
                .shortUrl(baseUrl + "/" + url.getCode())
                .build();
    }

    @Override
    public ShortCodeDataResponse getShortCodeData(String code) {
        Url url = urlRepository.findByCode(code).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Short code not found"));
        return ShortCodeDataResponse.builder()
                .createdAt(url.getCreatedAt())
                .clickCount(url.getClickCount())
                .build();
    }

    @Override
    public String resolveAndTrack(String code) {
        Url url = urlRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Short code not found"));
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
        return url.getOriginalUrl();
    }
}
