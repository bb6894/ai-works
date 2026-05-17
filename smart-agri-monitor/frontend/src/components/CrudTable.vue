<template>
  <div class="page">
    <div class="toolbar">
      <el-input v-model="keyword" clearable placeholder="搜索" style="max-width: 320px" @keyup.enter="load" />
      <div>
        <el-button @click="load">刷新</el-button>
        <el-button type="primary" @click="openCreate">新增</el-button>
      </div>
    </div>
    <div class="panel">
      <el-table v-loading="loading" :data="rows" row-key="id" stripe>
        <el-table-column v-for="column in columns" :key="column.prop" :prop="column.prop" :label="column.label" :width="column.width" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除？" @confirm="remove(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        style="margin-top: 16px"
        background
        layout="prev, pager, next, total"
        :total="total"
        @current-change="load"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑' : '新增'" width="560px">
      <el-form :model="form" label-width="110px">
        <el-form-item v-for="field in fields" :key="field.prop" :label="field.label">
          <el-select v-if="field.type === 'select'" v-model="form[field.prop]" clearable style="width: 100%">
            <el-option v-for="option in field.options" :key="option.value" :label="option.label" :value="option.value" />
          </el-select>
          <el-date-picker v-else-if="field.type === 'date'" v-model="form[field.prop]" value-format="YYYY-MM-DD" style="width: 100%" />
          <el-input-number v-else-if="field.type === 'number'" v-model="form[field.prop]" style="width: 100%" />
          <el-switch v-else-if="field.type === 'switch'" v-model="form[field.prop]" :active-value="1" :inactive-value="0" />
          <el-input v-else v-model="form[field.prop]" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  api: { type: Object, required: true },
  columns: { type: Array, required: true },
  fields: { type: Array, required: true },
  defaults: { type: Object, default: () => ({}) }
})

const rows = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({})

async function load() {
  loading.value = true
  try {
    const data = await props.api.page({ page: page.value, size: size.value, keyword: keyword.value })
    rows.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  form.value = { ...props.defaults }
  dialogVisible.value = true
}

function openEdit(row) {
  form.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (form.value.id) {
    await props.api.update(form.value.id, form.value)
  } else {
    await props.api.create(form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function remove(id) {
  await props.api.remove(id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>
