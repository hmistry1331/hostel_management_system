import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login, signup } from '../api/auth'

export default function AuthPage({ onAuthenticated }) {
  const navigate = useNavigate()
  const [mode, setMode] = useState('login')
  const [form, setForm] = useState({ name: '', email: '', password: '', role: 'STUDENT' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const update = (field, value) => setForm((current) => ({ ...current, [field]: value }))
  const submit = (event) => {
    event.preventDefault()
    setBusy(true)
    setError('')
    const request = mode === 'login' ? login({ email: form.email, password: form.password }) : signup(form)
    request.then(({ data }) => {
      localStorage.setItem('hostel_access_token', data.token)
      localStorage.setItem('hostel_user', JSON.stringify(data))
      onAuthenticated(data)
      navigate(data.role === 'ADMIN' ? '/admin' : data.role === 'WORKER' ? '/worker' : '/student', { replace: true })
    }).catch((requestError) => setError(requestError.response?.data?.detail || requestError.response?.data?.message || 'Authentication failed. Please check your details.')).finally(() => setBusy(false))
  }
  return <main className="grid min-h-screen place-items-center bg-slate-50 p-6"><section className="w-full max-w-md rounded-xl border border-slate-200 bg-white p-7 shadow-sm"><div className="mb-7 text-center"><h1 className="font-['Plus_Jakarta_Sans'] text-2xl font-bold text-slate-800">Service<span className="text-violet-600">Connect</span></h1><p className="mt-2 text-sm text-slate-500">{mode === 'login' ? 'Sign in to your workspace' : 'Create your hostel account'}</p></div><div className="mb-6 grid grid-cols-2 rounded-lg bg-slate-100 p-1"><button className={`rounded-md py-2 text-xs font-semibold ${mode === 'login' ? 'bg-white text-violet-600 shadow-sm' : 'text-slate-500'}`} onClick={() => { setMode('login'); setError('') }} type="button">Login</button><button className={`rounded-md py-2 text-xs font-semibold ${mode === 'signup' ? 'bg-white text-violet-600 shadow-sm' : 'text-slate-500'}`} onClick={() => { setMode('signup'); setError('') }} type="button">Sign up</button></div><form className="space-y-4" onSubmit={submit}>{mode === 'signup' && <label className="block text-xs font-semibold text-slate-600">Full name<input className="mt-2 block h-10 w-full rounded-md border border-slate-200 px-3 text-sm" required value={form.name} onChange={(event) => update('name', event.target.value)} /></label>}<label className="block text-xs font-semibold text-slate-600">Email<input className="mt-2 block h-10 w-full rounded-md border border-slate-200 px-3 text-sm" type="email" required value={form.email} onChange={(event) => update('email', event.target.value)} /></label><label className="block text-xs font-semibold text-slate-600">Password<input className="mt-2 block h-10 w-full rounded-md border border-slate-200 px-3 text-sm" minLength="8" required type="password" value={form.password} onChange={(event) => update('password', event.target.value)} /></label>{mode === 'signup' && <fieldset><legend className="text-xs font-semibold text-slate-600">I am signing up as</legend><div className="mt-2 grid grid-cols-2 gap-3"><label className={`cursor-pointer rounded-md border p-3 text-xs ${form.role === 'STUDENT' ? 'border-violet-500 bg-violet-50 text-violet-700' : 'border-slate-200 text-slate-500'}`}><input className="sr-only" type="radio" name="role" value="STUDENT" checked={form.role === 'STUDENT'} onChange={(event) => update('role', event.target.value)} />Student</label><label className={`cursor-pointer rounded-md border p-3 text-xs ${form.role === 'WORKER' ? 'border-violet-500 bg-violet-50 text-violet-700' : 'border-slate-200 text-slate-500'}`}><input className="sr-only" type="radio" name="role" value="WORKER" checked={form.role === 'WORKER'} onChange={(event) => update('role', event.target.value)} />Worker</label></div></fieldset>}{error && <p className="rounded-md bg-red-50 p-3 text-xs text-red-600">{error}</p>}<button className="w-full rounded-md bg-violet-600 px-4 py-3 text-xs font-semibold text-white disabled:opacity-60" disabled={busy} type="submit">{busy ? 'Please wait...' : mode === 'login' ? 'Login' : 'Create account'}</button></form><p className="mt-5 text-center text-[11px] text-slate-400">Admin accounts are created by the system administrator.</p></section></main>
}
