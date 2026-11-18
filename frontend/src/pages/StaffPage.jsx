import { useState } from 'react'
import { createStaff } from '../api/workers'
import { getApiErrorMessage } from '../api/client'

const days = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']
const initialForm = {
  name: '', email: '', password: '', tradeCategory: 'GENERAL', experienceYears: 0,
  shiftStart: '09:00', shiftEnd: '17:00', workingDays: ['MON', 'TUE', 'WED', 'THU', 'FRI'],
  preferredLanguage: 'ENGLISH',
}

export default function StaffPage({ staff, onCreated }) {
  const [form, setForm] = useState(initialForm)
  const [message, setMessage] = useState('')
  const [busy, setBusy] = useState(false)
  const update = (field, value) => setForm((current) => ({ ...current, [field]: value }))
  const toggleDay = (day) => update('workingDays', form.workingDays.includes(day)
    ? form.workingDays.filter((item) => item !== day)
    : [...form.workingDays, day])
  const submit = (event) => {
    event.preventDefault()
    if (!form.workingDays.length) return setMessage('Select at least one working day.')
    setBusy(true)
    setMessage('')
    createStaff({ ...form, available: true, slotDurationMinutes: 60, hasSmartphone: true, managedBySupervisor: false, experienceYears: Number(form.experienceYears), workingDays: form.workingDays.join(',') })
      .then(() => { setForm(initialForm); setMessage('Worker created successfully. Slots were generated from the schedule.'); onCreated() })
      .catch((error) => {
        setMessage(getApiErrorMessage(error))
      })
      .finally(() => setBusy(false))
  }
  return <div className="mx-auto max-w-[1100px] px-4 py-7 sm:px-10 sm:py-9">
    <div className="mb-7"><p className="mb-2 text-[11px] text-slate-400">Operations / Staff</p><h1 className="text-2xl font-bold tracking-tight text-slate-800">Maintenance staff</h1><p className="mt-1 text-sm text-slate-500">Create the worker login and profile together. The worker will receive access using these credentials.</p></div>
    <div className="grid gap-6 xl:grid-cols-[minmax(0,1.4fr)_minmax(280px,.8fr)]">
      <form className="rounded-lg border border-slate-200 bg-white p-5" onSubmit={submit}>
        <h2 className="text-sm font-bold text-slate-800">Add worker</h2>
        <div className="mt-4 grid gap-4 sm:grid-cols-2">
          <label className="text-xs font-semibold text-slate-600">Full name *<input className="mt-2 h-10 w-full rounded-md border border-slate-200 px-3 text-sm" required value={form.name} onChange={(e) => update('name', e.target.value)} /></label>
          <label className="text-xs font-semibold text-slate-600">Email *<input className="mt-2 h-10 w-full rounded-md border border-slate-200 px-3 text-sm" required type="email" value={form.email} onChange={(e) => update('email', e.target.value)} /></label>
          <label className="text-xs font-semibold text-slate-600">Temporary password *<input className="mt-2 h-10 w-full rounded-md border border-slate-200 px-3 text-sm" required minLength="8" maxLength="72" type="password" value={form.password} onChange={(e) => update('password', e.target.value)} /></label>
          <label className="text-xs font-semibold text-slate-600">Trade *<select className="mt-2 h-10 w-full rounded-md border border-slate-200 bg-white px-3 text-sm" required value={form.tradeCategory} onChange={(e) => update('tradeCategory', e.target.value)}>{['ELECTRICAL', 'PLUMBING', 'CARPENTRY', 'CLEANING', 'GENERAL'].map((value) => <option key={value}>{value}</option>)}</select></label>
          <label className="text-xs font-semibold text-slate-600">Experience (years) *<input className="mt-2 h-10 w-full rounded-md border border-slate-200 px-3 text-sm" min="0" required type="number" value={form.experienceYears} onChange={(e) => update('experienceYears', e.target.value)} /></label>
          <label className="text-xs font-semibold text-slate-600">Shift start *<input className="mt-2 h-10 w-full rounded-md border border-slate-200 px-3 text-sm" required type="time" value={form.shiftStart} onChange={(e) => update('shiftStart', e.target.value)} /></label>
          <label className="text-xs font-semibold text-slate-600">Shift end *<input className="mt-2 h-10 w-full rounded-md border border-slate-200 px-3 text-sm" required type="time" value={form.shiftEnd} onChange={(e) => update('shiftEnd', e.target.value)} /></label>
          <label className="text-xs font-semibold text-slate-600">Preferred language *<select className="mt-2 h-10 w-full rounded-md border border-slate-200 bg-white px-3 text-sm" required value={form.preferredLanguage} onChange={(e) => update('preferredLanguage', e.target.value)}><option>ENGLISH</option><option>HINDI</option><option>GUJARATI</option></select></label>
        </div>
        <fieldset className="mt-5"><legend className="text-xs font-semibold text-slate-600">Working days *</legend><div className="mt-2 flex flex-wrap gap-2">{days.map((day) => <label className={`cursor-pointer rounded-md border px-3 py-2 text-xs font-semibold ${form.workingDays.includes(day) ? 'border-violet-500 bg-violet-50 text-violet-700' : 'border-slate-200 text-slate-500'}`} key={day}><input className="sr-only" type="checkbox" checked={form.workingDays.includes(day)} onChange={() => toggleDay(day)} />{day}</label>)}</div></fieldset>
        {message && <p className="mt-4 rounded-md bg-slate-50 p-3 text-sm text-slate-600">{message}</p>}<button className="mt-5 rounded-md bg-violet-600 px-4 py-3 text-sm font-semibold text-white disabled:opacity-50" disabled={busy} type="submit">{busy ? 'Creating worker...' : 'Create worker'}</button>
      </form>
      <section className="rounded-lg border border-slate-200 bg-white p-5"><h2 className="text-sm font-bold text-slate-800">Current workers</h2><p className="mt-1 text-xs text-slate-400">{staff.length} worker profiles</p><div className="mt-4 space-y-3">{staff.map((worker) => <div className="flex items-center gap-3 border-t border-slate-100 pt-3" key={worker.id}><div className="grid size-9 place-items-center rounded-full bg-violet-50 text-xs font-bold text-violet-600">{worker.initials}</div><div className="min-w-0 flex-1"><strong className="block text-sm text-slate-700">{worker.name}</strong><span className="text-xs text-slate-500">{worker.role}</span></div><span className="text-[11px] text-slate-500">{worker.status}</span></div>)}</div></section>
    </div>
  </div>
}
