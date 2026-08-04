import { useSearchParams, useNavigate } from 'react-router-dom'
import './Compare.css'

const stores = ['Woolworths', 'New World', "Pak'nSave"]

// mock prices, swap for /api/prices later
const priceData: Record<string, Record<string, number>> = {
  'Milk 2L': { Woolworths: 4.45, 'New World': 5.0, "Pak'nSave": 4.0 },
  'Cheese Block 1kg': { Woolworths: 14.5, 'New World': 15.2, "Pak'nSave": 13.49 },
  'Butter 500g': { Woolworths: 7.5, 'New World': 7.99, "Pak'nSave": 6.99 },
  'Eggs Dozen': { Woolworths: 10.0, 'New World': 10.49, "Pak'nSave": 9.99 },
  'White Bread': { Woolworths: 3.5, 'New World': 3.99, "Pak'nSave": 3.2 },
  'Wholegrain Bread': { Woolworths: 4.5, 'New World': 4.99, "Pak'nSave": 4.29 },
  'Apples 1kg': { Woolworths: 4.5, 'New World': 4.99, "Pak'nSave": 3.99 },
  'Bananas 1kg': { Woolworths: 3.99, 'New World': 4.2, "Pak'nSave": 3.49 },
  'Potatoes 2kg': { Woolworths: 5.5, 'New World': 5.99, "Pak'nSave": 4.99 },
  'Tomatoes 500g': { Woolworths: 4.99, 'New World': 4.5, "Pak'nSave": 4.79 },
  'Chicken Breast 1kg': { Woolworths: 14.99, 'New World': 13.49, "Pak'nSave": 12.99 },
  'Beef Mince 500g': { Woolworths: 9.5, 'New World': 9.99, "Pak'nSave": 8.99 },
  'Rice 5kg': { Woolworths: 16.5, 'New World': 17.0, "Pak'nSave": 15.49 },
  'Pasta 500g': { Woolworths: 2.5, 'New World': 2.79, "Pak'nSave": 2.29 },
  'Weet-Bix 1.2kg': { Woolworths: 8.5, 'New World': 8.99, "Pak'nSave": 7.99 },
}

function Compare() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()

  const itemsParam = searchParams.get('items')
  let items: string[] = []
  if (itemsParam != null && itemsParam != '') {
    items = itemsParam.split(',')
  }

  // only keep items we actually have prices for
  items = items.filter((name: any) => priceData[name] != undefined)

  function cheapestStoreFor(name: string) {
    const row = priceData[name]
    let best = stores[0]
    for (let i = 1; i < stores.length; i++) {
      if (row[stores[i]] < row[best]) {
        best = stores[i]
      }
    }
    return best
  }

  function totalFor(store: string) {
    let total = 0
    for (let i = 0; i < items.length; i++) {
      total = total + priceData[items[i]][store]
    }
    return total
  }

  function cheapestBasket() {
    let best = stores[0]
    for (let i = 1; i < stores.length; i++) {
      if (totalFor(stores[i]) < totalFor(best)) {
        best = stores[i]
      }
    }
    return best
  }

  if (items.length == 0) {
    return (
      <div className='compare-page'>
        <div className='compare-empty'>
          <h1 className='compare-title'>Nothing to compare</h1>
          <p className='compare-subtitle'>
            Head back and pick a few items first.
          </p>
          <button
            className='compare-back'
            onClick={() => {
              navigate('/select-items')
            }}
          >
            Choose items
          </button>
        </div>
      </div>
    )
  }

  const winner = cheapestBasket()
  const mostExpensive = totalFor(
    stores.filter((s) => s != winner).sort((a, b) => totalFor(b) - totalFor(a))[0]
  )
  const saving = mostExpensive - totalFor(winner)

  return (
    <div className='compare-page'>
      <div className='compare-header'>
        <h1 className='compare-title'>Price comparison</h1>
        <p className='compare-subtitle'>
          {items.length} item{items.length != 1 ? 's' : ''} across{' '}
          {stores.length} stores
        </p>
      </div>

      <div className='compare-winner'>
        <span className='compare-winner-label'>Cheapest basket</span>
        <span className='compare-winner-store'>{winner}</span>
        <span className='compare-winner-total'>
          NZ${totalFor(winner).toFixed(2)}
        </span>
        <span className='compare-winner-saving'>
          saves you NZ${saving.toFixed(2)}
        </span>
      </div>

      <div className='compare-table-wrap'>
        <table className='compare-table'>
          <thead>
            <tr>
              <th>Item</th>
              {stores.map((store) => (
                <th key={store}>{store}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {items.map((name: string) => {
              const best = cheapestStoreFor(name)
              return (
                <tr key={name}>
                  <td className='compare-item-name'>{name}</td>
                  {stores.map((store) => (
                    <td
                      key={store}
                      className={
                        store == best ? 'compare-cell compare-best' : 'compare-cell'
                      }
                    >
                      NZ${priceData[name][store].toFixed(2)}
                    </td>
                  ))}
                </tr>
              )
            })}
          </tbody>
          <tfoot>
            <tr>
              <td className='compare-item-name'>Total</td>
              {stores.map((store) => (
                <td
                  key={store}
                  className={
                    store == winner
                      ? 'compare-total compare-best'
                      : 'compare-total'
                  }
                >
                  NZ${totalFor(store).toFixed(2)}
                </td>
              ))}
            </tr>
          </tfoot>
        </table>
      </div>

      <div className='compare-actions'>
        <button
          className='compare-back'
          onClick={() => {
            navigate('/select-items')
          }}
        >
          Back to items
        </button>
      </div>
    </div>
  )
}

export default Compare
