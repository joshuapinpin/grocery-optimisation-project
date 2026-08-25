import { useCart } from '../contexts/CartContext'
import { useNavigate } from 'react-router-dom'
import './Cart.css'

export default function Cart() {
  const { items, removeItem, clearCart } = useCart()
  const navigate = useNavigate()

  const handleCompare = () => {
    if (items.length > 0) {
      navigate('/compare?items=' + encodeURIComponent(items.map(i => i.id).join(',')))
    }
  }

  return (
    <div className='cart-page'>
      <div className='cart-container'>
        <div className='cart-header'>
          <h1>Shopping Cart</h1>
          <p className='cart-subtitle'>
            {items.length} item{items.length !== 1 ? 's' : ''} ready to compare
          </p>
        </div>

        {items.length === 0 ? (
          <div className='cart-empty'>
            <p className='cart-empty-text'>Your cart is empty</p>
            <p className='cart-empty-subtext'>
              Start by selecting items from the <strong>Select Items</strong> page
            </p>
            <button
              className='cart-continue-shopping'
              onClick={() => navigate('/select-items')}
            >
              Continue Shopping
            </button>
          </div>
        ) : (
          <>
            <div className='cart-items'>
              {items.map((item) => (
                <div key={item.id} className='cart-item'>
                  <div className='cart-item-content'>
                    <h3 className='cart-item-name'>{item.name}</h3>
                    {item.brand && (
                      <p className='cart-item-brand'>{item.brand}</p>
                    )}
                    {(item.unit || item.size) && (
                      <p className='cart-item-specs'>
                        {item.size && <span>{item.size}</span>}
                        {item.size && item.unit && <span> • </span>}
                        {item.unit && <span>{item.unit}</span>}
                      </p>
                    )}
                  </div>
                  <button
                    className='cart-remove-btn'
                    onClick={() => removeItem(item.id)}
                    title='Remove from cart'
                  >
                    ✕
                  </button>
                </div>
              ))}
            </div>

            <div className='cart-actions'>
              <div className='cart-actions-group'>
                <button
                  className='cart-action-btn cart-clear-btn'
                  onClick={clearCart}
                >
                  Clear Cart
                </button>
                <button
                  className='cart-action-btn cart-back-btn'
                  onClick={() => navigate('/select-items')}
                >
                  Add More Items
                </button>
              </div>
              <button
                className='cart-action-btn cart-compare-btn'
                onClick={handleCompare}
              >
                Compare Prices
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  )
}


