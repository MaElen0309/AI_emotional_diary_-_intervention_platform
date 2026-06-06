<template>
  <div class="solution-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>方案管理</span>
          <el-button type="primary" icon="Plus" @click="showCreateDialog">新增方案</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="difficultyLevel" label="难度" width="100">
          <template #default="{ row }">
            <el-tag :type="getDifficultyType(row.difficultyLevel)" size="small">
              {{ getDifficultyLabel(row.difficultyLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="useCount" label="使用次数" width="100" />
        <el-table-column prop="rating" label="评分" width="100">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="editSolution(row)">编辑</el-button>
            <template v-if="row.status === 0 || row.status === 2">
              <el-button link type="success" @click="handlePublish(row)">上架</el-button>
            </template>
            <template v-if="row.status === 1">
              <el-button link type="warning" @click="handleOffline(row)">下架</el-button>
            </template>
            <el-button link type="danger" @click="deleteSolution(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        :total="total"
        :page-size="pageSize"
        layout="prev, pager, next"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑方案' : '新增方案'" width="800px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="方案标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入方案标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="form.category" placeholder="选择分类">
                <el-option label="情绪调节" value="情绪调节" />
                <el-option label="压力缓解" value="压力缓解" />
                <el-option label="人际关系" value="人际关系" />
                <el-option label="自我成长" value="自我成长" />
                <el-option label="睡眠改善" value="睡眠改善" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="摘要" prop="summary">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="简要描述方案内容" maxlength="500" show-word-limit />
        </el-form-item>

        <el-form-item label="详细内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="输入方案的详细步骤和说明..." />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="难度等级">
              <el-select v-model="form.difficultyLevel" placeholder="选择难度">
                <el-option label="初级" :value="1" />
                <el-option label="中级" :value="2" />
                <el-option label="高级" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="建议时长(分)">
              <el-input-number v-model="form.durationMinutes" :min="5" :max="120" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="适用情绪">
              <el-input v-model="form.targetEmotion" placeholder="如：焦虑、悲伤" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="封面图片URL">
          <el-input v-model="form.coverImage" placeholder="可选，输入封面图链接" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref<any[]>([])

const form = reactive({
  id: null as number | null,
  title: '',
  summary: '',
  content: '',
  category: '',
  difficultyLevel: 1,
  durationMinutes: 30,
  targetEmotion: '',
  coverImage: '',
  status: 0
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入方案标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入详细内容', trigger: 'blur' }]
}

onMounted(() => loadData())

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/solutions/admin', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value }
    })
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    title: '',
    summary: '',
    content: '',
    category: '',
    difficultyLevel: 1,
    durationMinutes: 30,
    targetEmotion: '',
    coverImage: '',
    status: 0
  })
}

const showCreateDialog = () => {
  resetForm()
  isEdit.value = false
  dialogVisible.value = true
}

const editSolution = (row: any) => {
  Object.assign(form, row)
  isEdit.value = true
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value && form.id) {
      await request.put(`/solutions/${form.id}`, form)
      ElMessage.success('修改成功')
    } else {
      await request.post('/solutions', form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const handlePublish = async (row: any) => {
  try {
    await request.put(`/solutions/${row.id}/publish`)
    ElMessage.success('上架成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleOffline = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要下架该方案吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.put(`/solutions/${row.id}/offline`)
    ElMessage.success('下架成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const deleteSolution = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该方案吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/solutions/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const getDifficultyType = (level: number) => {
  const map: Record<number, string> = { 1: 'success', 2: 'warning', 3: 'danger' }
  return map[level] || ''
}

const getDifficultyLabel = (level: number) => {
  const map: Record<number, string> = { 1: '初级', 2: '中级', 3: '高级' }
  return map[level] || '未知'
}

const getStatusType = (status: number) => {
  const map: Record<number, string> = { 0: 'info', 1: 'success', 2: 'warning' }
  return map[status] || ''
}

const getStatusLabel = (status: number) => {
  const map: Record<number, string> = { 0: '草稿', 1: '已上架', 2: '已下架' }
  return map[status] || '未知'
}
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
