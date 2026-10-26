import apiClient from './client'

export const getJobsForStudent = (customerId) => apiClient.get(`/jobs/customer/${customerId}`)
export const getJobsForAdmin = () => apiClient.get('/jobs/admin')
export const getJobsForWorker = () => apiClient.get('/jobs/worker')
export const createJobRequest = (payload) => apiClient.post('/jobs/create', payload)
export const findSimilarJobs = (payload) => apiClient.get('/jobs/similar', { params: payload })
export const cancelJobRequest = (jobId) => apiClient.put(`/jobs/${jobId}/cancel`)
export const startJobRequest = (jobId) => apiClient.put(`/jobs/${jobId}/start`)
export const confirmJobCompletion = (jobId) => apiClient.put(`/jobs/${jobId}/confirm`)
export const rejectJobCompletion = (jobId, payload) => apiClient.put(`/jobs/${jobId}/reject`, payload)
