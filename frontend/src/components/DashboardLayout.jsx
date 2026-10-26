import Navbar from './Navbar'
import Sidebar from './Sidebar'

export default function DashboardLayout({ children, role }) {
  return <div className="flex min-h-screen bg-slate-50 font-sans text-slate-700"><Sidebar role={role} /><main className="min-w-0 flex-1"><Navbar role={role} />{children}</main></div>
}
