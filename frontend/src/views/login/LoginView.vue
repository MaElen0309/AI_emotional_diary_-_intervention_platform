<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-shapes">
      <div class="shape shape-1" />
      <div class="shape shape-2" />
      <div class="shape shape-3" />
    </div>

    <!-- 左侧品牌区域（桌面端显示） -->
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
          <div class="feature-item">
            <span class="feature-dot" />
            智能情绪分析
          </div>
          <div class="feature-item">
            <span class="feature-dot" />
            个性化干预方案
          </div>
          <div class="feature-item">
            <span class="feature-dot" />
            匿名树洞倾诉
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录区域 -->
    <div class="login-section">
      <div class="login-card">
        <!-- 移动端Logo -->
        <div class="mobile-logo">
          <div class="m-logo-icon">AI</div>
          <h2>情绪日记平台</h2>
        </div>

        <div class="login-header">
          <h2 class="login-title hide-on-mobile">欢迎回来</h2>
          <p class="login-subtitle">请登录您的账号继续使用</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large">
          <el-form-item prop="username">
            <template #label><span class="input-label">用户名</span></template>
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              clearable
              class="custom-input"
            />
          </el-form-item>

          <el-form-item prop="password">
            <template #label><span class="input-label">密码</span></template>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              class="custom-input"
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-button
            type="primary"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form>

        <div class="login-footer">
          <span class="footer-text">还没有账号？</span>
          <router-link to="/register" class="footer-link">立即注册</router-link>
        </div>
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
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await request.post('/auth/login', form)
    sessionStorage.setItem('currentUser', JSON.stringify(res.data))
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
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

/* ===== 背景装饰 ===== */
.bg-shapes {
  position: absolute;
  inset: 0;
  pointer-events: none;

  .shape {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    opacity: 0.4;
  }

  .shape-1 {
    width: 400px;
    height: 400px;
    background: $primary;
    top: -100px;
    right: -80px;
    opacity: 0.08;
  }

  .shape-2 {
    width: 300px;
    height: 300px;
    background: #10b981;
    bottom: -60px;
    left: 20%;
    opacity: 0.06;
  }

  .shape-3 {
    width: 200px;
    height: 200px;
    background: #f59e0b;
    top: 40%;
    left: 35%;
    opacity: 0.05;
  }
}

/* ===== 品牌区域 ===== */
.brand-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: linear-gradient(135deg, #1e1b4b 0%, #312e81 40%, #4338ca 100%);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background:
      radial-gradient(circle at 20% 30%, rgba($primary, 0.15), transparent 50%),
      radial-gradient(circle at 80% 70%, rgba(99, 102, 241, 0.12), transparent 50%);
  }

  @media (max-width: 900px) {
    display: none;
  }

  .brand-content {
    position: relative;
    z-index: 1;
    max-width: 420px;

    .brand-icon {
      margin-bottom: 28px;
    }

    h1 {
      color: white;
      font-size: 36px;
      font-weight: 800;
      margin: 0 0 16px 0;
      letter-spacing: -0.5px;
    }

    .brand-desc {
      color: rgba(255, 255, 255, 0.7);
      font-size: 15px;
      line-height: 1.7;
      margin: 0 0 36px 0;
    }

    .brand-features {
      display: flex;
      flex-direction: column;
      gap: 14px;

      .feature-item {
        display: flex;
        align-items: center;
        gap: 10px;
        color: rgba(255, 255, 255, 0.75);
        font-size: 14px;
        font-weight: 500;

        .feature-dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;
          background: $primary;
          flex-shrink: 0;
          box-shadow: 0 0 8px rgba($primary, 0.5);
        }
      }
    }
  }
}

/* ===== 登录区域 ===== */
.login-section {
  flex: 1;
  max-width: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
  position: relative;
  z-index: 1;

  @media (max-width: 480px) {
    padding: 20px 16px;
  }
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 24px;
  padding: 40px 36px;
  box-shadow:
    0 4px 6px rgba(0, 0, 0, 0.04),
    0 10px 40px rgba(0, 0, 0, 0.06);

  @media (max-width: 480px) {
    padding: 28px 22px;
    border-radius: 20px;
    box-shadow: none;
    background: transparent;
  }
}

/* 移动端Logo */
.mobile-logo {
  display: none;
  text-align: center;
  margin-bottom: 28px;

  .m-logo-icon {
    width: 52px;
    height: 52px;
    border-radius: 14px;
    background: linear-gradient(135deg, $primary, #8b5cf6);
    color: white;
    font-size: 18px;
    font-weight: 800;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 14px;
    box-shadow: 0 8px 20px rgba($primary, 0.3);
  }

  h2 {
    font-size: 22px;
    font-weight: 800;
    color: $text-dark;
    margin: 0;
  }

  @media (max-width: 900px) {
    display: block;
  }
}

/* 登录头部 */
.login-header {
  margin-bottom: 32px;

  .login-title {
    font-size: 26px;
    font-weight: 800;
    color: $text-dark;
    margin: 0 0 8px 0;
    letter-spacing: -0.5px;
  }

  .login-subtitle {
    color: $text-muted;
    font-size: 14px;
    margin: 0;
  }
}

/* 表单样式 */
.input-label {
  font-size: 13px;
  font-weight: 600;
  color: $text-dark;
}

:deep(.custom-input) {
  .el-input__wrapper {
    border-radius: 11px;
    padding: 4px 14px;
    box-shadow: 0 0 0 1px #e2e8f0 inset;
    transition: all 0.25s ease;

    &:hover {
      box-shadow: 0 0 0 1px #cbd5e1 inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 2px rgba($primary, 0.25), 0 0 0 1px $primary inset !important;
    }
  }

  .el-input__inner {
    height: 44px;
    font-size: 15px;
  }
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 46px;
  border-radius: 11px;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 2px;
  background: linear-gradient(135deg, $primary, #7c73ff);
  border: none;
  transition: all 0.3s ease;
  margin-top: 8px;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 20px rgba($primary, 0.35);
    background: linear-gradient(135deg, $primary-dark, $primary);
  }

  &:active {
    transform: translateY(0);
  }
}

/* 底部链接 */
.login-footer {
  text-align: center;
  margin-top: 28px;

  .footer-text {
    color: $text-muted;
    font-size: 14px;
  }

  .footer-link {
    color: $primary;
    font-weight: 600;
    text-decoration: none;
    margin-left: 4px;

    &:hover {
      text-decoration: underline;
    }
  }
}

.hide-on-mobile {
  @media (max-width: 900px) {
    display: none;
  }
}
</style>
