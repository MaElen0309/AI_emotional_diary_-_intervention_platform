import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/RegisterView.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/views/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '仪表盘', icon: 'DataLine' }
      },
      {
        path: 'diary',
        name: 'Diary',
        component: () => import('@/views/diary/DiaryList.vue'),
        meta: { title: '情绪日记', icon: 'Notebook' }
      },
      {
        path: 'diary/create',
        name: 'DiaryCreate',
        component: () => import('@/views/diary/DiaryCreate.vue'),
        meta: { title: '写日记', icon: 'EditPen' }
      },
      {
        path: 'treehole',
        name: 'Treehole',
        component: () => import('@/views/treehole/TreeholeList.vue'),
        meta: { title: '树洞', icon: 'ChatDotRound' }
      },
      {
        path: 'solutions',
        name: 'Solutions',
        component: () => import('@/views/solution/SolutionList.vue'),
        meta: { title: '方案库', icon: 'FolderOpened' }
      },
      {
        path: 'ai-analysis',
        name: 'AiAnalysis',
        component: () => import('@/views/ai/AiAnalysis.vue'),
        meta: { title: 'AI分析', icon: 'MagicStick' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/ProfileView.vue'),
        meta: { title: '个人信息', icon: 'UserFilled' }
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/views/layout/MainLayout.vue'),
    redirect: '/admin/users',
    meta: { requiresAdmin: true },
    children: [
      {
        path: 'users',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'audit',
        name: 'AuditManage',
        component: () => import('@/views/admin/AuditManage.vue'),
        meta: { title: '审核管理', icon: 'DocumentChecked' }
      },
      {
        path: 'solution-manage',
        name: 'SolutionManage',
        component: () => import('@/views/admin/SolutionManage.vue'),
        meta: { title: '方案管理', icon: 'Setting' }
      },
      {
        path: 'config',
        name: 'SystemConfig',
        component: () => import('@/views/admin/SystemConfig.vue'),
        meta: { title: '系统配置', icon: 'Tools' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title || ''} - AI情绪日记平台`

  if (to.path !== '/login') {
    const userStr = sessionStorage.getItem('currentUser')
    if (!userStr) {
      next('/login')
      return
    }

    const user = JSON.parse(userStr)
    if (to.meta.requiresAdmin && user.role !== 2 && user.role !== 1) {
      next('/')
      return
    }
  }

  next()
})

export default router
