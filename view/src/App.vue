<script>
import ChatMessage from './components/ChatMessage.vue'
import ChatInput from './components/ChatInput.vue'
import LoadingDots from './components/LoadingDots.vue'
import { chatWithSSE } from './api/chatApi.js'
import { generateMemoryId } from './utils/index.js'
import { marked } from 'marked'

export default {
  name: 'App',
  components: {
    ChatMessage,
    ChatInput,
    LoadingDots
  },
  data() {
    return {
      messages: [],
      memoryId: null,
      isAiTyping: false,
      isStreaming: false,
      currentAiResponse: '',
      currentEventSource: null,
      connectionError: false
    }
  },
  computed: {
    currentAiResponseRendered() {
      if (!this.currentAiResponse) return ''
      // 配置marked选项
      marked.setOptions({
        breaks: true, // 支持换行
        gfm: true, // 支持GitHub风格的Markdown
        sanitize: false, // 不过滤HTML（根据需要可以开启）
        highlight: function(code, lang) {
          // 可以在这里添加代码高亮功能
          return code
        }
      })
      return marked(this.currentAiResponse)
    }
  },
  methods: {
    sendMessage(message) {
      // 添加用户消息
      this.addMessage(message, true)
      
      // 开始AI回复
      this.startAiResponse(message)
    },
    
    addMessage(content, isUser = false) {
      const message = {
        id: Date.now() + Math.random(),
        content,
        isUser,
        timestamp: new Date()
      }
      this.messages.push(message)
      this.scrollToBottom()
    },
    
    startAiResponse(userMessage) {
      this.isAiTyping = true
      this.isStreaming = true
      this.currentAiResponse = ''
      this.connectionError = false
      
      // 关闭之前的连接
      if (this.currentEventSource) {
        this.currentEventSource.close()
      }
      
      // 开始SSE连接
      this.currentEventSource = chatWithSSE(
        this.memoryId,
        userMessage,
        this.handleAiMessage,
        this.handleAiError,
        this.handleAiClose
      )
    },
    
    handleAiMessage(data) {
      this.currentAiResponse += data
      this.scrollToBottom()
    },
    
    handleAiError(error) {
      console.error('AI 回复出错:', error)
      this.connectionError = true
      this.finishAiResponse()
      
      // 5秒后自动隐藏错误提示
      setTimeout(() => {
        this.connectionError = false
      }, 5000)
    },
    
    handleAiClose() {
      this.finishAiResponse()
    },
    
    finishAiResponse() {
      this.isStreaming = false
      
      // 如果有内容，添加到消息列表
      if (this.currentAiResponse.trim()) {
        this.addMessage(this.currentAiResponse.trim(), false)
      }
      
      // 重置状态
      this.isAiTyping = false
      this.currentAiResponse = ''
      
      // 重置连接错误状态（确保正常结束时清除错误提示）
      this.connectionError = false
      
      // 关闭连接
      if (this.currentEventSource) {
        this.currentEventSource.close()
        this.currentEventSource = null
      }
    },
    
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },
    
    initializeChat() {
      this.memoryId = generateMemoryId()
      console.log('聊天室ID:', this.memoryId)
    }
  },
  
  mounted() {
    this.initializeChat()
  },
  
  beforeUnmount() {
    // 组件销毁前关闭连接
    if (this.currentEventSource) {
      this.currentEventSource.close()
    }
  }
}
</script>

<template>
  <div class="app">
    <!-- 头部标题 -->
    <div class="app-header">
      <h1 class="app-title">银行智能体客服</h1>
      <div class="app-subtitle">帮助您解答银行相关业务问题</div>
    </div>

    <!-- 聊天区域 -->
    <div class="chat-container">
      <!-- 消息列表 -->
      <div class="messages-container" ref="messagesContainer">
        <div v-if="messages.length === 0" class="welcome-message">
          <div class="welcome-content">
            <div class="welcome-icon">🤖</div>
            <h2>欢迎使用银行智能体客服</h2>
            <p>我可以帮助您：</p>
            <ul>
              <li>解答个人金融服务相关业务</li>
              <li>提供电子银行操作指引</li>
              <li>分享个人电子银行交易规则</li>
            </ul>
            <p>请随时向我提问吧！</p>
          </div>
        </div>

        <!-- 历史消息 -->
        <ChatMessage
            v-for="message in messages"
            :key="message.id"
            :message="message.content"
            :is-user="message.isUser"
            :timestamp="message.timestamp"
        />

        <!-- AI 正在回复的消息 -->
        <div v-if="isAiTyping" class="chat-message ai-message">
          <div class="message-avatar">
            <div class="avatar ai-avatar">AI</div>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <div class="ai-typing-content">
                <div class="ai-response-text message-markdown" v-html="currentAiResponseRendered"></div>
                <LoadingDots v-if="isStreaming" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入框 -->
      <ChatInput
          :disabled="isAiTyping"
          @send-message="sendMessage"
          placeholder="请输入您的业务问题..."
      />
    </div>

    <!-- 连接状态提示 -->
    <div v-if="connectionError" class="connection-error">
      <div class="error-content">
        <span class="error-icon">⚠️</span>
        <span>连接服务器失败，请检查后端服务是否启动</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.app {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #1a1a1a 0%, #0d0d0d 100%);
  color: #ffffff;
}

.app-header {
  background: linear-gradient(to right, #2d2d2d, #1f1f1f);
  padding: 20px;
  border-bottom: 1px solid #333333;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.app-title {
  font-size: 24px;
  font-weight: bold;
  color: #ffffff;
  margin: 0;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.app-subtitle {
  font-size: 14px;
  color: #cccccc;
  margin-top: 5px;
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px 0;
  background: linear-gradient(180deg, #1a1a1a 0%, #242424 100%);
}

.welcome-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.welcome-content {
  text-align: center;
  max-width: 400px;
  color: #cccccc;
  background: rgba(40, 40, 40, 0.8);
  padding: 30px;
  border-radius: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid #444444;
}

.welcome-icon {
  font-size: 48px;
  margin-bottom: 20px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
}

.welcome-content h2 {
  font-size: 20px;
  margin-bottom: 15px;
  color: #ffffff;
}

.welcome-content p {
  margin-bottom: 10px;
  line-height: 1.5;
  color: #dddddd;
}

.welcome-content ul {
  text-align: left;
  margin: 15px 0;
  padding-left: 20px;
}

.welcome-content li {
  margin-bottom: 8px;
  color: #bbbbbb;
  position: relative;
}

.welcome-content li:before {
  content: "•";
  color: #4dabf7;
  font-weight: bold;
  position: absolute;
  left: -15px;
}

/* AI 正在回复时的消息样式 */
.chat-message {
  display: flex;
  margin-bottom: 20px;
  padding: 0 20px;
}

.ai-message {
  justify-content: flex-start;
  flex-direction: row;
}

.message-avatar {
  display: flex;
  align-items: flex-start;
  margin: 0 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.ai-avatar {
  background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
}

.message-content {
  max-width: 70%;
  min-width: 100px;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 18px;
  position: relative;
  word-wrap: break-word;
  word-break: break-word;
  background: linear-gradient(135deg, #2d2d2d 0%, #3d3d3d 100%);
  color: #ffffff;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  border: 1px solid #444444;
}

.ai-typing-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.ai-response-text {
  font-size: 14px;
  line-height: 1.5;
  color: #ffffff;
}

/* AI实时回复的Markdown样式 */
.ai-response-text.message-markdown h1,
.ai-response-text.message-markdown h2,
.ai-response-text.message-markdown h3,
.ai-response-text.message-markdown h4,
.ai-response-text.message-markdown h5,
.ai-response-text.message-markdown h6 {
  margin: 0.5em 0;
  font-weight: bold;
  color: #ffffff;
}

.ai-response-text.message-markdown h1 { font-size: 1.5em; color: #4dabf7; }
.ai-response-text.message-markdown h2 { font-size: 1.3em; color: #69db7c; }
.ai-response-text.message-markdown h3 { font-size: 1.2em; color: #ffa94d; }
.ai-response-text.message-markdown h4 { font-size: 1.1em; color: #ffd43b; }
.ai-response-text.message-markdown h5 { font-size: 1em; color: #cc5de8; }
.ai-response-text.message-markdown h6 { font-size: 0.9em; color: #9775fa; }

.ai-response-text.message-markdown p {
  margin: 0.5em 0;
  color: #dddddd;
}

.ai-response-text.message-markdown ul,
.ai-response-text.message-markdown ol {
  margin: 0.5em 0;
  padding-left: 1.5em;
}

.ai-response-text.message-markdown li {
  margin: 0.2em 0;
  color: #cccccc;
}

.ai-response-text.message-markdown code {
  background-color: rgba(255, 255, 255, 0.1);
  padding: 0.2em 0.4em;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.9em;
  color: #ffa94d;
  border: 1px solid #555555;
}

.ai-response-text.message-markdown pre {
  background-color: rgba(0, 0, 0, 0.3);
  padding: 1em;
  border-radius: 8px;
  overflow-x: auto;
  margin: 0.5em 0;
  border: 1px solid #555555;
}

.ai-response-text.message-markdown pre code {
  background-color: transparent;
  padding: 0;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.9em;
  color: #69db7c;
  border: none;
}

.ai-response-text.message-markdown blockquote {
  border-left: 4px solid #4dabf7;
  padding-left: 1em;
  margin: 0.5em 0;
  font-style: italic;
  color: #bbbbbb;
  background: rgba(77, 171, 247, 0.1);
  border-radius: 0 8px 8px 0;
}

.ai-response-text.message-markdown a {
  color: #4dabf7;
  text-decoration: none;
  border-bottom: 1px solid #4dabf7;
  transition: all 0.3s ease;
}

.ai-response-text.message-markdown a:hover {
  color: #69db7c;
  border-bottom-color: #69db7c;
}

.ai-response-text.message-markdown table {
  border-collapse: collapse;
  width: 100%;
  margin: 0.5em 0;
  border: 1px solid #555555;
}

.ai-response-text.message-markdown th,
.ai-response-text.message-markdown td {
  border: 1px solid #555555;
  padding: 0.5em;
  text-align: left;
  color: #dddddd;
}

.ai-response-text.message-markdown th {
  background-color: rgba(77, 171, 247, 0.2);
  font-weight: bold;
  color: #ffffff;
}

.ai-response-text.message-markdown hr {
  border: none;
  border-top: 1px solid #555555;
  margin: 1em 0;
}

.connection-error {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #ff6b6b 0%, #ff4757 100%);
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  z-index: 1000;
  animation: slideDown 0.3s ease-out;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.error-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.error-icon {
  font-size: 16px;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.3));
}

@keyframes slideDown {
  from {
    transform: translateX(-50%) translateY(-100%);
    opacity: 0;
  }
  to {
    transform: translateX(-50%) translateY(0);
    opacity: 1;
  }
}

/* 滚动条样式 */
.messages-container::-webkit-scrollbar {
  width: 8px;
}

.messages-container::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
}

.messages-container::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #4dabf7 0%, #228be6 100%);
  border-radius: 4px;
  border: 2px solid #1a1a1a;
}

.messages-container::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #69db7c 0%, #40c057 100%);
}

@media (max-width: 768px) {
  .app-header {
    padding: 15px;
  }

  .app-title {
    font-size: 20px;
  }

  .messages-container {
    padding: 15px 0;
  }

  .welcome-content {
    padding: 20px 15px;
    margin: 0 10px;
  }

  .message-content {
    max-width: 85%;
  }

  .chat-message {
    padding: 0 10px;
  }

  .welcome-content ul {
    padding-left: 15px;
  }
}
</style>