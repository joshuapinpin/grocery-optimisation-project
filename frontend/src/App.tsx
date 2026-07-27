import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Navbar from './components/Navbar'
import Homepage from './pages/Homepage'
import MockProducts from './pages/MockProducts'
import Login from './pages/Login'
import SelectItems from './pages/SelectItems'
import Compare from './pages/Compare'
import Register from './pages/Register'

function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/" element={<Homepage />} />
        <Route path="/mock-products" element={<MockProducts />} />
        <Route path="/login" element={<Login />} />
        <Route path="/select-items" element={<SelectItems />} />
        <Route path="/compare" element={<Compare />} />
        <Route path="/register" element={<Register />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App