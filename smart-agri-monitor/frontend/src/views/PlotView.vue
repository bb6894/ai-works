<template>
  <CrudTable :api="plotApi" :columns="columns" :fields="fields" :defaults="{ status: 1 }" />
</template>

<script setup>
import CrudTable from '../components/CrudTable.vue'
import { plotApi } from '../api/modules'

const columns = [
  { prop: 'name', label: '地块名称' },
  { prop: 'location', label: '位置' },
  { prop: 'areaMu', label: '面积(亩)' },
  { prop: 'cropType', label: '作物' },
  { prop: 'manager', label: '负责人' },
  { prop: 'status', label: '状态' }
]
const fields = [
  { prop: 'name', label: '地块名称' },
  { prop: 'location', label: '位置' },
  { prop: 'areaMu', label: '面积(亩)', type: 'number' },
  { prop: 'cropType', label: '作物' },
  { prop: 'manager', label: '负责人' },
  { prop: 'status', label: '启用', type: 'switch' }
]
</script>
