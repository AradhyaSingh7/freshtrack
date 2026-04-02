# 🌿 FreshTrack — Perishable Inventory & Waste Reduction System

FreshTrack is a full-stack kitchen inventory management system for restaurants. It tracks ingredients at the **batch level** — every supplier delivery creates a new batch with its own quantity and expiry date. When dishes are prepared, the system deducts from the soonest-expiring stock first (FIFO), flags ingredients approaching expiry, suggests dishes that would use up near-expiry stock, and gives managers a real rupee cost on all food waste.

**Live Demo:** [freshtrack-theta.vercel.app](https://freshtrack-theta.vercel.app/)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React, Vite, Tailwind CSS, Recharts |
| Backend | Spring Boot 3.4, Spring Security 6, JWT |
| Database | MySQL with Hibernate / JPA |
| Auth | JWT (stateless, role-based) |
| Deployment | Vercel (frontend), Render (backend), Railway (MySQL) |

---

## Features

### For Staff
- **Log dish preparation** — select a dish and number of portions, preview exactly which batches will be deducted before confirming
- **Log waste** — record wasted ingredients with a reason (spoilage, dropped, over-preparation, etc.) and flag whether the stock was already deducted
- **View inventory** — see current stock levels with low-stock indicators

### For Managers
- **Dashboard** — live overview of expiring batches, low stock alerts, waste cost this week, and dish suggestions
- **Supplier management** — track suppliers and view a reliability ranking based on average shelf life of their deliveries
- **Batch management** — record deliveries, monitor batch statuses (Active / Expiring Soon / Expired / Depleted)
- **Dish & recipe management** — build dishes with per-portion ingredient quantities
- **Waste analytics** — cost breakdown by reason with a bar chart, full filterable waste log

---

## Key Design Decisions

### FIFO Batch Deduction
Ingredients are never tracked as a single quantity. Every delivery creates a separate batch record with its own expiry date and quantity. When stock is consumed, the system always deducts from the earliest-expiring batch first. This minimises waste by ensuring older stock is used before newer stock.

```
Need 4kg Chicken Breast across three batches:

Batch #1 — expires 28 Mar — 1.5kg → take all 1.5kg  → DEPLETED
Batch #2 — expires 2 Apr  — 3.0kg → take 2.5kg      → 0.5kg remaining
Batch #3 — expires 10 Apr — 5.0kg → untouched
```

### Computed Inventory (Never Stored Totals)
Available quantity is never stored as a column. It is always computed by summing `quantity_remaining` across all active batches for an ingredient. This means the number is always accurate — it is structurally impossible for the displayed total to drift out of sync with the underlying batch records.

### Transactional Dish Preparation (`@Transactional`)
When a dish is prepared, all ingredient deductions happen inside a single database transaction. If any ingredient has insufficient stock, the entire operation rolls back — no partial deductions, no corrupted inventory state. This is enforced at the service layer using Spring's `@Transactional`.

```
Prepping 15 portions of Butter Chicken:
  ✓ Deduct Chicken Breast
  ✓ Deduct Fresh Cream
  ✗ Insufficient Tomatoes → entire transaction rolls back
  Result: inventory unchanged, clear error returned to caller
```

### Two-Case Waste Logging
Waste logging distinguishes between two fundamentally different scenarios:

- **Case A — Spoilage in storage:** Ingredient expired before it was prepped. Stock is still in inventory. Logging waste triggers a FIFO deduction.
- **Case B — Post-prep waste:** Ingredient was already deducted when the dish preparation was logged (e.g. a trainee drops prepped onions). Logging waste records the event for reporting purposes only — no deduction triggered.

The `already_deducted` boolean flag on the waste log table controls this behaviour, preventing double-deduction.

### Nightly Expiry Scheduler
A `@Scheduled` job runs at midnight every day. It scans all active batches, updates their status to `EXPIRING_SOON` if they expire within 48 hours, and marks them `EXPIRED` (zeroing quantity) if their expiry date has passed. Expiry is time-driven, not event-driven — it happens whether or not anyone is using the system.

### Supplier Reliability Ranking
Each supplier's reliability is computed on demand using a SQL window function — never stored. It ranks suppliers by the average number of days between delivery date and expiry date across all their batches. A higher number means fresher deliveries.

---

## Database Schema

```
users               — id, name, email, password, role (MANAGER/STAFF)
suppliers           — id, name, contact_person, phone, email
ingredients         — id, name, unit (KG/LITRE/PIECE), minimum_threshold
batches             — id, ingredient_id, supplier_id, quantity_original,
                      quantity_remaining, cost_per_unit, expiry_date,
                      received_date, status (ACTIVE/EXPIRING_SOON/EXPIRED/DEPLETED)
dishes              — id, name, description, is_active
dish_ingredients    — id, dish_id, ingredient_id, quantity_required  ← recipe table
usage_logs          — id, dish_id, portions_prepared, logged_by, logged_at
usage_log_items     — id, usage_log_id, batch_id, ingredient_id, quantity_deducted
waste_logs          — id, ingredient_id, batch_id, quantity_wasted, reason,
                      already_deducted, cost_at_time_of_waste, logged_by, logged_at
```

---

## API Reference

```
POST   /api/auth/login
POST   /api/auth/register

GET    /api/dashboard

GET    /api/ingredients               (MANAGER + STAFF)
POST   /api/ingredients               (MANAGER)
GET    /api/ingredients/low-stock     (MANAGER + STAFF)

GET    /api/batches                   (MANAGER + STAFF)
POST   /api/batches                   (MANAGER)
GET    /api/batches/expiring-soon     (MANAGER + STAFF)
GET    /api/batches/ingredient/{id}   (MANAGER + STAFF)

GET    /api/suppliers                 (MANAGER)
POST   /api/suppliers                 (MANAGER)
GET    /api/suppliers/reliability     (MANAGER)

GET    /api/dishes                    (MANAGER + STAFF)
POST   /api/dishes                    (MANAGER)
GET    /api/dishes/active             (MANAGER + STAFF)
GET    /api/dishes/suggestions        (MANAGER + STAFF)

POST   /api/usage                     (MANAGER + STAFF)
POST   /api/usage/preview             (MANAGER + STAFF)
GET    /api/usage/history             (MANAGER + STAFF)

POST   /api/waste                     (MANAGER + STAFF)
GET    /api/waste                     (MANAGER + STAFF)
GET    /api/waste/analytics           (MANAGER)
```

---

## Running Locally

### Prerequisites
- Java 17
- Maven 3.9+
- MySQL 8+
- Node.js 18+

### Backend

```bash
# Clone the repo
git clone https://github.com/your-username/freshtrack.git
cd freshtrack

# Create the database
mysql -u root -p -e "CREATE DATABASE freshtrack;"

# Configure credentials
# Edit src/main/resources/application.properties:
# spring.datasource.url=jdbc:mysql://localhost:3306/freshtrack
# spring.datasource.username=root
# spring.datasource.password=your_password

# Run
mvn spring-boot:run
```

### Frontend

```bash
cd freshtrack-ui
npm install
npm run dev
```

### Docker (Backend)

```bash
docker build -t freshtrack .
docker run -p 8080:8080 \
  --env-file .env \
  freshtrack
```

### Environment Variables

Create a `.env` file in the root directory:

```env
DB_URL=jdbc:mysql://host.docker.internal:3306/freshtrack
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_secure_random_secret
```
---

## Project Structure

```text
freshtrack/
├── src/main/java/com/freshtrack/
│   ├── auth/               — JWT filter, security config, login/register
│   │   └── jwt/            — JwtService, JwtAuthenticationFilter
│   ├── batch/              — Batch entity, FIFO deduction engine, nightly scheduler
│   ├── common/
│   │   ├── enums/          — Role, Unit, BatchStatus, WasteReason
│   │   └── exception/      — Custom exceptions, global exception handler
│   ├── config/             — DataSeeder (demo data on startup)
│   ├── dashboard/          — Single endpoint aggregating all dashboard data
│   ├── dish/               — Dish entity, recipe management
│   ├── ingredient/         — Ingredient entity, computed inventory
│   ├── supplier/           — Supplier entity, reliability ranking
│   ├── usage/              — Usage logging, preview, audit trail
│   ├── user/               — User entity, repository
│   └── waste/              — Waste logging, analytics
└── src/main/resources/
    └── application.properties

freshtrack-ui/
└── src/
    ├── api/                — Axios instance with JWT interceptors
    ├── context/            — Auth context (user state, login, logout)
    ├── components/         — Navbar, ProtectedRoute, StatCard
    └── pages/              — Dashboard, Ingredients, Batches, Suppliers,
                              Dishes, LogUsage, LogWaste, WasteAnalytics,
                              UsageHistory, Login
```

---

## Advanced Query Design

**Supplier reliability ranking (window function):**
```sql
SELECT s.name,
       COUNT(b.id) AS total_deliveries,
       ROUND(AVG(DATEDIFF(b.expiry_date, b.received_date)), 1) AS avg_shelf_life_days,
       RANK() OVER (
           ORDER BY AVG(DATEDIFF(b.expiry_date, b.received_date)) DESC
       ) AS freshness_rank
FROM suppliers s
JOIN batches b ON b.supplier_id = s.id
GROUP BY s.id, s.name
```

**Dish suggestions ranked by expiring ingredient overlap:**
```sql
SELECT d.name,
       COUNT(DISTINCT di.ingredient_id) AS expiring_ingredients_used,
       GROUP_CONCAT(i.name) AS expiring_ingredients
FROM dishes d
JOIN dish_ingredients di ON di.dish_id = d.id
JOIN ingredients i ON i.id = di.ingredient_id
JOIN batches b ON b.ingredient_id = i.id AND b.status = 'EXPIRING_SOON'
WHERE d.is_active = TRUE
GROUP BY d.id, d.name
ORDER BY expiring_ingredients_used DESC
```

**Computed inventory (never a stored total):**
```sql
SELECT i.name,
       SUM(b.quantity_remaining) AS available_quantity,
       MIN(b.expiry_date)        AS nearest_expiry
FROM ingredients i
LEFT JOIN batches b ON b.ingredient_id = i.id
  AND b.status IN ('ACTIVE', 'EXPIRING_SOON')
GROUP BY i.id, i.name
```
