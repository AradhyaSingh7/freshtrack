import { useState, useEffect } from 'react';
import api from '../api/axios';

export default function LogUsage() {
  const [dishes, setDishes] = useState([]);
  const [dishId, setDishId] = useState('');
  const [portions, setPortions] = useState('');
  const [preview, setPreview] = useState(null);
  const [previewError, setPreviewError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    api.get('/dishes/active').then(res => setDishes(res.data));
  }, []);

  const handlePreview = async () => {
    if (!dishId || !portions) return;
    setPreviewError('');
    setPreview(null);
    try {
      const res = await api.post('/usage/preview',
        { dishId: Number(dishId), portions: Number(portions) });
      setPreview(res.data);
    } catch (err) {
      setPreviewError(
        err.response?.data?.message || 'Insufficient stock for this preparation'
      );
    }
  };

  const handleConfirm = async () => {
    setLoading(true);
    try {
      await api.post('/usage',
        { dishId: Number(dishId), portions: Number(portions) });
      setSuccess(`Successfully logged ${portions} portions`);
      setDishId('');
      setPortions('');
      setPreview(null);
    } catch (err) {
      setPreviewError(
        err.response?.data?.message || 'Failed to log usage'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-8 max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Log Usage</h1>

      <div className="bg-white rounded-xl border p-6 space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Dish
          </label>
          <select
            value={dishId}
            onChange={e => {
              setDishId(e.target.value);
              setPreview(null);
              setPreviewError('');
              setSuccess('');
            }}
            className="w-full border rounded-lg px-3 py-2 text-sm
              focus:outline-none focus:ring-2 focus:ring-green-500">
            <option value="">Select a dish</option>
            {dishes.map(d => (
              <option key={d.id} value={d.id}>{d.name}</option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Portions
          </label>
          <input
            type="number"
            min="1"
            value={portions}
            onChange={e => {
              setPortions(e.target.value);
              setPreview(null);
              setPreviewError('');
              setSuccess('');
            }}
            className="w-full border rounded-lg px-3 py-2 text-sm
              focus:outline-none focus:ring-2 focus:ring-green-500"
            placeholder="How many portions?"
          />
        </div>

        <button
          onClick={handlePreview}
          disabled={!dishId || !portions}
          className="w-full bg-gray-100 hover:bg-gray-200 text-gray-700
            rounded-lg py-2 text-sm font-medium transition
            disabled:opacity-40">
          Preview Deductions
        </button>

        {previewError && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-3">
            <p className="text-sm text-red-700">{previewError}</p>
          </div>
        )}

        {preview && (
          <div className="bg-green-50 border border-green-200 rounded-lg p-4">
            <p className="text-sm font-semibold text-green-800 mb-2">
              Deduction Preview
            </p>
            {preview.deductions.map((d, i) => (
              <div key={i}
                className="flex justify-between text-sm py-1
                  border-b border-green-100 last:border-0">
                <span>{d.ingredientName}</span>
                <span className="text-gray-600">
                  {d.quantityDeducted} · batch expires {d.batchExpiryDate}
                </span>
              </div>
            ))}
            <button
              onClick={handleConfirm}
              disabled={loading}
              className="mt-3 w-full bg-green-700 hover:bg-green-600
                text-white rounded-lg py-2 text-sm font-medium
                transition disabled:opacity-50">
              {loading ? 'Confirming...' : 'Confirm Usage'}
            </button>
          </div>
        )}

        {success && (
          <div className="bg-green-50 border border-green-200
            rounded-lg p-3">
            <p className="text-sm text-green-700">{success}</p>
          </div>
        )}
      </div>
    </div>
  );
}