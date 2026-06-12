package com.businesslocator.dto;

import com.businesslocator.model.BusinessLocation;
import java.time.LocalDateTime;

public class BusinessLocationDTO {

    private Long id;
    private String businessName;
    private String displayName;
    private String address;
    private String city;
    private String state;
    private String country;
    private String countryCode;
    private String latitude;
    private String longitude;
    private String placeType;
    private String osmId;
    private LocalDateTime createdAt;
    private boolean savedToDb;

    public BusinessLocationDTO() {}

    public static BusinessLocationDTO fromEntity(BusinessLocation entity) {
        BusinessLocationDTO dto = new BusinessLocationDTO();
        dto.setId(entity.getId());
        dto.setBusinessName(entity.getBusinessName());
        dto.setDisplayName(entity.getDisplayName());
        dto.setAddress(entity.getAddress());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setCountry(entity.getCountry());
        dto.setCountryCode(entity.getCountryCode());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setPlaceType(entity.getPlaceType());
        dto.setOsmId(entity.getOsmId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setSavedToDb(true);
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }

    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }

    public String getPlaceType() { return placeType; }
    public void setPlaceType(String placeType) { this.placeType = placeType; }

    public String getOsmId() { return osmId; }
    public void setOsmId(String osmId) { this.osmId = osmId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isSavedToDb() { return savedToDb; }
    public void setSavedToDb(boolean savedToDb) { this.savedToDb = savedToDb; }
}
