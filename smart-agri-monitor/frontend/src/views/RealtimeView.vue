<template>
  <div class="page">
    <div class="toolbar">
      <el-select
        v-model="deviceId"
        clearable
        placeholder="选择设备"
        style="width: 260px"
        @change="load"
      >
        <el-option
          v-for="device in devices"
          :key="device.id"
          :label="device.deviceName"
          :value="device.id"
        />
      </el-select>
      <el-button @click="load">刷新</el-button>
    </div>
    <div class="panel">
      <el-table :data="rows" stripe>
        <el-table-column prop="deviceId" label="设备ID" />
        <el-table-column prop="metricType" label="指标" />
        <el-table-column prop="metricValue" label="数值" />
        <el-table-column prop="unit" label="单位" />
        <el-table-column prop="collectedAt" label="采集时间" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { deviceApi, sensorApi } from '../api/modules'

const devices = ref([])
const deviceId = ref()
const rows = ref([])
let timer

async function load() {
  rows.value = await sensorApi.latest({ deviceId: deviceId.value })
}

onMounted(async () => {
  devices.value = await deviceApi.all()
  await load()
  timer = setInterval(load, 10000)
})
onUnmounted(() => clearInterval(timer))
</script>
