import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { useEffect } from 'react'
import DashboardLayout from './components/DashboardLayout'
import { getJobsForAdmin } from './api/jobRequests'
import { getAvailableWorkers } from './api/workers'
import JobRequestsPage from './pages/JobRequestsPage'
import OverviewPage from './pages/OverviewPage'
import RoleSelectionPage from './pages/RoleSelectionPage'
import StudentPage from './pages/StudentPage'
import WorkerPage from './pages/WorkerPage'

const statusLabels = { PENDING_PHOTO: 'Pending', SUBMITTED: 'Pending', ASSIGNED: 'Pending', SCHEDULED: 'Pending', IN_PROGRESS: 'In progress', COMPLETED: 'Completed', NOT_SOLVED: 'Pending', CANCELLED: 'Cancelled' }
const formatLabel = (value) => value?.toLowerCase().replaceAll('_', ' ').replace(/\b\w/g, (letter) => letter.toUpperCase()) ?? 'Unknown'
const mapJob = (job) => ({ ...job, id: `JOB-${job.id}`, title: job.problemDescription, category: formatLabel(job.category), priority: formatLabel(job.urgency), status: statusLabels[job.status] ?? formatLabel(job.status), staff: job.workerId ? `Worker #${job.workerId}` : 'Unassigned', location: job.locationType === 'ROOM' ? `${job.block}${job.roomNumber ? `, Room ${job.roomNumber}` : ''}` : job.areaName || job.block, time: job.createdAt ? new Date(job.createdAt).toLocaleString() : '—' })
const mapWorker = (worker) => ({ initials: worker.name.split(/\s+/).map((part) => part[0]).join('').slice(0, 2).toUpperCase(), name: worker.name, role: formatLabel(worker.tradeCategory), jobs: 0, status: worker.available ? 'On duty' : 'Unavailable' })

function DataMessage({ children }) {
  return <div className="mx-auto max-w-[1440px] px-4 py-10 text-center text-sm text-slate-500 sm:px-10">{children}</div>
}

function AdminArea() {
  const [jobs, setJobs] = useState([])
  const [staff, setStaff] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  useEffect(() => { Promise.all([getJobsForAdmin(), getAvailableWorkers()]).then(([jobsResponse, workersResponse]) => { setJobs(jobsResponse.data.map(mapJob)); setStaff(workersResponse.data.map(mapWorker)) }).catch(() => setError('Unable to load admin data from the backend.')).finally(() => setLoading(false)) }, [])
  const content = loading ? <DataMessage>Loading admin data...</DataMessage> : error ? <DataMessage>{error}</DataMessage> : <Routes><Route index element={<OverviewPage jobs={jobs} staff={staff} />} /><Route path="jobs" element={<JobRequestsPage jobs={jobs} />} /><Route path="*" element={<OverviewPage jobs={jobs} staff={staff} />} /></Routes>
  return <DashboardLayout role="admin">{content}</DashboardLayout>
}

export default function App() {
  return <BrowserRouter><Routes><Route index element={<RoleSelectionPage />} /><Route path="/admin/*" element={<AdminArea />} /><Route path="/worker" element={<DashboardLayout role="worker"><WorkerPage /></DashboardLayout>} /><Route path="/student" element={<DashboardLayout role="student"><StudentPage /></DashboardLayout>} /><Route path="*" element={<Navigate to="/" replace />} /></Routes></BrowserRouter>
}
