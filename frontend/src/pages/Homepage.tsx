import { Link } from 'react-router-dom'
import './Homepage.css'

function Homepage() {
  return (
    <div className="homepage">
      <section className="hero-section">
        <div className="hero-content">
          <h1 className="hero-title">BagnSave</h1>
          <p className="hero-subtitle">
            The lowest prices and best routes for grocery stores across New Zealand
          </p>
          <Link to="/mock/products" className="cta-button">
            Shop Now
          </Link>
        </div>
        <div className="hero-image">
          <div className="hero-placeholder">🛍️</div>
        </div>
      </section>

      <section className="features-section">
        <h2>Why Choose BagnSave?</h2>
        <div className="features-grid">
          <div className="feature-card">
            <div className="feature-icon">🔍</div>
            <h3>Search & Compare</h3>
            <p>
              Easily search and compare bag prices across multiple stores in
              your area
            </p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">💰</div>
            <h3>Best Prices</h3>
            <p>Find the lowest prices and best routes for grocery stores across New Zealand</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">📍</div>
            <h3>Store Locator</h3>
            <p>Discover nearby stores and check real-time inventory availability</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">⭐</div>
            <h3>Reviews & Ratings</h3>
            <p>Read reviews from other shoppers and make informed decisions</p>
          </div>
        </div>
      </section>

      <section className="cta-section">
        <h2>Ready to Save?</h2>
        <p>Browse our collection of bags and find incredible deals today</p>
        <Link to="/mock/products" className="cta-button-secondary">
          Explore Products
        </Link>
      </section>

      <footer className="footer">
        <div className="footer-content">
          <div className="footer-section">
            <h4>About BagnSave</h4>
            <p>Your one-stop destination for finding the best bag deals</p>
          </div>
          <div className="footer-section">
            <h4>Quick Links</h4>
            <ul>
              <li>
                <Link to="/">Home</Link>
              </li>
              <li>
                <Link to="/mock/products">Products</Link>
              </li>
              <li>
                <Link to="#about">About</Link>
              </li>
            </ul>
          </div>
          <div className="footer-section">
            <h4>Contact</h4>
            <p>Email: info@bagnsave.com</p>
            <p>Phone: (555) 123-4567</p>
          </div>
        </div>
        <div className="footer-bottom">
          <p>&copy; 2026 BagnSave. All rights reserved.</p>
        </div>
      </footer>
    </div>
  )
}

export default Homepage

