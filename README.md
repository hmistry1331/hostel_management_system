# Hostel Management System

Hostel Management System is a web application for managing hostel maintenance
requests. Students can report problems, workers can handle assigned jobs, and
administrators can manage requests, staff, schedules, and room access.

The project has two separate applications:

- **Frontend:** React 19 with Vite
- **Backend:** Spring Boot REST API with Java 22
- **Database:** PostgreSQL

## What the system does

### Students

- Create an account and log in.
- Submit a maintenance or service request.
- Select the request category, urgency, location, and description.
- View submitted requests and their status.
- Confirm or reject a completed request.

### Workers

- Log in with a worker account.
- View the jobs assigned to them.
- Start a job, complete it, or report why it could not be solved.
- Upload supporting images when required.
- View the day's schedule.

### Administrators

- View an overview of jobs and available workers.
- View and assign jobs to workers.
- Create staff or worker accounts.
- Start, complete, or mark jobs as not solved.
- Manage room-entry logs, including key return information.
- View today's schedule.

## Architecture

```text
Browser
  |
  | React UI and Axios HTTP requests
  v
Frontend (Vite)
  |
  | REST API with Bearer JWT token
  v
Backend (Spring Boot)
  |
  | Spring Data JPA
v
PostgreSQL database
```

### Frontend structure

The frontend is in [`frontend/`](frontend/).

- `src/App.jsx` defines the application routes and role-based areas.
- `src/pages/` contains the main screens for authentication, students,
  workers, administrators, and profiles.
- `src/components/` contains shared layout and UI components.
- `src/api/` contains Axios calls to the backend.
- `src/store/` contains Zustand stores for authentication, theme, and language.
- `src/data/` contains local display data used by the UI.
- `public/` contains public assets such as icons.

The frontend stores the logged-in user and JWT access token in browser
`localStorage`. Axios automatically adds the token to protected API requests.

Main frontend routes:

| Route | Access | Purpose |
| --- | --- | --- |
| `/` | Public | Login and sign-up |
| `/admin` | Admin | Admin overview |
| `/admin/jobs` | Admin | Manage and assign jobs |
| `/admin/staff` | Admin | Manage staff and workers |
| `/worker` | Worker | View and process assigned jobs |
| `/student` | Student | Create and track requests |
| `/profile` | Logged-in users | View the user profile |

### Backend structure

The backend is in [`backend/`](backend/). It follows a feature-based
Spring Boot structure:

- `auth/` handles sign-up, login, users, roles, JWT creation, and JWT
  authentication.
- `job/` handles job requests, job states, categories, urgency, and completion.
- `worker/` handles workers, staff creation, worker availability, and schedules.
- `slot/` handles time-slot creation and booking.
- `entry/` handles room-entry and key-return logs.
- `location/` provides hostel location data.
- `api/` contains shared API error handling.

Each main feature is split into:

- `controller/` - HTTP endpoints
- `service/` - business rules
- `repository/` - database access
- `domain/` - JPA entities and enums
- `dto/` - request and response objects

## Main API groups

The backend runs under the `/api` path.

| API group | Examples |
| --- | --- |
| Authentication | `POST /api/auth/signup`, `POST /api/auth/login` |
| Jobs | `POST /api/jobs/create`, `GET /api/jobs/student/{customerId}`, `GET /api/jobs/worker` |
| Admin jobs | `GET /api/jobs/admin`, job assignment and admin status actions |
| Workers | `POST /api/admin/workers`, `GET /api/admin/workers/available` |
| Staff | `POST /api/admin/staff` |
| Time slots | `GET /api/slots/available`, `POST /api/slots/{slotId}/book` |
| Locations | `GET /api/locations` |
| Room entry | `/api/admin/jobs/{jobId}/entry-log` |

All API calls except authentication and error handling require a valid JWT.
Access is also checked by user role.

## Requirements

Install the following before running the project:

- Java 22
- Maven, or use the included Maven Wrapper
- Node.js and npm
- PostgreSQL

## Local setup

### 1. Configure PostgreSQL

Create a database or use a hosted PostgreSQL database. A local connection can
use:

```text
Host: localhost
Port: 5432
Database: hostel_management
Username: postgres
Password: <your-password>
```

If your database uses different values, set the backend environment variables
listed below. Hibernate is configured with `ddl-auto=update`, so the required
tables are updated when the backend starts.

### 2. Configure backend environment variables

The backend reads configuration from environment variables. `JWT_SECRET` is
required.

```text
DB_URL=jdbc:postgresql://localhost:5432/hostel_management
DB_USERNAME=postgres
DB_PASSWORD=<your-password>
JWT_SECRET=replace-with-a-long-random-secret
JWT_EXPIRATION_SECONDS=604800
UNIVERSITY_EMAIL_DOMAIN=
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://127.0.0.1:5173
```

Do not commit real passwords, JWT secrets, or production connection strings.

### 3. Configure the frontend

Create `frontend/.env` with the API URL and, if image uploads are enabled,
the Cloudinary upload settings:

```text
VITE_API_URL=http://localhost:8080/api
VITE_CLOUDINARY_CLOUD_NAME=your-cloudinary-cloud-name
VITE_CLOUDINARY_UPLOAD_PRESET=your-upload-preset
```

### 4. Start the backend

From the project root on Windows:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The API will be available at `http://localhost:8080/api`.

### 5. Start the frontend

Open another terminal:

```powershell
cd frontend
npm install
npm run dev
```

Open the URL printed by Vite, normally `http://localhost:5173`.

## Useful commands

### Frontend

```powershell
cd frontend
npm run dev       # Start the development server
npm run build     # Create a production build
npm run preview   # Preview the production build
npm run lint      # Run Oxlint
```

### Backend

```powershell
cd backend
.\mvnw.cmd test                  # Run backend tests
.\mvnw.cmd spring-boot:run       # Start the backend
.\mvnw.cmd clean package         # Build the backend JAR
```

## Authentication flow

1. A user signs up or logs in through the frontend.
2. The backend validates the request and returns user information with a JWT.
3. The frontend stores the user and token in `localStorage`.
4. Axios sends the token as:

   ```text
   Authorization: Bearer <token>
   ```

5. Spring Security validates the token and checks the user's role.
6. If a protected request returns `401`, the frontend logs the user out and
   returns to the login page.

## Testing

Backend tests use Spring Boot's test support and H2 for test database access.
Run them with:

```powershell
cd backend
.\mvnw.cmd test
```

For frontend validation, run:

```powershell
cd frontend
npm run lint
npm run build
```

## Project folders

```text
.
├── backend/
│   ├── src/main/java/       # Spring Boot source code
│   ├── src/main/resources/ # Backend configuration
│   ├── src/test/           # Backend tests
│   └── pom.xml             # Maven dependencies and build
├── frontend/
│   ├── src/                 # React source code
│   ├── public/              # Public frontend assets
│   ├── package.json         # npm scripts and dependencies
│   └── vite.config.js       # Vite configuration
└── README.md
```

## Notes for contributors

- Keep frontend API calls in `frontend/src/api/`.
- Keep business rules in backend services rather than controllers.
- Use DTOs for API input and output instead of exposing entities directly.
- Protect new backend endpoints with the existing Spring Security setup.
- Update this README when setup steps, roles, or major architectural areas
  change.
