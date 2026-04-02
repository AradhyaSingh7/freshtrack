import { useState, useEffect } from 'react';
import api from '../api/axios';

const REASONS = [
  'SPOILAGE', 'OVERPREPARATION', 'DROPPED', 'WRONG_ORDER', 'OTHER'
];

export default function LogWaste() {
  const [ingredients, setIngredients] = useState([]);
  const [form, setForm] = useState({
    ingredientId: '',
    quantity: '',
    reason: '',
    alreadyDeducted: false,
    notes: ''
  });
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    api.get('/ingredients').then(res => setIngredients(res.data));
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      await api.post('/waste', {
        ...form,
        ingredientId: Number(form.ingredientId),
        quantity: Number(form.quantity),
      });
      setSuccess('Waste logged successfully');
      setForm({
        ingredientId: '', quantity: '', reason: '',
        alreadyDeducted: false, notes: ''
      });
    } catch (err) {
      setError(
        err.response?.data?.message || 'Failed to log waste'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-8 max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Log Waste</h1>

      <div className="bg-white rounded-xl border p-6">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Ingredient
            </label>
            <select
              value={form.ingredientId}
              onChange={e => setForm({...form, ingredientId: e.target.value})}
              className="w-full border rounded-lg px-3 py-2 text-sm
                focus:outline-none focus:ring-2 focus:ring-green-500"
              required>
              <option value="">Select ingredient</option>
              {ingredients.map(i => (
                <option key={i.id} value={i.id}>
                  {i.name} ({i.availableQuantity} {i.unit} available)
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Quantity Wasted
            </label>
            <input
              type="number"
              step="0.001"
              min="0.001"
              value={form.quantity}
              onChange={e => setForm({...form, quantity: e.target.value})}
              className="w-full border rounded-lg px-3 py-2 text-sm
                focus:outline-none focus:ring-2 focus:ring-green-500"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Reason
            </label>
            <select
              value={form.reason}
              onChange={e => setForm({...form, reason: e.target.value})}
              className="w-full border rounded-lg px-3 py-2 text-sm
                focus:outline-none focus:ring-2 focus:ring-green-500"
              required>
              <option value="">Select reason</option>
              {REASONS.map(r => (
                <option key={r} value={r}>{r.replace('_', ' ')}</option>
              ))}
            </select>
          </div>

          <div className="flex items-start gap-3 bg-yellow-50
            border border-yellow-200 rounded-lg p-3">
            <input
              type="checkbox"
              id="alreadyDeducted"
              checked={form.alreadyDeducted}
              onChange={e =>
                setForm({...form, alreadyDeducted: e.target.checked})}
              className="mt-0.5"
            />
            <div>
              <label htmlFor="alreadyDeducted"
                className="text-sm font-medium text-yellow-800 cursor-pointer">
                Already deducted from inventory
              </label>
              <p className="text-xs text-yellow-700 mt-0.5">
                Check this if the ingredient was already logged as part of
                a dish preparation — for example, prepped food that was
                accidentally dropped.
              </p>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Notes (optional)
            </label>
            <textarea
              value={form.notes}
              onChange={e => setForm({...form, notes: e.target.value})}
              className="w-full border rounded-lg px-3 py-2 text-sm
                focus:outline-none focus:ring-2 focus:ring-green-500"
              rows={2}
            />
          </div>

          {error && (
            <p className="text-sm text-red-600">{error}</p>
          )}
          {success && (
            <p className="text-sm text-green-600">{success}</p>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-green-700 hover:bg-green-600 text-white
              rounded-lg py-2 text-sm font-medium transition
              disabled:opacity-50">
            {loading ? 'Logging...' : 'Log Waste'}
          </button>
        </form>
      </div>
    </div>
  );
}