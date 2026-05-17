<template>
  <div class="page">
    <div class="toolbar">
      <div style="display: flex; gap: 12px">
        <el-select v-model="query.deviceId" clearable placeholder="设备" style="width: 220px">
          <el-option v-for="device in devices" :key="device.id" :label="device.deviceName" :value="device.id" />
        </el-select>
        <el-select v-model="query.metricType" clearable placeholder="指标" style="width: 180px">
          <el-option label="温度" value="temperature" />
          <el-option label="空气湿度" value="air_humidity" />
          <el-option label="土壤湿度" value="soil_moisture" />
          <el-option label="光照" value="light" />
        </el-select>
      </div>
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <div class="panel">
      <div ref="chartRef" class="chart"></div>
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
import * as echarts from 'echarts'
import { nextTick, onMounted, ref } from 'vue'
import { deviceApi, sensorApi } from '../api/modules'

const devices = ref([])
const rows = ref([])
const chartRef = ref()
const query = ref({ metricType: 'temperature' })

async function load() {
  rows.value = await sensorApi.history(query.value)
  await nextTick()
  echarts.init(chartRef.value).setOption({
    title: { text: '历史曲线', left: 8, top: 8, textStyle: { fontSize: 14 } },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: rows.value.map((item) => item.collectedAt) },
    yAxis: { type: 'value' },
    series: [{ type: 'line', smooth: true, data: rows.value.map((item) => item.metricValue), color: '#2f80ed' }]
  })
}

onMounted(async () => {
  devices.value = await deviceApi.all()
  await load()
})
</script>
