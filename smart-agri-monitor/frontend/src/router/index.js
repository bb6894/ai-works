import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/DashboardView.vue'
import PlotView from '../views/PlotView.vue'
import DeviceView from '../views/DeviceView.vue'
import RealtimeView from '../views/RealtimeView.vue'
import RuleView from '../views/RuleView.vue'
import AlarmView from '../views/AlarmView.vue'
import HistoryView from '../views/HistoryView.vue'
import ReportView from '../views/ReportView.vue'
import OperationLogView from '../views/OperationLogView.vue'

const routes = [
  { path: '/login', component: LoginView },
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', component: DashboardView },
  { path: '/plots', component: PlotView },
  { path: '/devices', component: DeviceView },
  { path: '/realtime', component: RealtimeView },
  { path: '/rules', component: RuleView },
  { path: '/alarms', component: AlarmView },
  { path: '/history', component: HistoryView },
  { path: '/reports', component: ReportView },
  { path: '/operation-logs', component: OperationLogView }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.path !== '/login' && !localStorage.getItem('smart_agri_token')) {
    return '/login'
  }
  return true
})

export default router
