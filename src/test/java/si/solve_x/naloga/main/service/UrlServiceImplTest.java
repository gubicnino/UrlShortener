package si.solve_x.naloga.main.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import si.solve_x.naloga.main.dto.CreateUrlRequest;
import si.solve_x.naloga.main.dto.UrlResponse;
import si.solve_x.naloga.main.repository.UrlRepository;
import si.solve_x.naloga.main.service.implementation.UrlServiceImpl;
import si.solve_x.naloga.main.vao.Url;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlServiceImpl urlService;

    // Happy path: valid URL gets a generated code and a shortUrl
    @Test
    void create_validRequest_generatesCodeAndShortUrl() {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setUrl("https://example.com/some/long/path");

        // Simulate DB auto-increment: first save() sets the ID
        doAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            if (url.getId() == null) {
                url.setId(1L);
                url.setCreatedAt(Instant.now());
            }
            return url;
        }).when(urlRepository).save(any(Url.class));

        UrlResponse response = urlService.create(request);

        assertNotNull(response.getCode());
        assertTrue(response.getShortUrl().endsWith(response.getCode()));
        // save is called twice: once to get the ID, once to persist the code
        verify(urlRepository, times(2)).save(any(Url.class));
    }

    // Edge case: redirecting increments the click count
    @Test
    void resolveAndTrack_existingCode_incrementsClickCount() {
        Url url = Url.builder()
                .id(1L)
                .originalUrl("https://example.com")
                .code("abc")
                .createdAt(Instant.now())
                .clickCount(3)
                .build();
        when(urlRepository.findByCode("abc")).thenReturn(Optional.of(url));

        urlService.resolveAndTrack("abc");

        assertEquals(4, url.getClickCount());
        verify(urlRepository).save(url);
    }

}
