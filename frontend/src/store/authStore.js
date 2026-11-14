import { create } from 'zustand'

const STORAGE_KEY = 'hostel_user'
const TOKEN_KEY = 'hostel_access_token'

const readUser = () => {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) || 'null')
  } catch {
    return null
  }
}

export const useAuthStore = create((set) => ({
  user: readUser(),
  setUser: (user) => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(user))
    localStorage.setItem(TOKEN_KEY, user.token)
    set({ user })
  },
  logout: () => {
    localStorage.removeItem(STORAGE_KEY)
    localStorage.removeItem(TOKEN_KEY)
    set({ user: null })
  },
}))

export const rolePath = (role) => role === 'ADMIN' ? '/admin' : role === 'WORKER' ? '/worker' : '/student'
