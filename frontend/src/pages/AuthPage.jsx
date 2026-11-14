import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login, signup } from '../api/auth'
import { rolePath, useAuthStore } from '../store/authStore'
import { useLanguage } from '../store/languageStore'

export default function AuthPage() {
  const navigate = useNavigate()
  const [mode, setMode] = useState('login')
  const [form, setForm] = useState({ name: '', email: '', password: '' })
  const setUser = useAuthStore((state) => state.setUser)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const { language, setLanguage, t } = useLanguage()
  const update = (field, value) => setForm((current) => ({ ...current, [field]: value }))
  const submit = (event) => {
    event.preventDefault()
    setBusy(true)
    setError('')
    const request = mode === 'login' ? login({ email: form.email, password: form.password }) : signup({ name: form.name, email: form.email, password: form.password })
    request.then(({ data }) => {
      setUser(data)
      navigate(rolePath(data.role), { replace: true })
    }).catch((requestError) => setError(requestError.response?.data?.detail || requestError.response?.data?.message || requestError.response?.data?.error || 'Authentication failed. Please check your details.')).finally(() => setBusy(false))
  }
  return <main className="grid min-h-screen place-items-center bg-[#dff2f4] p-5 sm:p-8"><section className="relative w-full max-w-md rounded-2xl border border-white/80 bg-[#fffefa] p-7 shadow-2xl shadow-cyan-900/10 sm:p-9"><select className="absolute right-5 top-5 rounded-md border border-slate-200 bg-white px-2 py-1.5 text-[11px] font-semibold text-slate-500" aria-label={t('language')} value={language} onChange={(event) => setLanguage(event.target.value)}><option value="en">{t('english')}</option><option value="gu">{t('gujarati')}</option></select><div className="mb-7 text-center"><h1 className="font-['Plus_Jakarta_Sans'] text-2xl font-bold text-slate-800">Service<span className="text-violet-600">Connect</span></h1><p className="mt-2 text-sm text-slate-500">{mode === 'login' ? t('signInWorkspace') : t('createAccount')}</p></div><div className="mb-6 grid grid-cols-2 rounded-lg bg-slate-100 p-1"><button className={`rounded-md py-2 text-xs font-semibold ${mode === 'login' ? 'bg-white text-violet-600 shadow-sm' : 'text-slate-500'}`} onClick={() => { setMode('login'); setError('') }} type="button">{t('login')}</button><button className={`rounded-md py-2 text-xs font-semibold ${mode === 'signup' ? 'bg-white text-violet-600 shadow-sm' : 'text-slate-500'}`} onClick={() => { setMode('signup'); setError('') }} type="button">{t('signUp')}</button></div><form className="space-y-4" onSubmit={submit}>{mode === 'signup' && <label className="block text-xs font-semibold text-slate-600">{t('fullName')}<input className="mt-2 block h-10 w-full rounded-md border border-slate-200 px-3 text-sm" required value={form.name} onChange={(event) => update('name', event.target.value)} /></label>}<label className="block text-xs font-semibold text-slate-600">{t('email')}<input className="mt-2 block h-10 w-full rounded-md border border-slate-200 px-3 text-sm" type="email" required value={form.email} onChange={(event) => update('email', event.target.value)} /></label><label className="block text-xs font-semibold text-slate-600">{t('password')}<input className="mt-2 block h-10 w-full rounded-md border border-slate-200 px-3 text-sm" minLength="8" maxLength="72" required type="password" value={form.password} onChange={(event) => update('password', event.target.value)} /></label>{mode === 'signup' && <p className="rounded-md bg-violet-50 p-3 text-xs text-violet-700">Student accounts only. Staff accounts are created by a supervisor.</p>}{error && <p className="rounded-md bg-red-50 p-3 text-xs text-red-600">{error}</p>}<button className="w-full rounded-md bg-violet-600 px-4 py-3 text-xs font-semibold text-white disabled:opacity-60" disabled={busy} type="submit">{busy ? t('pleaseWait') : mode === 'login' ? t('login') : t('createAccountButton')}</button></form><p className="mt-5 text-center text-[11px] text-slate-400">Admin accounts are created by the system administrator.</p></section></main>
}
