import { BrowserRouter, Routes, Route} from 'react-router-dom'
import {AuthProvider} from './context/AuthContext'
import Navbar from './components/Navbar'
import Homepage from './pages/Homepage'
import MockProducts from './pages/MockProducts'
import Login from './pages/Login'
import SelectItems from './pages/SelectItems'
import SelectStores from './pages/SelectStores'
import Compare from './pages/Compare'
import Register from './pages/Register'
import NotFound from './pages/NotFound'
import ProtectedRoute from "./components/ProtectedRoute.tsx";
import Account from "./pages/Account.tsx";

function App() {
  return (
      <AuthProvider>
          <BrowserRouter>
              <Navbar />
              <Routes>
                  <Route path="/" element={<Homepage />} />
                  <Route path="/mock-products" element={<MockProducts />} />
                  <Route path="/login" element={<Login />} />
                  <Route path="/select-stores" element={<SelectStores />} />
                  <Route path="/select-items" element={<SelectItems />} />
                  <Route path="/compare" element={<Compare />} />
                  <Route path="/register" element={<Register />} />
                  <Route element={<ProtectedRoute />}>
                      <Route path="/account" element={<Account />} />
                  </Route>
                  {/* keep this last, it matches anything the routes above didn't */}
                  <Route path="*" element={<NotFound />} />
              </Routes>
          </BrowserRouter>
      </AuthProvider>
  )
}

export default App
