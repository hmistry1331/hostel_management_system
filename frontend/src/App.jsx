import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { useEffect, useState } from 'react'
import DashboardLayout from './components/DashboardLayout'
import { getJobsForAdmin } from './api/jobRequests'
import { getAvailableWorkers } from './api/workers'
import JobRequestsPage from './pages/JobRequestsPage'
import OverviewPage from './pages/OverviewPage'
import AuthPage from './pages/AuthPage'
import StudentPage from './pages/StudentPage'
import WorkerPage from './pages/WorkerPage'
import ProfilePage from './pages/ProfilePage'
import StaffPage from './pages/StaffPage'
import { rolePath, useAuthStore } from './store/authStore'
import ProtectedRoute from './components/ProtectedRoute'

const formatLabel = (value) => value?.toLowerCase().replaceAll('_', ' ').replace(/\b\w/g, (letter) => letter.toUpperCase()) ?? 'Unknown'
const mapJob = (job) => ({ ...job, displayId: `JOB-${job.id}`, title: job.problemDescription, category: formatLabel(job.category), urgency: job.urgency, status: job.status, staff: job.workerId ? `Worker #${job.workerId}` : 'Unassigned', location: job.locationType === 'ROOM' ? `${job.block}${job.roomNumber ? `, Room ${job.roomNumber}` : ''}` : job.areaName || job.block, time: job.scheduledSlotDate ? `${job.scheduledSlotDate} ${job.scheduledStartTime?.slice(0, 5)}` : (job.createdAt ? new Date(job.createdAt).toLocaleString() : '—') })

const getWorkerDutyStatus = (worker, now = new Date()) => {
  if (!worker.available || !worker.shiftStart || !worker.shiftEnd) return 'Off duty'

  const workingDays = (worker.workingDays || '').split(',').filter(Boolean)
  const dayNames = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT']
  if (workingDays.length > 0 && !workingDays.includes(dayNames[now.getDay()])) return 'Off duty'

  const toMinutes = (time) => {
    const [hours, minutes] = time.split(':').map(Number)
    return hours * 60 + minutes
  }

  const currentMinutes = now.getHours() * 60 + now.getMinutes()
  const shiftStart = toMinutes(worker.shiftStart)
  const shiftEnd = toMinutes(worker.shiftEnd)
  const isWithinShift = shiftStart <= shiftEnd
    ? currentMinutes >= shiftStart && currentMinutes < shiftEnd
    : currentMinutes >= shiftStart || currentMinutes < shiftEnd

  return isWithinShift ? 'On duty' : 'Off duty'
}

const mapWorker = (worker, now = new Date()) => ({ id: worker.id, initials: worker.name.split(/\s+/).map((part) => part[0]).join('').slice(0, 2).toUpperCase(), name: worker.name, role: formatLabel(worker.tradeCategory), jobs: 0, status: getWorkerDutyStatus(worker, now) })

function DataMessage({ children }) {
  return <div className="mx-auto max-w-[1440px] px-4 py-10 text-center text-sm text-slate-500 sm:px-10">{children}</div>
}

function AdminArea() {
  const [jobs, setJobs] = useState([])
  const [staff, setStaff] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const load = () => Promise.all([getJobsForAdmin(), getAvailableWorkers()]).then(([jobsResponse, workersResponse]) => { setJobs(jobsResponse.data.map(mapJob)); setStaff(workersResponse.data.map(mapWorker)) }).catch(() => setError('Unable to load admin data from the backend.')).finally(() => setLoading(false))
  useEffect(() => {
    load()
    const dutyRefresh = window.setInterval(load, 60 * 1000)
    return () => window.clearInterval(dutyRefresh)
  }, [])
  const content = loading ? <DataMessage>Loading admin data...</DataMessage> : error ? <DataMessage>{error}</DataMessage> : <Routes><Route index element={<OverviewPage jobs={jobs} staff={staff} />} /><Route path="jobs" element={<JobRequestsPage jobs={jobs} staff={staff} onAssigned={load} />} /><Route path="staff" element={<StaffPage staff={staff} onCreated={load} />} /><Route path="*" element={<OverviewPage jobs={jobs} staff={staff} />} /></Routes>
  return <DashboardLayout role="admin">{content}</DashboardLayout>
}

export default function App() {
  const user = useAuthStore((state) => state.user)
  return <BrowserRouter><Routes><Route index element={user ? <Navigate to={rolePath(user.role)} replace /> : <AuthPage />} /><Route path="/admin/*" element={<ProtectedRoute role="ADMIN"><AdminArea /></ProtectedRoute>} /><Route path="/worker" element={<ProtectedRoute role="WORKER"><DashboardLayout role="worker"><WorkerPage /></DashboardLayout></ProtectedRoute>} /><Route path="/student" element={<ProtectedRoute role="STUDENT"><DashboardLayout role="student"><StudentPage /></DashboardLayout></ProtectedRoute>} /><Route path="/profile" element={<ProtectedRoute><DashboardLayout role={user?.role?.toLowerCase()}><ProfilePage /></DashboardLayout></ProtectedRoute>} /><Route path="*" element={<Navigate to="/" replace />} /></Routes></BrowserRouter>
}
