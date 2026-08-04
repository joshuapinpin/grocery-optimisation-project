import { useEffect, useState } from 'react'
import './Products.css'

interface Store {
  id: number
  name: string
  isEnabled: boolean
  vendorName: string
}

interface Price {
  storeId: number
  productId: number
  originalPriceCent: number
  salePriceCent: number
  clubPriceCent: number
  onlinePriceCent: number
  multibuyPriceCent: number
  multibuyQuantity: number
  clubMultibuyPriceCent: number
  clubMultibuyQuantity: number
  updatedAt: string
}

interface ProductData {
  id: number
  name: string
  brand: string
  unit: string
  size: string
  redirectedTo: number | null
}

interface StoreLocation {
  storeId: number
  storeName: string
  price: number
}

interface Product {
  id: number
  name: string
  brand: string
  unit: string
  size: string
  locations: StoreLocation[]
}

const STORE_COLORS: Record<string, string> = {
  "Woolworths":   "#007837",
  "Pak'nSave":    "#b88a00",
  "New World":    "#c0021a",
  "Four Square":  "#d14510",
  "Fresh Choice": "#0055a4",
  "SuperValue":   "#5b4fcf",
}

export default function Products() {
  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [sortBy, setSortBy] = useState<'name' | 'price'>('name')

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch products (paginated, get first 100)
        const productsRes = await fetch('/api/products?page=0&size=100')
        const productsData = await productsRes.json()
        const productsList: ProductData[] = productsData.content || []

        // Fetch prices (paginated, get first 100)
        const pricesRes = await fetch('/api/prices?page=0&size=100')
        const pricesData = await pricesRes.json()
        const pricesList: Price[] = pricesData.content || []

        // Fetch stores
        const storesRes = await fetch('/api/stores/all')
        const storesList: Store[] = await storesRes.json()

        // Create a map of stores for quick lookup
        const storeMap: Record<number, Store> = {}
        storesList.forEach(store => {
          storeMap[store.id] = store
        })

        // Combine product and price data
        const combinedProducts: Product[] = productsList.map(product => {
          const productPrices = pricesList.filter(p => p.productId === product.id)
          const locations: StoreLocation[] = productPrices
            .map(price => ({
              storeId: price.storeId,
              storeName: storeMap[price.storeId]?.name || `Store ${price.storeId}`,
              price: price.salePriceCent ? price.salePriceCent / 100 : price.originalPriceCent / 100,
            }))
            .filter(loc => loc.price > 0) // Filter out zero prices

          return {
            id: product.id,
            name: product.name,
            brand: product.brand,
            unit: product.unit,
            size: product.size,
            locations: locations,
          }
        })

        // Only show products that have at least one price
        const productsWithPrices = combinedProducts.filter(p => p.locations.length > 0)
        setProducts(productsWithPrices)
        setLoading(false)
      } catch (err) {
        console.error('Error fetching products:', err)
        setError('Failed to load products. Is the backend running?')
        setLoading(false)
      }
    }

    fetchData()
  }, [])

  const getSortedProducts = () => {
    const sorted = [...products]
    if (sortBy === 'price') {
      sorted.sort((a, b) => {
        const minA = Math.min(...a.locations.map(l => l.price))
        const minB = Math.min(...b.locations.map(l => l.price))
        return minA - minB
      })
    } else {
      sorted.sort((a, b) => a.name.localeCompare(b.name))
    }
    return sorted
  }

  const getMinPrice = (product: Product) => {
    return Math.min(...product.locations.map(l => l.price))
  }

  return (
    <div className="products-page">
      <div className="products-container">
        <div className="products-header">
          <div>
            <h1>Products</h1>
            <p className="products-subtitle">Browse our available products across all stores</p>
          </div>
          <div className="sort-controls">
            <label htmlFor="sort-select">Sort by:</label>
            <select
              id="sort-select"
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value as 'name' | 'price')}
              className="sort-select"
            >
              <option value="name">Product Name</option>
              <option value="price">Lowest Price</option>
            </select>
          </div>
        </div>

        {loading && (
          <div className="products-state">
            <div className="spinner"></div>
            <p>Loading products…</p>
          </div>
        )}

        {error && (
          <div className="products-state products-error">
            <p>⚠️ {error}</p>
          </div>
        )}

        {!loading && !error && products.length === 0 && (
          <div className="products-state">
            <p>No products available</p>
          </div>
        )}

        {!loading && !error && products.length > 0 && (
          <div className="products-grid">
            {getSortedProducts().map((product) => {
              const minPrice = getMinPrice(product)
              return (
                <div key={product.id} className="product-card">
                  <div className="product-card-content">
                    <h3 className="product-name">{product.name}</h3>

                    {product.brand && (
                      <p className="product-brand">{product.brand}</p>
                    )}

                    {(product.unit || product.size) && (
                      <p className="product-details">
                        {product.size && <span>{product.size}</span>}
                        {product.size && product.unit && <span> • </span>}
                        {product.unit && <span>{product.unit}</span>}
                      </p>
                    )}

                    <div className="product-price-section">
                      <div className="min-price">
                        <span className="price-label">From</span>
                        <span className="price-value">NZ${minPrice.toFixed(2)}</span>
                      </div>
                    </div>

                    <div className="product-stores">
                      <p className="stores-label">Available at:</p>
                      <div className="stores-list">
                        {product.locations.map((location) => (
                          <div key={location.storeId} className="store-item">
                            <span
                              className="store-indicator"
                              style={{ backgroundColor: STORE_COLORS[location.storeName] ?? '#999' }}
                            ></span>
                            <div className="store-info">
                              <span className="store-name">{location.storeName}</span>
                              <span className="store-price">NZ${location.price.toFixed(2)}</span>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  </div>
                </div>
              )
            })}
          </div>
        )}
      </div>
    </div>
  )
}



