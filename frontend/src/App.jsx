import { useState, useEffect, useCallback } from 'react';
import { businessApi } from './services/api';
import './App.css';

const PIN_SVG = (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
    <circle cx="12" cy="10" r="3"/>
  </svg>
);

const SEARCH_SVG = (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
    <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
  </svg>
);

const DB_SVG = (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <ellipse cx="12" cy="5" rx="9" ry="3"/><path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3"/><path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"/>
  </svg>
);

const TRASH_SVG = (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14H6L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4h6v2"/>
  </svg>
);

const CLOSE_SVG = (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
    <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
  </svg>
);

function LocationCard({ loc, onDelete }) {
  const [deleting, setDeleting] = useState(false);

  const handleDelete = async () => {
    if (!window.confirm('Remove this location?')) return;
    setDeleting(true);
    try {
      await onDelete(loc.id);
    } catch {
      setDeleting(false);
    }
  };

  const flag = loc.countryCode
    ? String.fromCodePoint(...[...loc.countryCode.toUpperCase()].map(c => 0x1F1E6 - 65 + c.charCodeAt(0)))
    : '🌐';

  return (
    <div className={`location-card ${deleting ? 'fading' : ''}`}>
      <div className="card-header">
        <div className="card-title-row">
          <span className="card-flag">{flag}</span>
          <h3 className="card-name">{loc.businessName}</h3>
          {loc.placeType && <span className="card-tag">{loc.placeType}</span>}
        </div>
        <button className="btn-icon delete-btn" onClick={handleDelete} title="Delete" disabled={deleting}>
          {TRASH_SVG}
        </button>
      </div>

      <div className="card-address">
        <span className="addr-icon">{PIN_SVG}</span>
        <div className="addr-lines">
          {loc.address && <span className="addr-street">{loc.address}</span>}
          <span className="addr-place">
            {[loc.city, loc.state, loc.country].filter(Boolean).join(', ') || loc.displayName?.split(',').slice(0,3).join(',') || '—'}
          </span>
        </div>
      </div>

      {loc.latitude && loc.longitude && (
        <div className="card-coords">
          <span className="coord-label">LAT</span>
          <span className="coord-val">{parseFloat(loc.latitude).toFixed(5)}</span>
          <span className="coord-sep">·</span>
          <span className="coord-label">LNG</span>
          <span className="coord-val">{parseFloat(loc.longitude).toFixed(5)}</span>
        </div>
      )}
    </div>
  );
}

function Toast({ message, type, onClose }) {
  useEffect(() => {
    const t = setTimeout(onClose, 4000);
    return () => clearTimeout(t);
  }, [onClose]);

  return (
    <div className={`toast toast-${type}`}>
      <span>{message}</span>
      <button className="toast-close" onClick={onClose}>{CLOSE_SVG}</button>
    </div>
  );
}

export default function App() {
  const [searchInput, setSearchInput] = useState('');
  const [dbQuery, setDbQuery] = useState('');
  const [locations, setLocations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [dbLoading, setDbLoading] = useState(false);
  const [activeTab, setActiveTab] = useState('search');
  const [businessNames, setBusinessNames] = useState([]);
  const [toast, setToast] = useState(null);
  const [searchCount, setSearchCount] = useState(null);
  const [totalCount, setTotalCount] = useState(0);

  const showToast = (message, type = 'success') => {
    setToast({ message, type });
  };

  const loadAllLocations = useCallback(async () => {
    setDbLoading(true);
    try {
      const data = await businessApi.getAllLocations();
      setLocations(data);
      setTotalCount(data.length);
      setSearchCount(null);
    } catch (e) {
      showToast('Failed to load locations', 'error');
    } finally {
      setDbLoading(false);
    }
  }, []);

  const loadBusinessNames = useCallback(async () => {
    try {
      const names = await businessApi.getAllBusinessNames();
      setBusinessNames(names);
    } catch {}
  }, []);

  useEffect(() => {
    if (activeTab === 'database') {
      loadAllLocations();
      loadBusinessNames();
    }
  }, [activeTab, loadAllLocations, loadBusinessNames]);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchInput.trim()) return;
    setLoading(true);
    setLocations([]);
    setSearchCount(null);
    try {
      const result = await businessApi.searchBusiness(searchInput.trim());
      setLocations(result.locations || []);
      setSearchCount(result.count || 0);
      if (result.count > 0) {
        showToast(`Found ${result.count} location${result.count !== 1 ? 's' : ''} for "${searchInput.trim()}"`, 'success');
      } else {
        showToast(`No results found for "${searchInput.trim()}"`, 'info');
      }
    } catch {
      showToast('Search failed. Is the backend running?', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleDbSearch = async (e) => {
    e.preventDefault();
    if (!dbQuery.trim()) {
      loadAllLocations();
      return;
    }
    setDbLoading(true);
    try {
      const data = await businessApi.searchSaved(dbQuery.trim());
      setLocations(data);
      setSearchCount(data.length);
    } catch {
      showToast('DB search failed', 'error');
    } finally {
      setDbLoading(false);
    }
  };

  const handleDelete = async (id) => {
    await businessApi.deleteLocation(id);
    setLocations(prev => prev.filter(l => l.id !== id));
    setTotalCount(prev => prev - 1);
    showToast('Location removed', 'success');
    loadBusinessNames();
  };

  const handleNameChip = async (name) => {
    setDbQuery(name);
    setDbLoading(true);
    try {
      const data = await businessApi.getByBusinessName(name);
      setLocations(data);
      setSearchCount(data.length);
    } catch {
      showToast('Failed to filter by name', 'error');
    } finally {
      setDbLoading(false);
    }
  };

  const clearDbSearch = () => {
    setDbQuery('');
    loadAllLocations();
  };

  return (
    <div className="app">
      {toast && (
        <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />
      )}

      <header className="app-header">
        <div className="header-inner">
          <div className="logo">
            <span className="logo-icon">{PIN_SVG}</span>
            <span className="logo-text">BizLocator</span>
          </div>
          <p className="header-tagline">Find & track business locations worldwide</p>
        </div>
      </header>

      <main className="app-main">
        <div className="tabs">
          <button
            className={`tab-btn ${activeTab === 'search' ? 'active' : ''}`}
            onClick={() => { setActiveTab('search'); setLocations([]); setSearchCount(null); }}
          >
            {SEARCH_SVG}
            <span>Search OSM</span>
          </button>
          <button
            className={`tab-btn ${activeTab === 'database' ? 'active' : ''}`}
            onClick={() => setActiveTab('database')}
          >
            {DB_SVG}
            <span>Saved Records</span>
            {totalCount > 0 && <span className="tab-badge">{totalCount}</span>}
          </button>
        </div>

        {activeTab === 'search' && (
          <section className="search-section">
            <div className="search-hero">
              <h2 className="section-title">Find Business Locations</h2>
              <p className="section-desc">Search OpenStreetMap for real-world business locations. Results are automatically saved to the database.</p>
            </div>

            <form className="search-form" onSubmit={handleSearch}>
              <div className="input-group">
                <span className="input-icon">{PIN_SVG}</span>
                <input
                  type="text"
                  className="search-input"
                  placeholder="e.g. Starbucks, McDonald's, Walmart..."
                  value={searchInput}
                  onChange={e => setSearchInput(e.target.value)}
                  disabled={loading}
                />
                <button type="submit" className="btn-primary" disabled={loading || !searchInput.trim()}>
                  {loading ? <span className="spinner" /> : SEARCH_SVG}
                  <span>{loading ? 'Searching...' : 'Search'}</span>
                </button>
              </div>
            </form>

            {loading && (
              <div className="loading-state">
                <div className="loading-dots">
                  <span /><span /><span />
                </div>
                <p>Querying OpenStreetMap Nominatim API...</p>
              </div>
            )}

            {!loading && searchCount !== null && (
              <div className="results-header">
                <span className="results-count">
                  {searchCount} location{searchCount !== 1 ? 's' : ''} found
                </span>
                <span className="results-saved">· saved to database</span>
              </div>
            )}

            {!loading && locations.length > 0 && (
              <div className="cards-grid">
                {locations.map((loc) => (
                  <LocationCard key={loc.id} loc={loc} onDelete={handleDelete} />
                ))}
              </div>
            )}

            {!loading && searchCount === 0 && (
              <div className="empty-state">
                <span className="empty-icon">🔍</span>
                <p>No locations found for "<strong>{searchInput}</strong>"</p>
                <span className="empty-hint">Try a different spelling or a broader name</span>
              </div>
            )}
          </section>
        )}

        {activeTab === 'database' && (
          <section className="database-section">
            <div className="db-header">
              <div>
                <h2 className="section-title">Saved Locations</h2>
                <p className="section-desc">{totalCount} record{totalCount !== 1 ? 's' : ''} stored in H2 database</p>
              </div>
            </div>

            <form className="search-form" onSubmit={handleDbSearch}>
              <div className="input-group">
                <span className="input-icon">{SEARCH_SVG}</span>
                <input
                  type="text"
                  className="search-input"
                  placeholder="Filter by business name..."
                  value={dbQuery}
                  onChange={e => setDbQuery(e.target.value)}
                />
                <button type="submit" className="btn-secondary">Filter</button>
                {dbQuery && (
                  <button type="button" className="btn-ghost" onClick={clearDbSearch}>
                    {CLOSE_SVG}
                  </button>
                )}
              </div>
            </form>

            {businessNames.length > 0 && (
              <div className="chips-row">
                <span className="chips-label">Businesses:</span>
                {businessNames.map(name => (
                  <button
                    key={name}
                    className={`chip ${dbQuery === name ? 'chip-active' : ''}`}
                    onClick={() => handleNameChip(name)}
                  >
                    {name}
                  </button>
                ))}
              </div>
            )}

            {dbLoading && (
              <div className="loading-state">
                <div className="loading-dots"><span /><span /><span /></div>
                <p>Loading from database...</p>
              </div>
            )}

            {!dbLoading && searchCount !== null && dbQuery && (
              <div className="results-header">
                <span className="results-count">{searchCount} match{searchCount !== 1 ? 'es' : ''}</span>
                <span className="results-saved"> for "{dbQuery}"</span>
              </div>
            )}

            {!dbLoading && locations.length > 0 && (
              <div className="cards-grid">
                {locations.map(loc => (
                  <LocationCard key={loc.id} loc={loc} onDelete={handleDelete} />
                ))}
              </div>
            )}

            {!dbLoading && locations.length === 0 && (
              <div className="empty-state">
                <span className="empty-icon">🗄️</span>
                <p>{dbQuery ? `No records match "${dbQuery}"` : 'No saved records yet'}</p>
                <span className="empty-hint">
                  {dbQuery ? 'Clear the filter to see all records' : 'Use the Search tab to find and save business locations'}
                </span>
              </div>
            )}
          </section>
        )}
      </main>

      <footer className="app-footer">
        <span>Powered by <a href="https://nominatim.openstreetmap.org" target="_blank" rel="noreferrer">OpenStreetMap Nominatim</a></span>
        <span className="footer-sep">·</span>
        <span>Data stored in H2 in-memory database</span>
      </footer>
    </div>
  );
}
