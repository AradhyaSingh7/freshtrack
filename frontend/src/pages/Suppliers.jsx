import { useState, useEffect } from 'react';
import api from '../api/axios';

export default function Suppliers() {
  const [suppliers, setSuppliers] = useState([]);
  const [reliability, setReliability] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    name: '', contactPerson: '', phone: '', email: ''
  });
  const [error, setError] = useState('');

  const load = () => {
    api.get('/suppliers').then(res => setSuppliers(res.data));
    api.get('/suppliers/reliability')
      .then(res => setReliability(res.data))
      .catch(() => {});
  };

  useEffect(() => { load(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.post('/suppliers', form);
      setShowForm(false);
      setForm({ name: '', contactPerson: '', phone: '', email: '' });
      load();
    } catch (err) {
      setError('Failed to create supplier');
    }
  };

  return (
    <div className="p-8 max-w-5xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Suppliers</h1>
        <button onClick={() => setShowForm(!showForm)}
          className="bg-green-700 hover:bg-green-600 text-white
            px-4 py-2 rounded-lg text-sm font-medium transition">
          + Add Supplier
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit}
          className="bg-white border rounded-xl p-5 mb-6">
          <div className="grid grid-cols-2 gap-3 mb-3">
            {[
              ['name','Supplier Name', true],
              ['contactPerson','Contact Person', false],
              ['phone','Phone', false],
              ['email','Email', false]
            ].map(([field, placeholder, required]) => (
              <input key={field}
                placeholder={placeholder}
                value={form[field]}
                onChange={e =>
                  setForm({...form, [field]: e.target.value})}
                className="border rounded-lg px-3 py-2 text-sm
                  focus:outline-none focus:ring-2 focus:ring-green-500"
                required={required}
              />
            ))}
          </div>
          {error && <p className="text-sm text-red-600 mb-2">{error}</p>}
          <button type="submit"
            className="bg-green-700 text-white px-4 py-2
              rounded-lg text-sm font-medium">
            Save
          </button>
        </form>
      )}

      {reliability.length > 0 && (
        <div className="bg-white border rounded-xl p-5 mb-6">
          <h2 className="font-semibold text-gray-700 mb-3">
            Reliability Ranking
          </h2>
          <table className="w-full text-sm">
            <thead className="border-b">
              <tr>
                {['Rank','Supplier','Deliveries',
                  'Avg Shelf Life'].map(h => (
                  <th key={h}
                    className="text-left pb-2
                      text-gray-500 font-medium">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {reliability.map(r => (
                <tr key={r.supplierId}
                  className="border-b last:border-0">
                  <td className="py-2 font-bold text-green-700">
                    #{r.freshnessRank}
                  </td>
                  <td className="py-2 font-medium">{r.supplierName}</td>
                  <td className="py-2 text-gray-500">
                    {r.totalDeliveries}
                  </td>
                  <td className="py-2 text-gray-500">
                    {r.avgShelfLifeDays} days
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div className="bg-white rounded-xl border overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              {['Name','Contact','Phone','Email'].map(h => (
                <th key={h}
                  className="text-left px-4 py-3
                    text-gray-600 font-medium">
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {suppliers.map(s => (
              <tr key={s.id}
                className="border-b last:border-0 hover:bg-gray-50">
                <td className="px-4 py-3 font-medium">{s.name}</td>
                <td className="px-4 py-3 text-gray-500">
                  {s.contactPerson || '—'}
                </td>
                <td className="px-4 py-3 text-gray-500">
                  {s.phone || '—'}
                </td>
                <td className="px-4 py-3 text-gray-500">
                  {s.email || '—'}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}