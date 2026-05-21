<template>
  <router-view v-if="isLogin" />
  <el-container v-else class="app-shell">
    <el-aside width="232px" class="sidebar">
      <div class="brand">
        <span class="brand-mark">Ag</span>
        <span>智慧农业平台</span>
      </div>
      <el-menu :default-active="$route.path" router class="menu">
        <el-menu-item index="/dashboard">首页看板</el-menu-item>
        <el-menu-item index="/plots">地块管理</el-menu-item>
        <el-menu-item index="/devices">设备管理</el-menu-item>
        <el-menu-item index="/realtime">实时数据</el-menu-item>
        <el-menu-item index="/rules">预警规则</el-menu-item>
        <el-menu-item index="/alarms">告警处理</el-menu-item>
        <el-menu-item index="/history">历史数据</el-menu-item>
        <el-menu-item index="/reports">报表导出</el-menu-item>
        <el-menu-item index="/operation-logs">操作日志</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <div>{{ title }}</div>
        <el-button size="small" @click="logout">退出</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from './api/client'

const route = useRoute()
const router = useRouter()
const titleMap = {
  '/dashboard': '首页看板',
  '/plots': '地块管理',
  '/devices': '设备管理',
  '/realtime': '实时数据',
  '/rules': '预警规则',
  '/alarms': '告警处理',
  '/history': '历史数据',
  '/reports': '报表导出',
  '/operation-logs': '操作日志'
}
const isLogin = computed(() => route.path === '/login')
const title = computed(() => titleMap[route.path] || '智慧农业平台')

async function logout() {
  try {
    await api.post('/auth/logout')
  } catch (error) {
    console.warn('logout ignored', error)
  }
  localStorage.removeItem('smart_agri_token')
  router.push('/login')
}
</script>
