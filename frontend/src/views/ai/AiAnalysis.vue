<template>
  <div class="ai-analysis-page">
    <!-- 左侧输入区 -->
    <div class="input-panel">
      <div class="panel-card">
        <div class="card-header">
          <span class="header-icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2a10 10 0 1 0 10 10H12V2Z"/><path d="m4.93 19.07 1.41-1.41"/><path d="m12 20 .01-.01"/><path d="M12 8v4"/></svg>
          </span>
          <h3>AI 情绪分析</h3>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules">
          <el-form-item prop="content">
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="14"
              placeholder="写下你的心情、烦恼或任何想倾诉的内容...&#10;&#10;AI 将为你分析情绪状态，并自动生成个性化调节方案"
              maxlength="3000"
              show-word-limit
              resize="none"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" size="large" :loading="analyzing || generatingSolution" @click="handleAnalyze">
              <template #icon>
                <svg v-if="!analyzing && !generatingSolution" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M9.937 15.5A2 2 0 0 0 8.5 14.063l-6.135-1.582a.5.5 0 0 1 0-.962L8.5 9.936A2 2 0 0 0 9.937 8.5l1.582-6.135a.5.5 0 0 1 .963 0L14.063 8.5A2 2 0 0 0 15.5 9.937l6.135 1.581a.5.5 0 0 1 0 .964L15.5 14.063a2 2 0 0 0-1.437 1.437l-1.582 6.135a.5.5 0 0 1-.963 0z"/></svg>
                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" class="spin-icon"><path d="M21 12a9 9 0 1 1-6.219-8.56"/></svg>
              </template>
              {{ analyzing ? '正在分析...' : generatingSolution ? '生成方案中...' : '开始分析' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 右侧结果区 -->
    <div class="result-panel">
      <!-- 空状态 -->
      <div v-if="!analysisResult && !generatingSolution" class="empty-state">
        <div class="empty-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#c7d2fe" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M8 14s1.5 2 4 2 4-2 4-2"/><line x1="9" y1="9" x2="9.01" y2="9"/><line x1="15" y1="9" x2="15.01" y2="9"/></svg>
        </div>
        <p>在左侧输入内容后<br>AI 将为你分析情绪并生成调节方案</p>
      </div>

      <!-- 加载中 -->
      <div v-if="analyzing && !analysisResult" class="loading-state">
        <div class="loading-card">
          <div class="loading-spinner"></div>
          <p>AI 正在深度分析你的情绪...</p>
        </div>
      </div>

      <!-- 分析结果 + 方案（内联显示） -->
      <template v-if="analysisResult">
        <!-- 情绪识别卡片 -->
        <div class="result-section emotion-section">
          <div class="section-header">
            <span class="section-badge">情绪识别</span>
            <el-tag :type="getEmotionType(analysisResult.emotion_analysis?.emotion_type)" size="small" effect="dark">
              {{ analysisResult.emotion_analysis?.emotion_type }}
            </el-tag>
          </div>

          <div class="emotion-body">
            <div class="emotion-score">
              <div class="score-ring">
                <svg viewBox="0 0 100 100">
                  <circle cx="50" cy="50" r="42" fill="none" stroke="#e2e8f0" stroke-width="8"/>
                  <circle cx="50" cy="50" r="42" fill="none" :stroke="scoreColor" stroke-width="8"
                    stroke-dasharray="264" :stroke-dashoffset="264 - (264 * (analysisResult.emotion_analysis?.emotion_score || 0) / 10)"
                    stroke-linecap="round" transform="rotate(-90 50 50)"/>
                </svg>
                <span class="score-num">{{ analysisResult.emotion_analysis?.emotion_score }}</span>
              </div>
              <span class="score-label">强度</span>
            </div>

            <div class="emotion-info">
              <p class="summary-text">{{ analysisResult.emotion_analysis?.summary }}</p>
              <div class="keywords-row" v-if="analysisResult.emotion_analysis?.keywords">
                <span class="kw-label">关键词</span>
                <el-tag v-for="(kw, idx) in analysisResult.emotion_analysis.keywords.slice(0, 4)" :key="idx" size="small" round>{{ kw }}</el-tag>
              </div>
            </div>
          </div>
        </div>

        <!-- 风险评估卡片 -->
        <div class="result-section risk-section" v-if="analysisResult.risk_assessment">
          <div class="section-header">
            <span class="section-badge risk">风险评估</span>
          </div>
          <div class="risk-body">
            <div class="risk-level-bar">
              <div class="risk-fill" :class="'risk-' + (analysisResult.risk_assessment.risk_level || 0)"
                   :style="{ width: ((analysisResult.risk_assessment.risk_level || 0) / 3 * 100) + '%' }"></div>
            </div>
            <div class="risk-text">{{ getRiskLabel(analysisResult.risk_assessment.risk_level) }}</div>
            <p class="risk-recommendation">{{ analysisResult.risk_assessment.recommendation }}</p>
          </div>
        </div>

        <!-- 自动生成的调节方案 - 直接内联显示 -->
        <div class="result-section solution-section">
          <div class="section-header">
            <span class="section-badge solution">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M9.937 15.5A2 2 0 0 0 8.5 14.063l-6.135-1.582a.5.5 0 0 1 0-.962L8.5 9.936A2 2 0 0 0 9.937 8.5l1.582-6.135a.5.5 0 0 1 .963 0L14.063 8.5A2 2 0 0 0 15.5 9.937l6.135 1.581a.5.5 0 0 1 0 .964L15.5 14.063a2 2 0 0 0-1.437 1.437l-1.582 6.135a.5.5 0 0 1-.963 0Z"/></svg>
              个性化调节方案
            </span>
          </div>

          <!-- 方案加载中 -->
          <div v-if="generatingSolution" class="solution-loading">
            <div class="solution-loading-spinner"></div>
            <p>AI 正在为你量身定制调节方案...</p>
          </div>

          <!-- 方案内容 -->
          <div v-if="generatedSolution && !generatingSolution" class="solution-content">
            <h4 class="solution-title">{{ generatedSolution.title }}</h4>

            <!-- 调节方法列表 -->
            <div class="methods-list" v-if="generatedSolution.methods">
              <div v-for="(method, idx) in generatedSolution.methods" :key="idx" class="method-card">
                <div class="method-index">{{ idx + 1 }}</div>
                <div class="method-body">
                  <div class="method-top">
                    <h5 class="method-name">{{ method.name }}</h5>
                    <div class="method-tags">
                      <span class="duration-tag">
                        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                        {{ method.duration_minutes }}分钟
                      </span>
                      <el-tag size="small" :type="getDifficultyType(method.difficulty)" effect="plain" round>
                        {{ method.difficulty }}
                      </el-tag>
                    </div>
                  </div>
                  <p class="method-desc">{{ method.description }}</p>
                </div>
              </div>
            </div>

            <!-- 每日建议 -->
            <div class="daily-tips" v-if="generatedSolution.daily_tips">
              <h5 class="tips-title">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"/></svg>
                每日建议
              </h5>
              <ul>
                <li v-for="(tip, idx) in generatedSolution.daily_tips" :key="idx">{{ tip }}</li>
              </ul>
            </div>

            <!-- 推荐资源 -->
            <div class="resources" v-if="generatedSolution.resources">
              <h5 class="resources-title">推荐资源</h5>
              <div class="resource-list">
                <span v-for="(res, idx) in generatedSolution.resources" :key="idx" class="resource-chip">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg>
                  {{ res }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import request from '@/utils/request'

const formRef = ref<FormInstance>()
const analyzing = ref(false)
const generatingSolution = ref(false)
const analysisResult = ref<any>(null)
const generatedSolution = ref<any>(null)

const form = reactive({ content: '' })

const rules: FormRules = {
  content: [{ required: true, message: '请输入要分析的内容', trigger: 'blur' }]
}

const scoreColor = computed(() => {
  const score = analysisResult.value?.emotion_analysis?.emotion_score || 0
  if (score <= 3) return '#10b981'
  if (score <= 6) return '#f59e0b'
  return '#ef4444'
})

const handleAnalyze = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  // 重置状态
  analysisResult.value = null
  generatedSolution.value = null
  analyzing.value = true

  try {
    const res = await request.post('/ai/analyze/content', { content: form.content })
    analysisResult.value = res.data
    ElMessage.success('分析完成')

    // 分析完成后自动生成方案
    autoGenerateSolution()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '分析失败')
  } finally {
    analyzing.value = false
  }
}

const autoGenerateSolution = async () => {
  if (!analysisResult.value?.emotion_analysis?.emotion_type) return

  generatingSolution.value = true
  try {
    const res = await request.post('/ai/generate/solution', {
      emotionType: analysisResult.value.emotion_analysis.emotion_type,
      content: form.content
    })
    generatedSolution.value = res.data
    ElMessage.success('调节方案已生成')
  } catch (error: any) {
    ElMessage.error('方案生成失败：' + (error.response?.data?.message || error.message))
  } finally {
    generatingSolution.value = false
  }
}

const getEmotionType = (type: string) => {
  const map: Record<string, string> = {
    '开心': 'success', '平静': '', '焦虑': 'warning', '悲伤': 'danger', '愤怒': 'danger', '恐惧': 'danger'
  }
  return map[type] || ''
}

const getRiskLabel = (level: number) => {
  const map: Record<number, string> = { 0: '正常', 1: '低风险', 2: '中风险', 3: '高风险' }
  return map[level] || '未知'
}

const getDifficultyType = (diff: string) => {
  const map: Record<string, string> = { '初级': 'success', '中级': 'warning', '高级': 'danger' }
  return map[diff] || ''
}
</script>

<style scoped lang="scss">
$primary: #6c63ff;
$primary-light: #8b83ff;
$bg-card: #ffffff;
$bg-body: #f1f5f9;
$text-dark: #1e293b;
$text-muted: #94a3b8;

.ai-analysis-page {
  display: grid;
  grid-template-columns: 520px 1fr;
  gap: 24px;
  min-height: calc(100vh - 60px);
}

/* ===== 输入面板 ===== */
.input-panel {
  position: sticky;
  top: 84px;
  align-self: start;

  .panel-card {
    background: $bg-card;
    border-radius: 16px;
    padding: 32px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .card-header {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 24px;

      .header-icon {
        width: 44px;
        height: 44px;
        border-radius: 12px;
        background: linear-gradient(135deg, $primary, #a78bfa);
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      h3 {
        font-size: 18px;
        font-weight: 700;
        color: $text-dark;
      }
    }

    :deep(.el-button--primary) {
      width: 100%;
      height: 48px;
      font-size: 15px;
      border-radius: 12px;

      &.is-loading {
        opacity: 0.85;
      }
    }

    :deep(.el-textarea__inner) {
      border-radius: 12px;
      font-size: 15px;
      line-height: 1.75;
      padding: 16px 18px;
      transition: all 0.25s ease;

      &:focus {
        box-shadow: 0 0 0 3px rgba($primary, 0.12);
      }
    }
  }
}

/* ===== 结果面板 ===== */
.result-panel {
  display: flex;
  flex-direction: column;
  gap: 18px;
  overflow-y: auto;
  padding-right: 4px;

  &::-webkit-scrollbar {
    width: 5px;
  }
  &::-webkit-scrollbar-thumb {
    background: #cbd5e1;
    border-radius: 5px;
  }
}

/* ===== 空状态 ===== */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  text-align: center;

  .empty-icon {
    margin-bottom: 20px;
    animation: float 3s ease-in-out infinite;
  }

  p {
    color: $text-muted;
    font-size: 15px;
    line-height: 1.8;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

/* ===== 加载状态 ===== */
.loading-state {
  .loading-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 200px;
    gap: 18px;

    .loading-spinner {
      width: 44px;
      height: 44px;
      border: 3.5px solid #e2e8f0;
      border-top-color: $primary;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }

    p {
      color: $text-muted;
      font-size: 14px;
    }
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.spin-icon {
  animation: spin 1s linear infinite;
}

/* ===== 结果区块通用 ===== */
.result-section {
  background: $bg-card;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

  .section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 18px;

    .section-badge {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      font-size: 14px;
      font-weight: 700;
      color: $primary;
      padding: 5px 14px;
      background: rgba($primary, 0.08);
      border-radius: 20px;

      &.risk { color: #f59e0b; background: rgba(245, 158, 11, 0.08); }
      &.solution { color: #10b981; background: rgba(16, 185, 129, 0.08); }
    }
  }
}

/* ===== 情绪识别 ===== */
.emotion-section {
  .emotion-body {
    display: flex;
    gap: 24px;
    align-items: flex-start;
  }

  .emotion-score {
    flex-shrink: 0;
    text-align: center;

    .score-ring {
      position: relative;
      width: 90px;
      height: 90px;

      svg { position: absolute; inset: 0; }

      .score-num {
        position: absolute;
        inset: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 26px;
        font-weight: 800;
        color: $text-dark;
      }
    }

    .score-label {
      display: block;
      margin-top: 6px;
      font-size: 12px;
      color: $text-muted;
      font-weight: 600;
    }
  }

  .emotion-info {
    flex: 1;

    .summary-text {
      font-size: 14.5px;
      line-height: 1.75;
      color: #475569;
      margin-bottom: 14px;
      padding: 14px 16px;
      background: #f8fafc;
      border-radius: 10px;
      border-left: 3px solid $primary;
    }

    .keywords-row {
      display: flex;
      align-items: center;
      gap: 8px;
      flex-wrap: wrap;

      .kw-label {
        font-size: 12px;
        font-weight: 700;
        color: $text-muted;
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }
    }
  }
}

/* ===== 风险评估 ===== */
.risk-section {
  .risk-body {
    .risk-level-bar {
      height: 8px;
      background: #f1f5f9;
      border-radius: 4px;
      overflow: hidden;
      margin-bottom: 10px;

      .risk-fill {
        height: 100%;
        border-radius: 4px;
        transition: width 0.6s ease;

        &.risk-0 { background: #10b981; }
        &.risk-1 { background: #f59e0b; }
        &.risk-2 { background: #f97316; }
        &.risk-3 { background: #ef4444; }
      }
    }

    .risk-text {
      font-weight: 700;
      font-size: 14px;
      color: $text-dark;
      margin-bottom: 10px;
    }

    .risk-recommendation {
      font-size: 14px;
      line-height: 1.7;
      color: #64748b;
      padding: 14px 16px;
      background: #fffbeb;
      border-radius: 10px;
      border-left: 3px solid #f59e0b;
    }
  }
}

/* ===== 调节方案 ===== */
.solution-section {
  border: 1.5px solid rgba($primary, 0.1);

  .solution-loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 30px;
    gap: 14px;

    .solution-loading-spinner {
      width: 36px;
      height: 36px;
      border: 3px solid #e2e8f0;
      border-top-color: #10b981;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }

    p {
      color: $text-muted;
      font-size: 14px;
    }
  }

  .solution-content {
    .solution-title {
      font-size: 17px;
      font-weight: 750;
      color: $text-dark;
      text-align: center;
      margin-bottom: 22px;
      padding-bottom: 16px;
      border-bottom: 1px solid #f1f5f9;
    }

    /* 方法卡片 */
    .methods-list {
      display: flex;
      flex-direction: column;
      gap: 14px;
      margin-bottom: 24px;
    }

    .method-card {
      display: flex;
      gap: 14px;
      padding: 18px;
      background: #fafbff;
      border-radius: 14px;
      border: 1px solid #f0efff;
      transition: all 0.25s ease;

      &:hover {
        background: #f5f3ff;
        border-color: rgba($primary, 0.2);
        transform: translateX(4px);
      }

      .method-index {
        width: 32px;
        height: 32px;
        border-radius: 10px;
        background: linear-gradient(135deg, $primary, $primary-light);
        color: white;
        font-size: 14px;
        font-weight: 800;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
      }

      .method-body {
        flex: 1;

        .method-top {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 8px;

          .method-name {
            font-size: 15px;
            font-weight: 700;
            color: $text-dark;
          }

          .method-tags {
            display: flex;
            align-items: center;
            gap: 8px;

            .duration-tag {
              display: inline-flex;
              align-items: center;
              gap: 4px;
              font-size: 12px;
              font-weight: 600;
              color: #64748b;
            }
          }
        }

        .method-desc {
          font-size: 13.5px;
          line-height: 1.7;
          color: #64748b;
        }
      }
    }

    /* 每日建议 */
    .daily-tips {
      background: linear-gradient(135deg, #ecfdf5 0%, #f0fdf4 100%);
      border-radius: 14px;
      padding: 20px;
      margin-bottom: 20px;

      .tips-title {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 14px;
        font-weight: 700;
        color: #059669;
        margin-bottom: 12px;
      }

      ul {
        list-style: none;
        padding: 0;

        li {
          position: relative;
          padding: 6px 0 6px 20px;
          font-size: 13.5px;
          line-height: 1.65;
          color: #047857;

          &::before {
            content: '';
            position: absolute;
            left: 0;
            top: 14px;
            width: 7px;
            height: 7px;
            border-radius: 50%;
            background: #10b981;
          }
        }
      }
    }

    /* 推荐资源 */
    .resources {
      .resources-title {
        font-size: 14px;
        font-weight: 700;
        color: $text-dark;
        margin-bottom: 12px;
      }

      .resource-list {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;

        .resource-chip {
          display: inline-flex;
          align-items: center;
          gap: 5px;
          padding: 6px 14px;
          background: #f1f5f9;
          border-radius: 20px;
          font-size: 13px;
          color: #475569;
          font-weight: 500;
          transition: all 0.2s;

          &:hover {
            background: #e2e8f0;
            color: $primary;
          }
        }
      }
    }
  }
}

/* ===== 响应式 ===== */
@media (max-width: 992px) {
  .ai-analysis-page {
    grid-template-columns: 1fr;
  }

  .input-panel {
    position: static;
  }
}

@media (max-width: 767px) {
  .ai-analysis-page {
    gap: 16px;
  }

  .input-panel .panel-card {
    padding: 20px;
  }

  .result-section {
    padding: 18px;
    border-radius: 14px;
  }

  .emotion-section .emotion-body {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .keywords-row {
    justify-content: center !important;
  }
}
</style>
