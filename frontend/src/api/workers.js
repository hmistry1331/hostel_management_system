import apiClient from './client'

export const getAvailableWorkers = () => apiClient.get('/admin/workers/available')
export const acceptJobRequest = (jobId, workerId) => apiClient.post(`/worker/jobs/${jobId}/accept`, null, { params: { workerId } })
