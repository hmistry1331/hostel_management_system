import { useEffect, useState } from 'react'
import Badge from '../components/Badge'
import { getJobsForWorker } from '../api/jobRequests'
import { acceptJobRequest } from '../api/workers'

const mapJob = (job) => ({ ...job, displayId: `JOB-${job.id}`, location: job.locationType === 'ROOM' ? `${job.block}${job.roomNumber ? `, Room ${job.roomNumber}` : ''}` : job.areaName || job.block, category: job.category?.toLowerCase(), urgency: job.urgency?.toLowerCase(), status: job.status?.toLowerCase().replaceAll('_', ' ') })

export default function WorkerPage() {
  const [jobs, setJobs] = useState([])
  const [message, setMessage] = useState('Loading available requests...')
  useEffect(() => { getJobsForWorker().then(({ data }) => { setJobs(data.map(mapJob)); setMessage('') }).catch(() => setMessage('Unable to load available requests.')) }, [])
  const user = JSON.parse(localStorage.getItem('hostel_user') || '{}')
  const accept = (jobId) => acceptJobRequest(jobId, user.workerId).then(({ data }) => setJobs((current) => current.filter((job) => job.id !== data.id))).catch(() => setMessage('This request could not be accepted.'))
  return <div className="mx-auto max-w-[1100px] px-4 py-8 sm:px-10"><p className="text-[11px] text-slate-400">Worker workspace</p><h1 className="mt-2 font-['Plus_Jakarta_Sans'] text-2xl font-bold text-slate-800">Available job requests</h1><p className="mt-1 text-sm text-slate-500">Accept a request to take responsibility for the maintenance work.</p>{message && <p className="mt-8 text-sm text-slate-500">{message}</p>}<div className="mt-6 grid gap-3">{jobs.map((job) => <article className="rounded-lg border border-slate-200 bg-white p-5" key={job.id}><div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center"><div><p className="text-[10px] font-semibold text-violet-600">{job.displayId} · {job.category}</p><h2 className="mt-1 text-sm font-bold text-slate-700">{job.problemDescription}</h2><p className="mt-1 text-xs text-slate-500">{job.location} · {job.urgency}</p></div><div className="flex items-center gap-3"><Badge kind={job.status}>{job.status}</Badge><button className="rounded-md bg-violet-600 px-4 py-2 text-xs font-semibold text-white" onClick={() => accept(job.id)}>Accept request</button></div></div></article>)}</div></div>
}
