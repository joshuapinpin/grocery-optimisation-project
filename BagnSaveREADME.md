# BagnSave

A grocery price comparison app that helps users find the best deals across supermarkets.

Built with React + TypeScript (frontend) and Spring Boot + Java (backend).

---

## Prerequisites

Make sure you have the following installed before running the project:

| Tool | Version | Download |
| :--- | :--- | :--- |
| Node.js | LTS | https://nodejs.org |
| JDK | 21 | https://adoptium.net |
| Git | Latest | https://git-scm.com |

> **Windows users:** We recommend setting up WSL2 (Windows Subsystem for Linux) before getting started. It avoids common path and line-ending issues. Install it by running `wsl --install -d Ubuntu` in PowerShell as Administrator.

---

## Getting started

### 1. Clone the repo

```bash
git clone https://gitlab.ecs.vuw.ac.nz/course-work/engr301/2026/project1/team10/BagnSave.git
cd BagnSave
```

### 2. Configure your git identity (first time only)

```bash
git config user.name "Your Name"
git config user.email "your@student.email.ac.nz"
```

---

## Running the frontend

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173 in your browser.

---

## Running the backend

Open the `backend/` folder in IntelliJ IDEA. It will automatically detect the Maven project and download dependencies — give it a minute on first load.

Then run `BackendApplication.java` using the green play button in IntelliJ.

Alternatively, from the terminal:

```bash
cd backend
./mvnw spring-boot:run
```

The backend runs at http://localhost:8080.

---

## Verifying everything works

With both running, open http://localhost:5173 — you should see **"Hello from the backend!"** fetched live from Spring Boot. This confirms the frontend and backend are connected correctly.

---

## Project structure

```
BagnSave/
├── frontend/          # React + TypeScript (Vite)
│   └── src/
│       ├── components/    # reusable UI components
│       ├── pages/         # route-level views
│       ├── api/           # Axios clients
│       ├── hooks/         # React Query hooks
│       └── types/         # TypeScript interfaces (OpenAPI contract)
│
├── backend/           # Spring Boot (Java, Maven)
│   └── src/main/java/com/BagnSave/backend/
│       ├── controller/    # REST endpoints
│       ├── service/       # business logic
│       ├── repository/    # data access (JPA + DuckDB)
│       ├── model/         # entities and DTOs
│       └── config/        # Security, CORS, database config
│
└── README.md
```

---

## Git workflow

We use a feature branch workflow. Do not push directly to `main`.

```bash
# create a branch for your work
git checkout -b feature/your-feature-name

# do your work, then stage and commit
git add .
git commit -m "feat: describe what you did"

# push your branch
git push origin feature/your-feature-name
```

Then open a **merge request** on GitLab and request a review from a teammate. At least one approval is required before merging into `main`.

### Commit message conventions

| Prefix | Use for |
| :--- | :--- |
| `feat:` | new feature |
| `fix:` | bug fix |
| `chore:` | setup, config, dependencies |
| `docs:` | README or documentation changes |
| `refactor:` | code changes with no behaviour change |

---

## Tech stack

| Layer | Technology |
| :--- | :--- |
| Frontend | React, TypeScript, Vite |
| API client | Axios, React Query |
| Backend | Spring Boot, Java |
| Auth | Spring Security (session-based) |
| User database | PostgreSQL |
| Product database | DuckDB |
| Frontend hosting | Vercel |
| Backend hosting | Railway |

---

## Team

| Name | Role |
| :--- | :--- |
| Hunter Fromont | Price optimisation algorithm |
| Jack Scrivener | Repository & database |
| Josh Pinpin | Login & authentication |
| Nick Kho | Shopping list logic |
| Yaqoob Chaudry | UI & UX |
