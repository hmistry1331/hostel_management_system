import { NavLink } from 'react-router-dom'
import Icon from './Icon'
import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'
import { useLanguage } from '../store/languageStore'

export default function Sidebar({ role }) {
  const navigate = useNavigate()
  const logout = useAuthStore((state) => state.logout)
  const { t } = useLanguage()
  const basePath = `/${role}`
  const navItems = role === 'admin'
    ? [[`${basePath}`, t('overview'), 'grid'], [`${basePath}/jobs`, t('jobRequests'), 'clipboard'], [`${basePath}/staff`, t('maintenanceStaff'), 'users']]
    : role === 'worker'
      ? [[basePath, t('assignedRequests'), 'clipboard']]
      : [[basePath, t('createRequest'), 'plus']]
  const workspaceLabel = role === 'admin' ? t('adminWorkspace') : role === 'worker' ? t('workerWorkspace') : t('studentWorkspace')
  return <aside className="hidden w-64 shrink-0 flex-col border-r border-slate-200 bg-white px-3.5 py-6 lg:flex">
    <div className="flex items-center gap-3 px-3 pb-10"><div className="grid size-9 place-items-center rounded-xl bg-violet-600 text-lg font-extrabold text-white shadow-lg shadow-cyan-100">S</div><div><strong className="block font-['Plus_Jakarta_Sans'] text-base font-extrabold tracking-tight text-slate-800">Service<span className="text-violet-600">Connect</span></strong><small className="block text-[8px] font-bold tracking-[1.3px] text-slate-400">{workspaceLabel}</small></div></div>
    <div className="px-3 pb-3 text-[10px] font-bold tracking-widest text-slate-400">{t('workspace')}</div>
    <nav className="grid gap-1">{navItems.map(([to, label, icon]) => <NavLink className={({ isActive }) => `flex w-full items-center gap-3 rounded-lg px-3.5 py-2.5 text-left text-[13px] transition hover:bg-violet-50 hover:text-slate-800 ${isActive ? 'bg-violet-50 font-semibold text-violet-700' : 'text-slate-500'}`} key={to} to={to}><Icon name={icon} />{label}</NavLink>)}</nav>
    <div className="mt-auto"><NavLink className="mb-2 flex w-full items-center gap-3 px-3.5 py-2.5 text-left text-[13px] text-slate-500" to="/profile"><Icon name="user" />{t('myProfile')}</NavLink><button className="mb-5 flex w-full items-center gap-3 px-3.5 py-2.5 text-left text-[13px] text-slate-500 hover:text-red-600" onClick={() => { logout(); navigate('/') }}><Icon name="logout" />{t('logout')}</button><div className="flex items-center gap-2.5 border-t border-slate-200 px-2 pt-4"><div className="grid size-8 place-items-center rounded-full bg-violet-50 text-[9px] font-bold text-violet-600">{role.slice(0, 2).toUpperCase()}</div><div><strong className="block text-xs capitalize text-slate-800">{role}</strong><small className="text-[10px] text-slate-400">{t('workspace')}</small></div></div></div>
  </aside>
}
