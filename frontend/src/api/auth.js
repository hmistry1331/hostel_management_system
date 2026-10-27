import apiClient from './client'

export const login = (payload) => apiClient.post('/auth/login', payload)
export const signup = (payload) => apiClient.post('/auth/signup', payload)
