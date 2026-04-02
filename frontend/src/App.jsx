import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import ProtectedRoute from './components/ProtectedRoute';
import{AuthProvider} from './context/AuthContext'
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Ingredients from './pages/Ingredients';
import Batches from './pages/Batches';
import Suppliers from './pages/Suppliers';
import Dishes from './pages/Dishes';
import LogUsage from './pages/LogUsage';
import LogWaste from './pages/LogWaste';
import WasteAnalytics from './pages/WasteAnalytics';
import UsageHistory from './pages/UsageHistory';
export default function App(){
  return(
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path = "/login" element = {<Login/>}/>

          <Route path = "/" element = {<Navigate to = "/dashboard" replace />}/>

          <Route path="/dashboard" element= {
            <ProtectedRoute>
              <Navbar /><Dashboard />
            </ProtectedRoute>
          }/>

          <Route path="/ingredients" element= {
            <ProtectedRoute>
              <Navbar /><Ingredients />
            </ProtectedRoute>
          }/>

          <Route path="/batches" element= {
            <ProtectedRoute>
              <Navbar /><Batches />
            </ProtectedRoute>
          }/>
          <Route path="/suppliers" element= {
            <ProtectedRoute>
              <Navbar /><Suppliers />
            </ProtectedRoute>
          }/>
          <Route path="/dishes" element={
            <ProtectedRoute managerOnly>
              <Navbar /><Dishes />
            </ProtectedRoute>
          } />

          <Route path="/log-usage" element={
            <ProtectedRoute>
              <Navbar /><LogUsage />
            </ProtectedRoute>
          } />

          <Route path="/log-waste" element={
            <ProtectedRoute>
              <Navbar /><LogWaste />
            </ProtectedRoute>
          } />

          <Route path="/waste-analytics" element={
            <ProtectedRoute managerOnly>
              <Navbar /><WasteAnalytics />
            </ProtectedRoute>
          } />

          <Route path="/usage-history" element={
            <ProtectedRoute>
              <Navbar /><UsageHistory />
            </ProtectedRoute>
          } />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}