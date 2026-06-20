package si.solve_x.naloga.main.service;

import si.solve_x.naloga.main.dto.CreateUrlRequest;
import si.solve_x.naloga.main.dto.ShortCodeDataResponse;
import si.solve_x.naloga.main.dto.UrlResponse;
import si.solve_x.naloga.main.repository.UrlRepository;

public interface UrlService {
    UrlResponse create(CreateUrlRequest request);

    ShortCodeDataResponse getShortCodeData(String code);

    String resolveAndTrack(String code);

}
