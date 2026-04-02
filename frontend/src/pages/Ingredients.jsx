import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';

export default function Ingredients() {
  const { user } = useAuth();
  const [ingredients, setIngredients] = useState([]);
  const [form, setForm] = useState({
    name: '', unit: 'KG', minimumThreshold: ''
  });
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState('');

  const load = () =>
    api.get('/ingredients').then(res => setIngredients(res.data));

  useEffect(() => { load(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.post('/ingredients', {
        ...form,
        minimumThreshold: Number(form.minimumThreshold)
      });
      setForm({ name: '', unit: 'KG', minimumThreshold: '' });
      setShowForm(false);
      load();
    } catch (err) {
      setError(err.response?.data?.name || 'Failed to create ingredient');
    }
  };

  return (
    <div className="p-8 max-w-5xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Inventory</h1>
        {user?.role === 'MANAGER' && (
          <button
            onClick={() => setShowForm(!showForm)}
            className="bg-green-700 hover:bg-green-600 text-white
              px-4 py-2 rounded-lg text-sm font-medium transition">
            + Add Ingredient
          </button>
        )}
      </div>

      {showForm && (
        <form onSubmit={handleSubmit}
          className="bg-white border rounded-xl p-5 mb-6 space-y-3">
          <div className="grid grid-cols-3 gap-3">
            <input
              placeholder="Ingredient name"
              value={form.name}
              onChange={e => setForm({...form, name: e.target.value})}
              className="border rounded-lg px-3 py-2 text-sm
                focus:outline-none focus:ring-2 focus:ring-green-500"
              required
            />
            <select
              value={form.unit}
              onChange={e => setForm({...form, unit: e.target.value})}
              className="border rounded-lg px-3 py-2 text-sm
                focus:outline-none focus:ring-2 focus:ring-green-500">
              <option>KG</option>
              <option>LITRE</option>
              <option>PIECE</option>
            </select>
            <input
              type="number" step="0.001" min="0.001"
              placeholder="Min threshold"
              value={form.minimumThreshold}
              onChange={e =>
                setForm({...form, minimumThreshold: e.target.value})}
              className="border rounded-lg px-3 py-2 text-sm
                focus:outline-none focus:ring-2 focus:ring-green-500"
              required
            />
          </div>
          {error && <p className="text-sm text-red-600">{error}</p>}
          <button type="submit"
            className="bg-green-700 text-white px-4 py-2
              rounded-lg text-sm font-medium">
            Save
          </button>
        </form>
      )}

      <div className="bg-white rounded-xl border overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              {['Name','Unit','Min Threshold',
                'Available','Status'].map(h => (
                <th key={h}
                  className="text-left px-4 py-3
                    text-gray-600 font-medium">
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {ingredients.map(i => {
              const isLow = i.availableQuantity < i.minimumThreshold;
              return (
                <tr key={i.id} className="border-b last:border-0
                  hover:bg-gray-50">
                  <td className="px-4 py-3 font-medium">{i.name}</td>
                  <td className="px-4 py-3 text-gray-500">{i.unit}</td>
                  <td className="px-4 py-3 text-gray-500">
                    {i.minimumThreshold}
                  </td>
                  <td className="px-4 py-3">{i.availableQuantity}</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-0.5 rounded-full text-xs
                      font-medium ${isLow
                        ? 'bg-red-100 text-red-700'
                        : 'bg-green-100 text-green-700'}`}>
                      {isLow ? 'Low Stock' : 'OK'}
                    </span>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}