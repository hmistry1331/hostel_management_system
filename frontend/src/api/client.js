import axios from 'axios'
import { toast } from 'react-toastify'
import { useAuthStore } from '../store/authStore'

const redact = (value) => {
  if (!value || typeof value !== 'object') return value
  if (Array.isArray(value)) return value.map(redact)
  return Object.fromEntries(Object.entries(value).map(([key, item]) => [
    key.toLowerCase().includes('password') ? key : key,
    key.toLowerCase().includes('password') ? '[redacted]' : redact(item),
  ]))
}

const requestData = (value) => {
  if (!value) return undefined
  if (typeof value !== 'string') return value
  try {
    return JSON.parse(value)
  } catch {
    return value
  }
}

export const getApiErrorMessage = (error) => {
  const data = error.response?.data
  if (Array.isArray(data?.errors)) {
    return data.errors.map((item) => item.defaultMessage || item.message || item).join(', ')
  }
  if (data?.errors && typeof data.errors === 'object') {
    return Object.entries(data.errors).map(([field, detail]) => `${field}: ${detail}`).join(', ')
  }
  return data?.detail || data?.message || data?.error
    || (error.request ? 'Unable to reach the server.' : error.message)
    || 'Something went wrong. Please try again.'
}

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
})

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('hostel_access_token')
  if (token) {
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const isAuthRequest = error.config?.url?.startsWith('/auth/')
    const message = getApiErrorMessage(error)

    console.error('[API request failed]', {
      method: error.config?.method?.toUpperCase(),
      url: error.config?.url,
      status: error.response?.status,
      request: redact(requestData(error.config?.data)),
      response: error.response?.data,
      message,
    })
    toast.error(message)

    if (error.response?.status === 401 && !isAuthRequest) {
      useAuthStore.getState().logout()
      if (window.location.pathname !== '/') window.location.assign('/')
    }
    return Promise.reject(error)
  },
)

export default apiClient
