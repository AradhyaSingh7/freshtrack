import { useState } from "react";
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

export default function Login(){
    const [email, setEmail]= useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit= async(e)=>{
        e.preventDefault();
        setError('');
        setLoading(true);
        try{
            const res = await api.post('/auth/login', {email, password});
            login(
                { name: res.data.name, role:res.data.role},
                res.data.token
            );
            navigate('/dashboard');
        } catch(err){
            setError('Invalid email or password');
        } finally{
            setLoading(false);
        }
    };
    return(
        <div className="min-h-screen bg-green-50 flex items-center justify-center">
            <div className="bg-white rounded-xl shadow-md p-8 w-full max-w-sm">
                <h1 className="text-2xl font-bold text-green-800 mb-1">🌿 FreshTrack</h1>
                 <p className="text-sm text-gray-500 mb-6">
                Kitchen Inventory Management
                </p>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Email
                        </label>
                        <input type="email" value={email} onChange={e=> setEmail(e.target.value)}
                        className="w-full border rounded-lg px-3 py-2 text-sm
                            focus:outline-none focus:ring-2 focus:ring-green-500"
                        required/>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Password
                        </label>
                        <input type="password" value={password} onChange={e=> setPassword(e.target.value)}
                        className="w-full border rounded-lg px-3 py-2 text-sm
                            focus:outline-none focus:ring-2 focus:ring-green-500"
                        required/>
                    </div>
                    {error && (
                        <p className="text-sm text-red-600">{error}</p>
                     )}
                     <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-green-700 hover:bg-green-600 text-white
                        rounded-lg py-2 text-sm font-medium transition
                        disabled:opacity-50">
                        {loading ? 'Logging in...' : 'Login'}
                    </button>
                </form>
            </div>
        </div>
    );
}