import { Link, useNavigate } from "react-router-dom";
import { useAuth } from '../context/AuthContext';

export default function Navbar(){
    const {user, logout} = useAuth();
    const navigate = useNavigate();

    const handleLogout=()=>{
        logout();
        navigate('/login');
    };

    return(
    <nav className="bg-green-800 text-white px-6 py-3 flex items-center justify-between">
        <div className="flex items-center gap-6">
            <span className="font-bold text-lg tracking-wide">🌿 FreshTrack </span>
            <Link to= "/dashboard" className="text-sm hover:text-green-200 transition">
                Dashboard
            </Link>
            <Link to="/ingredients"
          className="text-sm hover:text-green-200 transition">
            Inventory
        </Link>
        <Link to="/log-usage"
          className="text-sm hover:text-green-200 transition">
            Log Usage
        </Link>
        <Link to="/log-waste"
          className="text-sm hover:text-green-200 transition">
            Log Waste
        </Link>
        { user?.role=='MANAGER' && (
            <>
                <Link to="/suppliers"
              className="text-sm hover:text-green-200 transition">
                Suppliers
            </Link>
            <Link to="/dishes"
              className="text-sm hover:text-green-200 transition">
                Dishes
            </Link>
            <Link to="/waste-analytics"
              className="text-sm hover:text-green-200 transition">
                Analytics
            </Link>
            </>
        )}
        </div>
        <div className="flex items-center gap-4">
            <span className="text-sm text-green-200">
          {user?.name} · {user?.role}
        </span>
        <button onClick={handleLogout}
        className="text-sm bg-green-700 hover:bg-green-600
            px-3 py-1 rounded transition"
        > Logout
        </button>
        </div>
    </nav>
    );
}