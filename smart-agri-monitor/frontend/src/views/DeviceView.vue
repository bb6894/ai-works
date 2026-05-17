<template>
  <CrudTable :api="deviceApi" :columns="columns" :fields="fields" :defaults="{ onlineStatus: 1, deviceType: 'multi_sensor' }" />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudTable from '../components/CrudTable.vue'
import { deviceApi, plotApi } from '../api/modules'

const plotOptions = ref([])
const columns = [
  { prop: 'deviceCode', label: '设备编码' },
  { prop: 'deviceName', label: '设备名称' },
  { prop: 'plotId', label: '地块ID' },
  { prop: 'deviceType', label: '类型' },
  { prop: 'onlineStatus', label: '在线状态' },
  { prop: 'lastSeenAt', label: '最后上报' }
]
const fields = ref([
  { prop: 'deviceCode', label: '设备编码' },
  { prop: 'deviceName', label: '设备名称' },
  { prop: 'plotId', label: '所属地块', type: 'select', options: plotOptions.value },
  { prop: 'deviceType', label: '类型' },
  { prop: 'onlineStatus', label: '在线', type: 'switch' },
  { prop: 'installedAt', label: '安装日期', type: 'date' }
])

onMounted(async () => {
  const plots = await plotApi.all()
  plotOptions.value = plots.map((item) => ({ label: item.name, value: item.id }))
  fields.value[2].options = plotOptions.value
})
</script>
