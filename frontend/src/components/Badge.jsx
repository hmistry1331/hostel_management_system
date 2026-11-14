const colors = {
  URGENT: 'bg-red-50 text-red-500', NORMAL: 'bg-slate-100 text-slate-500',
  SUBMITTED: 'bg-amber-50 text-amber-600', ASSIGNED: 'bg-blue-50 text-blue-600',
  SCHEDULED: 'bg-violet-50 text-violet-600', IN_PROGRESS: 'bg-violet-50 text-violet-600',
  COMPLETED: 'bg-emerald-50 text-emerald-600', NOT_SOLVED: 'bg-orange-50 text-orange-600',
  CANCELLED: 'bg-red-50 text-red-600',
}

export default function Badge({ children, kind }) {
  return <span className={`inline-flex rounded px-2 py-1 text-[9px] font-semibold ${colors[kind] || 'bg-slate-100 text-slate-500'}`}>{children}</span>
}
