package si.solve_x.naloga.main.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import si.solve_x.naloga.main.dto.UrlResponse;
import si.solve_x.naloga.main.service.UrlService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    // Happy path: valid URL returns 201 with code and shortUrl in the body
    @Test
    void shorten_validUrl_returns201WithCodeAndShortUrl() throws Exception {
        when(urlService.create(any())).thenReturn(
                UrlResponse.builder()
                        .code("1")
                        .shortUrl("http://localhost:8080/1")
                        .build()
        );

        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/1"));
    }

    // Edge case: blank URL returns 400 with an error message
    @Test
    void shorten_blankUrl_returns400WithError() throws Exception {
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // Edge case: ftp:// URL returns 400 (only http/https are allowed)
    @Test
    void shorten_ftpUrl_returns400WithError() throws Exception {
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"ftp://files.example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // Edge case: redirect with unknown code returns 404
    @Test
    void redirect_unknownCode_returns404() throws Exception {
        when(urlService.resolveAndTrack("unknown"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Short code not found"));

        mockMvc.perform(get("/unknown"))
                .andExpect(status().isNotFound());
    }
}
