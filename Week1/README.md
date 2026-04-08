# SEP-2 – Shopping Cart with Localization

A JavaFX desktop shopping cart application backed by a MariaDB database. The UI supports multiple languages (English, Finnish, Swedish, Japanese, Arabic) loaded from the database at runtime.

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java (JDK) | 21+ |
| Maven | 3.9+ |
| Docker & Docker Compose | any recent version |

---

## Option A – Run with Docker Compose (recommended)

Docker Compose starts both the MariaDB database and the application together. The database schema and seed data are applied automatically on first start.

```bash
cd Week1
docker compose up --build
```

> **Note:** The app requires a display (X11/Wayland) to render the JavaFX GUI inside the container. On Windows, use an X server such as [VcXsrv](https://sourceforge.net/projects/vcxsrv/) or [WSLg](https://github.com/microsoft/wslg) and set the `DISPLAY` environment variable before running.

To stop and remove containers:

```bash
docker compose down
```

---

## Option B – Run locally (database via Docker, app via Maven)

### 1. Start the database

```bash
cd Week1
docker compose up db -d
```

This starts a MariaDB 11.5 container on port `3306` and automatically runs `src/main/resources/schema.sql` to create the database, tables, and seed data.

### 2. Configure the database connection (optional)

The app reads connection settings from environment variables and falls back to these defaults:

| Variable | Default |
|----------|---------|
| `DB_URL` | `jdbc:mariadb://localhost:3306/shopping_cart_localization` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | *(empty)* |

The Docker Compose database uses password `root`, so set `DB_PASSWORD` before running:

```bash
# Windows (Command Prompt)
set DB_PASSWORD=root

# Windows (PowerShell)
$env:DB_PASSWORD = "root"

# macOS / Linux
export DB_PASSWORD=root
```

### 3. Run the application

```bash
cd Week1
mvn javafx:run
```

---

## Database schema

The schema is located at `Week1/src/main/resources/schema.sql`. It creates three tables:

- **`cart_records`** – saved cart sessions (total items, cost, language, timestamp)
- **`cart_items`** – individual line items linked to a cart record
- **`localization_strings`** – UI labels for each supported locale (`en_US`, `fi_FI`, `sv_SE`, `ja_JP`, `ar_SA`)

To apply the schema manually against a running MariaDB instance:

```bash
mysql -u root -p shopping_cart_localization < Week1/src/main/resources/schema.sql
```

---

## Running tests

```bash
cd Week1
mvn test
```
A JaCoCo code-coverage report is generated at `Week1/target/site/jacoco/index.html` after the test run.
