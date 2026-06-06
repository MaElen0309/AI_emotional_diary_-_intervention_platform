<template>
  <div class="user-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div>
            <el-input v-model="keyword" placeholder="搜索用户名/姓名" clearable style="width: 200px; margin-right: 10px;" 
                      @keyup.enter="loadData" />
            <el-button type="primary" icon="Search" @click="loadData">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">{{ getRoleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" 
                       @change="(val: number) => handleStatusChange(row.id, val)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewUser(row)">查看</el-button>
            <el-button link type="danger" @click="deleteUser(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" title="用户详情" width="600px">
      <el-descriptions :column="2" border v-if="currentUser">
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ currentUser.realName }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ getRoleLabel(currentUser.role) }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ currentUser.studentId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="院系">{{ currentUser.department || '-' }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ currentUser.major || '-' }}</el-descriptions-item>
        <el-descriptions-item label="年级">{{ currentUser.grade || '-' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ currentUser.createTime }}</el-descriptions-item>
      </el-descriptions>
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
const currentUser = ref<any>(null)

onMounted(() => loadData())

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/users', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value }
    })
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const viewUser = async (row: any) => {
  try {
    const res = await request.get(`/admin/users/${row.id}`)
    currentUser.value = res.data
    dialogVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const handleStatusChange = async (userId: number, status: number) => {
  try {
    await request.put(`/admin/users/${userId}/status`, { status })
    ElMessage.success('状态更新成功')
  } catch (error) {
    loadData()
    console.error(error)
  }
}

const deleteUser = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？此操作不可恢复！`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.delete(`/admin/users/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const getRoleType = (role: number) => {
  const map: Record<number, string> = { 0: '', 1: 'success', 2: 'danger' }
  return map[role] || ''
}

const getRoleLabel = (role: number) => {
  const map: Record<number, string> = { 0: '学生', 1: '心理老师', 2: '管理员' }
  return map[role] || '未知'
}
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
