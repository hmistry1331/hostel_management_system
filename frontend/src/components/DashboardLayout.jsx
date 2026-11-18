import Navbar from './Navbar'
import Sidebar from './Sidebar'
import { useState } from 'react'

export default function DashboardLayout({ children, role }) {
  const [menuOpen, setMenuOpen] = useState(false)
  return <div className="flex min-h-screen bg-slate-50 font-sans text-slate-700"><Sidebar role={role} open={menuOpen} onClose={() => setMenuOpen(false)} /><main className="min-w-0 flex-1"><Navbar role={role} onMenu={() => setMenuOpen(true)} />{children}</main></div>
}
