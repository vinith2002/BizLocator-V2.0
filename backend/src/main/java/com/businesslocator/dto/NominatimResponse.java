package com.businesslocator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimResponse {

    @JsonProperty("place_id")
    private String placeId;

    @JsonProperty("osm_id")
    private String osmId;

    @JsonProperty("osm_type")
    private String osmType;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("lat")
    private String lat;

    @JsonProperty("lon")
    private String lon;

    @JsonProperty("type")
    private String type;

    @JsonProperty("class")
    private String placeClass;

    @JsonProperty("address")
    private AddressDetails address;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddressDetails {
        @JsonProperty("shop")
        private String shop;

        @JsonProperty("amenity")
        private String amenity;

        @JsonProperty("road")
        private String road;

        @JsonProperty("house_number")
        private String houseNumber;

        @JsonProperty("suburb")
        private String suburb;

        @JsonProperty("city")
        private String city;

        @JsonProperty("town")
        private String town;

        @JsonProperty("village")
        private String village;

        @JsonProperty("county")
        private String county;

        @JsonProperty("state")
        private String state;

        @JsonProperty("postcode")
        private String postcode;

        @JsonProperty("country")
        private String country;

        @JsonProperty("country_code")
        private String countryCode;

        public String getResolvedCity() {
            if (city != null && !city.isBlank()) return city;
            if (town != null && !town.isBlank()) return town;
            if (village != null && !village.isBlank()) return village;
            if (suburb != null && !suburb.isBlank()) return suburb;
            if (county != null && !county.isBlank()) return county;
            return "";
        }

        public String getStreetAddress() {
            StringBuilder sb = new StringBuilder();
            if (houseNumber != null && !houseNumber.isBlank()) sb.append(houseNumber).append(" ");
            if (road != null && !road.isBlank()) sb.append(road);
            return sb.toString().trim();
        }

        // Getters
        public String getShop() { return shop; }
        public String getAmenity() { return amenity; }
        public String getRoad() { return road; }
        public String getHouseNumber() { return houseNumber; }
        public String getSuburb() { return suburb; }
        public String getCity() { return city; }
        public String getTown() { return town; }
        public String getVillage() { return village; }
        public String getCounty() { return county; }
        public String getState() { return state; }
        public String getPostcode() { return postcode; }
        public String getCountry() { return country; }
        public String getCountryCode() { return countryCode; }
    }

    // Getters
    public String getPlaceId() { return placeId; }
    public String getOsmId() { return osmId; }
    public String getOsmType() { return osmType; }
    public String getDisplayName() { return displayName; }
    public String getLat() { return lat; }
    public String getLon() { return lon; }
    public String getType() { return type; }
    public String getPlaceClass() { return placeClass; }
    public AddressDetails getAddress() { return address; }
}
