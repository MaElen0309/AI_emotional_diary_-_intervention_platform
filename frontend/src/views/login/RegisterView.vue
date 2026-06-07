<template>
  <div class="register-page">
    <div class="bg-shapes">
      <div class="shape shape-1" />
      <div class="shape shape-2" />
      <div class="shape shape-3" />
    </div>

    <div class="brand-section">
      <div class="brand-content">
        <h1>AI情绪日记</h1>
        <p class="brand-desc">用AI记录情绪，用数据理解自己<br/>每一天都值得被温柔对待</p>
        <div class="brand-features">
          <span><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg> 智能情绪分析</span>
          <span><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg> 个性化方案</span>
          <span><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg> 匿名树洞</span>
        </div>
      </div>
    </div>

    <div class="register-section">
      <div class="register-card">
        <h2 class="card-title">创建账号</h2>
        <p class="card-subtitle">注册后即可使用全部功能</p>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="form.username" placeholder="设置用户名" prefix-icon="User" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="form.nickname" placeholder="你的昵称" prefix-icon="Star" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="form.realName" placeholder="真实姓名" prefix-icon="Postcard" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="身份" prop="role">
                <el-select v-model="form.role" placeholder="选择身份" style="width: 100%">
                  <el-option label="学生" :value="0" />
                  <el-option label="老师" :value="1" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="设置密码（至少6位）" prefix-icon="Lock" show-password />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" prefix-icon="Lock" show-password />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" class="register-btn" :loading="loading" @click="handleRegister">
              立即注册
            </el-button>
          </el-form-item>
        </el-form>

        <div class="card-footer">
          已有账号？
          <router-link to="/login" class="footer-link">去登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  realName: '',
  password: '',
  confirmPassword: '',
  role: 0 as number
})

const validateConfirm = (_rule: any, value: string, callback: any) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await request.post('/auth/register', {
      username: form.username,
      password: form.password,
      nickname: form.nickname,
      realName: form.realName,
      role: form.role
    })
    ElMessage.success('注册成功，即将跳转到登录页')
    setTimeout(() => router.push('/login'), 1500)
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
$primary: #6c63ff;
$primary-dark: #5a52d5;

.register-page {
  min-height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
  background: #f8faff;
}

.bg-shapes {
  .shape {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    opacity: 0.35;
    z-index: 0;
  }
  .shape-1 { width: 400px; height: 400px; background: $primary; top: -100px; right: -80px; }
  .shape-2 { width: 300px; height: 300px; background: #10b981; bottom: -60px; left: 20%; }
  .shape-3 { width: 200px; height: 200px; background: #f59e0b; top: 40%; right: 30%; }
}

.brand-section {
  width: 45%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: linear-gradient(135deg, #1e1b4b 0%, #312e81 50%, #4338ca 100%);
  color: white;
  position: relative;
  z-index: 1;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background:
      radial-gradient(circle at 20% 30%, rgba($primary, 0.15), transparent 50%),
      radial-gradient(circle at 80% 70%, rgba(99, 102, 241, 0.12), transparent 50%);
  }

  .brand-content {
    position: relative;
    z-index: 1;
    max-width: 420px;

    h1 {
      font-size: 36px;
      font-weight: 800;
      margin: 0 0 16px 0;
      letter-spacing: -0.5px;
    }

    .brand-desc {
      font-size: 15px;
      line-height: 1.7;
      opacity: 0.85;
      margin-bottom: 32px;
    }

    .brand-features {
      display: flex;
      flex-direction: column;
      gap: 10px;

      span {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 13.5px;
        opacity: 0.8;

        svg { color: #34d399; flex-shrink: 0; }
      }
    }
  }
}

.register-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
  z-index: 1;

  .register-card {
    width: 100%;
    max-width: 480px;
    background: white;
    border-radius: 20px;
    padding: 40px;
    box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);

    .card-title {
      font-size: 24px;
      font-weight: 800;
      color: #1e293b;
      margin: 0 0 6px 0;
    }

    .card-subtitle {
      color: #94a3b8;
      font-size: 14px;
      margin: 0 0 28px 0;
    }

    .register-btn {
      width: 100%;
      height: 46px;
      border-radius: 11px;
      background: linear-gradient(135deg, $primary, #7c73ff);
      border: none;
      font-size: 15px;
      font-weight: 600;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 6px 20px rgba($primary, 0.35);
        background: linear-gradient(135deg, $primary-dark, $primary);
      }
    }

    .card-footer {
      text-align: center;
      margin-top: 20px;
      color: #94a3b8;
      font-size: 14px;

      .footer-link {
        color: $primary;
        font-weight: 600;
        text-decoration: none;
        margin-left: 4px;

        &:hover { text-decoration: underline; }
      }
    }
  }
}

@media (max-width: 900px) {
  .brand-section { display: none; }
  .register-section { padding: 20px; }
  .register-card { padding: 28px 24px; }
}
</style>
