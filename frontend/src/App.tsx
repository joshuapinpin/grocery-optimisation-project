import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import MockProducts from './pages/MockProducts'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/mock/products" element={<MockProducts />} />
        <Route path="*" element={<Navigate to="/mock/products" replace />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
