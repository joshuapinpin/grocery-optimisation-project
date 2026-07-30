import { Link } from 'react-router-dom'
import './NotFound.css'

function NotFound() {
  return (
    <div className='notfound-page'>
      <div className='notfound-card'>
        <span className='notfound-emoji'>🧺</span>
        <h1 className='notfound-code'>404</h1>
        <p className='notfound-title'>We couldn't find that page</p>
        <p className='notfound-subtitle'>
          The link might be broken, or the page may have moved.
        </p>
        <Link to='/' className='notfound-button'>
          Back to home
        </Link>
      </div>
    </div>
  )
}

export default NotFound
