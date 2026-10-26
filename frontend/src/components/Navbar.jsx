import { useLocation } from 'react-router-dom'
import Icon from './Icon'

export default function Navbar({ role }) {
  const location = useLocation()
  const title = location.pathname.includes('/jobs') ? 'Job requests' : role === 'worker' ? 'Available requests' : role === 'student' ? 'Create request' : 'Overview'
  return <header className="flex h-[73px] items-center justify-between border-b border-slate-200 bg-white px-5 sm:px-10"><div className="flex items-center gap-3 text-xs"><button className="text-slate-500 lg:hidden" aria-label="Open menu"><Icon name="menu" /></button><span className="text-slate-400 capitalize">{role}</span><b className="font-normal text-slate-300">/</b><strong className="text-slate-600">{title}</strong></div><div className="flex items-center gap-4"><button className="relative text-slate-500"><Icon name="bell" /><i className="absolute right-0 top-0 size-1.5 rounded-full border border-white bg-red-400" /></button><div className="grid size-8 place-items-center rounded-full bg-violet-50 text-[10px] font-bold text-violet-600">{role.slice(0, 2).toUpperCase()}</div></div></header>
}
