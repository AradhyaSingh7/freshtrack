import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';

export default function Batches() {
  const { user } = useAuth();
  const [batches, setBatches] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [ingredients, setIngredients] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    ingredientId: '', supplierId: '', quantity: '',
    costPerUnit: '', expiryDate: '', receivedDate: ''
  });
  const [error, setError] = useState('');

  const load = () =>
    api.get('/batches').then(res => setBatches(res.data));

  useEffect(() => {
    load();
    api.get('/suppliers').then(res => setSuppliers(res.data));
    api.get('/ingredients').then(res => setIngredients(res.data));
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.post('/batches', {
        ...form,
        ingredientId: Number(form.ingredientId),
        supplierId: Number(form.supplierId),
        quantity: Number(form.quantity),
        costPerUnit: Number(form.costPerUnit),
      });
      setShowForm(false);
      setForm({
        ingredientId: '', supplierId: '', quantity: '',
        costPerUnit: '', expiryDate: '', receivedDate: ''
      });
      load();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to record delivery');
    }
  };

  const statusColors = {
    ACTIVE: 'bg-green-100 text-green-700',
    EXPIRING_SOON: 'bg-yellow-100 text-yellow-700',
    EXPIRED: 'bg-red-100 text-red-700',
    DEPLETED: 'bg-gray-100 text-gray-500',
  };

  return (
    <div className="p-8 max-w-6xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Batches</h1>
        {user?.role === 'MANAGER' && (
          <button onClick={() => setShowForm(!showForm)}
            className="bg-green-700 hover:bg-green-600 text-white
              px-4 py-2 rounded-lg text-sm font-medium transition">
            + Record Delivery
          </button>
        )}
      </div>

      {showForm && (
        <form onSubmit={handleSubmit}
          className="bg-white border rounded-xl p-5 mb-6">
          <div className="grid grid-cols-3 gap-3 mb-3">
            <select value={form.ingredientId}
              onChange={e =>
                setForm({...form, ingredientId: e.target.value})}
              className="border rounded-lg px-3 py-2 text-sm
                focus:outline-none focus:ring-2 focus:ring-green-500"
              required>
              <option value="">Select ingredient</option>
              {ingredients.map(i => (
                <option key={i.id} value={i.id}>{i.name}</option>
              ))}
            </select>
            <select value={form.supplierId}
              onChange={e =>
                setForm({...form, supplierId: e.target.value})}
              className="border rounded-lg px-3 py-2 text-sm
                focus:outline-none focus:ring-2 focus:ring-green-500"
              required>
              <option value="">Select supplier</option>
              {suppliers.map(s => (
                <option key={s.id} value={s.id}>{s.name}</option>
              ))}
            </select>
            <input type="number" step="0.001" min="0.001"
              placeholder="Quantity"
              value={form.quantity}
              onChange={e => setForm({...form, quantity: e.target.value})}
              className="border rounded-lg px-3 py-2 text-sm
                focus:outline-none focus:ring-2 focus:ring-green-500"
              required
            />
            <input type="number" step="0.01" min="0.01"
              placeholder="Cost per unit (₹)"
              value={form.costPerUnit}
              onChange={e =>
                setForm({...form, costPerUnit: e.target.value})}
              className="border rounded-lg px-3 py-2 text-sm
                focus:outline-none focus:ring-2 focus:ring-green-500"
              required
            />
            <div>
              <label className="block text-xs text-gray-500 mb-1">
                Expiry Date
              </label>
              <input type="date"
                value={form.expiryDate}
                onChange={e =>
                  setForm({...form, expiryDate: e.target.value})}
                className="w-full border rounded-lg px-3 py-2 text-sm
                  focus:outline-none focus:ring-2 focus:ring-green-500"
                required
              />
            </div>
            <div>
              <label className="block text-xs text-gray-500 mb-1">
                Received Date
              </label>
              <input type="date"
                value={form.receivedDate}
                onChange={e =>
                  setForm({...form, receivedDate: e.target.value})}
                className="w-full border rounded-lg px-3 py-2 text-sm
                  focus:outline-none focus:ring-2 focus:ring-green-500"
                required
              />
            </div>
          </div>
          {error && <p className="text-sm text-red-600 mb-2">{error}</p>}
          <button type="submit"
            className="bg-green-700 text-white px-4 py-2
              rounded-lg text-sm font-medium">
            Save Delivery
          </button>
        </form>
      )}

      <div className="bg-white rounded-xl border overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              {['Ingredient','Supplier','Original',
                'Remaining','Expiry','Status'].map(h => (
                <th key={h}
                  className="text-left px-4 py-3
                    text-gray-600 font-medium">
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {batches.map(b => (
              <tr key={b.id}
                className="border-b last:border-0 hover:bg-gray-50">
                <td className="px-4 py-3 font-medium">
                  {b.ingredientName}
                </td>
                <td className="px-4 py-3 text-gray-500">
                  {b.supplierName}
                </td>
                <td className="px-4 py-3">{b.quantityOriginal}</td>
                <td className="px-4 py-3">{b.quantityRemaining}</td>
                <td className="px-4 py-3">{b.expiryDate}</td>
                <td className="px-4 py-3">
                  <span className={`px-2 py-0.5 rounded-full text-xs
                    font-medium ${statusColors[b.status]}`}>
                    {b.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}