<template>
  <div class="page">
    <div class="panel">
      <el-space>
        <el-button type="primary" @click="download('/reports/sensor-data', 'sensor-data.xlsx')"
          >导出传感器数据</el-button
        >
        <el-button type="warning" @click="download('/reports/alarms', 'alarm-records.xlsx')"
          >导出告警记录</el-button
        >
      </el-space>
    </div>
  </div>
</template>

<script setup>
import api from '../api/client'

async function download(path, filename) {
  const response = await api.get(path, { responseType: 'blob' })
  const blob = new Blob([response.data])
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}
</script>
