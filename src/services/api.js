import axios from 'axios';

const API_BASE = '/api/businesses';

export const businessApi = {
  // Search OSM and save to DB
  searchBusiness: async (businessName) => {
    const response = await axios.post(`${API_BASE}/search`, { businessName });
    return response.data;
  },

  // Get all locations from DB
  getAllLocations: async () => {
    const response = await axios.get(API_BASE);
    return response.data;
  },

  // Get locations by business name
  getByBusinessName: async (name) => {
    const response = await axios.get(`${API_BASE}/by-name/${encodeURIComponent(name)}`);
    return response.data;
  },

  // Search saved records
  searchSaved: async (query) => {
    const response = await axios.get(`${API_BASE}/search-saved`, { params: { query } });
    return response.data;
  },

  // Get all business names
  getAllBusinessNames: async () => {
    const response = await axios.get(`${API_BASE}/names`);
    return response.data;
  },

  // Delete a location
  deleteLocation: async (id) => {
    const response = await axios.delete(`${API_BASE}/${id}`);
    return response.data;
  }
};
