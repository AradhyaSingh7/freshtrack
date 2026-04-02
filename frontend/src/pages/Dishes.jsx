import { useState, useEffect } from 'react';
import api from '../api/axios';

export default function Dishes() {
  const [dishes, setDishes] = useState([]);
  const [ingredients, setIngredients] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    name: '', description: '', ingredients: []
  });
  const [error, setError] = useState('');

  const load = () =>
    api.get('/dishes').then(res => setDishes(res.data));

  useEffect(() => {
    load();
    api.get('/ingredients').then(res => setIngredients(res.data));
  }, []);

  const addIngredientRow = () => {
    setForm({...form,
      ingredients: [
        ...form.ingredients,
        { ingredientId: '', quantityRequired: '' }
      ]
    });
  };

  const updateIngredientRow = (index, field, value) => {
    const updated = [...form.ingredients];
    updated[index] = { ...updated[index], [field]: value };
    setForm({...form, ingredients: updated});
  };

  const removeIngredientRow = (index) => {
    setForm({...form,
      ingredients: form.ingredients.filter((_, i) => i !== index)
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.post('/dishes', {
        ...form,
        ingredients: form.ingredients.map(i => ({
          ingredientId: Number(i.ingredientId),
          quantityRequired: Number(i.quantityRequired)
        }))
      });
      setShowForm(false);
      setForm({ name: '', description: '', ingredients: [] });
      load();
    } catch (err) {
      setError('Failed to create dish');
    }
  };

  return (
    <div className="p-8 max-w-5xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Dishes</h1>
        <button onClick={() => setShowForm(!showForm)}
          className="bg-green-700 hover:bg-green-600 text-white
            px-4 py-2 rounded-lg text-sm font-medium transition">
          + Add Dish
        </button>
      </div>

      {showForm && (
        <div className="bg-white border rounded-xl p-5 mb-6">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-3">
              <input placeholder="Dish name"
                value={form.name}
                onChange={e => setForm({...form, name: e.target.value})}
                className="border rounded-lg px-3 py-2 text-sm
                  focus:outline-none focus:ring-2 focus:ring-green-500"
                required
              />
              <input placeholder="Description (optional)"
                value={form.description}
                onChange={e =>
                  setForm({...form, description: e.target.value})}
                className="border rounded-lg px-3 py-2 text-sm
                  focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>

            <div>
              <div className="flex justify-between items-center mb-2">
                <p className="text-sm font-medium text-gray-700">
                  Recipe (per portion)
                </p>
                <button type="button" onClick={addIngredientRow}
                  className="text-sm text-green-700 hover:text-green-600">
                  + Add ingredient
                </button>
              </div>
              {form.ingredients.map((row, i) => (
                <div key={i} className="flex gap-2 mb-2">
                  <select value={row.ingredientId}
                    onChange={e =>
                      updateIngredientRow(i, 'ingredientId', e.target.value)}
                    className="flex-1 border rounded-lg px-3 py-2 text-sm
                      focus:outline-none focus:ring-2 focus:ring-green-500"
                    required>
                    <option value="">Select ingredient</option>
                    {ingredients.map(ing => (
                      <option key={ing.id} value={ing.id}>
                        {ing.name}
                      </option>
                    ))}
                  </select>
                  <input type="number" step="0.001" min="0.001"
                    placeholder="Qty per portion"
                    value={row.quantityRequired}
                    onChange={e =>
                      updateIngredientRow(
                        i, 'quantityRequired', e.target.value)}
                    className="w-36 border rounded-lg px-3 py-2 text-sm
                      focus:outline-none focus:ring-2 focus:ring-green-500"
                    required
                  />
                  <button type="button"
                    onClick={() => removeIngredientRow(i)}
                    className="text-red-400 hover:text-red-600 px-2">
                    ✕
                  </button>
                </div>
              ))}
            </div>

            {error && <p className="text-sm text-red-600">{error}</p>}
            <button type="submit"
              className="bg-green-700 text-white px-4 py-2
                rounded-lg text-sm font-medium">
              Save Dish
            </button>
          </form>
        </div>
      )}

      <div className="space-y-3">
        {dishes.map(dish => (
          <div key={dish.id}
            className="bg-white border rounded-xl p-5">
            <div className="flex justify-between items-start">
              <div>
                <h3 className="font-semibold text-gray-800">{dish.name}</h3>
                {dish.description && (
                  <p className="text-sm text-gray-500 mt-0.5">
                    {dish.description}
                  </p>
                )}
              </div>
              <span className={`px-2 py-0.5 rounded-full text-xs
                font-medium ${dish.isActive
                  ? 'bg-green-100 text-green-700'
                  : 'bg-gray-100 text-gray-500'}`}>
                {dish.isActive ? 'Active' : 'Inactive'}
              </span>
            </div>
            <div className="mt-3 space-y-1">
              {dish.recipe.map((r, i) => (
                <div key={i}
                  className="flex justify-between text-sm text-gray-600">
                  <span>{r.ingredientName}</span>
                  <span>{r.quantityRequired} {r.unit} per portion</span>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
