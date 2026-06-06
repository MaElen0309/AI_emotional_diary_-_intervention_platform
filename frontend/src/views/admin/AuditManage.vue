<template>
  <div class="audit-manage">
    <el-card>
      <template #header>
        <span>📋 树洞内容审核</span>
      </template>

      <el-tabs v-model="activeTab" @tab-change="loadData">
        <el-tab-pane label="待审核" name="pending">
          <el-badge :value="pendingCount" :max="99" class="tab-badge" />
        </el-tab-pane>
        <el-tab-pane label="已通过" name="approved" />
        <el-tab-pane label="已拒绝" name="rejected" />
      </el-tabs>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="anonymousName" label="发布者" width="120">
          <template #default="{ row }">
            {{ row.anonymousName || '匿名' }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容摘要" min-width="250" show-overflow-tooltip />
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <template v-if="activeTab === 'pending'">
              <el-button link type="success" @click="handleAudit(row, 1)">通过</el-button>
              <el-button link type="danger" @click="showRejectDialog(row)">拒绝</el-button>
            </template>
            <el-button link type="primary" @click="viewDetail(row)">查看详情</el-button>
            <el-button link type="info" @click="deleteContent(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        :total="total"
        :page-size="pageSize"
        layout="prev, pager, next"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: center;"
      />
    </el-card>

    <el-dialog v-model="rejectVisible" title="审核拒绝" width="500px">
      <el-form>
        <el-form-item label="拒绝原因">
          <el-input v-model="rejectRemark" type="textarea" :rows="4" placeholder="请输入拒绝原因..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="handleReject">确认拒绝</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="内容详情" width="700px">
      <div v-if="currentItem">
        <h3>{{ currentItem.title }}</h3>
        <p style="color: #666; margin-bottom: 15px;">
          发布者：{{ currentItem.anonymousName || '匿名' }} | 时间：{{ currentItem.createTime }}
        </p>
        <div style="line-height: 1.8; white-space: pre-wrap;">{{ currentItem.content }}</div>
        
        <el-divider v-if="currentItem.auditRemark" content-position="left">
          审核备注
        </el-divider>
        <p v-if="currentItem.auditRemark" style="color: #f56c6c;">{{ currentItem.auditRemark }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const activeTab = ref('pending')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const pendingCount = ref(0)
const tableData = ref<any[]>([])
const rejectVisible = ref(false)
const detailVisible = ref(false)
const rejectRemark = ref('')
const currentItem = ref<any>(null)

onMounted(() => loadData())

const loadData = async () => {
  loading.value = true
  try {
    const url = activeTab.value === 'pending' ? '/treehole/pending' : '/treehole/published'
    const res = await request.get(url, {
      params: { pageNum: pageNum.value, pageSize: pageSize.value }
    })
    tableData.value = res.data.list || []
    total.value = res.data.total || 0

    if (activeTab.value === 'pending') {
      pendingCount.value = total.value
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAudit = async (item: any, status: number) => {
  try {
    await ElMessageBox.confirm(
      `确定要${status === 1 ? '通过' : '拒绝'}该内容吗？`,
      '确认操作',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'info' }
    )
    
    await request.put(`/treehole/${item.id}/audit`, { status, remark: '' })
    ElMessage.success(status === 1 ? '已通过' : '已拒绝')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const showRejectDialog = (item: any) => {
  currentItem.value = item
  rejectRemark.value = ''
  rejectVisible.value = true
}

const handleReject = async () => {
  if (!rejectRemark.value.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }

  try {
    await request.put(`/treehole/${currentItem.value.id}/audit`, { 
      status: 2, 
      remark: rejectRemark.value 
    })
    ElMessage.success('已拒绝')
    rejectVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const viewDetail = (item: any) => {
  currentItem.value = item
  detailVisible.value = true
}

const deleteContent = async (item: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该内容吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.delete(`/treehole/${item.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}
</script>

<style scoped lang="scss">
.tab-badge {
  margin-left: 10px;
}
</style>
