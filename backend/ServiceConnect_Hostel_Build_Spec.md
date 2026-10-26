# ServiceConnect — AI-Powered Hostel Maintenance Management System
### Build Specification & Master Prompt (B.Tech Minor Project, DAU)

> **How to use this file:** Paste it at the start of a conversation with an AI coding assistant (or keep it in the repo root as `SPEC.md`) so every task is done against the same context, stack, rules and scope. Sections 0–2 are the "prompt"; the rest is the specification it refers to.

---

## 0. Role & objective (prompt preamble)

You are a senior full-stack engineer helping two students adapt an existing Spring Boot + React marketplace app into a **hostel maintenance management system** for a university hostel (Dhirubhai Ambani University).

Your job on every task:
1. Work **inside the existing codebase and conventions** (package `com.labourproject.project`, existing entities, DTOs, JWT security, `ddl-auto=validate`).
2. Make **small, incremental, reviewable changes** — one feature/week at a time, as per the roadmap in Section 16.
3. For every schema change, give the **SQL migration** (Hibernate will not create or alter tables).
4. Prefer **simple, explainable solutions** — this is a student project that must be demoed and defended in front of a faculty panel.
5. When something in this spec is ambiguous or conflicts with the code, **say so and ask** rather than guessing.

## 1. Project at a glance

| Item | Detail |
|---|---|
| Name | ServiceConnect — Hostel Maintenance Management System |
| Type | B.Tech Minor Project (BMP), 3–4 month timeline |
| University | Dhirubhai Ambani University |
| Team | Harsh Mistry, Vedant Patel |
| Faculty supervisor | Prof. Purbasha Das |
| One-line summary | Students report room issues digitally, book a preferred visit window, and track repairs in real time; supervisors manage assignment; maintenance staff follow a simple daily schedule and submit proof of work. |

**Core problem (from our student survey):** maintenance staff do not arrive on time, and students are often absent when staff visit — so workers visit empty/locked rooms and issues stay unresolved.

**Primary value:** coordinated visit windows + a clear audit trail (who, when, proof) + weekend/low-staff self-service.

## 2. Ground rules (must follow)

1. This is a **separate project** from the original ServiceConnect marketplace. Do **not** bring back marketplace concepts: GPS/radius matching, budgets/pricing, quotes/bidding, worker verification-for-hire, city-level service areas.
2. Focus **only** on hostel-maintenance features. Every suggestion must work in a college hostel context and be student-friendly.
3. Maintenance staff are **not tech-savvy**, may speak only Gujarati/Hindi, and some have only keypad phones. Design the worker experience *from the worker's perspective, not ours*.
4. Business logic lives in **Spring Boot**. Anything outside it (e.g. n8n for notification delivery, if used) handles **side effects only**.
5. Don't silently change existing auth (JWT + Google OAuth2) behaviour.
6. UI wording is hostel-specific: *Customer → Student*, *Worker → Maintenance Staff*, *Post a Service Request → Report a Room Issue*, *Admin → Supervisor*. Internal role strings (`CUSTOMER`, `WORKER`, `ADMIN`) stay unchanged for now to avoid touching JWT claims and security matchers; rename only in UI/labels.

---

## 3. Tech stack (fixed)

### Backend
| Concern | Choice |
|---|---|
| Language / runtime | Java 22 |
| Framework | Spring Boot 4.0 |
| Security | Spring Security, stateless JWT (jjwt), BCrypt, Google OAuth2 (auth-code flow, already built) |
| Persistence | Spring Data JPA + Hibernate, **MySQL**, `spring.jpa.hibernate.ddl-auto=validate` |
| Validation | **`jakarta.validation`** (not `javax`) |
| AI | Spring AI (OpenAI-compatible client) pointed at **Groq**, model `llama-3.3-70b-versatile` |
| Image storage | Cloudinary (5 MB upload limit) |
| Push notifications | Firebase Cloud Messaging (FCM, free) via Firebase Admin SDK |
| SMS (fallback for keypad-phone staff) | SMS gateway — provider **TBD** (keep behind a `SmsService` interface) |
| Scheduled jobs | Spring `@Scheduled` (slot generation, reminders, urgent-escalation checks) |
| Build | Maven |
| Error handling | Existing `GlobalExceptionHandler` + `ApiError` response shape |

### Frontend
| Concern | Choice |
|---|---|
| Library / build | React.js + Vite |
| Styling | Tailwind CSS (mobile-first — students mostly use phones) |
| State | Zustand |
| Routing | React Router *(suggested)* |
| Push | Firebase JS SDK + service worker (FCM web push) |
| Offline / installable | PWA (service worker + cache; e.g. `vite-plugin-pwa`) |
| i18n | Language toggle: English / Gujarati / Hindi *(e.g. `react-i18next`)* |

### Deployment
| Layer | Host |
|---|---|
| Frontend | Vercel |
| Backend | Render |
| Database | Railway (MySQL) |
| Source | GitHub (README must document hostel features) |

---

## 4. Users & roles

| UI label | Role string | Typical user | Main capabilities |
|---|---|---|---|
| Student | `CUSTOMER` | Hostel resident | Report issue (room / common area), attach before-photo, pick preferred visit window, cancel/reschedule, confirm or reject completion, rate, upvote common-area issues |
| Maintenance Staff | `WORKER` | Electrician, plumber, carpenter, cleaner | See today's schedule (room + block + icon), mark in-progress, upload after-photo + student ID photo, mark Solved / Not Solved (with reason) |
| Supervisor | `ADMIN` | Hostel supervisor | See all complaints (sorted by block/floor), assign/reassign staff, manage worker schedules, act on behalf of staff without smartphones, authorize locked-room entry, view analytics, print daily sheets |

**Role-model note (open):** the hostel supervisor mapped onto the existing `ADMIN` role (no new role). The supervisor reported that assignment is actually done by the college **maintenance team**; who exactly holds the "assign" permission is to be finalized (see Section 18). Build the assign endpoint behind `hasRole("ADMIN")` for now so it can be re-pointed easily.

**Staff without smartphones:** such workers are registered **in person** by the supervisor, who manages their assignments and status updates on their behalf (`managed_by_supervisor = true`; no login required).

---

## 5. Insights from the hostel supervisor survey (design inputs)

1. **Scheduling is shift-based.** Complaints can be raised any time, but staff handle them within their own working hours (e.g. 10 AM–7 PM) — not on demand. → Time slots are a **preferred window inside the worker's shift**, not a guaranteed exact appointment.
2. **Proof of completion** = after-photo of the work **plus a photo of the student's ID card**. (Not an on-screen signature.)
3. **Workers fear being accused of theft** when entering rooms. → Build an audit trail (entry log + timestamped photos).
4. **Locked-room + urgent flow (already practiced today):** if the room is locked, the student is absent and the job is urgent, the supervisor (who holds the master keys) unlocks the room and stays present while the worker completes the job; the worker re-locks the room and returns the key.
5. **"Not solved" needs a reason**, notably **"room was locked"**.
6. **Workers need a UI in their own language** and must not be forced into complex flows.
7. **Weekend gap:** staff are absent on weekends; the system should let the maintenance side handle/queue issues without full staff present.
8. The supervisor's mental model of status is just "pending until done"; we **keep our own** detailed status flow (Section 8).

---

## 6. Feature scope

### 6.1 Keep (already built — do not break)
- JWT authentication + Google OAuth2 (with `PENDING_PROFILE` completion step)
- Role-based access (Student / Worker / Supervisor)
- Complaint registration with photos (Cloudinary)
- Status tracking
- Reviews & ratings
- Admin/Supervisor dashboard
- Before / after photo upload
- Global exception handling

### 6.2 Remove (marketplace leftovers)
- GPS matching: `customer_latitude`, `customer_longitude`, `customer_city`, Haversine native query `findWorkerJobFeedByRadius`, worker `home_latitude/home_longitude/home_city/service_area`, `GujaratLocationCatalog`
- Budget: `budget_amount` and its DTO/UI fields (hostel services are free)
- Quote/bidding system: `/api/quotes/**`, `QUOTE_RECEIVED` status → replaced by **direct assignment**
- Worker self-signup/verification-for-hire flow → replaced by supervisor-managed registration

### 6.3 Modify
| Area | From → To |
|---|---|
| Location | GPS → **Block + Floor + Room Number** (dropdowns) + **Location type** (`ROOM` / `COMMON_AREA`) |
| Assignment | Worker accepts from open feed → **Supervisor assigns** worker directly |
| Status flow | `PENDING → ACCEPTED → QUOTE_RECEIVED → IN_PROGRESS → COMPLETED` → see Section 8 |
| Worker view | Job feed by radius → **Daily schedule** (room, block, issue icon, time window) |
| Complaint photo | Optional → **mandatory before-photo** (with offline exception, Section 13) |
| Completion | Worker marks complete → **after-photo + ID-card photo required**, then student confirms/rejects |
| Terminology | Marketplace → hostel (Section 2, rule 6) |

### 6.4 Add (new for BMP)

**P0 — Core (roadmap Weeks 1–12)**
1. **Time-slot booking** *(most important feature — give it the most time)*
2. Room number + block + floor (replaces GPS)
3. Urgency level (`URGENT` / `NORMAL`)
4. New hostel status flow
5. Worker daily schedule view
6. Push notifications (FCM) + in-app bell with unread count
7. Common-area complaint **upvoting** / duplicate detection
8. Hostel dashboard stats (complaints this month, average resolution time, most common issues)
9. Hostel-specific UI language + mobile-responsive pass

**P1 — Real-world mitigations (decided, build after core)**
10. SMS notification to keypad-phone staff on assignment
11. Printable daily job sheet per worker (from supervisor dashboard)
12. Locked-room **entry-authorization log**
13. "Not Solved" outcome with reason (`ROOM_LOCKED`, etc.)
14. Student confirm/reject after completion
15. Reminders, cancellation window, grace period
16. Gujarati/Hindi language toggle + icon-based worker view
17. Worker leave handling → supervisor reassignment
18. Urgent-job auto-escalation (24 h)
19. PWA offline complaint submission (`PENDING_PHOTO` sub-state)

**P2 — Stretch (only if time remains)**
20. Voice-note on complaint (≈10 s)
21. Strike system for invalid complaints (3 strikes → flag to supervisor)
22. Auto-assignment rules by category (e.g. electrical → fixed worker)
23. Temporary admin delegation when supervisor is away
24. Auto-prompt to cancel open complaints when student changes room

---

## 7. Data model

> Existing tables stay (`users`, `workers`, `job_requests`, reviews). Table name `job_requests` and entity `Job_Requests` are kept to minimise churn. **Every change below needs a SQL migration** (`ddl-auto=validate`).

### 7.1 `job_requests` (modified)
| Column | Notes |
|---|---|
| `id`, `customer_id`, `worker_id` (nullable), `problem_description`, `category`, `status`, `created_at`, `updated_at` | existing |
| `before_image_url`, `after_image_url` | existing |
| `block` (varchar), `floor` (int), `room_number` (varchar) | **new** — replaces `customer_location` |
| `location_type` | **new** — `ROOM` \| `COMMON_AREA` |
| `urgency` | **new** — `URGENT` \| `NORMAL` (default `NORMAL`) |
| `contact_number` | **new** — worker can call before arriving |
| `id_card_photo_url` | **new** — captured at completion; access restricted, tied to this record only |
| `not_solved_reason` | **new** — enum, nullable (`ROOM_LOCKED`, `STUDENT_ABSENT`, `PARTS_UNAVAILABLE`, `OTHER`) |
| `student_confirmation` | **new** — `PENDING` \| `CONFIRMED` \| `REJECTED`; `rejection_reason` text |
| `upvote_count` | **new** — default 0 (common-area duplicates) |
| `scheduled_slot_id` | **new** — FK → `time_slots` (nullable) |
| `completed_at`, `escalated_at` | **new** — for analytics and escalation |
| ~~`customer_latitude`, `customer_longitude`, `customer_city`, `customer_location`, `budget_amount`~~ | **drop** (`budget_amount` is a primitive `double` in the entity — update entity, DTOs, constructor, `toString`) |

### 7.2 `workers` (modified)
Keep: `id`, `user_id`, `trade_category`, `experience_years`, `available`.
Add: `shift_start`, `shift_end`, `working_days`, `has_smartphone` (bool), `managed_by_supervisor` (bool), `preferred_language`.
Drop: `home_latitude`, `home_longitude`, `home_city`, `service_area`, `verification_status` (staff are registered in person).

### 7.3 New tables
```
time_slots(id, worker_id, slot_date, start_time, end_time, is_booked, job_id)
  UNIQUE(worker_id, slot_date, start_time)

worker_schedules(id, worker_id, day_of_week, shift_start, shift_end, slot_duration_minutes)

worker_leaves(id, worker_id, from_date, to_date, reason)

complaint_upvotes(id, job_id, user_id, created_at)  UNIQUE(job_id, user_id)

room_entry_logs(id, job_id, worker_id, authorized_by_user_id, reason, entered_at, exited_at, keys_returned_at)

device_tokens(id, user_id, fcm_token, platform, created_at)

notifications(id, user_id, type, title, body, job_id, is_read, created_at)
```
Optional on `users`: `preferred_language`, `invalid_complaint_count` (strike system, P2).

### 7.4 Hostel location catalog
Replace `GujaratLocationCatalog` with a `HostelLocationCatalog` (blocks → floors → rooms) that feeds the dropdowns and validates submitted values.

---

## 8. Status workflow

```
(PENDING_PHOTO) → SUBMITTED → ASSIGNED → SCHEDULED → IN_PROGRESS → COMPLETED
                                                 ↘ NOT_SOLVED (reason) → (re-schedule → SCHEDULED)
                         any open state → CANCELLED
```

| Status | Meaning | Set by |
|---|---|---|
| `PENDING_PHOTO` | Submitted offline without photo; **not assignable** until a photo is attached | System |
| `SUBMITTED` | Complaint received | Student |
| `ASSIGNED` | Supervisor picked a worker | Supervisor |
| `SCHEDULED` | Student picked a preferred window; slot booked | Student |
| `IN_PROGRESS` | Worker started | Worker / Supervisor on behalf |
| `COMPLETED` | After-photo + ID photo uploaded, marked solved | Worker / Supervisor on behalf |
| `NOT_SOLVED` | Visit happened but not fixed; reason mandatory | Worker / Supervisor on behalf |
| `CANCELLED` | Student/supervisor cancelled | Student / Supervisor |

Implementation notes:
- Replace string statuses with a `JobStatus` **enum**; centralise allowed transitions in one place and reject illegal ones.
- Update every status check in the service layer and the frontend badges (new names + colors).
- **Cannot** move to `COMPLETED` without an after-photo and ID photo.
- After `COMPLETED`, notify the student to confirm or reject (with reason). Rejection reopens the job to the supervisor.

---

## 9. Core flows

### A. Report an issue (Student)
1. Pick **Location type** (My Room / Common Area) → Block → Floor → Room (not needed for common areas; use area name).
2. Choose category (manual, or **AI classify** suggestion via `/api/ai/classify`; AI may also suggest urgency).
3. Describe issue; **upload before-photo (mandatory)**; add contact number; select urgency.
4. System checks for **similar open complaints** (same block/floor/category/common area) and offers **upvote instead of duplicate**.
5. Submit → status `SUBMITTED` → supervisor notified.

### B. Assign & schedule
1. Supervisor sees complaints sorted by **block → floor**, urgent first; assigns a worker.
2. Student is notified and sees **available windows inside that worker's shift** (next N days).
3. Student picks a window → `bookSlot()` → `SCHEDULED`; confirmation page ("Worker coming Tuesday 2 PM", add-to-calendar).
4. **Double-booking protection (decided):** atomic conditional update `UPDATE time_slots SET is_booked = true, job_id = :jobId WHERE id = :slotId AND is_booked = false` (`@Modifying`, check affected rows == 1) **plus** unique DB constraint on `(worker_id, slot_date, start_time)`.
5. Student may cancel/reschedule until **2 hours before**; later cancellations set a penalty flag.

### C. Visit & completion
1. Worker (or supervisor on their behalf) sees today's schedule: *Room 204, Block B — 🔌 Electrical — 10:00–11:00*.
2. Worker may call the student (contact number shown). Grace period: **wait 10 minutes**.
3. If student/room unavailable → mark `NOT_SOLVED` with reason (e.g. `ROOM_LOCKED`) → job returns for re-scheduling.
4. If fixed → upload **after-photo + student ID-card photo** → `COMPLETED`.
5. Student gets "Please confirm the issue is resolved" → confirm / reject with reason → rating prompt.

### D. Locked room + urgent job (mirrors today's real process)
1. Worker arrives; room locked; student absent; job is `URGENT`.
2. Worker requests entry from the supervisor (in-app request or phone).
3. Supervisor opens the room with the master key and **stays present**.
4. System records a `room_entry_logs` row (who authorized, when entered, when exited, when keys returned).
5. Worker completes job, re-locks, returns key; completion proceeds as in flow C.
*Purpose: protects workers from theft accusations and gives the supervisor an audit trail.*

### E. Common-area complaints
Grouped separately from room complaints; upvotes raise priority; supervisor handles them as one ticket rather than N duplicates.

---

## 10. API surface (REST, prefix `/api`)

> Keep existing patterns: controller → service → repository, DTOs with `@Valid`, `Authentication` for identity, errors via `ApiError`.

**Auth** — `/api/auth/**` (existing, public)

**Jobs (complaints)**
| Method & path | Role | Purpose |
|---|---|---|
| `POST /jobs/create` | CUSTOMER | Create complaint (location, urgency, contact, before-photo) |
| `GET /jobs/customer/{customerId}` | CUSTOMER | My complaints |
| `GET /jobs/similar?block=&floor=&category=&locationType=` | CUSTOMER | Duplicate check |
| `POST /jobs/{id}/upvote` | CUSTOMER | Upvote common-area complaint |
| `PUT /jobs/assign/{id}` (`workerId`) | ADMIN | Assign worker *(replaces `/accept`)* |
| `PUT /jobs/status/{id}` | WORKER / ADMIN | Move status; body carries `notSolvedReason` when applicable |
| `POST /jobs/{id}/complete` | WORKER / ADMIN | After-photo + ID photo → `COMPLETED` |
| `PUT /jobs/{id}/confirm` · `/reject` | CUSTOMER | Student confirmation |
| `PUT /jobs/{id}/cancel` | CUSTOMER / ADMIN | Cancel |
| `GET /jobs/my-assigned` | WORKER | Assigned jobs |
| `GET /jobs/worker/schedule/today` | WORKER | Today's schedule |

**Time slots**
| Method & path | Role | Purpose |
|---|---|---|
| `GET /slots/available?workerId=&date=` | CUSTOMER | Available windows |
| `POST /slots/book` | CUSTOMER | Book slot for a job |
| `PUT /slots/{id}/cancel` | CUSTOMER / ADMIN | Cancel & free slot |

**Admin / Supervisor**
| Method & path | Purpose |
|---|---|
| `POST/PUT /admin/workers` | Register/edit staff in person (incl. no-smartphone) |
| `POST/PUT /admin/schedules` | Set worker shifts / days / slot length |
| `POST /admin/leaves` | Mark leave → flag open jobs for reassignment |
| `GET /admin/schedule/today` | Today's schedule for all workers |
| `GET /admin/schedule/today/print` | Printable daily job sheet |
| `POST /admin/jobs/{id}/entry-log` | Record locked-room authorization |
| `GET /admin/analytics/summary` | Monthly totals, avg resolution time, top issues |

**Notifications**
`POST /notifications/token` (register FCM token) · `GET /notifications` · `PUT /notifications/{id}/read`

**AI** — `POST /ai/classify` (category + suggested urgency)

**Reviews / images** — existing endpoints retained (`/reviews/**`, `/images/**`).

**Security config:** remove `/api/quotes/**` matchers; add matchers for the new endpoints using the existing `hasRole(...)` style; keep CORS allow-list driven by `app.cors.allowed-origins`.

---

## 11. Frontend pages & components

**Student**
- Report a Room Issue (location dropdowns, category, urgency, photo, contact, similar-issue check)
- My Complaints (status badges, timeline)
- Slot picker (calendar-style, windows within worker shift) + confirmation page (add to calendar)
- Confirm / reject completion, rating
- Notification bell (unread count + dropdown)

**Maintenance Staff**
- **Today's Schedule** — large cards: room + block, **issue icon** (🔌 Electrical · 🔧 Plumbing · 🪑 Carpentry · 🧹 Cleaning), time window, call-student button
- Complete-job screen — after-photo + ID-photo capture, Solved / Not Solved (with reason, incl. *Room was locked*)
- Language toggle (Gujarati / Hindi / English); minimal text, big buttons

**Supervisor**
- Complaint list sorted by block/floor, urgent highlighted, upvote counts
- Assign worker dialog; reassignment on leave
- Worker schedule management; today's schedule for all workers; **print daily sheet**
- Act-on-behalf controls for staff without smartphones
- Entry-authorization log screen
- Dashboard: complaints this month, average resolution time, most common issues, per-block stats

**Cross-cutting**
- Mobile-first, tested on phones; PWA installable; skeleton/offline states
- Status badges with new names/colors; Zustand stores for auth, jobs, slots, notifications

---

## 12. Notifications

**Channels:** FCM push (web) + in-app bell for students/supervisors; **SMS** for staff on keypad phones. Put delivery behind a `NotificationService` interface (and `SmsService`) so the delivery mechanism (direct vs. n8n webhook) can change without touching business logic.

**Triggers**
| Event | Recipient |
|---|---|
| New complaint submitted | Supervisor |
| Complaint assigned | Student; Worker (push or **SMS**: "Room 204, Block B, Electrical, student available 2 PM") |
| Slot booked / rescheduled / cancelled | Worker, Student, Supervisor |
| Day-before visit & **1 hour before** slot | Student ("Worker coming at 2 PM — available?") and Worker |
| Marked completed | Student (confirm / reject) |
| Marked Not Solved | Student, Supervisor |
| Worker reassigned | Student, new Worker |
| `URGENT` job not completed in **24 h** | Supervisor (escalation) |

---

## 13. Real-world scenarios & mitigations

| # | Scenario | Mitigation | Priority |
|---|---|---|---|
| 1 | Staff has only a keypad phone | Supervisor acts as middleman (shows daily list, updates status on behalf) · **SMS** on assignment · **printed daily sheet** | P1 |
| 2 | Hostel Wi-Fi slow/down | PWA with cached data · offline complaint form that syncs later · paper register as emergency backup only | P1 |
| 3 | Student booked slot but has an emergency lecture | Reminder 1 h before · free cancel/reschedule until 2 h before (then penalty flag) · worker waits 10 min and can call student | P1 |
| 4 | Worker marks complete without fixing | Mandatory after-photo + ID photo · student confirm/reject · ratings; low ratings flagged to supervisor | P0/P1 |
| 5 | 10 students report the same corridor light | Similar-complaint check + **upvote** · "My Room" vs "Common Area" split | P0 |
| 6 | Staff speak only Gujarati/Hindi | Language toggle · icon-based worker view · (stretch) voice notes | P1 |
| 7 | Fake/outdated complaint wastes a visit | Mandatory before-photo · (stretch) 3-strike flag | P0 / P2 |
| 8 | Worker on leave, jobs stuck | Supervisor reassignment + notification · urgent auto-escalation at 24 h | P1 |
| 9 | Student moved rooms / went home | Contact-number field (worker calls first) · (stretch) prompt to cancel open complaints on room change | P0 / P2 |
| 10 | Supervisor on leave, complaints pile up at `SUBMITTED` | (stretch) temporary admin delegation · auto-assignment rules by category | P2 |
| 11 | Workers fear theft accusations in rooms | Locked-room flow with supervisor present + **entry log** + timestamped photos | P1 |
| 12 | Staff absent on weekends | Complaints queue with urgency; supervisor/maintenance side can triage and act via the app | P1 |
| 13 | Offline submission without photo | **`PENDING_PHOTO`** sub-state: text submits offline, photo attached later; ticket is not assignable until a photo exists (keeps anti-fraud intent) | P1 |

---

## 14. Non-functional requirements

- **Security:** BCrypt passwords; JWT stateless; role checks on every endpoint; students may only read/modify their own complaints; ID-card photos accessible only to the complaint's student, assigned worker and supervisor; never log secrets/tokens.
- **Privacy:** store ID-card photo only against the complaint record; state a retention approach in the report.
- **Accessibility:** large touch targets, icon + text, minimal reading for staff; Gujarati/Hindi strings for the worker interface.
- **Performance:** paginate lists; index `job_requests(status, category, created_at)` and `time_slots(worker_id, slot_date, is_booked)`.
- **Reliability:** transactional slot booking; idempotent notification sending; scheduled jobs safe to re-run.
- **Mobile:** every page usable at ~380 px width.
- **Auditability:** status changes and room entries timestamped with actor.

---

## 15. Configuration & deployment

**Environment variables** (loaded via `.env` / hosting dashboard; never commit):
`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION_MS`, `GROQ_API_KEY`, `FRONTEND_URL` (CORS), `PORT`, `GOOGLE_OAUTH_CLIENT_ID`, `GOOGLE_OAUTH_CLIENT_SECRET`, `GOOGLE_OAUTH_REDIRECT_URI`, `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`, plus new: Firebase service-account credentials and the SMS provider key.

**Frontend env:** API base URL, Firebase web config, VAPID key.

**Deploy:** push to GitHub → Vercel (frontend) · Render (backend) · Railway (MySQL). Run SQL migrations on Railway **before** deploying backend changes (because of `ddl-auto=validate`).

---

## 16. Roadmap (≈16 weeks)

| Weeks | Focus | Outcome |
|---|---|---|
| 1 | Adapt existing features: remove GPS, remove budget, remove quotes → direct assignment, add urgency | Basic hostel flow runs |
| 2 | Room/Block/Floor fields, admin list sorted by block/floor, worker view without GPS | Location model done |
| 3 | `JobStatus` enum + new status flow, service checks, frontend badges | Hostel statuses live |
| 4 | End-to-end testing + bug fixes + field-name updates | **Demo-able base version** |
| 5 | Time-slot backend (table, entity, repo, service, endpoints, atomic booking) | Slot API |
| 6 | Admin worker schedules, assign → slot picking, today's schedule dashboard | Supervisor tooling |
| 7 | Student slot-picker UI, confirmation page, worker daily schedule | Slot UX |
| 8 | Conflict/cancellation/rebooking tests + fixes | **Biggest problem solved** |
| 9 | FCM backend + notification triggers | Push backend |
| 10 | FCM frontend + bell/unread/dropdown | Push frontend |
| 11 | Hostel language & branding, dashboard stats, mobile responsiveness | Polish |
| 12 | Full 4-role end-to-end test, final bug fixes, README | **Safety buffer** |
| 13–14 | Buffer: finish anything delayed (also place P1 real-world mitigations here) | — |
| 15 | BMP report + presentation (15–20 slides) | Report ready |
| 16 | Demo practice, final deploy, viva prep | Present |

---

## 17. Known issues in the existing code (fix while touching these files)

1. `JobRequestController` imports `javax.validation.Valid` — with Spring Boot 4 / `jakarta.persistence` elsewhere this should be **`jakarta.validation.Valid`**.
2. `Job_Requests` all-args constructor assigns `beforeImageUrl`/`afterImageUrl` to themselves (they are not constructor parameters) — fix or remove the constructor.
3. `GlobalExceptionHandler` maps **every** `RuntimeException` to HTTP 400, which hides genuine server errors; introduce specific exceptions (e.g. invalid state transition, slot already booked → 409).
4. `UserServiceImpl` still depends on `GujaratLocationCatalog` and worker GPS coordinates — remove with the worker model change.
5. `SecurityConfig` still lists `/api/quotes/**` and radius-feed endpoints — remove.
6. `findWorkerJobFeedByRadius` native Haversine query — delete.
7. `ddl-auto=validate` means schema and entities must match exactly — ship each change with its SQL script.

---

## 18. Open items

- Who holds the **assign** permission in practice (hostel supervisor vs. maintenance team/head) → decides whether a separate role/scope is needed beyond `ADMIN`.
- SMS gateway provider and cost for staff notifications.
- Whether FCM is called directly from Spring Boot or routed through n8n (keep behind `NotificationService` either way).
- Slot length (e.g. 30/60 min) and how many days ahead students can book.
- Retention period for ID-card photos.
- Final Gujarati/Hindi string translations (review with actual staff).

---

## 19. How the AI assistant should respond

- Restate the task in one sentence, then list the **files to change/create**.
- Provide **complete code** for changed files (or clear diffs), following existing package/naming conventions.
- Include the **SQL migration** for any schema change.
- Include a **short test checklist** (manual steps or JUnit) for the feature.
- Flag anything that conflicts with Sections 2, 8 or 17, and list assumptions explicitly.
- Keep explanations short and student-friendly; mention trade-offs worth noting in the BMP report.
