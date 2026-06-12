package com.businesslocator.service;

import com.businesslocator.dto.BusinessLocationDTO;
import com.businesslocator.dto.NominatimResponse;
import com.businesslocator.model.BusinessLocation;
import com.businesslocator.repository.BusinessLocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessLocationService {

    private static final Logger log = LoggerFactory.getLogger(BusinessLocationService.class);

    private final BusinessLocationRepository repository;
    private final NominatimService nominatimService;

    public BusinessLocationService(BusinessLocationRepository repository, NominatimService nominatimService) {
        this.repository = repository;
        this.nominatimService = nominatimService;
    }

    public List<BusinessLocationDTO> searchAndSave(String businessName) {
        log.info("Searching for business: {}", businessName);

        List<NominatimResponse> nominatimResults = nominatimService.searchBusiness(businessName);

        if (nominatimResults.isEmpty()) {
            log.warn("No results from Nominatim for: {}", businessName);
            return new ArrayList<>();
        }

        List<BusinessLocationDTO> savedLocations = new ArrayList<>();

        for (NominatimResponse result : nominatimResults) {
            String osmId = result.getOsmId() != null ? result.getOsmId() : result.getPlaceId();

            // Skip duplicates
            if (osmId != null && repository.existsByOsmIdAndBusinessName(osmId, businessName)) {
                log.debug("Skipping duplicate OSM ID: {} for business: {}", osmId, businessName);
                continue;
            }

            NominatimResponse.AddressDetails addr = result.getAddress();
            String address = "";
            String city = "";
            String state = "";
            String country = "";
            String countryCode = "";

            if (addr != null) {
                address = addr.getStreetAddress();
                city = addr.getResolvedCity();
                state = addr.getState() != null ? addr.getState() : "";
                country = addr.getCountry() != null ? addr.getCountry() : "";
                countryCode = addr.getCountryCode() != null ? addr.getCountryCode().toUpperCase() : "";
            }

            // Use display name parts as fallback
            if (address.isBlank() && result.getDisplayName() != null) {
                String[] parts = result.getDisplayName().split(",");
                if (parts.length > 0) address = parts[0].trim();
            }

            BusinessLocation location = new BusinessLocation(
                    businessName,
                    result.getDisplayName(),
                    address,
                    city,
                    state,
                    country,
                    countryCode,
                    result.getLat(),
                    result.getLon(),
                    result.getType(),
                    osmId
            );

            BusinessLocation saved = repository.save(location);
            savedLocations.add(BusinessLocationDTO.fromEntity(saved));
        }

        log.info("Saved {} new locations for '{}'", savedLocations.size(), businessName);
        return savedLocations;
    }

    public List<BusinessLocationDTO> getAllLocations() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(BusinessLocationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<BusinessLocationDTO> getLocationsByBusinessName(String businessName) {
        return repository.findByBusinessNameIgnoreCaseOrderByCreatedAtDesc(businessName)
                .stream()
                .map(BusinessLocationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<BusinessLocationDTO> searchSaved(String query) {
        return repository.searchByBusinessName(query)
                .stream()
                .map(BusinessLocationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<String> getAllBusinessNames() {
        return repository.findAllDistinctBusinessNames();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
