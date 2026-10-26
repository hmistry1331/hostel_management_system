const colors = {
  Urgent: 'bg-red-50 text-red-500', Normal: 'bg-slate-100 text-slate-500', Low: 'bg-green-50 text-green-600',
  Pending: 'bg-amber-50 text-amber-600', 'In progress': 'bg-violet-50 text-violet-600', Completed: 'bg-emerald-50 text-emerald-600',
}

export default function Badge({ children, kind }) {
  return <span className={`inline-flex rounded px-2 py-1 text-[9px] font-semibold ${colors[kind] || 'bg-slate-100 text-slate-500'}`}>{children}</span>
}
