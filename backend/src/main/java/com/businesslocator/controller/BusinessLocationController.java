package com.businesslocator.controller;

import com.businesslocator.dto.BusinessLocationDTO;
import com.businesslocator.service.BusinessLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/businesses")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class BusinessLocationController {

    private final BusinessLocationService service;

    public BusinessLocationController(BusinessLocationService service) {
        this.service = service;
    }

    /**
     * Search for a business using OpenStreetMap Nominatim and save results to H2 DB
     */
    @PostMapping("/search")
    public ResponseEntity<?> searchBusiness(@RequestBody Map<String, String> request) {
        String businessName = request.get("businessName");

        if (businessName == null || businessName.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Business name is required"));
        }

        List<BusinessLocationDTO> results = service.searchAndSave(businessName.trim());

        if (results.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "message", "No new locations found for: " + businessName,
                    "locations", results,
                    "count", 0
            ));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Found and saved " + results.size() + " location(s)",
                "locations", results,
                "count", results.size()
        ));
    }

    /**
     * Get all saved business locations from H2 DB
     */
    @GetMapping
    public ResponseEntity<List<BusinessLocationDTO>> getAllLocations() {
        return ResponseEntity.ok(service.getAllLocations());
    }

    /**
     * Get saved locations by exact business name
     */
    @GetMapping("/by-name/{businessName}")
    public ResponseEntity<List<BusinessLocationDTO>> getByBusinessName(
            @PathVariable String businessName) {
        return ResponseEntity.ok(service.getLocationsByBusinessName(businessName));
    }

    /**
     * Search saved records by partial business name
     */
    @GetMapping("/search-saved")
    public ResponseEntity<List<BusinessLocationDTO>> searchSaved(
            @RequestParam String query) {
        return ResponseEntity.ok(service.searchSaved(query));
    }

    /**
     * Get all distinct business names in the DB
     */
    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllBusinessNames() {
        return ResponseEntity.ok(service.getAllBusinessNames());
    }

    /**
     * Delete a location by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Location deleted successfully"));
    }
}
