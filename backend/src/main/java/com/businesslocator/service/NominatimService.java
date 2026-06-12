package com.businesslocator.service;

import com.businesslocator.dto.NominatimResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class NominatimService {

    private static final Logger log = LoggerFactory.getLogger(NominatimService.class);

    private static final int PAGE_SIZE = 50;       // max allowed by Nominatim per request
    private static final int MAX_PAGES = 20;        // safety cap → up to 1000 results
    private static final long RATE_LIMIT_MS = 1100; // Nominatim requires 1 req/sec

    @Value("${nominatim.base-url}")
    private String nominatimBaseUrl;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public NominatimService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<NominatimResponse> searchBusiness(String businessName) {
        List<NominatimResponse> allResults = new ArrayList<>();
        String encodedName = URLEncoder.encode(businessName, StandardCharsets.UTF_8);

        for (int page = 0; page < MAX_PAGES; page++) {
            try {
                // Nominatim uses offset-based pagination
                int offset = page * PAGE_SIZE;
                String url = nominatimBaseUrl
                        + "?q=" + encodedName
                        + "&format=json"
                        + "&addressdetails=1"
                        + "&limit=" + PAGE_SIZE
                        + "&offset=" + offset;

                log.info("Fetching page {} (offset {}) for '{}'", page + 1, offset, businessName);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("User-Agent", "BusinessLocatorApp/1.0 (educational project)")
                        .header("Accept", "application/json")
                        .timeout(Duration.ofSeconds(15))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    log.error("Nominatim returned HTTP {} on page {}", response.statusCode(), page + 1);
                    break;
                }

                NominatimResponse[] pageResults = objectMapper.readValue(response.body(), NominatimResponse[].class);
                log.info("Page {} returned {} results", page + 1, pageResults.length);

                if (pageResults.length == 0) {
                    log.info("No more results at page {}, stopping pagination", page + 1);
                    break;
                }

                allResults.addAll(Arrays.asList(pageResults));

                // Stop if this page returned fewer than PAGE_SIZE — last page
                if (pageResults.length < PAGE_SIZE) {
                    log.info("Last page reached (got {} < {}), stopping", pageResults.length, PAGE_SIZE);
                    break;
                }

                // Respect Nominatim's usage policy: max 1 request per second
                if (page < MAX_PAGES - 1) {
                    Thread.sleep(RATE_LIMIT_MS);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Pagination interrupted at page {}", page + 1);
                break;
            } catch (Exception e) {
                log.error("Error on page {} for '{}': {}", page + 1, businessName, e.getMessage());
                break;
            }
        }

        log.info("Total results fetched for '{}': {}", businessName, allResults.size());
        return allResults;
    }
}
