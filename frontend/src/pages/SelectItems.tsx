import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './SelectItems.css'

// mock data for now, swap this for /api/products once the backend compiles
const allItems = [
  { id: 1, name: 'Milk 2L', emoji: '🥛', category: 'Dairy' },
  { id: 2, name: 'Cheese Block 1kg', emoji: '🧀', category: 'Dairy' },
  { id: 3, name: 'Butter 500g', emoji: '🧈', category: 'Dairy' },
  { id: 4, name: 'Eggs Dozen', emoji: '🥚', category: 'Dairy' },
  { id: 5, name: 'White Bread', emoji: '🍞', category: 'Bakery' },
  { id: 6, name: 'Wholegrain Bread', emoji: '🥖', category: 'Bakery' },
  { id: 7, name: 'Apples 1kg', emoji: '🍎', category: 'Produce' },
  { id: 8, name: 'Bananas 1kg', emoji: '🍌', category: 'Produce' },
  { id: 9, name: 'Potatoes 2kg', emoji: '🥔', category: 'Produce' },
  { id: 10, name: 'Tomatoes 500g', emoji: '🍅', category: 'Produce' },
  { id: 11, name: 'Chicken Breast 1kg', emoji: '🍗', category: 'Meat' },
  { id: 12, name: 'Beef Mince 500g', emoji: '🥩', category: 'Meat' },
  { id: 13, name: 'Rice 5kg', emoji: '🍚', category: 'Pantry' },
  { id: 14, name: 'Pasta 500g', emoji: '🍝', category: 'Pantry' },
  { id: 15, name: 'Weet-Bix 1.2kg', emoji: '🥣', category: 'Pantry' },
]

const categories = ['All', 'Dairy', 'Bakery', 'Produce', 'Meat', 'Pantry']

function SelectItems() {
  const [picked, setPicked] = useState<string[]>([])
  const [search, setSearch] = useState('')
  const [activeCategory, setActiveCategory] = useState('All')
  const [errorMsg, setErrorMsg] = useState('')
  const navigate = useNavigate()

  function isPicked(name: string) {
    return picked.indexOf(name) != -1
  }

  function togglePick(name: string) {
    if (isPicked(name)) {
      const newList = picked.filter((x: string) => x != name)
      setPicked(newList)
    } else {
      setPicked([...picked, name])
    }
    setErrorMsg('')
  }

  function clearAll() {
    setPicked([])
    setErrorMsg('')
  }

  function handleCompare() {
    if (picked.length == 0) {
      setErrorMsg('pick at least one item first')
      return
    }
    navigate('/compare?items=' + encodeURIComponent(picked.join(',')))
  }

  // filter by category first, then by the search box
  let shownItems = allItems
  if (activeCategory != 'All') {
    shownItems = shownItems.filter((item) => item.category == activeCategory)
  }
  if (search != '') {
    shownItems = shownItems.filter((item) =>
      item.name.toLowerCase().includes(search.toLowerCase())
    )
  }

  return (
    <div className='select-page'>
      <div className='select-header'>
        <h1 className='select-title'>What are you buying?</h1>
        <p className='select-subtitle'>
          Pick your items and we'll show you which store is cheapest
        </p>
      </div>

      <div className='select-controls'>
        <input
          type='text'
          className='select-search'
          placeholder='Search items...'
          value={search}
          onChange={(e) => {
            setSearch(e.target.value)
          }}
        />

        <div className='select-categories'>
          {categories.map((cat) => (
            <button
              key={cat}
              className={
                activeCategory == cat
                  ? 'select-cat select-cat-active'
                  : 'select-cat'
              }
              onClick={() => {
                setActiveCategory(cat)
              }}
            >
              {cat}
            </button>
          ))}
        </div>
      </div>

      <div className='select-grid'>
        {shownItems.map((item) => (
          <button
            key={item.id}
            className={
              isPicked(item.name)
                ? 'select-item select-item-picked'
                : 'select-item'
            }
            onClick={() => {
              togglePick(item.name)
            }}
          >
            <span className='select-item-emoji'>{item.emoji}</span>
            <span className='select-item-name'>{item.name}</span>
            <span className='select-item-cat'>{item.category}</span>
            {isPicked(item.name) ? (
              <span className='select-item-tick'>✓</span>
            ) : null}
          </button>
        ))}
      </div>

      {shownItems.length == 0 ? (
        <p className='select-empty'>No items match "{search}"</p>
      ) : null}

      <div className='select-bar'>
        <div className='select-bar-info'>
          {picked.length == 0 ? (
            <span className='select-bar-empty'>Nothing selected yet</span>
          ) : (
            <span className='select-bar-count'>
              {picked.length} item{picked.length != 1 ? 's' : ''} selected
            </span>
          )}
          {errorMsg != '' ? (
            <span className='select-error'>{errorMsg}</span>
          ) : null}
        </div>

        <div className='select-bar-actions'>
          <button
            className='select-clear'
            onClick={clearAll}
            disabled={picked.length == 0}
          >
            Clear
          </button>
          <button className='select-compare' onClick={handleCompare}>
            Compare Prices
          </button>
        </div>
      </div>
    </div>
  )
}

export default SelectItems
