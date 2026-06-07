<template>
  <div class="profile-page">
    <div class="page-header">
      <h2>个人信息</h2>
      <p>管理你的账户信息</p>
    </div>

    <div class="profile-content">
      <el-row :gutter="24">
        <!-- 左侧头像卡片 -->
        <el-col :xs="24" :sm="8">
          <div class="avatar-card">
            <div class="avatar-wrapper">
              <el-avatar :size="100" class="user-avatar">{{ avatarText }}</el-avatar>
              <span class="avatar-badge">点击更换</span>
            </div>
            <h3 class="user-name">{{ userInfo.nickname || userInfo.username }}</h3>
            <el-tag :type="roleTagType" size="small" effect="light" round>{{ roleLabel }}</el-tag>
          </div>
        </el-col>

        <!-- 右侧表单 -->
        <el-col :xs="24" :sm="16">
          <div class="form-card">
            <h4 class="section-title">基本信息</h4>
            <el-form ref="formRef" :model="form" label-width="80px" label-position="top" size="large" :disabled="!editing">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="用户名">
                    <el-input v-model="form.username" disabled />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="昵称">
                    <el-input v-model="form.nickname" placeholder="你的昵称" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="真实姓名">
                    <el-input v-model="form.realName" placeholder="真实姓名" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="身份角色">
                    <el-select v-model="form.role" disabled style="width: 100%">
                      <el-option label="学生" :value="0" />
                      <el-option label="老师" :value="1" />
                      <el-option label="管理员" :value="2" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item v-if="editing" label="新密码（留空则不修改）">
                <el-input v-model="newPassword" type="password" placeholder="输入新密码" show-password />
              </el-form-item>

              <el-form-item v-if="editing && newPassword" label="确认新密码">
                <el-input v-model="confirmPassword" type="password" placeholder="再次输入新密码" show-password />
              </el-form-item>
            </el-form>

            <!-- 操作按钮在表单外部 -->
            <div class="form-actions">
              <template v-if="!editing">
                <el-button type="primary" @click="startEdit">编辑信息</el-button>
              </template>
              <template v-else>
                <el-button @click="cancelEdit">取消</el-button>
                <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
              </template>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const formRef = ref()
const saving = ref(false)
const editing = ref(false)
const newPassword = ref('')
const confirmPassword = ref('')
const userInfo = ref<any>({})

const form = reactive({
  username: '',
  nickname: '',
  realName: '',
  role: 0
})

const avatarText = computed(() => {
  return (userInfo.value.nickname || userInfo.value.username || '').slice(0, 1).toUpperCase()
})

const roleLabel = computed(() => {
  const map: Record<number, string> = { 0: '学生', 1: '老师', 2: '管理员' }
  return map[userInfo.value.role] || '未知'
})

const roleTagType = computed(() => {
  const map: Record<number, string> = { 0: '', 1: 'warning', 2: 'danger' }
  return map[userInfo.value.role] || ''
})

onMounted(() => {
  const userStr = sessionStorage.getItem('currentUser')
  if (userStr) {
    userInfo.value = JSON.parse(userStr)
    form.username = userInfo.value.username
    form.nickname = userInfo.value.nickname || ''
    form.realName = userInfo.value.realName || ''
    form.role = userInfo.value.role || 0
  }
})

const startEdit = () => {
  editing.value = true
}

const cancelEdit = () => {
  editing.value = false
  newPassword.value = ''
  confirmPassword.value = ''
  // 恢复原始数据
  form.nickname = userInfo.value.nickname || ''
  form.realName = userInfo.value.realName || ''
}

const handleSave = async () => {
  if (newPassword.value && newPassword.value !== confirmPassword.value) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  saving.value = true
  try {
    const data: any = {
      nickname: form.nickname,
      realName: form.realName
    }
    if (newPassword.value) {
      data.newPassword = newPassword.value
    }
    await request.put('/auth/profile', data)

    // 更新本地存储
    userInfo.value.nickname = form.nickname
    userInfo.value.realName = form.realName
    sessionStorage.setItem('currentUser', JSON.stringify(userInfo.value))

    editing.value = false
    newPassword.value = ''
    confirmPassword.value = ''
    ElMessage.success('保存成功')
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped lang="scss">
$primary: #6c63ff;

.profile-page {
  max-width: 900px;
}

.page-header {
  margin-bottom: 24px;

  h2 {
    font-size: 22px;
    font-weight: 750;
    color: #1e293b;
    margin: 0 0 4px 0;
  }

  p {
    color: #94a3b8;
    font-size: 14px;
    margin: 0;
  }
}

.profile-content {
  .avatar-card {
    background: white;
    border-radius: 16px;
    padding: 36px 24px;
    text-align: center;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .avatar-wrapper {
      position: relative;
      display: inline-block;
      margin-bottom: 18px;

      .user-avatar {
        background: linear-gradient(135deg, $primary, #a78bfa);
        color: white;
        font-size: 36px;
        font-weight: 700;
      }

      .avatar-badge {
        position: absolute;
        bottom: -2px;
        left: 50%;
        transform: translateX(-50%);
        background: rgba($primary, 0.9);
        color: white;
        font-size: 11px;
        padding: 3px 10px;
        border-radius: 10px;
        opacity: 0;
        transition: opacity 0.25s;
      }

      &:hover .avatar-badge { opacity: 1; }
    }

    .user-name {
      font-size: 18px;
      font-weight: 700;
      color: #1e293b;
      margin: 0 0 10px 0;
    }
  }

  .form-card {
    background: white;
    border-radius: 16px;
    padding: 28px 32px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .section-title {
      font-size: 16px;
      font-weight: 700;
      color: #1e293b;
      margin: 0 0 22px 0;
      padding-bottom: 14px;
      border-bottom: 1px solid #f1f5f9;
    }

    .form-actions {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      margin-top: 8px;
      padding-top: 16px;
      border-top: 1px solid #f1f5f9;
    }
  }
}

@media (max-width: 767px) {
  .avatar-card { margin-bottom: 20px; }
  .form-card { padding: 20px; }
}
</style>
