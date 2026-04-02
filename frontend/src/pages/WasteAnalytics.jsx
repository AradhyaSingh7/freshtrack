import { useState, useEffect } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip,
  ResponsiveContainer, CartesianGrid } from 'recharts';
import api from '../api/axios';

export default function WasteAnalytics() {
  const [analytics, setAnalytics] = useState([]);
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const end = new Date().toISOString().slice(0, 19);
    const start = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000)
      .toISOString().slice(0, 19);

    Promise.all([
      api.get(`/waste/analytics?start=${start}&end=${end}`),
      api.get('/waste')
    ]).then(([analyticsRes, logsRes]) => {
      setAnalytics(analyticsRes.data);
      setLogs(logsRes.data);
    }).finally(() => setLoading(false));
  }, []);

  const totalCost = analytics.reduce(
    (sum, row) => sum + (row.totalCost || 0), 0
  );

  if (loading) return (
    <div className="p-8 text-gray-500">Loading analytics...</div>
  );

  return (
    <div className="p-8 max-w-5xl mx-auto">
      <h1 className="text-2xl font-bold text-gray-800 mb-2">
        Waste Analytics
      </h1>
      <p className="text-sm text-gray-500 mb-6">Last 30 days</p>

      <div className="grid grid-cols-2 gap-4 mb-8">
        <div className="bg-white border rounded-xl p-5">
          <p className="text-sm text-gray-500">Total Waste Cost</p>
          <p className="text-3xl font-bold text-red-600 mt-1">
            ₹{totalCost.toFixed(2)}
          </p>
        </div>
        <div className="bg-white border rounded-xl p-5">
          <p className="text-sm text-gray-500">Total Waste Events</p>
          <p className="text-3xl font-bold text-gray-800 mt-1">
            {analytics.reduce((s, r) => s + (r.count || 0), 0)}
          </p>
        </div>
      </div>

      <div className="bg-white border rounded-xl p-5 mb-8">
        <h2 className="font-semibold text-gray-700 mb-4">
          Waste Cost by Reason
        </h2>
        <ResponsiveContainer width="100%" height={250}>
          <BarChart data={analytics}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="reason"
              tick={{ fontSize: 12 }} />
            <YAxis tick={{ fontSize: 12 }} />
            <Tooltip
              formatter={(value) => [`₹${value.toFixed(2)}`, 'Cost']}
            />
            <Bar dataKey="totalCost" fill="#4A7C59" radius={[4,4,0,0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>

      <div className="bg-white rounded-xl border overflow-hidden">
        <h2 className="font-semibold text-gray-700 px-5 py-4 border-b">
          Waste Log
        </h2>
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              {['Ingredient','Quantity','Reason',
                'Cost','Already Deducted','Logged By','Date'].map(h => (
                <th key={h}
                  className="text-left px-4 py-3
                    text-gray-600 font-medium">
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {logs.map(log => (
              <tr key={log.id}
                className="border-b last:border-0 hover:bg-gray-50">
                <td className="px-4 py-3 font-medium">
                  {log.ingredientName}
                </td>
                <td className="px-4 py-3">{log.quantityWasted}</td>
                <td className="px-4 py-3 text-gray-500">
                  {log.reason.replace('_', ' ')}
                </td>
                <td className="px-4 py-3 text-red-600">
                  ₹{log.costAtTimeOfWaste?.toFixed(2) ?? '—'}
                </td>
                <td className="px-4 py-3">
                  <span className={`px-2 py-0.5 rounded-full text-xs
                    ${log.alreadyDeducted
                      ? 'bg-gray-100 text-gray-500'
                      : 'bg-orange-100 text-orange-700'}`}>
                    {log.alreadyDeducted ? 'Yes' : 'No'}
                  </span>
                </td>
                <td className="px-4 py-3 text-gray-500">
                  {log.loggedBy}
                </td>
                <td className="px-4 py-3 text-gray-500">
                  {new Date(log.loggedAt).toLocaleDateString()}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}