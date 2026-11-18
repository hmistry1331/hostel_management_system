import { useLocation, useNavigate } from 'react-router-dom'
import Icon from './Icon'
import { useAuthStore } from '../store/authStore'
import { useLanguage } from '../store/languageStore'
import { useTheme } from '../store/themeStore'

export default function Navbar({ role, onMenu }) {
  const location = useLocation()
  const navigate = useNavigate()
  const user = useAuthStore((state) => state.user)
  const logout = useAuthStore((state) => state.logout)
  const { language, setLanguage, t } = useLanguage()
  const { theme, toggleTheme } = useTheme()
  const title = location.pathname.includes('/jobs') ? t('jobRequests') : role === 'worker' ? t('availableRequests') : role === 'student' ? t('createRequest') : t('overview')
  return <header className="flex h-[78px] items-center justify-between border-b border-slate-200 bg-white/90 px-5 backdrop-blur sm:px-10"><div className="flex items-center gap-3 text-xs"><button className="text-slate-500 lg:hidden" aria-label="Open menu" onClick={onMenu}><Icon name="menu" /></button><span className="text-slate-400 capitalize">{role}</span><b className="font-normal text-slate-300">/</b><strong className="text-slate-600">{title}</strong></div><div className="flex items-center gap-2 sm:gap-3"><label className="sr-only" htmlFor="language-select">{t('language')}</label><select id="language-select" className="rounded-md border border-slate-200 bg-white px-2 py-1.5 text-[11px] font-semibold text-slate-500" value={language} onChange={(event) => setLanguage(event.target.value)}><option value="en">{t('english')}</option><option value="gu">{t('gujarati')}</option></select><button className="grid size-9 place-items-center rounded-md border border-slate-200 text-sm text-slate-500 transition hover:border-cyan-300 hover:text-cyan-700" aria-label={theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'} title={theme === 'dark' ? 'Light mode' : 'Dark mode'} onClick={toggleTheme}>{theme === 'dark' ? '☀' : '☾'}</button><button className="relative text-slate-500" aria-label={t('notifications')}><Icon name="bell" /><i className="absolute right-0 top-0 size-1.5 rounded-full border border-white bg-red-400" /></button><button className="flex items-center gap-2 text-left" onClick={() => navigate('/profile')}><div className="grid size-9 place-items-center rounded-full bg-violet-50 text-[10px] font-bold text-violet-600">{user?.name?.slice(0, 1).toUpperCase() || role.slice(0, 2).toUpperCase()}</div><span className="hidden text-xs font-medium text-slate-600 sm:block">{user?.name || role}</span></button><button className="rounded-md border border-slate-200 px-2.5 py-1.5 text-[11px] font-semibold text-slate-500 hover:text-red-600" onClick={() => { logout(); navigate('/') }}>{t('logout')}</button></div></header>
}
