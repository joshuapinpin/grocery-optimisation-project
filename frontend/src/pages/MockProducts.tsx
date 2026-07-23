import { useEffect, useState } from 'react'
import './MockProducts.css'

interface StoreLocation {
  storeId: number
  storeName: string
  price: number
}

interface Product {
  id: number
  name: string
  image: string
  locations: StoreLocation[]
}

interface CartItem {
  productId: number
  productName: string
  storeId: number
  storeName: string
  price: number
}

const STORE_ACCENT: Record<string, string> = {
  "Woolworths":   "#007837",
  "Pak'nSave":    "#b88a00",
  "New World":    "#c0021a",
  "Four Square":  "#d14510",
  "Fresh Choice": "#0055a4",
  "SuperValue":   "#5b4fcf",
}

const PRODUCT_THEME: Record<number, { light: string; dark: string; emoji: string }> = {
  1: { light: 'linear-gradient(135deg,#e0f2fe,#7dd3fc)', dark: 'linear-gradient(135deg,#0c2a3a,#0e3f5c)', emoji: '🥛' },
  2: { light: 'linear-gradient(135deg,#fef3c7,#fde68a)', dark: 'linear-gradient(135deg,#2d2000,#3d2e00)', emoji: '🍞' },
  3: { light: 'linear-gradient(135deg,#fefce8,#fef08a)', dark: 'linear-gradient(135deg,#2a2600,#3a3300)', emoji: '🥚' },
  4: { light: 'linear-gradient(135deg,#dcfce7,#86efac)', dark: 'linear-gradient(135deg,#052e16,#064e2a)', emoji: '🍎' },
  5: { light: 'linear-gradient(135deg,#fff7ed,#fed7aa)', dark: 'linear-gradient(135deg,#2c1400,#3d2000)', emoji: '🍊' },
}

function StoreDot({ store }: { store: string }) {
  return (
    <span
      className="mp-store-dot"
      style={{ background: STORE_ACCENT[store] ?? '#888' }}
      aria-hidden
    />
  )
}

export default function MockProducts() {
  const [products, setProducts] = useState<Product[]>([])
  const [cart, setCart] = useState<CartItem[]>([])
  const [statusMessage, setStatusMessage] = useState<{ text: string; ok: boolean } | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [darkMode, setDarkMode] = useState(
    () => window.matchMedia('(prefers-color-scheme: dark)').matches
  )

  useEffect(() => {
    const mq = window.matchMedia('(prefers-color-scheme: dark)')
    const handler = (e: MediaQueryListEvent) => setDarkMode(e.matches)
    mq.addEventListener('change', handler)
    return () => mq.removeEventListener('change', handler)
  }, [])

  useEffect(() => {
    fetch('/api/products')
      .then(res => res.json())
      .then(data => { setProducts(data); setLoading(false) })
      .catch(() => { setError('Failed to load products. Is the backend running?'); setLoading(false) })
  }, [])

  const addToCart = (product: Product, location: StoreLocation) => {
    setCart(prev => [...prev, {
      productId: product.id,
      productName: product.name,
      storeId: location.storeId,
      storeName: location.storeName,
      price: location.price,
    }])
    setStatusMessage(null)
  }

  const removeFromCart = (index: number) =>
    setCart(prev => prev.filter((_, i) => i !== index))

  const storeShoppingList = async () => {
    if (cart.length === 0) return
    try {
      const res = await fetch('/api/shopping-list', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(cart),
      })
      const data = await res.json()
      setStatusMessage({ text: data.message, ok: true })
    } catch {
      setStatusMessage({ text: 'Failed to store shopping list.', ok: false })
    }
  }

  const cartTotal = cart.reduce((sum, item) => sum + item.price, 0)

  const downloadCart = () => {
    const blob = new Blob([JSON.stringify(cart, null, 2)], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'shopping-cart.json'
    a.click()
    URL.revokeObjectURL(url)
  }

  const loadCart = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return
    const reader = new FileReader()
    reader.onload = event => {
      try {
        const items = JSON.parse(event.target?.result as string) as CartItem[]
        setCart(items)
        setStatusMessage({ text: `Loaded ${items.length} item(s) from file.`, ok: true })
      } catch {
        setStatusMessage({ text: 'Invalid cart file.', ok: false })
      }
    }
    reader.readAsText(file)
    e.target.value = ''
  }

  return (
    <div className="mp-page">
      <div className="mp-layout">
        <main className="mp-products">
          {loading && (
            <div className="mp-state-card">
              <span className="mp-spinner" />
              <p>Loading products…</p>
            </div>
          )}
          {error && <div className="mp-state-card mp-state-error">{error}</div>}
          {products.map((product, index) => {
            const theme = PRODUCT_THEME[product.id] ?? PRODUCT_THEME[1]
            const cheapestPrice = Math.min(...product.locations.map(l => l.price))
            return (
              <div key={product.id} className="mp-card" style={{ '--card-i': index } as React.CSSProperties}>
                <div
                  className="mp-card-image"
                  style={{ background: darkMode ? theme.dark : theme.light }}
                >
                  <span className="mp-card-emoji">{theme.emoji}</span>
                </div>

                <div className="mp-card-body">
                  <p className="mp-card-name">{product.name}</p>

                  <div className="mp-store-list">
                    {product.locations.map(loc => (
                      <button
                        key={loc.storeId}
                        className="mp-add-btn"
                        style={{ '--sc': STORE_ACCENT[loc.storeName] ?? '#2d6a4f' } as React.CSSProperties}
                        onClick={() => addToCart(product, loc)}
                      >
                        <StoreDot store={loc.storeName} />
                        <span className="mp-store-name">{loc.storeName}</span>
<span className="mp-price">NZ${loc.price.toFixed(2)}</span>
                        <span className="mp-add-pill">+ Add</span>
                      </button>
                    ))}
                    <button
                      className="mp-any-btn"
                      onClick={() => addToCart(product, { storeId: 0, storeName: 'Any', price: cheapestPrice })}
                    >
                      <span className="mp-any-icon">✦</span>
                      <span className="mp-store-name">Any store</span>
                      <span className="mp-any-from">from NZ${cheapestPrice.toFixed(2)}</span>
                      <span className="mp-add-pill">+ Add</span>
                    </button>
                  </div>
                </div>
              </div>
            )
          })}
        </main>

        <aside className="mp-cart-panel" id="cart-panel">
          <div className="mp-cart-header">
            <p className="mp-cart-title">Your Cart</p>
            {cart.length > 0 && (
              <span className="mp-cart-badge">{cart.length} item{cart.length !== 1 ? 's' : ''}</span>
            )}
          </div>

          {cart.length === 0 ? (
            <div className="mp-cart-empty">
              <span className="mp-cart-empty-icon">🛒</span>
              <p>Add items from the products list to get started.</p>
            </div>
          ) : (
            <ul className="mp-cart-list">
              {cart.map((item, i) => (
                <li key={i} className="mp-cart-item">
                  <StoreDot store={item.storeName} />
                  <span className="mp-cart-item-name">{item.productName}</span>
                  <span
                    className="mp-cart-item-store"
                    style={{ color: STORE_ACCENT[item.storeName] ?? undefined }}
                  >
                    {item.storeName}
                  </span>
                  <span className="mp-cart-item-price">NZ${item.price.toFixed(2)}</span>
                  <button className="mp-remove-btn" onClick={() => removeFromCart(i)} title="Remove">✕</button>
                </li>
              ))}
            </ul>
          )}

          <div className="mp-cart-footer">
            {cart.length > 0 && (
              <div className="mp-cart-total">
                <span>Estimated total</span>
                <strong>NZ${cartTotal.toFixed(2)}</strong>
              </div>
            )}
            <button
              className="mp-store-btn"
              onClick={storeShoppingList}
              disabled={cart.length === 0}
            >
              Save Shopping List
            </button>
            <div className="mp-cart-actions">
              <button
                className="mp-action-btn"
                onClick={downloadCart}
                disabled={cart.length === 0}
                title="Export cart as JSON"
              >
                Download
              </button>
              <label className="mp-action-btn mp-action-btn--load">
                Load
                <input
                  type="file"
                  accept=".json"
                  onChange={loadCart}
                  style={{ display: 'none' }}
                />
              </label>
            </div>
            {statusMessage && (
              <p className={`mp-status-msg ${statusMessage.ok ? 'mp-status-ok' : 'mp-status-err'}`}>
                {statusMessage.ok ? '✓ ' : '✗ '}{statusMessage.text}
              </p>
            )}
          </div>
        </aside>
      </div>
    </div>
  )
}
