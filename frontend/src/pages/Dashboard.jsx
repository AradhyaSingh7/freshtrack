import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import StatCard from '../components/StatCard';
import api from '../api/axios';

export default function Dashboard() {
  const { user } = useAuth();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/dashboard')
      .then(res => setData(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return (
    <div className="p-8 text-gray-500">Loading dashboard...</div>
  );

  return (
    <div className="p-8 max-w-6xl mx-auto">
      <h1 className="text-2xl font-bold text-gray-800 mb-1">
        Good morning, {user?.name}
      </h1>
      <p className="text-sm text-gray-500 mb-6">
        Here's what needs attention today
      </p>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        <StatCard
          label="Total Ingredients"
          value={data.totalIngredients}
          color="green"
        />
        <StatCard
          label="Expiring Soon"
          value={data.expiringSoonCount}
          color={data.expiringSoonCount > 0 ? 'yellow' : 'green'}
          alert={data.expiringSoonCount > 0}
        />
        <StatCard
          label="Low Stock"
          value={data.lowStockCount}
          color={data.lowStockCount > 0 ? 'red' : 'green'}
          alert={data.lowStockCount > 0}
        />
        <StatCard
          label="Waste Cost This Week"
          value={`₹${data.totalWasteCostThisWeek?.toFixed(2) ?? '0.00'}`}
          color="blue"
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl border p-5">
          <h2 className="font-semibold text-gray-700 mb-3">
            ⚠️ Expiring Soon
          </h2>
          {data.expiringSoonBatches.length === 0
            ? <p className="text-sm text-gray-400">
                Nothing expiring in the next 48 hours
              </p>
            : data.expiringSoonBatches.map(batch => (
              <div key={batch.id}
                className="flex justify-between text-sm py-2
                  border-b last:border-0">
                <span className="font-medium">{batch.ingredientName}</span>
                <span className="text-gray-500">
                  {batch.quantityRemaining} · expires {batch.expiryDate}
                </span>
              </div>
            ))
          }
        </div>

        <div className="bg-white rounded-xl border p-5">
          <h2 className="font-semibold text-gray-700 mb-3">
            💡 Cook These Today
          </h2>
          {data.suggestedDishes.length === 0
            ? <p className="text-sm text-gray-400">
                No suggestions right now
              </p>
            : data.suggestedDishes.map(dish => (
              <div key={dish.id}
                className="flex justify-between text-sm py-2
                  border-b last:border-0">
                <span className="font-medium">{dish.name}</span>
                <span className="text-green-600 text-xs">
                  uses expiring stock
                </span>
              </div>
            ))
          }
        </div>
      </div>
    </div>
  );
}