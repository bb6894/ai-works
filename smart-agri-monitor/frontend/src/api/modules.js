import api from './client'

export const authApi = {
  login: (payload) => api.post('/auth/login', payload),
  me: () => api.get('/auth/me')
}

export const plotApi = crud('/plots')
export const deviceApi = crud('/devices')
export const ruleApi = {
  ...crud('/rules'),
  toggle: (id, enabled) => api.put(`/rules/${id}/toggle`, null, { params: { enabled } })
}

export const sensorApi = {
  latest: (params) => api.get('/sensor-data/latest', { params }),
  history: (params) => api.get('/sensor-data/history', { params }),
  ingest: (payload) => api.post('/sensor-data', payload)
}

export const alarmApi = {
  page: (params) => api.get('/alarms', { params }),
  handle: (id, payload) => api.put(`/alarms/${id}/handle`, payload)
}

export const dashboardApi = {
  stats: () => api.get('/dashboard/stats')
}

export const reportApi = {
  sensorDataUrl: () => `${api.defaults.baseURL}/reports/sensor-data`,
  alarmsUrl: () => `${api.defaults.baseURL}/reports/alarms`
}

export const operationLogApi = {
  page: (params) => api.get('/operation-logs', { params })
}

function crud(path) {
  return {
    page: (params) => api.get(path, { params }),
    all: () => api.get(`${path}/all`),
    create: (payload) => api.post(path, payload),
    update: (id, payload) => api.put(`${path}/${id}`, payload),
    remove: (id) => api.delete(`${path}/${id}`)
  }
}
