import { Link } from 'react-router-dom'

const roles = [
  ['admin', 'Admin', 'View requests, manage workers, and oversee hostel operations.'],
  ['worker', 'Worker', 'Review available maintenance requests and accept assigned work.'],
  ['student', 'Student', 'Create maintenance requests and track your own requests.'],
]

export default function RoleSelectionPage() {
  return <main className="grid min-h-screen place-items-center bg-slate-50 p-6"><section className="w-full max-w-3xl"><div className="mb-8 text-center"><h1 className="font-['Plus_Jakarta_Sans'] text-3xl font-bold text-slate-800">Service<span className="text-violet-600">Connect</span></h1><p className="mt-2 text-sm text-slate-500">Choose your workspace</p></div><div className="grid gap-4 md:grid-cols-3">{roles.map(([role, label, description]) => <Link className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:border-violet-300 hover:shadow-md" key={role} to={`/${role}`}><h2 className="font-['Plus_Jakarta_Sans'] text-lg font-bold text-slate-800">{label}</h2><p className="mt-2 text-xs leading-5 text-slate-500">{description}</p><span className="mt-5 block text-xs font-semibold text-violet-600">Open workspace →</span></Link>)}</div><p className="mt-6 text-center text-[11px] text-slate-400">Role selection is for the current development build. Add authentication before production use.</p></section></main>
}
