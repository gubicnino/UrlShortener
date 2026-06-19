package si.solve_x.naloga.main.controller;

import java.net.URI;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import si.solve_x.naloga.main.dto.CreateUrlRequest;
import si.solve_x.naloga.main.dto.UrlResponse;
import si.solve_x.naloga.main.service.UrlService;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/api/urls")
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@Valid @RequestBody CreateUrlRequest request) {
        UrlResponse urlResponse = urlService.create(request);
        return urlResponse.getShortCode();
    }

    @GetMapping("/api/urls/{shortCode}")
    public UrlResponse getByShortCode(@PathVariable String shortCode) {
        return urlService.getByShortCode(shortCode);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = urlService.resolveAndTrack(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
