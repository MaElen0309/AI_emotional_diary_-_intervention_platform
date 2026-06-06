<template>
  <div class="diary-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的情绪日记</span>
          <el-button type="primary" icon="Plus" @click="$router.push('/diary/create')">
            写日记
          </el-button>
        </div>
      </template>

      <el-form :inline="true" class="search-form">
        <el-form-item label="搜索">
          <el-input v-model="keyword" placeholder="输入标题或内容关键词" clearable 
                    @keyup.enter="loadData" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="loadData">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="emotionType" label="情绪类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.emotionType" :type="getEmotionType(row.emotionType)">
              {{ getEmotionLabel(row.emotionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.riskLevel != null" :type="getRiskType(row.riskLevel)" size="small">
              {{ getRiskLabel(row.riskLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">查看</el-button>
            <el-button link type="warning" @click="analyzeDiary(row)">AI分析</el-button>
            <el-button link type="danger" @click="deleteDiary(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" title="日记详情" width="700px">
      <div v-if="currentDiary">
        <h3>{{ currentDiary.title }}</h3>
        <p style="color: #666; margin-bottom: 15px;">{{ currentDiary.createTime }}</p>
        <div style="line-height: 1.8; white-space: pre-wrap;">{{ currentDiary.content }}</div>
        
        <el-divider v-if="currentDiary.aiAnalysisResult" content-position="left">
          AI分析结果
        </el-divider>
        <div v-if="currentDiary.aiAnalysisResult" class="ai-result">
          <pre>{{ formatJson(currentDiary.aiAnalysisResult) }}</pre>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref<any[]>([])
const dialogVisible = ref(false)
const currentDiary = ref<any>(null)

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/diary', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        keyword: keyword.value
      }
    })
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const viewDetail = async (row: any) => {
  try {
    const res = await request.get(`/diary/${row.id}`)
    currentDiary.value = res.data
    dialogVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const analyzeDiary = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要对该日记进行AI分析吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    ElMessage.info('正在分析中，请稍候...')
    const res = await request.post(`/ai/analyze/diary/${row.id}`)
    ElMessage.success('分析完成')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const deleteDiary = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该日记吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.delete(`/diary/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const getEmotionType = (type: number) => {
  const map: Record<number, string> = { 1: 'success', 2: '', 3: 'warning', 4: 'danger', 5: 'danger' }
  return map[type] || ''
}

const getEmotionLabel = (type: number) => {
  const map: Record<number, string> = { 1: '开心', 2: '平静', 3: '焦虑', 4: '悲伤', 5: '愤怒', 6: '恐惧', 7: '其他' }
  return map[type] || '未知'
}

const getRiskType = (level: number) => {
  const map: Record<number, string> = { 0: 'info', 1: 'warning', 2: 'danger', 3: 'danger' }
  return map[level] || ''
}

const getRiskLabel = (level: number) => {
  const map: Record<number, string> = { 0: '正常', 1: '低风险', 2: '中风险', 3: '高风险' }
  return map[level] || '未知'
}

const formatJson = (jsonStr: string) => {
  try {
    return JSON.stringify(JSON.parse(jsonStr), null, 2)
  } catch {
    return jsonStr
  }
}
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.ai-result {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;

  pre {
    margin: 0;
    font-family: inherit;
    white-space: pre-wrap;
    word-break: break-all;
  }
}
</style>
