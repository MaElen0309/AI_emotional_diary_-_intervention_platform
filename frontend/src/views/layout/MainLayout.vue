<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <span v-if="!isCollapse">情绪日记平台</span>
        <span v-else>AI</span>
      </div>
      <el-menu :default-active="activeMenu" :collapse="isCollapse" router background-color="#304156"
               text-color="#bfcbd9" active-text-color="#409eff">
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

        <template v-if="isAdmin">
          <el-divider />
          <el-sub-menu index="admin">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/admin/users">用户管理</el-menu-item>
            <el-menu-item index="/admin/audit">审核管理</el-menu-item>
            <el-menu-item index="/admin/solution-manage">方案管理</el-menu-item>
            <el-menu-item index="/admin/config">系统配置</el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
        </div>
        <div class="right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32">{{ userInfo.nickname || userInfo.username }}</el-avatar>
              <span class="username">{{ userInfo.nickname || userInfo.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
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

      <el-main class="main">
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
const userInfo = ref<any>({})

const activeMenu = computed(() => route.path)
const isAdmin = computed(() => userInfo.value.role === 2)

onMounted(() => {
  const userStr = sessionStorage.getItem('currentUser')
  if (userStr) {
    userInfo.value = JSON.parse(userStr)
  }
})

const handleCommand = (command: string) => {
  switch (command) {
    case 'logout':
      handleLogout()
      break
    case 'password':
      ElMessage.info('修改密码功能开发中')
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
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow-y: auto;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 18px;
    font-weight: bold;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  }

  .el-menu {
    border-right: none;
  }
}

.header {
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;

  .collapse-btn {
    font-size: 20px;
    cursor: pointer;
    color: #666;

    &:hover {
      color: #409eff;
    }
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;

    .username {
      color: #333;
    }
  }
}

.main {
  background: #f5f7fa;
  overflow-y: auto;
}
</style>
