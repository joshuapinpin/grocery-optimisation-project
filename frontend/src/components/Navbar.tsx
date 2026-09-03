import { useState } from 'react'
import { Link, useNavigate} from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Navbar.css'

function Navbar() {

  const { user, isAuthenticated, loading, logout } = useAuth()
  const [menuOpen, setMenuOpen] = useState(false)
  const navigate = useNavigate()

  async function handleLogout() {
    await logout()
    setMenuOpen(false)
    navigate('/')
  }

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-logo">
          <span className="logo-icon">🧺</span>
          BagnSave
        </Link>
        <ul className="nav-menu">
          <li className="nav-item">
            <Link to="/" className="nav-link">Home</Link>
          </li>
          <li className="nav-item">
            <Link to="/select-items" className="nav-link">Shop</Link>
          </li>
          <li className="nav-item">
            <Link to="#about" className="nav-link">About</Link>
          </li>
        </ul>

        <div className="nav-actions">
          {loading ? null : isAuthenticated ? (
              <div style={{ position: 'relative' }}>
                <button
                    className="login-button"
                    onClick={() => setMenuOpen((open) => !open)}
                >
                  {user?.name ?? 'Account'}
                </button>
                {menuOpen && (
                    <div
                        style={{
                          position: 'absolute',
                          right: 0,
                          top: '110%',
                          background: 'var(--bg)',
                          border: '1px solid var(--border)',
                          borderRadius: '8px',
                          boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
                          minWidth: '180px',
                          zIndex: 200,
                          overflow: 'hidden',
                        }}
                    >
                      <Link
                          to="/account"
                          className="nav-link"
                          style={{ display: 'block', padding: '10px 16px' }}
                          onClick={() => setMenuOpen(false)}
                      >
                        My Account
                      </Link>
                      <Link
                          to="/lists"
                          className="nav-link"
                          style={{ display: 'block', padding: '10px 16px' }}
                          onClick={() => setMenuOpen(false)}
                      >
                        My Shopping Lists
                      </Link>
                      <button
                          onClick={handleLogout}
                          style={{
                            display: 'block',
                            width: '100%',
                            textAlign: 'left',
                            padding: '10px 16px',
                            background: 'none',
                            border: 'none',
                            cursor: 'pointer',
                            color: 'var(--text-h)',
                          }}
                      >
                        Log Out
                      </button>
                    </div>
                )}
              </div>
          ) : (
              <>
                <Link to="/register" className="login-button">Register</Link>
                <Link to="/login" className="login-button">Sign In</Link>
              </>
          )}
        </div>
      </div>
    </nav>
  )
}

export default Navbar

