<template>
  <div class="page">
    <CrudTable :api="ruleApi" :columns="columns" :fields="fields" :defaults="defaults" />
  </div>
</template>

<script setup>
import CrudTable from '../components/CrudTable.vue'
import { ruleApi } from '../api/modules'

const defaults = {
  enabled: 1,
  alarmLevel: 'warning',
  compareOperator: 'GT',
  metricType: 'temperature'
}
const metricOptions = [
  { label: '温度', value: 'temperature' },
  { label: '空气湿度', value: 'air_humidity' },
  { label: '土壤湿度', value: 'soil_moisture' },
  { label: '光照', value: 'light' }
]
const operatorOptions = [
  { label: '大于', value: 'GT' },
  { label: '大于等于', value: 'GTE' },
  { label: '小于', value: 'LT' },
  { label: '小于等于', value: 'LTE' },
  { label: '区间外', value: 'OUTSIDE' }
]
const columns = [
  { prop: 'ruleName', label: '规则名称' },
  { prop: 'metricType', label: '指标' },
  { prop: 'compareOperator', label: '比较符' },
  { prop: 'thresholdValue', label: '阈值1' },
  { prop: 'thresholdValue2', label: '阈值2' },
  { prop: 'alarmLevel', label: '等级' },
  { prop: 'enabled', label: '启用' }
]
const fields = [
  { prop: 'ruleName', label: '规则名称' },
  { prop: 'plotId', label: '地块ID', type: 'number' },
  { prop: 'deviceId', label: '设备ID', type: 'number' },
  { prop: 'metricType', label: '指标', type: 'select', options: metricOptions },
  { prop: 'compareOperator', label: '比较符', type: 'select', options: operatorOptions },
  { prop: 'thresholdValue', label: '阈值1', type: 'number' },
  { prop: 'thresholdValue2', label: '阈值2', type: 'number' },
  {
    prop: 'alarmLevel',
    label: '等级',
    type: 'select',
    options: [
      { label: '一般', value: 'warning' },
      { label: '严重', value: 'critical' }
    ]
  },
  { prop: 'enabled', label: '启用', type: 'switch' }
]
</script>
