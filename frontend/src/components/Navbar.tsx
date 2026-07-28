import { Link } from 'react-router-dom'
import './Navbar.css'

function Navbar() {
  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-logo">
          <span className="logo-icon">🧺</span>
          BagnSave
        </Link>
        <ul className="nav-menu">
          <li className="nav-item">
            <Link to="/" className="nav-link">
              Home
            </Link>
          </li>
          <li className="nav-item">
            <Link to="/products" className="nav-link">
              Shop
            </Link>
          </li>
          <li className="nav-item">
            <Link to="#about" className="nav-link">
              About
            </Link>
          </li>
        </ul>

        <div className="nav-actions">
          <Link to="/login" className="login-button">
            Sign In
          </Link>
        </div>
      </div>
    </nav>
  )
}

export default Navbar

