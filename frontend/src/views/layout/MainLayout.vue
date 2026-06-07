<template>
  <el-container class="layout-container">
    <!-- 移动端遮罩 -->
    <transition name="fade">
      <div v-if="showMobileMenu" class="mobile-overlay" @click="showMobileMenu = false" />
    </transition>

    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '240px'" class="aside"
              :class="{ 'aside-mobile': isMobile, 'aside-open': showMobileMenu }">
      <div class="logo-area">
        <div class="logo-icon">AI</div>
        <span v-if="!isCollapse" class="logo-text">情绪日记</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        router
        class="side-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>

        <el-sub-menu index="diary">
          <template #title>
            <el-icon><Notebook /></el-icon>
            <span>情绪日记</span>
          </template>
          <el-menu-item index="/diary">日记列表</el-menu-item>
          <el-menu-item index="/diary/create">写日记</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/treehole">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>树洞</template>
        </el-menu-item>

        <el-menu-item index="/solutions">
          <el-icon><FolderOpened /></el-icon>
          <template #title>方案库</template>
        </el-menu-item>

        <el-menu-item index="/ai-analysis">
          <el-icon><MagicStick /></el-icon>
          <template #title>AI分析</template>
        </el-menu-item>

        <template v-if="isTeacherOrAdmin">
          <div class="menu-divider" />
          <el-sub-menu index="admin">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/admin/users">用户管理</el-menu-item>
            <el-menu-item index="/admin/audit">审核管理</el-menu-item>
            <template v-if="isSuperAdmin">
              <el-menu-item index="/admin/solution-manage">方案管理</el-menu-item>
              <el-menu-item index="/admin/config">系统配置</el-menu-item>
            </template>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶栏 -->
      <el-header class="header">
        <div class="header-left">
          <button class="menu-toggle" @click="toggleSidebar">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="3" y1="6" x2="21" y2="6" />
              <line x1="3" y1="12" x2="21" y2="12" />
              <line x1="3" y1="18" x2="21" y2="18" />
            </svg>
          </button>
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-trigger">
              <el-avatar :size="34" class="user-avatar">{{ avatarText }}</el-avatar>
              <span class="user-name hide-on-mobile">{{ userInfo.nickname || userInfo.username }}</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)
const showMobileMenu = ref(false)
const windowWidth = ref(window.innerWidth)
const userInfo = ref<any>({})

const activeMenu = computed(() => route.path)
const isAdmin = computed(() => userInfo.value.role === 2)
const isTeacherOrAdmin = computed(() => (userInfo.value.role ?? 0) >= 1)
const isSuperAdmin = computed(() => userInfo.value.role === 2)
const isMobile = computed(() => windowWidth.value < 768)

const avatarText = computed(() => {
  const name = (userInfo.value.nickname || userInfo.value.username || '').slice(0, 1).toUpperCase()
  return name
})

const pageTitles: Record<string, string> = {
  '/dashboard': '仪表盘',
  '/diary': '日记列表',
  '/diary/create': '写日记',
  '/treehole': '树洞',
  '/solutions': '方案库',
  '/ai-analysis': 'AI分析',
  '/admin/users': '用户管理',
  '/admin/audit': '审核管理',
  '/admin/solution-manage': '方案管理',
  '/admin/config': '系统配置',
}
const pageTitle = computed(() => pageTitles[route.path] || '')

onMounted(() => {
  const userStr = sessionStorage.getItem('currentUser')
  if (userStr) {
    userInfo.value = JSON.parse(userStr)
  }
  window.addEventListener('resize', () => {
    windowWidth.value = window.innerWidth
    if (windowWidth.value >= 768) {
      showMobileMenu.value = false
    }
  })
})

const toggleSidebar = () => {
  if (isMobile.value) {
    showMobileMenu.value = !showMobileMenu.value
  } else {
    isCollapse.value = !isCollapse.value
  }
}

const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'password':
      router.push('/profile')
      break
    case 'logout':
      handleLogout()
      break
  }
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.post('/auth/logout')
    sessionStorage.removeItem('currentUser')
    router.push('/login')
    ElMessage.success('退出成功')
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}
</script>

<style scoped lang="scss">
/* ===== 变量 ===== */
$sidebar-bg: linear-gradient(180deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
$sidebar-width: 240px;
$sidebar-collapsed: 64px;
$header-height: 60px;
$primary: #6c63ff;
$primary-light: #8b83ff;
$text-dark: #1e293b;
$text-muted: #94a3b8;
$bg-body: #f1f5f9;
$border-color: #e2e8f0;

/* ===== 布局容器 ===== */
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.main-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* ===== 侧边栏 ===== */
.aside {
  background: $sidebar-bg;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow-y: auto;
  overflow-x: hidden;
  flex-shrink: 0;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.15);
  z-index: 100;

  &::-webkit-scrollbar {
    width: 4px;
  }
  &::-webkit-scrollbar-thumb {
    background: rgba(255,255,255,0.15);
    border-radius: 4px;
  }

  .logo-area {
    height: $header-height;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    padding: 0 16px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);

    .logo-icon {
      width: 36px;
      height: 36px;
      border-radius: 10px;
      background: linear-gradient(135deg, $primary, $primary-light);
      color: white;
      font-size: 14px;
      font-weight: 800;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
      box-shadow: 0 4px 12px rgba($primary, 0.35);
    }

    .logo-text {
      font-size: 17px;
      font-weight: 700;
      color: white;
      white-space: nowrap;
      letter-spacing: 0.5px;
    }
  }

  .menu-divider {
    height: 1px;
    margin: 8px 16px;
    background: rgba(255, 255, 255, 0.08);
  }

  /* 菜单样式覆盖 */
  :deep(.side-menu) {
    border-right: none;
    background: transparent;
    padding: 8px;

    .el-menu-item,
    .el-sub-menu__title {
      color: rgba(255, 255, 255, 0.7);
      border-radius: 10px;
      margin-bottom: 4px;
      height: 46px;
      line-height: 46px;
      transition: all 0.25s ease;

      &:hover {
        background: rgba(255, 255, 255, 0.08);
        color: white;
      }

      .el-icon {
        font-size: 18px;
        vertical-align: middle;
      }
    }

    .el-menu-item.is-active {
      background: linear-gradient(90deg, rgba($primary, 0.3), rgba($primary, 0.15));
      color: white;
      font-weight: 600;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 3px;
        height: 22px;
        background: $primary;
        border-radius: 0 3px 3px 0;
      }
    }

    .el-sub-menu.is-opened > .el-sub-menu__title {
      color: white;
    }

    /* 子菜单样式 */
    :deep(.el-sub-menu__icon-arrow) {
      color: rgba(255, 255, 255, 0.6);
    }

    .el-menu {
      background: transparent !important;

      .el-menu-item {
        color: rgba(255, 255, 255, 0.8) !important;
        height: 42px;
        line-height: 42px;
        padding-left: 52px !important;
        min-width: auto;
        border-radius: 8px;
        margin-bottom: 2px;
        background: transparent !important;

        &:hover {
          background: rgba(255, 255, 255, 0.1) !important;
          color: #fff !important;
        }

        &.is-active {
          background: rgba($primary, 0.25) !important;
          color: #fff !important;
          font-weight: 600;
        }
      }
    }

    .el-menu--collapse {
      .el-menu-item,
      .el-sub-menu__title {
        padding: 0 !important;
        justify-content: center;
      }
    }
  }
}

/* ===== 顶栏 ===== */
.header {
  height: $header-height;
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid $border-color;
  flex-shrink: 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);

  .header-left {
    display: flex;
    align-items: center;
    gap: 14px;

    .menu-toggle {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 38px;
      height: 38px;
      border: none;
      background: $bg-body;
      border-radius: 10px;
      cursor: pointer;
      color: $text-muted;
      transition: all 0.2s;

      &:hover {
        background: #e2e8f0;
        color: $text-dark;
      }
    }

    .page-title {
      font-size: 17px;
      font-weight: 650;
      color: $text-dark;
      letter-spacing: -0.3px;
    }
  }

  .header-right {
    .user-trigger {
      display: flex;
      align-items: center;
      gap: 10px;
      cursor: pointer;
      padding: 4px 10px 4px 4px;
      border-radius: 12px;
      transition: background 0.2s;

      &:hover {
        background: $bg-body;
      }

      .user-avatar {
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: white;
        font-size: 14px;
        font-weight: 600;
      }

      .user-name {
        font-size: 14px;
        font-weight: 500;
        color: $text-dark;
      }

      .arrow-icon {
        color: $text-muted;
        font-size: 12px;
      }
    }
  }
}

/* ===== 内容区 ===== */
.main-content {
  background: $bg-body;
  overflow-y: auto;
  padding: 24px;

  &::-webkit-scrollbar {
    width: 6px;
  }
  &::-webkit-scrollbar-thumb {
    background: #cbd5e1;
    border-radius: 6px;
  }
}

/* ===== 移动端遮罩 ===== */
.mobile-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.55);
  backdrop-filter: blur(4px);
  z-index: 99;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ===== 移动端响应式 ===== */
@media (max-width: 767px) {
  .aside {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    width: $sidebar-width !important;
    transform: translateX(-100%);
    will-change: transform;

    &.aside-open {
      transform: translateX(0);
    }

    :deep(.side-menu.el-menu--collapse) {
      width: $sidebar-width !important;
    }
  }

  .header {
    padding: 0 14px;

    .page-title {
      font-size: 15px;
    }
  }

  .main-content {
    padding: 16px;
  }

  .hide-on-mobile {
    display: none;
  }
}

@media (max-width: 480px) {
  .header {
    padding: 0 10px;

    .page-title {
      font-size: 14px;
    }
  }

  .main-content {
    padding: 12px;
  }
}
</style>
