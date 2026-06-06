<template>
  <div class="ai-analysis">
    <el-row :gutter="20">
      <el-col :span="14">
        <el-card>
          <template #header>
            <span>🤖 AI情绪分析</span>
          </template>

          <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="内容" prop="content">
              <el-input v-model="form.content" type="textarea" :rows="8" 
                        placeholder="请输入需要分析的文本内容（日记、想法、感受等）..." 
                        maxlength="3000" show-word-limit />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" icon="MagicStick" :loading="analyzing" @click="handleAnalyze">
                开始AI分析
              </el-button>
              <el-button @click="form.content = ''">清空</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card v-if="analysisResult">
          <template #header>
            <span>📊 分析结果</span>
          </template>

          <div class="result-section" v-if="analysisResult.emotion_analysis">
            <h4>📈 情绪识别</h4>
            <div class="emotion-display">
              <div class="emotion-main">
                <el-tag size="large" :type="getEmotionType(analysisResult.emotion_analysis.emotion_type)">
                  {{ analysisResult.emotion_analysis.emotion_type }}
                </el-tag>
                <div class="score-circle">
                  <el-progress type="circle" :percentage="analysisResult.emotion_analysis.emotion_score * 10" 
                            :width="80" :format="(val: number) => `${val/10}`" />
                  <span class="label">强度</span>
                </div>
              </div>
              
              <div class="keywords" v-if="analysisResult.emotion_analysis.keywords">
                <span class="label-text">关键词：</span>
                <el-tag v-for="(kw, idx) in analysisResult.emotion_analysis.keywords" :key="idx" 
                        style="margin-right: 5px; margin-bottom: 5px;">
                  {{ kw }}
                </el-tag>
              </div>

              <div class="summary-box">
                <strong>AI总结：</strong>{{ analysisResult.emotion_analysis.summary }}
              </div>

              <div class="suggestions" v-if="analysisResult.emotion_analysis.suggestions">
                <strong>💡 建议：</strong>
                <ul>
                  <li v-for="(s, idx) in analysisResult.emotion_analysis.suggestions" :key="idx">{{ s }}</li>
                </ul>
              </div>
            </div>
          </div>

          <el-divider />

          <div class="risk-section" v-if="analysisResult.risk_assessment">
            <h4>⚠️ 风险评估</h4>
            <el-alert 
              :title="`风险等级：${getRiskLabel(analysisResult.risk_assessment.risk_level)}`"
              :type="getRiskAlertType(analysisResult.risk_assessment.risk_level)"
              show-icon :closable="false"
              style="margin-bottom: 15px;">
            </el-alert>

            <div class="recommendation" v-if="analysisResult.risk_assessment.recommendation">
              <strong>建议措施：</strong>
              <p>{{ analysisResult.risk_assessment.recommendation }}</p>
            </div>
          </div>

          <div style="margin-top: 20px;">
            <el-button type="success" icon="FolderOpened" @click="showGenerateSolution">
              生成个性化方案
            </el-button>
          </div>
        </el-card>

        <el-card v-else class="placeholder-card">
          <el-empty description="请在左侧输入内容并点击分析按钮" />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="solutionVisible" title="个性化心理调节方案" width="700px">
      <div v-if="generatedSolution" class="generated-solution">
        <h3>{{ generatedSolution.title }}</h3>
        
        <div class="methods-section" v-if="generatedSolution.methods">
          <h4>📋 调节方法</h4>
          <el-timeline>
            <el-timeline-item v-for="(method, idx) in generatedSolution.methods" :key="idx"
                             :timestamp="`${method.duration_minutes}分钟`" placement="top">
              <el-card shadow="hover">
                <h5>{{ method.name }}</h5>
                <p>{{ method.description }}</p>
                <el-tag size="small" :type="getDifficultyType(method.difficulty)">
                  {{ method.difficulty }}
                </el-tag>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>

        <div class="tips-section" v-if="generatedSolution.daily_tips">
          <h4>🌟 每日建议</h4>
          <ul>
            <li v-for="(tip, idx) in generatedSolution.daily_tips" :key="idx">{{ tip }}</li>
          </ul>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import request from '@/utils/request'

const formRef = ref<FormInstance>()
const analyzing = ref(false)
const solutionVisible = ref(false)
const analysisResult = ref<any>(null)
const generatedSolution = ref<any>(null)

const form = reactive({
  content: ''
})

const rules: FormRules = {
  content: [{ required: true, message: '请输入要分析的内容', trigger: 'blur' }]
}

const handleAnalyze = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  analyzing.value = true
  try {
    const res = await request.post('/ai/analyze/content', { content: form.content })
    analysisResult.value = res.data
    ElMessage.success('分析完成')
  } catch (error) {
    console.error(error)
  } finally {
    analyzing.value = false
  }
}

const showGenerateSolution = async () => {
  if (!analysisResult.value?.emotion_analysis?.emotion_type) {
    ElMessage.warning('请先进行情绪分析')
    return
  }

  try {
    ElMessage.info('正在生成个性化方案...')
    const res = await request.post('/ai/generate/solution', {
      emotionType: analysisResult.value.emotion_analysis.emotion_type,
      content: form.content
    })
    generatedSolution.value = res.data
    solutionVisible.value = true
  } catch (error) {
    console.error(error)
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

const getRiskAlertType = (level: number) => {
  const map: Record<number, string> = { 0: 'info', 1: 'warning', 2: 'error', 3: 'error' }
  return map[level] || 'info'
}

const getDifficultyType = (diff: string) => {
  const map: Record<string, string> = { '初级': 'success', '中级': 'warning', '高级': 'danger' }
  return map[diff] || ''
}
</script>

<style scoped lang="scss">
.result-section, .risk-section {
  h4 {
    margin-bottom: 15px;
    color: #333;
  }

  .emotion-display {
    .emotion-main {
      display: flex;
      justify-content: space-around;
      align-items: center;
      margin-bottom: 20px;
      padding: 20px;
      background: linear-gradient(135deg, #f5f7fa 0%, #fff 100%);
      border-radius: 8px;

      .score-circle {
        text-align: center;

        .label {
          display: block;
          margin-top: 5px;
          font-size: 12px;
          color: #999;
        }
      }
    }

    .keywords {
      margin-bottom: 15px;

      .label-text {
        font-weight: bold;
        margin-right: 10px;
      }
    }

    .summary-box {
      background: #ecf5ff;
      padding: 15px;
      border-radius: 4px;
      margin-bottom: 15px;
      line-height: 1.6;
    }

    .suggestions {
      ul {
        padding-left: 20px;
        line-height: 2;

        li {
          color: #666;
        }
      }
    }
  }

  .recommendation {
    background: #fef0e6;
    padding: 15px;
    border-radius: 4px;
    line-height: 1.6;

    p {
      margin-top: 8px;
      color: #666;
    }
  }
}

.placeholder-card {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.generated-solution {
  h3 {
    text-align: center;
    color: #333;
    margin-bottom: 25px;
  }

  h4 {
    margin-bottom: 15px;
    color: #555;
  }

  .tips-section {
    ul {
      padding-left: 20px;
      line-height: 2;

      li {
        color: #666;
      }
    }
  }
}
</style>
