<template>
  <div class="treehole-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>🌳 匿名树洞</span>
          <el-button type="primary" icon="Plus" @click="showPublishDialog">发布心声</el-button>
        </div>
      </template>

      <div class="treehole-content" v-loading="loading">
        <div v-if="tableData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无内容，快来分享你的故事吧~" />
        </div>

        <div v-for="item in tableData" :key="item.id" class="treehole-item">
          <div class="item-header">
            <div class="author-info">
              <el-avatar :size="36">{{ item.anonymousName?.charAt(0) || '匿' }}</el-avatar>
              <span class="name">{{ item.anonymousName || '匿名用户' }}</span>
              <el-tag size="small" type="info">{{ formatTime(item.createTime) }}</el-tag>
            </div>
          </div>

          <h3 class="item-title">{{ item.title }}</h3>
          <p class="item-content">{{ item.content }}</p>

          <div class="item-footer">
            <span class="stat-item" @click="handleLike(item)">
              <el-icon><Star /></el-icon>
              {{ item.likeCount || 0 }}
            </span>
            <span class="stat-item" @click="toggleComments(item)">
              <el-icon><ChatDotRound /></el-icon>
              {{ item.commentCount || 0 }}
            </span>
            <span class="stat-item" @click="toggleFavorite(item)">
              <el-icon :class="{ 'is-favorited': item.isFavorited }"><CollectionTag /></el-icon>
              {{ item.favoriteCount || 0 }}
            </span>
            <span class="stat-item">
              <el-icon><View /></el-icon>
              {{ item.viewCount || 0 }}
            </span>
          </div>

          <!-- 评论区 -->
          <div v-if="expandedId === item.id" class="comment-section">
            <div class="comment-list" v-loading="commentLoading[item.id]">
              <div v-if="comments[item.id]?.length === 0 && !commentLoading[item.id]" class="no-comment">
                暂无评论，来说两句吧~
              </div>

              <div v-for="comment in comments[item.id]" :key="comment.id" class="comment-item">
                <div class="comment-header">
                  <el-avatar :size="28">{{ comment.userAnonymousName?.charAt(0) || '匿' }}</el-avatar>
                  <span class="comment-name">{{ comment.userAnonymousName || '匿名用户' }}</span>
                  <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <div class="comment-actions">
                  <span class="action-btn" @click="handleCommentLike(comment)">
                    <el-icon><Star /></el-icon> {{ comment.likeCount || 0 }}
                  </span>
                  <span class="action-btn reply-btn" @click="showReplyInput(item, comment)">回复</span>
                </div>

                <!-- 回复列表 -->
                <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
                  <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                    <el-avatar :size="22">{{ reply.userAnonymousName?.charAt(0) || '匿' }}</el-avatar>
                    <span class="reply-name">{{ reply.userAnonymousName || '匿名用户' }}</span>
                    <span class="reply-text">{{ reply.content }}</span>
                    <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                  </div>
                </div>

                <!-- 回复输入框 -->
                <div v-if="replyTo === comment.id" class="reply-input-wrap">
                  <el-input
                    v-model="replyContent"
                    placeholder="回复这条评论..."
                    size="small"
                    @keyup.enter="submitReply(item, comment)"
                  >
                    <template #append>
                      <el-button size="small" @click="submitReply(item, comment)" :loading="submitting">发送</el-button>
                      <el-button size="small" @click="cancelReply">取消</el-button>
                    </template>
                  </el-input>
                </div>
              </div>
            </div>

            <!-- 评论输入 -->
            <div class="comment-input-area">
              <el-input
                v-model="commentInputs[item.id]"
                type="textarea"
                :rows="2"
                placeholder="写下你的评论..."
                maxlength="500"
                show-word-limit
              />
              <div class="comment-submit-row">
                <el-input
                  v-model="commentNames[item.id]"
                  placeholder="匿名昵称（可选）"
                  style="width: 150px; margin-right: 10px;"
                  maxlength="20"
                />
                <el-button type="primary" size="small" @click="submitComment(item)" :loading="submitting">
                  发表评论
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <el-pagination
        v-model:current-page="pageNum"
        :total="total"
        :page-size="pageSize"
        layout="prev, pager, next"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: center;"
      />
    </el-card>

    <el-dialog v-model="publishVisible" title="发布到树洞" width="600px">
      <el-form ref="publishFormRef" :model="publishForm" :rules="publishRules" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="publishForm.anonymousName" placeholder="可选，不填则完全匿名" maxlength="20" />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="publishForm.title" placeholder="请输入标题" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="publishForm.content" type="textarea" :rows="6" placeholder="说出你的心里话..."
                    maxlength="2000" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handlePublish">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Star, View, ChatDotRound, CollectionTag } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const submitting = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref<any[]>([])
const publishVisible = ref(false)
const publishFormRef = ref<FormInstance>()

// 评论相关状态
const expandedId = ref<number | null>(null)
const comments = ref<Record<number, any[]>>({})
const commentLoading = ref<Record<number, boolean>>({})
const commentInputs = ref<Record<string, string>>({})
const commentNames = ref<Record<string, string>>({})

// 回复相关状态
const replyTo = ref<number | null>(null)
const replyContent = ref('')

// 收藏状态缓存
const favoriteStatusCache = ref<Record<number, boolean>>({})

const publishForm = reactive({
  anonymousName: '',
  title: '',
  content: ''
})

const publishRules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
})

function formatTime(time: string) {
  if (!time) return ''
  const d = new Date(time)
  return `${d.getMonth() + 1}-${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/treehole/published', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value }
    })
    tableData.value = res.data.list || []
    total.value = res.data.total || 0

    // 批量获取收藏状态
    if (tableData.value.length > 0) {
      for (const item of tableData.value) {
        try {
          const favRes = await request.get(`/treehole/${item.id}/favorite/status`)
          item.isFavorited = favRes.data.isFavorited
          item.favoriteCount = favRes.data.count
        } catch (e) {
          item.isFavorited = false
          item.favoriteCount = 0
        }
      }
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

async function toggleComments(item: any) {
  if (expandedId.value === item.id) {
    expandedId.value = null
    return
  }

  expandedId.value = item.id

  // 加载评论
  if (!comments.value[item.id]) {
    commentLoading.value[item.id] = true
    try {
      const res = await request.get(`/treehole/${item.id}/comments`, {
        params: { pageNum: 1, pageSize: 50 }
      })
      comments.value[item.id] = res.data.list || []
    } catch (error) {
      console.error(error)
      comments.value[item.id] = []
    } finally {
      commentLoading.value[item.id] = false
    }
  }
}

async function toggleFavorite(item: any) {
  try {
    const res = await request.post(`/treehole/${item.id}/favorite`)
    item.isFavorited = !item.isFavorited

    // 更新收藏数
    if (res.message.includes('取消')) {
      item.favoriteCount = Math.max((item.favoriteCount || 1) - 1, 0)
      ElMessage.success('已取消收藏')
    } else {
      item.favoriteCount = (item.favoriteCount || 0) + 1
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    console.error(error)
  }
}

async function submitComment(item: any) {
  const content = commentInputs.value[item.id]
  if (!content || !content.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  submitting.value = true
  try {
    await request.post(`/treehole/${item.id}/comments`, {
      content: content.trim(),
      anonymousName: commentNames.value[item.id] || '',
      parentId: null
    })

    ElMessage.success('评论成功')
    commentInputs.value[item.id] = ''
    commentNames.value[item.id] = ''

    // 刷新评论列表
    comments.value[item.id] = null
    commentLoading.value[item.id] = true
    const res = await request.get(`/treehole/${item.id}/comments`, {
      params: { pageNum: 1, pageSize: 50 }
    })
    comments.value[item.id] = res.data.list || []

    // 更新帖子评论数
    item.commentCount = (item.commentCount || 0) + 1
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

function showReplyInput(item: any, comment: any) {
  replyTo.value = comment.id
  replyContent.value = ''
}

function cancelReply() {
  replyTo.value = null
  replyContent.value = ''
}

async function submitReply(item: any, parentComment: any) {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  submitting.value = true
  try {
    await request.post(`/treehole/${item.id}/comments`, {
      content: replyContent.value.trim(),
      anonymousName: '',
      parentId: parentComment.id
    })

    ElMessage.success('回复成功')
    replyTo.value = null
    replyContent.value = ''

    // 刷新评论
    comments.value[item.id] = null
    commentLoading.value[item.id] = true
    const res = await request.get(`/treehole/${item.id}/comments`, {
      params: { pageNum: 1, pageSize: 50 }
    })
    comments.value[item.id] = res.data.list || []

    item.commentCount = (item.commentCount || 0) + 1
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

async function handleCommentLike(comment: any) {
  try {
    await request.post(`/treehole/comments/${comment.id}/like`)
    comment.likeCount = (comment.likeCount || 0) + 1
  } catch (error) {
    console.error(error)
  }
}

function showPublishDialog() {
  Object.assign(publishForm, { anonymousName: '', title: '', content: '' })
  publishVisible.value = true
}

async function handlePublish() {
  const valid = await publishFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await request.post('/treehole', publishForm)
    ElMessage.success('发布成功，等待审核后显示')
    publishVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

async function handleLike(item: any) {
  try {
    await request.post(`/treehole/${item.id}/like`)
    item.likeCount = (item.likeCount || 0) + 1
    ElMessage.success('点赞成功')
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.treehole-item {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .author-info {
      display: flex;
      align-items: center;
      gap: 10px;

      .name {
        font-weight: 500;
        color: #333;
      }
    }
  }

  .item-title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 10px;
    color: #333;
  }

  .item-content {
    color: #666;
    line-height: 1.6;
    margin-bottom: 15px;
    display: -webkit-box;
    -webkit-line-clamp: 4;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .item-footer {
    display: flex;
    gap: 20px;
    padding-top: 12px;
    border-top: 1px solid #f0f0f0;

    .stat-item {
      display: flex;
      align-items: center;
      gap: 5px;
      cursor: pointer;
      color: #999;
      transition: color 0.2s;

      &:hover {
        color: #409eff;
      }

      .is-favorited {
        color: #f56c6c;
      }
    }
  }

  // 评论区样式
  .comment-section {
    margin-top: 15px;
    padding-top: 15px;
    border-top: 1px dashed #eee;

    .no-comment {
      text-align: center;
      color: #999;
      padding: 20px 0;
      font-size: 14px;
    }

    .comment-list {
      max-height: 400px;
      overflow-y: auto;
      margin-bottom: 15px;
    }

    .comment-item {
      padding: 12px 0;
      border-bottom: 1px solid #f5f5f5;

      &:last-child {
        border-bottom: none;
      }

      .comment-header {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 6px;

        .comment-name {
          font-weight: 500;
          font-size: 13px;
          color: #333;
        }

        .comment-time {
          font-size: 12px;
          color: #bbb;
          margin-left: auto;
        }
      }

      .comment-text {
        font-size: 14px;
        color: #555;
        line-height: 1.5;
        margin-left: 36px;
        margin-bottom: 6px;
      }

      .comment-actions {
        display: flex;
        gap: 15px;
        margin-left: 36px;

        .action-btn {
          font-size: 12px;
          color: #999;
          cursor: pointer;
          display: flex;
          align-items: center;
          gap: 3px;

          &:hover {
            color: #409eff;
          }
        }

        .reply-btn:hover {
          color: #67c23a;
        }
      }

      // 回复列表
      .reply-list {
        background: #fafafa;
        border-radius: 6px;
        padding: 10px 12px;
        margin-top: 8px;
        margin-left: 36px;
      }

      .reply-item {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 5px 0;
        font-size: 13px;

        .reply-name {
          color: #409eff;
          font-weight: 500;
        }

        .reply-text {
          color: #666;
          flex: 1;
        }

        .reply-time {
          color: #ccc;
          font-size: 11px;
        }
      }

      // 回复输入框
      .reply-input-wrap {
        margin-top: 8px;
        margin-left: 36px;
      }
    }

    // 评论输入区
    .comment-input-area {
      background: #f9f9f9;
      border-radius: 8px;
      padding: 12px;

      .comment-submit-row {
        display: flex;
        align-items: center;
        justify-content: flex-end;
        margin-top: 8px;
      }
    }
  }
}

.empty-state {
  padding: 60px 0;
}
</style>
