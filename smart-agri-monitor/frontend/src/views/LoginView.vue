<template>
  <div class="login-page">
    <div class="login-box">
      <h1 class="login-title">智慧农业设备监测与预警平台</h1>
      <el-form :model="form" label-position="top" @keyup.enter="login">
        <el-form-item label="账号">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="login">登录</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../api/modules'

const router = useRouter()
const loading = ref(false)
const form = ref({ username: 'admin', password: 'admin123' })

async function login() {
  loading.value = true
  try {
    const data = await authApi.login(form.value)
    localStorage.setItem('smart_agri_token', data.token)
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>
