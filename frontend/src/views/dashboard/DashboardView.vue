<template>
  <div class="dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-text">
        <h1>欢迎回来</h1>
        <p>今天也要记录心情哦~</p>
      </div>
      <div class="welcome-actions">
        <el-button type="primary" round @click="$router.push('/diary/create')">
          <el-icon><EditPen /></el-icon>
          写日记
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div v-for="(stat, index) in statCards" :key="index" class="stat-card" :style="{ '--accent': stat.color }">
        <div class="stat-bg-icon">
          <component :is="stat.icon" />
        </div>
        <div class="stat-body">
          <span class="stat-number">{{ stats[stat.key] }}</span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-grid">
      <!-- 最近日记 -->
      <div class="card-panel">
        <div class="panel-header">
          <h3>最近情绪记录</h3>
          <el-button text type="primary" @click="$router.push('/diary')">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div class="diary-list" v-if="recentDiaries.length > 0">
          <div v-for="item in recentDiaries" :key="item.id" class="diary-item" @click="$router.push(`/diary/${item.id}`)">
            <div class="diary-emotion" :class="'emotion-' + (item.emotionType || 1)">{{ getEmotionLabel(item.emotionType) }}</div>
            <div class="diary-info">
              <div class="diary-title">{{ item.title || '无标题' }}</div>
              <div class="diary-time">{{ item.createTime }}</div>
            </div>
            <el-icon class="arrow"><ArrowRight /></el-icon>
          </div>
        </div>
        <div v-else class="empty-state">
          <el-icon :size="40"><Notebook /></el-icon>
          <p>还没有日记记录</p>
          <el-button type="primary" size="small" @click="$router.push('/diary/create')">开始写第一篇</el-button>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="card-panel quick-panel">
        <div class="panel-header">
          <h3>快捷操作</h3>
        </div>
        <div class="quick-grid">
          <button class="quick-btn quick-btn-primary" @click="$router.push('/diary/create')">
            <el-icon><EditPen /></el-icon>
            <span>写日记</span>
          </button>
          <button class="quick-btn quick-btn-success" @click="$router.push('/ai-analysis')">
            <el-icon><MagicStick /></el-icon>
            <span>AI分析</span>
          </button>
          <button class="quick-btn quick-btn-warning" @click="$router.push('/solutions')">
            <el-icon><FolderOpened /></el-icon>
            <span>浏览方案</span>
          </button>
          <button class="quick-btn quick-btn-info" @click="$router.push('/treehole')">
            <el-icon><ChatDotRound /></el-icon>
            <span>去树洞</span>
          </button>
        </div>

        <!-- 今日提示 -->
        <div class="tip-card">
          <div class="tip-icon">
            <el-icon><Sunny /></el-icon>
          </div>
          <div class="tip-content">
            <strong>每日小贴士</strong>
            <p>每天花5分钟记录情绪，有助于更好地了解自己。</p>
          </div>
        </div>
      </div>
    </div>
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

const statCards = [
  { key: 'diaryCount', label: '日记总数', icon: 'Notebook', color: '#6c63ff' },
  { key: 'treeholeCount', label: '树洞帖子', icon: 'ChatDotRound', color: '#10b981' },
  { key: 'aiAnalysisCount', label: 'AI分析次数', icon: 'MagicStick', color: '#f59e0b' },
  { key: 'solutionCount', label: '方案数量', icon: 'FolderOpened', color: '#ef4444' },
]

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

const getEmotionLabel = (type: number) => {
  const map: Record<number, string> = { 1: '开心', 2: '平静', 3: '焦虑', 4: '悲伤', 5: '愤怒', 6: '恐惧', 7: '其他' }
  return map[type] || '未知'
}
</script>

<style scoped lang="scss">
/* ===== 变量 ===== */
$primary: #6c63ff;
$success: #10b981;
$warning: #f59e0b;
$danger: #ef4444;
$info: #3b82f6;
$text-dark: #1e293b;
$text-muted: #94a3b8;
$bg-card: #ffffff;
$bg-body: #f1f5f9;

/* ===== 欢迎区域 ===== */
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  padding: 24px 28px;
  background: linear-gradient(135deg, $primary, #8b5cf6);
  border-radius: 16px;
  color: white;
  box-shadow: 0 8px 24px rgba($primary, 0.25);

  .welcome-text h1 {
    font-size: 24px;
    font-weight: 800;
    margin: 0 0 4px 0;
    letter-spacing: -0.5px;
  }

  .welcome-text p {
    margin: 0;
    opacity: 0.85;
    font-size: 14px;
  }

  .welcome-actions .el-button {
    background: white;
    color: $primary;
    border: none;
    font-weight: 600;
    padding: 10px 22px;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
  }

  @media (max-width: 767px) {
    flex-direction: column;
    gap: 16px;
    padding: 20px;
    text-align: center;

    .welcome-text h1 {
      font-size: 20px;
    }
  }
}

/* ===== 统计卡片 ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
  margin-bottom: 28px;

  .stat-card {
    position: relative;
    background: $bg-card;
    border-radius: 16px;
    padding: 22px 24px;
    overflow: hidden;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06), 0 1px 2px rgba(0, 0, 0, 0.04);
    transition: transform 0.25s ease, box-shadow 0.25s ease;
    cursor: default;

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 12px 28px rgba(0, 0, 0, 0.1);
    }

    .stat-bg-icon {
      position: absolute;
      right: -4px;
      top: -4px;
      width: 72px;
      height: 72px;
      border-radius: 50%;
      background: var(--accent);
      opacity: 0.08;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 32px;
      color: var(--accent);

      svg {
        position: relative;
        right: -6px;
        bottom: -6px;
      }
    }

    .stat-body {
      position: relative;
      z-index: 1;
      display: flex;
      flex-direction: column;
      gap: 4px;

      .stat-number {
        font-size: 30px;
        font-weight: 800;
        color: $text-dark;
        line-height: 1.1;
        letter-spacing: -1px;
      }

      .stat-label {
        font-size: 13px;
        color: $text-muted;
        font-weight: 500;
      }
    }
  }

  @media (max-width: 992px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 480px) {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;

    .stat-card {
      padding: 16px 18px;

      .stat-bg-icon {
        width: 56px;
        height: 56px;
        font-size: 26px;
      }

      .stat-body .stat-number {
        font-size: 24px;
      }
    }
  }
}

/* ===== 内容网格 ===== */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 24px;

  @media (max-width: 992px) {
    grid-template-columns: 1fr;
  }
}

/* ===== 面板通用样式 ===== */
.card-panel {
  background: $bg-card;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06), 0 1px 2px rgba(0, 0, 0, 0.04);

  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h3 {
      font-size: 17px;
      font-weight: 700;
      color: $text-dark;
      margin: 0;
    }
  }
}

/* ===== 日记列表 ===== */
.diary-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.diary-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 12px;
  transition: all 0.2s ease;
  cursor: pointer;
  border: 1px solid transparent;

  &:hover {
    background: $bg-body;
    border-color: #e2e8f0;
  }

  .diary-emotion {
    flex-shrink: 0;
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
    min-width: 48px;
    text-align: center;

    &.emotion-1 { background: #dcfce7; color: #166534; }
    &.emotion-2 { background: #dbeafe; color: #1e40af; }
    &.emotion-3 { background: #fef3c7; color: #92400e; }
    &.emotion-4 { background: #fee2e2; color: #991b1b; }
    &.emotion-5 { background: #fce7f3; color: #9d174d; }
  }

  .diary-info {
    flex: 1;
    min-width: 0;

    .diary-title {
      font-size: 14px;
      font-weight: 600;
      color: $text-dark;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .diary-time {
      font-size: 12px;
      color: $text-muted;
      margin-top: 3px;
    }
  }

  .arrow {
    color: $text-muted;
    font-size: 14px;
    flex-shrink: 0;
    opacity: 0;
    transition: opacity 0.2s;
  }

  &:hover .arrow {
    opacity: 1;
  }
}

.empty-state {
  text-align: center;
  padding: 36px 20px;
  color: $text-muted;

  .el-icon {
    margin-bottom: 12px;
    opacity: 0.35;
  }

  p {
    margin: 0 0 16px 0;
    font-size: 14px;
  }
}

/* ===== 快捷操作面板 ===== */
.quick-panel {
  display: flex;
  flex-direction: column;
}

.quick-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 20px;
}

.quick-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 20px 12px;
  border-radius: 14px;
  border: none;
  cursor: pointer;
  transition: all 0.25s ease;
  font-size: 13px;
  font-weight: 600;
  color: white;

  .el-icon {
    font-size: 24px;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  }

  &-primary { background: linear-gradient(135deg, $primary, #8b5cf6); }
  &-success { background: linear-gradient(135deg, $success, #34d399); }
  &-warning { background: linear-gradient(135deg, $warning, #fbbf24); }
  &-info { background: linear-gradient(135deg, $info, #60a5fa); }
}

/* ===== 提示卡片 ===== */
.tip-card {
  display: flex;
  gap: 14px;
  padding: 18px;
  background: linear-gradient(135deg, #fefce8, #fef9c3);
  border-radius: 14px;
  border-left: 4px solid #eab308;

  .tip-icon {
    flex-shrink: 0;
    width: 40px;
    height: 40px;
    border-radius: 10px;
    background: white;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #ca8a04;
    font-size: 20px;
  }

  .tip-content {
    strong {
      display: block;
      font-size: 13px;
      color: #854d0e;
      margin-bottom: 4px;
    }

    p {
      margin: 0;
      font-size: 12px;
      color: #a16207;
      line-height: 1.55;
    }
  }
}
</style>
