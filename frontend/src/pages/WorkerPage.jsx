import { useEffect, useState } from 'react'
import Badge from '../components/Badge'
import { completeJobRequest, getJobsForWorker, notSolvedJobRequest } from '../api/jobRequests'

const icons = { ELECTRICAL: '🔌', PLUMBING: '🔧', CARPENTRY: '🪑', CLEANING: '🧹' }
const uploadImage = async (file, folder) => {
  if (!file) throw new Error('Please select the required photo.')
  if (!import.meta.env.VITE_CLOUDINARY_CLOUD_NAME || !import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET) throw new Error('Photo upload is not configured. Set VITE_CLOUDINARY_CLOUD_NAME and VITE_CLOUDINARY_UPLOAD_PRESET.')
  const body = new FormData()
  body.append('file', file)
  body.append('upload_preset', import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET)
  body.append('folder', `hostel-management/${folder}`)
  const response = await fetch(`https://api.cloudinary.com/v1_1/${import.meta.env.VITE_CLOUDINARY_CLOUD_NAME}/image/upload`, { method: 'POST', body })
  const result = await response.json()
  if (!response.ok || !result.secure_url) throw new Error(result.error?.message || 'Photo upload failed.')
  return result.secure_url
}

export default function WorkerPage() {
  const [jobs, setJobs] = useState([])
  const [message, setMessage] = useState('Loading today’s schedule...')
  const [activeJob, setActiveJob] = useState(null)
  const [afterImage, setAfterImage] = useState(null)
  const [idCardImage, setIdCardImage] = useState(null)
  const loadJobs = () => getJobsForWorker().then(({ data }) => {
    setJobs(data.map((job) => ({
      ...job,
      jobId: job.id,
      startTime: job.scheduledStartTime,
      endTime: job.scheduledEndTime,
    })))
    setMessage('')
  }).catch(() => setMessage('Unable to load assigned jobs.'))
  useEffect(() => { loadJobs() }, [])
  const complete = async () => { try { const afterImageUrl = await uploadImage(afterImage, 'after-images'); const idCardPhotoUrl = await uploadImage(idCardImage, 'id-card-images'); await completeJobRequest(activeJob.jobId, { afterImageUrl, idCardPhotoUrl }); setActiveJob(null); setMessage('Job marked complete.'); loadJobs() } catch (error) { setMessage(error.message || 'Could not complete job.') } }
  const notSolved = (job) => notSolvedJobRequest(job.jobId, { reason: 'OTHER' }).then(() => { setActiveJob(null); setMessage('Job marked not solved.'); return loadJobs() }).catch((error) => setMessage(error.response?.data?.message || 'Could not update job.'))
  const openCompletion = (job) => { setAfterImage(null); setIdCardImage(null); setActiveJob(job) }
  return <div className="mx-auto max-w-[1100px] px-4 py-8 sm:px-10"><p className="text-xs text-slate-400">Worker workspace</p><h1 className="mt-2 text-2xl font-bold text-slate-800">Today’s schedule</h1><p className="mt-1 text-base text-slate-500">Jobs assigned by your supervisor.</p>{message && <p className="mt-8 text-base text-slate-500">{message}</p>}<div className="mt-6 grid gap-4">{jobs.map((job) => <article className="rounded-lg border border-slate-200 bg-white p-5" key={job.jobId}><div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center"><div><p className="text-sm font-semibold text-violet-600">{icons[job.category] || '🛠️'} {job.startTime ? `${job.startTime.slice(0, 5)}–${job.endTime.slice(0, 5)}` : 'Awaiting student time slot'} · {job.category}</p><h2 className="mt-2 text-lg font-bold text-slate-700">{job.block}{job.roomNumber ? `, Room ${job.roomNumber}` : ''}</h2><p className="mt-2 text-base text-slate-500">{job.urgency} · {job.contactNumber || 'No contact number'}</p>{job.contactNumber && <a className="mt-2 inline-block text-base font-semibold text-violet-600" href={`tel:${job.contactNumber}`}>Call student</a>}</div><div className="flex flex-wrap items-center gap-2"><Badge kind={job.status}>{job.status}</Badge>{!['COMPLETED', 'NOT_SOLVED', 'CANCELLED'].includes(job.status) && <><button className="rounded-md bg-emerald-600 px-4 py-3 text-sm font-semibold text-white" onClick={() => openCompletion(job)}>Completed</button><button className="rounded-md border border-orange-200 px-4 py-3 text-sm font-semibold text-orange-700" onClick={() => notSolved(job)}>Not solved</button></>}</div></div></article>)}</div>{activeJob && <div className="fixed inset-0 z-10 grid place-items-center bg-slate-900/40 p-4"><section className="w-full max-w-md rounded-lg bg-white p-6"><h2 className="text-lg font-bold text-slate-800">Complete job</h2><label className="mt-4 block text-sm font-semibold">After photo<input className="mt-2 block w-full" accept="image/*" capture="environment" required type="file" onChange={(e) => setAfterImage(e.target.files[0])} /></label><label className="mt-4 block text-sm font-semibold">Student ID photo<input className="mt-2 block w-full" accept="image/*" capture="environment" required type="file" onChange={(e) => setIdCardImage(e.target.files[0])} /></label><div className="mt-5 flex gap-2"><button className="flex-1 rounded-md border px-3 py-3 text-sm" onClick={() => setActiveJob(null)}>Cancel</button><button className="flex-1 rounded-md bg-emerald-600 px-3 py-3 text-sm font-semibold text-white disabled:opacity-50" disabled={!afterImage || !idCardImage} onClick={complete}>Save completion</button></div></section></div>}</div>
}
