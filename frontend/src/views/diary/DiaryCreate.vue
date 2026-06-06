<template>
  <div class="diary-create">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑日记' : '写日记' }}</span>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入日记标题" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="情绪类型" prop="emotionType">
          <el-select v-model="form.emotionType" placeholder="选择当前主要情绪">
            <el-option label="😊 开心" :value="1" />
            <el-option label="😌 平静" :value="2" />
            <el-option label="😰 焦虑" :value="3" />
            <el-option label="😢 悲伤" :value="4" />
            <el-option label="😡 愤怒" :value="5" />
            <el-option label="😨 恐惧" :value="6" />
            <el-option label="😶 其他" :value="7" />
          </el-select>
        </el-form-item>

        <el-form-item label="情绪强度" prop="emotionScore">
          <div class="score-slider">
            <el-slider v-model="form.emotionScore" :min="1" :max="10" show-stops :format-tooltip="(val: number) => `${val}分`" />
            <span class="score-text">{{ form.emotionScore }} 分</span>
          </div>
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="记录你的心情和想法..." 
                    maxlength="5000" show-word-limit />
        </el-form-item>

        <el-form-item label="隐私设置">
          <el-switch v-model="isPrivate" active-text="私密" inactive-text="公开" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            {{ isEdit ? '保存修改' : '发布日记' }}
          </el-button>
          <el-button @click="$router.back()">返回</el-button>
          <el-button type="success" icon="MagicStick" @click="handleAiAnalyze" :loading="analyzing">
            AI智能分析
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="aiResult" style="margin-top: 20px;" class="ai-analysis-card">
      <template #header>
        <div class="analysis-header">
          <span>🤖 AI分析结果</span>
          <el-tag type="success">实时分析</el-tag>
        </div>
      </template>
      
      <div class="analysis-content" v-if="aiResult.emotion_analysis">
        <h4>📊 情绪分析</h4>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="情绪类型">{{ aiResult.emotion_analysis.emotion_type }}</el-descriptions-item>
          <el-descriptions-item label="强度评分">{{ aiResult.emotion_analysis.emotion_score }}/10</el-descriptions-item>
          <el-descriptions-item label="关键词" :span="2">
            <el-tag v-for="(kw, idx) in aiResult.emotion_analysis.keywords" :key="idx" style="margin-right: 5px;">
              {{ kw }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总结" :span="2">{{ aiResult.emotion_analysis.summary }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <h4>💡 建议</h4>
        <ul class="suggestions-list">
          <li v-for="(suggestion, idx) in aiResult.emotion_analysis.suggestions" :key="idx">
            {{ suggestion }}
          </li>
        </ul>
      </div>

      <div class="risk-warning" v-if="aiResult.risk_assessment && aiResult.risk_assessment.risk_level > 0">
        <el-alert :title="`⚠️ 风险评估：${getRiskLabel(aiResult.risk_assessment.risk_level)}`" 
                  :type="aiResult.risk_assessment.risk_level >= 2 ? 'error' : 'warning'" show-icon :closable="false">
          {{ aiResult.risk_assessment.recommendation }}
        </el-alert>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const submitting = ref(false)
const analyzing = ref(false)
const isPrivate = ref(true)
const aiResult = ref<any>(null)

const isEdit = !!route.params.id

const form = reactive({
  title: '',
  emotionType: null as number | null,
  emotionScore: 5,
  content: '',
  isPrivate: 1
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入日记标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入日记内容', trigger: 'blur' }]
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    form.isPrivate = isPrivate.value ? 1 : 0
    
    if (isEdit) {
      await request.put(`/diary/${route.params.id}`, form)
      ElMessage.success('修改成功')
    } else {
      await request.post('/diary', form)
      ElMessage.success('发布成功')
    }
    
    router.push('/diary')
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const handleAiAnalyze = async () => {
  if (!form.content) {
    ElMessage.warning('请先填写日记内容')
    return
  }

  analyzing.value = true
  try {
    const res = await request.post('/ai/analyze/content', { content: form.content })
    aiResult.value = res.data
    ElMessage.success('AI分析完成')
  } catch (error) {
    console.error(error)
  } finally {
    analyzing.value = false
  }
}

const getRiskLabel = (level: number) => {
  const map: Record<number, string> = { 1: '低风险', 2: '中风险', 3: '高风险' }
  return map[level] || '正常'
}
</script>

<style scoped lang="scss">
.score-slider {
  display: flex;
  align-items: center;
  gap: 20px;
  flex: 1;

  .el-slider {
    flex: 1;
  }

  .score-text {
    font-weight: bold;
    color: #409eff;
    min-width: 50px;
  }
}

.ai-analysis-card {
  .analysis-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  h4 {
    margin-bottom: 15px;
    color: #333;
  }

  .suggestions-list {
    padding-left: 20px;
    line-height: 2;

    li {
      color: #666;
    }
  }

  .risk-warning {
    margin-top: 20px;
  }
}
</style>
