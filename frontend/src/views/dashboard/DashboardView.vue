<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon :size="30"><Notebook /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.diaryCount }}</div>
              <div class="stat-label">日记总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon :size="30"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.treeholeCount }}</div>
              <div class="stat-label">树洞帖子</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c;">
              <el-icon :size="30"><MagicStick /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.aiAnalysisCount }}</div>
              <div class="stat-label">AI分析次数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c;">
              <el-icon :size="30"><FolderOpened /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.solutionCount }}</div>
              <div class="stat-label">方案数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>最近情绪记录</span>
          </template>
          <el-table :data="recentDiaries" stripe>
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="emotionType" label="情绪类型" width="120">
              <template #default="{ row }">
                <el-tag :type="getEmotionType(row.emotionType)">{{ getEmotionLabel(row.emotionType) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" icon="EditPen" @click="$router.push('/diary/create')">写日记</el-button>
            <el-button type="success" icon="MagicStick" @click="$router.push('/ai-analysis')">AI分析</el-button>
            <el-button type="warning" icon="FolderOpened" @click="$router.push('/solutions')">浏览方案</el-button>
            <el-button type="info" icon="ChatDotRound" @click="$router.push('/treehole')">去树洞</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const stats = ref({
  diaryCount: 0,
  treeholeCount: 0,
  aiAnalysisCount: 0,
  solutionCount: 0
})

const recentDiaries = ref<any[]>([])

onMounted(async () => {
  try {
    const [diaryRes] = await Promise.all([
      request.get('/diary', { params: { pageNum: 1, pageSize: 5 } })
    ])
    
    if (diaryRes.data?.list) {
      recentDiaries.value = diaryRes.data.list
      stats.value.diaryCount = diaryRes.data.total || 0
    }
  } catch (error) {
    console.error('获取数据失败', error)
  }
})

const getEmotionType = (type: number) => {
  const map: Record<number, string> = {
    1: 'success', 2: 'info', 3: 'warning', 4: 'danger', 5: 'danger'
  }
  return map[type] || 'info'
}

const getEmotionLabel = (type: number) => {
  const map: Record<number, string> = {
    1: '开心', 2: '平静', 3: '焦虑', 4: '悲伤', 5: '愤怒', 6: '恐惧', 7: '其他'
  }
  return map[type] || '未知'
}
</script>

<style scoped lang="scss">
.stat-cards {
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      gap: 20px;

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
      }

      .stat-info {
        .stat-number {
          font-size: 28px;
          font-weight: bold;
          color: #333;
        }

        .stat-label {
          font-size: 14px;
          color: #999;
          margin-top: 4px;
        }
      }
    }
  }
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;

  .el-button {
    width: 100%;
  }
}
</style>
