<template>
  <div class="login-page">
    <div class="bg-shapes">
      <div class="shape shape-1" />
      <div class="shape shape-2" />
      <div class="shape shape-3" />
    </div>

    <div class="brand-section">
      <div class="brand-content">
        <div class="brand-icon">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
            <rect width="48" height="48" rx="14" fill="white" opacity="0.2"/>
            <text x="50%" y="55%" dominant-baseline="middle" text-anchor="middle" fill="white" font-size="20" font-weight="800">AI</text>
          </svg>
        </div>
        <h1>AI情绪日记</h1>
        <p class="brand-desc">用AI记录情绪，用数据理解自己<br/>每一天都值得被温柔对待</p>
        <div class="brand-features">
          <div class="feature-item"><span class="feature-dot" />智能情绪分析</div>
          <div class="feature-item"><span class="feature-dot" />个性化干预方案</div>
          <div class="feature-item"><span class="feature-dot" />匿名树洞倾诉</div>
        </div>
      </div>
    </div>

    <div class="login-section">
      <div class="login-card">
        <!-- Tab 切换 -->
        <div class="tab-switch">
          <button
            :class="['tab-btn', { active: activeTab === 'login' }]"
            @click="activeTab = 'login'"
          >登录</button>
          <button
            :class="['tab-btn', { active: activeTab === 'register' }]"
            @click="switchToRegister"
          >注册</button>
        </div>

        <!-- 登录表单 -->
        <template v-if="activeTab === 'login'">
          <el-form ref="loginRef" :model="loginForm" :rules="loginRules" label-position="top" size="large">
            <el-form-item prop="username">
              <template #label><span class="input-label">用户名</span></template>
              <el-input v-model="loginForm.username" placeholder="请输入用户名" :prefix-icon="User" clearable class="custom-input" />
            </el-form-item>
            <el-form-item prop="password">
              <template #label><span class="input-label">密码</span></template>
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password class="custom-input" @keyup.enter="handleLogin" />
            </el-form-item>
            <el-button type="primary" :loading="loading" class="submit-btn" @click="handleLogin">{{ loading ? '登录中...' : '登 录' }}</el-button>
          </el-form>
        </template>

        <!-- 注册表单 -->
        <template v-if="activeTab === 'register'">
          <el-form ref="regRef" :model="regForm" :rules="regRules" label-position="top" size="large">
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item prop="username">
                  <template #label><span class="input-label">用户名</span></template>
                  <el-input v-model="regForm.username" placeholder="设置用户名" class="custom-input-sm" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="nickname">
                  <template #label><span class="input-label">昵称</span></template>
                  <el-input v-model="regForm.nickname" placeholder="你的昵称" class="custom-input-sm" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item prop="realName">
                  <template #label><span class="input-label">真实姓名</span></template>
                  <el-input v-model="regForm.realName" placeholder="真实姓名" class="custom-input-sm" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="role">
                  <template #label><span class="input-label">身份</span></template>
                  <el-select v-model="regForm.role" placeholder="选择身份" style="width: 100%" class="custom-select-sm">
                    <el-option label="学生" :value="0" />
                    <el-option label="老师" :value="1" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item prop="password">
              <template #label><span class="input-label">密码</span></template>
              <el-input v-model="regForm.password" type="password" placeholder="设置密码（至少6位）" show-password class="custom-input" />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <template #label><span class="input-label">确认密码</span></template>
              <el-input v-model="regForm.confirmPassword" type="password" placeholder="再次输入密码" show-password class="custom-input" />
            </el-form-item>
            <el-button type="primary" :loading="registering" class="submit-btn reg-btn" @click="handleRegister">{{ registering ? '注册中...' : '立即注册' }}</el-button>
          </el-form>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const activeTab = ref<'login' | 'register'>('login')
const loading = ref(false)
const registering = ref(false)

const loginRef = ref<FormInstance>()
const regRef = ref<FormInstance>()

const loginForm = reactive({ username: '', password: '' })
const regForm = reactive({ username: '', nickname: '', realName: '', password: '', confirmPassword: '', role: 0 })

const validateConfirm = (_rule: any, value: string, callback: any) => {
  if (value !== regForm.password) callback(new Error('两次密码不一致'))
  else callback()
}

const loginRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const regRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '3-20个字符', trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const switchToRegister = () => {
  activeTab.value = 'register'
}

const handleLogin = async () => {
  const valid = await loginRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await request.post('/auth/login', loginForm)
    sessionStorage.setItem('currentUser', JSON.stringify(res.data))
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) { console.error(error) }
  finally { loading.value = false }
}

const handleRegister = async () => {
  const valid = await regRef.value?.validate().catch(() => false)
  if (!valid) return
  registering.value = true
  try {
    await request.post('/auth/register', {
      username: regForm.username,
      password: regForm.password,
      nickname: regForm.nickname,
      realName: regForm.realName,
      role: regForm.role
    })
    ElMessage.success('注册成功，请登录')
    // 切回登录并自动填入用户名
    activeTab.value = 'login'
    loginForm.username = regForm.username
    loginForm.password = ''
    // 重置注册表单
    Object.assign(regForm, { username: '', nickname: '', realName: '', password: '', confirmPassword: '', role: 0 })
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '注册失败')
  }
  finally { registering.value = false }
}
</script>

<style scoped lang="scss">
$primary: #6c63ff;
$primary-dark: #5a52d5;
$text-dark: #1e293b;
$text-muted: #94a3b8;

.login-page {
  display: flex;
  min-height: 100vh;
  background: #f8fafc;
  position: relative;
  overflow: hidden;
}

.bg-shapes {
  position: absolute; inset: 0; pointer-events: none;
  .shape { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.4; }
  .shape-1 { width: 400px; height: 400px; background: $primary; top: -100px; right: -80px; opacity: 0.08; }
  .shape-2 { width: 300px; height: 300px; background: #10b981; bottom: -60px; left: 20%; opacity: 0.06; }
  .shape-3 { width: 200px; height: 200px; background: #f59e0b; top: 40%; left: 35%; opacity: 0.05; }
}

.brand-section {
  flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px;
  background: linear-gradient(135deg, #1e1b4b 0%, #312e81 40%, #4338ca 100%);
  position: relative; overflow: hidden;
  &::before {
    content: ''; position: absolute; inset: 0;
    background: radial-gradient(circle at 20% 30%, rgba($primary, 0.15), transparent 50%),
                radial-gradient(circle at 80% 70%, rgba(99, 102, 241, 0.12), transparent 50%);
  }
  @media (max-width: 900px) { display: none; }
  .brand-content {
    position: relative; z-index: 1; max-width: 420px;
    .brand-icon { margin-bottom: 28px; }
    h1 { color: white; font-size: 36px; font-weight: 800; margin: 0 0 16px 0; letter-spacing: -0.5px; }
    .brand-desc { color: rgba(255,255,255,0.7); font-size: 15px; line-height: 1.7; margin: 0 0 36px 0; }
    .brand-features {
      display: flex; flex-direction: column; gap: 14px;
      .feature-item { display: flex; align-items: center; gap: 10px; color: rgba(255,255,255,0.75); font-size: 14px; font-weight: 500;
        .feature-dot { width: 8px; height: 8px; border-radius: 50%; background: $primary; flex-shrink: 0; box-shadow: 0 0 8px rgba($primary, 0.5); }
      }
    }
  }
}

.login-section {
  flex: 1; max-width: 520px; display: flex; align-items: center; justify-content: center; padding: 32px; position: relative; z-index: 1;
  @media (max-width: 480px) { padding: 20px 16px; }
}

.login-card {
  width: 100%; max-width: 440px; background: white; border-radius: 24px; padding: 36px 32px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.04), 0 10px 40px rgba(0,0,0,0.06);
  @media (max-width: 480px) { padding: 24px 20px; border-radius: 20px; box-shadow: none; background: transparent; }
}

/* Tab 切换 */
.tab-switch {
  display: flex; background: #f1f5f9; border-radius: 12px; padding: 4px; margin-bottom: 28px;

  .tab-btn {
    flex: 1; padding: 10px; border: none; background: none; border-radius: 9px;
    font-size: 14.5px; font-weight: 600; color: $text-muted; cursor: pointer;
    transition: all 0.25s ease;

    &.active {
      background: white; color: $text-dark; box-shadow: 0 2px 8px rgba(0,0,0,0.06);
    }

    &:hover:not(.active) { color: $text-dark; }
  }
}

/* 表单样式 */
.input-label { font-size: 13px; font-weight: 600; color: $text-dark; }

:deep(.custom-input), :deep(.custom-input-sm) {
  .el-input__wrapper {
    border-radius: 11px; padding: 4px 14px;
    box-shadow: 0 0 0 1px #e2e8f0 inset; transition: all 0.25s ease;
    &:hover { box-shadow: 0 0 0 1px #cbd5e1 inset; }
    &.is-focus { box-shadow: 0 0 0 2px rgba($primary, 0.25), 0 0 0 1px $primary inset !important; }
  }
  .el-input__inner { height: 44px; font-size: 15px; }
}

:deep(.custom-input-sm) .el-input__inner { height: 42px; font-size: 14px; }
:deep(.custom-select-sm) .el-input__inner { height: 42px; font-size: 14px; }

.submit-btn {
  width: 100%; height: 46px; border-radius: 11px; font-size: 16px; font-weight: 700;
  letter-spacing: 2px; background: linear-gradient(135deg, $primary, #7c73ff);
  border: none; transition: all 0.3s ease; margin-top: 8px;
  &:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba($primary, 0.35); background: linear-gradient(135deg, $primary-dark, $primary); }
  &:active { transform: translateY(0); }
}

.reg-btn {
  background: linear-gradient(135deg, #10b981, #34d399);
  &:hover { box-shadow: 0 6px 20px rgba(16, 185, 129, 0.35); background: linear-gradient(135deg, #059669, #10b981); }
}
</style>
