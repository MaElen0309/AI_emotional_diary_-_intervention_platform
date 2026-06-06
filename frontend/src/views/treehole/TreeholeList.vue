<template>
  <div class="treehole-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>🌳 匿名树洞</span>
          <el-button type="primary" icon="Plus" @click="showPublishDialog">发布心声</el-button>
        </div>
      </template>

      <div class="treehole-content" v-loading="loading">
        <div v-if="tableData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无内容，快来分享你的故事吧~" />
        </div>

        <div v-for="item in tableData" :key="item.id" class="treehole-item">
          <div class="item-header">
            <div class="author-info">
              <el-avatar :size="36">{{ item.anonymousName?.charAt(0) || '匿' }}</el-avatar>
              <span class="name">{{ item.anonymousName || '匿名用户' }}</span>
              <el-tag size="small" type="info">{{ item.createTime }}</el-tag>
            </div>
          </div>

          <h3 class="item-title">{{ item.title }}</h3>
          <p class="item-content">{{ item.content }}</p>

          <div class="item-footer">
            <span class="stat-item" @click="handleLike(item)">
              <el-icon><Star /></el-icon>
              {{ item.likeCount || 0 }}
            </span>
            <span class="stat-item">
              <el-icon><View /></el-icon>
              {{ item.viewCount || 0 }}
            </span>
          </div>
        </div>
      </div>

      <el-pagination
        v-model:current-page="pageNum"
        :total="total"
        :page-size="pageSize"
        layout="prev, pager, next"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: center;"
      />
    </el-card>

    <el-dialog v-model="publishVisible" title="发布到树洞" width="600px">
      <el-form ref="publishFormRef" :model="publishForm" :rules="publishRules" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="publishForm.anonymousName" placeholder="可选，不填则完全匿名" maxlength="20" />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="publishForm.title" placeholder="请输入标题" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="publishForm.content" type="textarea" :rows="6" placeholder="说出你的心里话..." 
                    maxlength="2000" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handlePublish">发布</el-button>
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
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref<any[]>([])
const publishVisible = ref(false)
const publishFormRef = ref<FormInstance>()

const publishForm = reactive({
  anonymousName: '',
  title: '',
  content: ''
})

const publishRules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/treehole/published', {
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

const showPublishDialog = () => {
  Object.assign(publishForm, { anonymousName: '', title: '', content: '' })
  publishVisible.value = true
}

const handlePublish = async () => {
  const valid = await publishFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await request.post('/treehole', publishForm)
    ElMessage.success('发布成功，等待审核后显示')
    publishVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const handleLike = async (item: any) => {
  try {
    await request.post(`/treehole/${item.id}/like`)
    item.likeCount = (item.likeCount || 0) + 1
    ElMessage.success('点赞成功')
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.treehole-item {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .author-info {
      display: flex;
      align-items: center;
      gap: 10px;

      .name {
        font-weight: 500;
        color: #333;
      }
    }
  }

  .item-title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 10px;
    color: #333;
  }

  .item-content {
    color: #666;
    line-height: 1.6;
    margin-bottom: 15px;
    display: -webkit-box;
    -webkit-line-clamp: 4;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .item-footer {
    display: flex;
    gap: 20px;
    padding-top: 12px;
    border-top: 1px solid #f0f0f0;

    .stat-item {
      display: flex;
      align-items: center;
      gap: 5px;
      cursor: pointer;
      color: #999;
      
      &:hover {
        color: #409eff;
      }
    }
  }
}

.empty-state {
  padding: 60px 0;
}
</style>
