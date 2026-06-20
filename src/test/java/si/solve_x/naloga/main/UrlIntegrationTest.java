package si.solve_x.naloga.main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import si.solve_x.naloga.main.repository.UrlRepository;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
class UrlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;

    @BeforeEach
    void setUp() {
        urlRepository.deleteAll();
    }

    private String extractCode(MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        // body je oblike: {"code":"abc","shortUrl":"..."}
        String codeField = "\"code\":\"";
        int start = body.indexOf(codeField) + codeField.length();
        int end = body.indexOf("\"", start);
        return body.substring(start, end);
    }

    // Happy path: shorten → redirect → stats — polni stack skozi pravo bazo
    @Test
    void shortenThenRedirectThenStats_fullFlow() throws Exception {
        // 1. Shorten
        MvcResult shortenResult = mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com/some/long/path\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.shortUrl").value(startsWith("http://localhost:8080/")))
                .andReturn();

        String code = extractCode(shortenResult);

        // 2. Redirect — mora vrniti 302 in povečati click count
        mockMvc.perform(get("/" + code))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com/some/long/path"));

        // 3. Stats — click count mora biti 1
        mockMvc.perform(get("/api/stats/" + code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clickCount").value(1))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    // Edge case: redirect na neobstoječ code vrne 404
    @Test
    void redirect_unknownCode_returns404() throws Exception {
        mockMvc.perform(get("/neobstojec"))
                .andExpect(status().isNotFound());
    }

    // Edge case: stats za neobstoječ code vrne 404
    @Test
    void stats_unknownCode_returns404() throws Exception {
        mockMvc.perform(get("/api/stats/neobstojec"))
                .andExpect(status().isNotFound());
    }

    // Edge case: prazen URL vrne 400 (@NotBlank validacija)
    @Test
    void shorten_blankUrl_returns400() throws Exception {
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // Edge case: neveljaven URL (brez http/https) vrne 400 (@Pattern validacija)
    @Test
    void shorten_invalidUrl_returns400() throws Exception {
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"ftp://files.example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // Edge case: vsak redirect posebej poveča click count
    @Test
    void redirect_multipleVisits_incrementsClickCountEachTime() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        String code = extractCode(result);

        mockMvc.perform(get("/" + code));
        mockMvc.perform(get("/" + code));
        mockMvc.perform(get("/" + code));

        mockMvc.perform(get("/api/stats/" + code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clickCount").value(3));
    }
}
