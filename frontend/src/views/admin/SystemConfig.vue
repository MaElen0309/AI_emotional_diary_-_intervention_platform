<template>
  <div class="system-config">
    <el-card>
      <template #header>
        <span>⚙️ 系统参数配置</span>
      </template>

      <el-alert title="修改系统参数后立即生效，请谨慎操作" type="warning" show-icon :closable="false" 
               style="margin-bottom: 20px;" />

      <el-form v-loading="loading" label-width="150px">
        <el-divider content-position="left">基本设置</el-divider>
        
        <el-form-item label="网站名称">
          <el-input v-model="configs['site.name']" placeholder="网站名称" style="width: 400px;" />
        </el-form-item>

        <el-divider content-position="left">AI服务设置</el-divider>
        
        <el-form-item label="自动AI分析">
          <el-switch v-model="aiAutoAnalyze" active-text="开启" inactive-text="关闭" />
          <span class="form-tip">用户发布日记时是否自动进行AI情绪分析</span>
        </el-form-item>

        <el-divider content-position="left">树洞设置</el-divider>
        
        <el-form-item label="自动审核通过">
          <el-switch v-model="treeholeAutoAudit" active-text="开启" inactive-text="关闭" />
          <span class="form-tip">开启后发布的树洞内容无需人工审核直接显示</span>
        </el-form-item>

        <el-divider content-position="left">会话设置</el-divider>
        
        <el-form-item label="Session超时时间">
          <el-input-number v-model="sessionTimeout" :min="300" :max="86400" :step="60" />
          <span class="form-tip">单位：秒，默认1800秒（30分钟）</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="saving" @click="saveConfig">保存配置</el-button>
          <el-button @click="loadConfig">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>📊 系统统计信息</span>
      </template>
      
      <el-row :gutter="20" v-loading="statsLoading">
        <el-col :span="6" v-for="stat in statsList" :key="stat.label">
          <div class="stat-item">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const saving = ref(false)
const statsLoading = ref(false)
const configs = reactive<Record<string, string>>({})
const aiAutoAnalyze = ref(true)
const treeholeAutoAudit = ref(false)
const sessionTimeout = ref(1800)

const statsList = ref([
  { label: '注册用户数', value: 0 },
  { label: '日记总数', value: 0 },
  { label: '树洞帖子数', value: 0 },
  { label: 'AI分析次数', value: 0 }
])

onMounted(() => {
  loadConfig()
  loadStats()
})

const loadConfig = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/config')
    Object.assign(configs, res.data)
    
    aiAutoAnalyze.value = configs['ai.auto_analyze'] === 'true'
    treeholeAutoAudit.value = configs['audit.treehole_auto'] === 'true'
    sessionTimeout.value = parseInt(configs['session.timeout'] || '1800')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const saveConfig = async () => {
  saving.value = true
  try {
    const dataToUpdate: Record<string, string> = { ...configs }
    dataToUpdate['ai.auto_analyze'] = String(aiAutoAnalyze.value)
    dataToUpdate['audit.treehole_auto'] = String(treeholeAutoAudit.value)
    dataToUpdate['session.timeout'] = String(sessionTimeout.value)

    await request.put('/admin/config', dataToUpdate)
    ElMessage.success('配置保存成功')
  } catch (error) {
    console.error(error)
  } finally {
    saving.value = false
  }
}

const loadStats = async () => {
  statsLoading.value = true
  try {
    const [userRes, diaryRes, treeholeRes] = await Promise.all([
      request.get('/admin/users', { params: { pageSize: 1 } }),
      request.get('/diary', { params: { pageSize: 1 } }),
      request.get('/treehole/published', { params: { pageSize: 1 } })
    ])

    statsList.value[0].value = userRes.data?.total || 0
    statsList.value[1].value = diaryRes.data?.total || 0
    statsList.value[2].value = treeholeRes.data?.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    statsLoading.value = false
  }
}
</script>

<style scoped lang="scss">
.form-tip {
  margin-left: 15px;
  color: #999;
  font-size: 13px;
}

.stat-item {
  text-align: center;
  padding: 25px 0;

  .stat-value {
    font-size: 32px;
    font-weight: bold;
    color: #409eff;
  }

  .stat-label {
    margin-top: 8px;
    color: #666;
    font-size: 14px;
  }
}
</style>
