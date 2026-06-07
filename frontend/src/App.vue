<template>
  <router-view />
</template>

<script setup lang="ts">
</script>

<style>
/* ===== 全局重置与基础样式 ===== */
*,
*::before,
*::after {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

html {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-rendering: optimizeLegibility;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  color: #1e293b;
  background: #f1f5f9;
  line-height: 1.6;
}

#app {
  width: 100%;
  height: 100vh;
}

/* ===== Element Plus 全局覆盖 ===== */
.el-card {
  border: none !important;
  border-radius: 14px !important;
}

.el-button--primary {
  --el-button-bg-color: #6c63ff !important;
  --el-button-border-color: #6c63ff !important;
  --el-button-hover-bg-color: #5a52d5 !important;
  --el-button-hover-border-color: #5a52d5 !important;
  --el-button-active-bg-color: #4f46e5 !important;
  --el-button-active-border-color: #4f46e5 !important;
  border-radius: 10px !important;
  font-weight: 600 !important;
}

.el-table {
  border-radius: 12px !important;
  overflow: hidden;

  th.el-table__cell {
    background: #f8fafc !important;
    font-weight: 700 !important;
    color: #475569 !important;
    font-size: 13px !important;
  }
}

.el-tag {
  border-radius: 20px !important;
  font-weight: 600 !important;
  padding: 2px 10px !important;
}

/* 侧边栏子菜单背景修复 */
.el-menu--inline {
  background: transparent !important;
}
.el-menu--inline .el-menu-item {
  color: rgba(255, 255, 255, 0.8) !important;
  background: transparent !important;
}
.el-menu--inline .el-menu-item:hover,
.el-menu--inline .el-menu-item.is-active {
  background: rgba(108, 99, 255, 0.2) !important;
  color: #fff !important;
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 6px;
}
::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 移动端触摸优化 */
@media (max-width: 767px) {
  body {
    font-size: 15px;
  }

  .el-dialog {
    width: 92% !important;
    margin-top: 10vh !important;
    border-radius: 16px !important;
  }
}
</style>
