import apiClient from './client'

export const getAvailableWorkers = () => apiClient.get('/admin/workers/available')
export const createStaff = (payload) => apiClient.post('/admin/staff', payload)
export const assignJobRequest = (jobId, workerId) => apiClient.post(`/admin/jobs/${jobId}/assign`, null, { params: { workerId } })
export const getAvailableSlots = (jobId, from, to) => apiClient.get('/slots/available', { params: { jobId, from, to } })
export const bookSlot = (slotId, jobId) => apiClient.post(`/slots/${slotId}/book`, { jobId })
export const cancelSlot = (slotId) => apiClient.put(`/slots/${slotId}/cancel`)
export const getLocations = () => apiClient.get('/locations')

// Add a function to create a new worker (admin only)
export const createWorker = (payload) => apiClient.post('/admin/workers', payload);
