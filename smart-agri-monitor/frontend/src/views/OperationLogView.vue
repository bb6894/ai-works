<template>
  <div class="page">
    <div class="toolbar">
      <el-input v-model="keyword" clearable placeholder="搜索日志" style="max-width: 320px" @keyup.enter="load" />
      <el-button @click="load">刷新</el-button>
    </div>
    <div class="panel">
      <el-table :data="rows" stripe>
        <el-table-column prop="username" label="用户" />
        <el-table-column prop="moduleName" label="模块" />
        <el-table-column prop="actionName" label="动作" />
        <el-table-column prop="requestMethod" label="方法" />
        <el-table-column prop="requestUri" label="路径" min-width="180" />
        <el-table-column prop="success" label="成功" />
        <el-table-column prop="createdAt" label="时间" width="180" />
      </el-table>
      <el-pagination v-model:current-page="page" style="margin-top: 16px" background layout="prev, pager, next, total" :total="total" @current-change="load" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { operationLogApi } from '../api/modules'

const rows = ref([])
const page = ref(1)
const total = ref(0)
const keyword = ref('')

async function load() {
  const data = await operationLogApi.page({ page: page.value, size: 10, keyword: keyword.value })
  rows.value = data.records
  total.value = data.total
}

onMounted(load)
</script>
