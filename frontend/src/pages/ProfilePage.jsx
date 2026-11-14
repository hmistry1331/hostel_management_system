import { useAuthStore } from '../store/authStore'

const roleLabels = { STUDENT: 'Student', WORKER: 'Maintenance staff', ADMIN: 'Supervisor' }

export default function ProfilePage() {
  const user = useAuthStore((state) => state.user)
  const fields = [
    ['Full name', user?.name || '—'],
    ['Email', user?.email || 'Email is not included in the login response'],
    ['Role', roleLabels[user?.role] || user?.role || '—'],
    ['User ID', user?.userId || '—'],
    ...(user?.workerId ? [['Worker ID', user.workerId]] : []),
  ]

  return <div className="mx-auto max-w-[760px] px-4 py-8 sm:px-10">
    <p className="text-[11px] text-slate-400">Account</p>
    <h1 className="mt-2 font-['Plus_Jakarta_Sans'] text-2xl font-bold text-slate-800">My profile</h1>
    <p className="mt-1 text-sm text-slate-500">Your account details and workspace access.</p>
    <section className="mt-6 rounded-lg border border-slate-200 bg-white p-5">
      <div className="flex items-center gap-4 border-b border-slate-100 pb-5">
        <div className="grid size-14 place-items-center rounded-full bg-violet-50 text-lg font-bold text-violet-600">{user?.name?.slice(0, 1).toUpperCase() || '?'}</div>
        <div><h2 className="font-semibold text-slate-800">{user?.name || 'User'}</h2><p className="mt-1 text-xs text-slate-500">{roleLabels[user?.role] || user?.role}</p></div>
      </div>
      <dl className="grid gap-4 pt-5 sm:grid-cols-2">{fields.map(([label, value]) => <div key={label}><dt className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">{label}</dt><dd className="mt-1 text-sm text-slate-700">{value}</dd></div>)}</dl>
    </section>
  </div>
}
