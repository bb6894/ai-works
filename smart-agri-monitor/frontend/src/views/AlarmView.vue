<template>
  <div class="page">
    <div class="toolbar">
      <el-input v-model="keyword" clearable placeholder="搜索告警" style="max-width: 320px" @keyup.enter="load" />
      <el-button @click="load">刷新</el-button>
    </div>
    <div class="panel">
      <el-table :data="rows" stripe>
        <el-table-column prop="message" label="告警信息" min-width="240" />
        <el-table-column prop="metricType" label="指标" />
        <el-table-column prop="metricValue" label="数值" />
        <el-table-column prop="alarmLevel" label="等级" />
        <el-table-column prop="status" label="状态" />
        <el-table-column prop="triggeredAt" label="触发时间" width="180" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="handle(row, 'processing')">处理中</el-button>
            <el-button size="small" type="success" @click="handle(row, 'resolved')">已处理</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" style="margin-top: 16px" background layout="prev, pager, next, total" :total="total" @current-change="load" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { alarmApi } from '../api/modules'

const rows = ref([])
const page = ref(1)
const total = ref(0)
const keyword = ref('')

async function load() {
  const data = await alarmApi.page({ page: page.value, size: 10, keyword: keyword.value })
  rows.value = data.records
  total.value = data.total
}

async function handle(row, status) {
  const { value } = await ElMessageBox.prompt('处理说明', '告警处理', { inputValue: status === 'resolved' ? '现场已确认并恢复正常' : '已派单处理' })
  await alarmApi.handle(row.id, { status, remark: value })
  ElMessage.success('处理成功')
  load()
}

onMounted(load)
</script>
