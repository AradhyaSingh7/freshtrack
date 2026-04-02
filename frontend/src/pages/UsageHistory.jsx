import { useState, useEffect } from 'react';
import api from '../api/axios';

export default function UsageHistory() {
  const [logs, setLogs] = useState([]);
  const [expanded, setExpanded] = useState(null);

  useEffect(() => {
    api.get('/usage/history').then(res => setLogs(res.data));
  }, []);

  return (
    <div className="p-8 max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">
        Usage History
      </h1>
      <div className="space-y-3">
        {logs.map(log => (
          <div key={log.id}
            className="bg-white border rounded-xl overflow-hidden">
            <button
              onClick={() =>
                setExpanded(expanded === log.id ? null : log.id)}
              className="w-full flex justify-between items-center
                px-5 py-4 hover:bg-gray-50 transition text-left">
              <div>
                <span className="font-medium">{log.dishName}</span>
                <span className="text-gray-500 text-sm ml-2">
                  {log.portionsPrepared} portions
                </span>
              </div>
              <div className="flex items-center gap-4">
                <span className="text-sm text-gray-400">
                  {log.loggedBy} ·{' '}
                  {new Date(log.loggedAt).toLocaleString()}
                </span>
                <span className="text-gray-400">
                  {expanded === log.id ? '▲' : '▼'}
                </span>
              </div>
            </button>
            {expanded === log.id && (
              <div className="border-t px-5 py-3 bg-gray-50">
                <p className="text-xs font-semibold text-gray-500
                  uppercase mb-2">
                  Batch Deductions
                </p>
                {log.deductions.map((d, i) => (
                  <div key={i}
                    className="flex justify-between text-sm py-1">
                    <span>{d.ingredientName}</span>
                    <span className="text-gray-500">
                      {d.quantityDeducted} · batch #{d.batchId} ·
                      expires {d.batchExpiryDate}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}