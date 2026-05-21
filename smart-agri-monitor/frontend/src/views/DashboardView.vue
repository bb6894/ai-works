<template>
  <div class="page">
    <div class="stats-grid">
      <div v-for="item in statsCards" :key="item.label" class="stat-card">
        <div class="stat-label">{{ item.label }}</div>
        <div class="stat-value">{{ item.value }}</div>
      </div>
    </div>
    <div class="chart-grid">
      <div class="panel">
        <div ref="statusChart" class="chart"></div>
      </div>
      <div class="panel">
        <div ref="alarmChart" class="chart"></div>
      </div>
    </div>
    <div class="panel">
      <div ref="metricChart" class="chart"></div>
    </div>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import { dashboardApi } from '../api/modules'

const stats = ref({})
const statusChart = ref()
const alarmChart = ref()
const metricChart = ref()
let timer

const statsCards = computed(() => [
  { label: '设备总数', value: stats.value.deviceTotal ?? 0 },
  { label: '在线设备', value: stats.value.onlineDeviceTotal ?? 0 },
  { label: '今日数据量', value: stats.value.todayDataTotal ?? 0 },
  { label: '未处理告警', value: stats.value.pendingAlarmTotal ?? 0 }
])

async function load() {
  stats.value = await dashboardApi.stats()
  await nextTick()
  renderCharts()
}

function renderCharts() {
  echarts.init(statusChart.value).setOption({
    title: { text: '设备状态', left: 8, top: 8, textStyle: { fontSize: 14 } },
    tooltip: {},
    series: [{ type: 'pie', radius: ['45%', '70%'], data: stats.value.deviceStatus || [] }]
  })
  echarts.init(alarmChart.value).setOption({
    title: { text: '告警趋势', left: 8, top: 8, textStyle: { fontSize: 14 } },
    tooltip: {},
    xAxis: { type: 'category', data: (stats.value.alarmTrend || []).map((i) => i.date) },
    yAxis: { type: 'value' },
    series: [
      {
        type: 'bar',
        data: (stats.value.alarmTrend || []).map((i) => i.count),
        itemStyle: { color: '#e85d4f' }
      }
    ]
  })
  echarts.init(metricChart.value).setOption({
    title: { text: '近期环境指标', left: 8, top: 8, textStyle: { fontSize: 14 } },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: (stats.value.metricTrend || []).map((i) => i.time.slice(11, 19))
    },
    yAxis: { type: 'value' },
    series: [
      {
        type: 'line',
        smooth: true,
        data: (stats.value.metricTrend || []).map((i) => i.value),
        color: '#2ba471'
      }
    ]
  })
}

onMounted(() => {
  load()
  timer = setInterval(load, 10000)
})
onUnmounted(() => clearInterval(timer))
</script>
