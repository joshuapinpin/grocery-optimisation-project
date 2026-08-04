import { useEffect, useState } from 'react'
import './SelectStores.css'

interface Store {
  id: number
  name: string
  isEnabled: boolean
  vendorName?: string
}

interface PaginationResponse {
  content: Store[]
  page: {
    totalPages: number
  }
}

interface VendorGroup {
  name: string
  stores: Store[]
  isExpanded: boolean
}

export default function SelectStores() {
  const [vendorGroups, setVendorGroups] = useState<VendorGroup[]>([])
  const [selectedStores, setSelectedStores] = useState<number[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [search, setSearch] = useState('')

  // Fetch all stores with pagination
  useEffect(() => {
    const fetchAllStores = async () => {
      try {
        let allStores: Store[] = []
        let page = 0
        let hasMore = true

        while (hasMore) {
          const res = await fetch(`/api/stores?page=${page}&size=50`)
          const data: PaginationResponse = await res.json()
          allStores = [...allStores, ...data.content]
          hasMore = page < data.page.totalPages - 1
          page++
        }

        // Group stores by vendor
        const grouped = allStores.reduce((acc, store) => {
          const vendorName = store.vendorName || store.name.split(' ')[0]
          const existing = acc.find(g => g.name === vendorName)

          if (existing) {
            existing.stores.push(store)
          } else {
            acc.push({
              name: vendorName,
              stores: [store],
              isExpanded: false
            })
          }
          return acc
        }, [] as VendorGroup[])

        // Sort vendors and their stores
        grouped.sort((a, b) => a.name.localeCompare(b.name))
        grouped.forEach(g => g.stores.sort((a, b) => a.name.localeCompare(b.name)))

        setVendorGroups(grouped)
        setLoading(false)
      } catch {
        setError('Failed to load stores. Is the backend running?')
        setLoading(false)
      }
    }

    fetchAllStores()
  }, [])

  const toggleStore = (storeId: number) => {
    setSelectedStores(prev =>
      prev.includes(storeId)
        ? prev.filter(id => id !== storeId)
        : [...prev, storeId]
    )
  }

  const toggleVendor = (vendorName: string) => {
    setVendorGroups(prev =>
      prev.map(g =>
        g.name === vendorName ? { ...g, isExpanded: !g.isExpanded } : g
      )
    )
  }

  const toggleVendorStores = (vendorName: string) => {
    const vendor = vendorGroups.find(g => g.name === vendorName)
    if (!vendor) return

    const vendorStoreIds = vendor.stores.map(s => s.id)
    const allSelected = vendorStoreIds.every(id => selectedStores.includes(id))

    if (allSelected) {
      setSelectedStores(prev => prev.filter(id => !vendorStoreIds.includes(id)))
    } else {
      setSelectedStores(prev => [
        ...prev,
        ...vendorStoreIds.filter(id => !prev.includes(id))
      ])
    }
  }

  const selectAll = () => {
    const allStoreIds = vendorGroups.flatMap(g => g.stores.map(s => s.id))
    if (selectedStores.length === allStoreIds.length) {
      setSelectedStores([])
    } else {
      setSelectedStores(allStoreIds)
    }
  }

  // derive filtered vendor groups based on the search box
  const filteredVendorGroups = vendorGroups
    .map(g => ({
      ...g,
      stores: g.stores.filter(s => s.name.toLowerCase().includes(search.toLowerCase()))
    }))
    .filter(g => g.stores.length > 0)
    .map(g => (search !== '' ? { ...g, isExpanded: true } : g))

  const totalStores = vendorGroups.reduce((sum, g) => sum + g.stores.length, 0)

  return (
    <div className="ss-page">
      <div className="ss-container">
        <div className="ss-header">
          <h1>Select Stores</h1>
          <p className="ss-subtitle">Choose which stores to compare prices from</p>
        </div>

        {loading && (
          <div className="ss-state-card">
            <span className="ss-spinner" />
            <p>Loading stores…</p>
          </div>
        )}

        {error && (
          <div className="ss-state-card ss-state-error">
            <span className="ss-error-icon">⚠️</span>
            <p>{error}</p>
          </div>
        )}

        {!loading && !error && (
          <>
            <div className="ss-controls">
              <input
                type="text"
                className="ss-search"
                placeholder="Search stores..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />

              <button
                className="ss-select-all-btn"
                onClick={selectAll}
              >
                {selectedStores.length === totalStores ? 'Deselect All' : 'Select All'}
              </button>
              <p className="ss-count">
                {selectedStores.length} of {totalStores} stores selected
              </p>
            </div>

            <div className="ss-vendors">
              {filteredVendorGroups.length === 0 ? (
                <p className="ss-empty">No stores match "{search}"</p>
              ) : (
                filteredVendorGroups.map((vendor) => {
                  const vendorStoreIds = vendor.stores.map(s => s.id)
                  const vendorSelected = vendorStoreIds.filter(id => selectedStores.includes(id)).length
                  const isAllSelected = vendorSelected === vendor.stores.length && vendor.stores.length > 0
                  const isSomeSelected = vendorSelected > 0

                  return (
                    <div key={vendor.name} className="ss-vendor-group">
                      <button
                        className="ss-vendor-header"
                        onClick={() => toggleVendor(vendor.name)}
                      >
                        <span className="ss-vendor-toggle">
                          {vendor.isExpanded ? '▼' : '▶'}
                        </span>
                        <span className="ss-vendor-name">{vendor.name}</span>
                        <span className="ss-vendor-count">
                          {vendorSelected}/{vendor.stores.length}
                        </span>
                        <button
                          className={`ss-vendor-checkbox ${isAllSelected ? 'ss-vendor-checkbox--checked' : isSomeSelected ? 'ss-vendor-checkbox--partial' : ''}`}
                          onClick={(e) => {
                            e.stopPropagation()
                            toggleVendorStores(vendor.name)
                          }}
                        >
                          {isAllSelected ? '✓' : isSomeSelected ? '−' : ''}
                        </button>
                      </button>

                      {vendor.isExpanded && (
                        <div className="ss-stores-list">
                          {vendor.stores.map((store) => {
                            const isSelected = selectedStores.includes(store.id)
                            return (
                              <label key={store.id} className="ss-store-item">
                                <input
                                  type="checkbox"
                                  checked={isSelected}
                                  onChange={() => toggleStore(store.id)}
                                  disabled={!store.isEnabled}
                                />
                                <span className={`ss-store-label ${!store.isEnabled ? 'ss-store-label--disabled' : ''}`}>
                                  {store.name}
                                </span>
                              </label>
                            )
                          })}
                        </div>
                      )}
                    </div>
                  )
                })
              )}
            </div>

            {selectedStores.length > 0 && (
              <div className="ss-footer">
                <button className="ss-continue-btn">
                  Continue with {selectedStores.length} store{selectedStores.length !== 1 ? 's' : ''}
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}
