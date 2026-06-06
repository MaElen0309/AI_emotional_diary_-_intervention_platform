<template>
  <div class="solution-list">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-input v-model="keyword" placeholder="搜索调节方案..." prefix-icon="Search" clearable 
                  size="large" style="margin-bottom: 20px;" @keyup.enter="loadData" />
      </el-col>
    </el-row>

    <el-row :gutter="20" v-loading="loading">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in tableData" :key="item.id" style="margin-bottom: 20px;">
        <el-card shadow="hover" class="solution-card" @click="viewDetail(item)">
          <div class="card-cover" v-if="item.coverImage">
            <img :src="item.coverImage" alt="" />
          </div>
          <div class="card-cover default-cover" v-else>
            <el-icon :size="48"><FolderOpened /></el-icon>
          </div>

          <h4 class="card-title">{{ item.title }}</h4>
          <p class="card-summary">{{ item.summary || '暂无描述' }}</p>

          <div class="card-meta">
            <el-tag size="small" type="info">{{ item.category || '未分类' }}</el-tag>
            <el-rate v-model="item.rating" disabled size="small" />
          </div>

          <div class="card-stats">
            <span><el-icon><View /></el-icon> {{ item.useCount || 0 }}次使用</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && tableData.length === 0" description="暂无方案" />

    <el-pagination
      v-model:current-page="pageNum"
      :total="total"
      :page-size="12"
      layout="prev, pager, next"
      @current-change="loadData"
      style="margin-top: 20px; justify-content: center;"
    />

    <el-dialog v-model="detailVisible" :title="currentSolution?.title" width="800px">
      <div v-if="currentSolution" class="solution-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="分类">{{ currentSolution.category }}</el-descriptions-item>
          <el-descriptions-item label="难度">
            <el-tag :type="getDifficultyType(currentSolution.difficultyLevel)">
              {{ getDifficultyLabel(currentSolution.difficultyLevel) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="建议时长">{{ currentSolution.durationMinutes }}分钟</el-descriptions-item>
          <el-descriptions-item label="评分">
            <el-rate v-model="currentSolution.rating" disabled />
          </el-descriptions-item>
        </el-descriptions>

        <div class="detail-content" v-html="currentSolution.content"></div>

        <div style="margin-top: 30px; text-align: center;">
          <el-button type="primary" size="large" icon="VideoPlay" @click="useSolution">
            开始使用此方案
          </el-button>
          <el-button size="large" icon="Star" @click="likeSolution">点赞</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const keyword = ref('')
const pageNum = ref(1)
const total = ref(0)
const tableData = ref<any[]>([])
const detailVisible = ref(false)
const currentSolution = ref<any>(null)

onMounted(() => loadData())

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/solutions', {
      params: { pageNum: pageNum.value, pageSize: 12, keyword: keyword.value }
    })
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const viewDetail = async (item: any) => {
  try {
    const res = await request.get(`/solutions/${item.id}`)
    currentSolution.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const useSolution = async () => {
  if (!currentSolution.value) return
  
  try {
    await request.post(`/solutions/${currentSolution.value.id}/use`)
    ElMessage.success('已开始使用该方案')
    detailVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const likeSolution = async () => {
  if (!currentSolution.value) return
  
  try {
    await request.post(`/solutions/${currentSolution.value.id}/like`)
    currentSolution.value.likeCount = (currentSolution.value.likeCount || 0) + 1
    ElMessage.success('点赞成功')
  } catch (error) {
    console.error(error)
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
</script>

<style scoped lang="scss">
.solution-card {
  cursor: pointer;
  height: 100%;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-5px);
  }

  .card-cover {
    height: 160px;
    background: #f5f7fa;
    border-radius: 8px;
    margin-bottom: 15px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    &.default-cover {
      color: #c0c4cc;
    }
  }

  .card-title {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .card-summary {
    color: #999;
    font-size: 13px;
    line-height: 1.5;
    height: 40px;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    margin-bottom: 12px;
  }

  .card-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
  }

  .card-stats {
    color: #999;
    font-size: 13px;

    span {
      display: inline-flex;
      align-items: center;
      gap: 4px;
    }
  }
}

.solution-detail {
  .detail-content {
    margin-top: 20px;
    padding: 20px;
    background: #f9f9f9;
    border-radius: 8px;
    line-height: 1.8;
  }
}
</style>
