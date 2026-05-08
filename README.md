# 基于 LangChain4j + RAG 的银行智能体客服问答系统

## 项目简介

本项目是一套完整的**银行智能体客服问答系统**，旨在帮助用户全面、便捷地回答银行业务相关问题。

系统支持用户询问智能体银行业务相关问题，智能体会根据用户问题进行详细解答。

系统深度集成 **AI 能力**，基于 **Qwen3-Max 大模型**，结合**银行知识库**进行检索增强生成（RAG），并接入**智谱 Web 搜索 MCP 工具**实现联网搜索，显著提升回答的准确性和时效性。

---

## 技术架构

本项目采用 **前后端分离架构**：

### 前端
| 技术 | 用途 |
|------|------|
| Vue3 | 构建用户界面 |
| ElementUI | 搭建页面组件 |
| Axios | 实现前后端数据交互 |

### 后端
| 技术 | 用途 |
|------|------|
| SpringBoot3 | 遵循三层架构（Controller、Service、Mapper） |
| LangChain4j | 集成大语言模型开发能力 |
| MySQL | 系统数据库，持久化聊天记忆 |
| Pinecone向量数据库 | 存储知识库切片向量，用于检索增强生成|

### AI 能力集成
| 能力 | 说明 |
|------|------|
| Qwen3-Max | 大语言模型基座 |
| RAG（检索增强生成） | 结合银行知识库 |
| 智谱 Web 搜索 | 联网获取银行业务相关信息 |

---

## 主要功能
| 功能模块 |
|----------|
| 解答个人金融服务相关业务 |
| 提供电子银行操作指引 |
| 分享个人电子银行交易规则 |
---

## 项目亮点
- **专业化**：AI 结合银行知识库 RAG，结合MCP联网搜索，建议具备专业可信度
- **企业级**：前后端分离，架构清晰，易于扩展与维护

---

## 快速开始

### 环境要求
| 环境 | 版本要求 |
|------|----------|
| JDK | 21 |
| Node.js | 22.18.0 |
| MySQL | 8.x |
| IDEA | 2025 及以上 |

---

## 项目部署

### 1. 数据库配置
将项目中的 SQL 文件导入 Navicat 等数据库管理工具中完成初始化。

### 2. 前端配置（VSCode/WebStorm等）
将view导入VSCode等开发软件，从src进入终端，终端里面输入npm i指令安装前端项目依赖，安装完成后输入pnpm dev启动前端

### 3. 后端配置（IDEA）
使用 IDEA 打开 `api` 后端项目，按以下步骤配置：

#### 2.1 项目结构设置
1. 打开 IDEA **Project Structure（项目结构）**
2. 在 `Modules → Sources` 中选择 `21 Lambdas`
3. 在 `Modules → Dependencies` 中指定 **JDK 21**（与环境要求统一）

#### 2.2 Maven 环境配置
1. 打开 IDEA **Settings（设置）**
2. 搜索 `maven` 并完成以下配置：
   - `Maven home path`：本机 Maven 安装目录
   - `User settings file`：本机 Maven 配置文件路径
   - `Local repository`：本机 Maven 本地仓库路径

#### 2.3 配置文件修改（application.yml）
修改后端配置文件，更新以下信息：
- 数据库名称
- MySQL 用户名
- MySQL 密码

### 4. AI API Key 配置
#### 4.1 阿里云百炼平台
1. 登录阿里云百炼平台，创建账号并开通服务
2. 获取 API Key
3. 填入配置文件中 `langchain4j` 对应的 `api-key` 字段

#### 4.2 智谱大模型
1. 登录智谱大模型官网，创建应用并获取 API Key
2. 填入配置文件中 `big-model` 对应的 `api-key` 字段

<p align="center">
  <img width="680" height="830" alt="配置示例" src="https://github.com/user-attachments/assets/9e799d39-2b5f-4312-8828-d85214d54895" />
</p>

#### 4.3 Pinecone向量知识库
1. 进入Pinecone官网：https://www.pinecone.io/
2. 注册账号，获取Pinecone API Key，注意每个账号只能获取一个，务必将其记下来，别的项目也能用
   <img width="2549" height="1403" alt="image" src="https://github.com/user-attachments/assets/62b788ca-7f44-4e39-a443-ff202fe5277a" />
3. EmbeddingStoreConfig类中将key填入.apiKey("")中
   <img width="1466" height="1388" alt="image" src="https://github.com/user-attachments/assets/4cf0a0e7-92df-4932-8af3-a43b30ccb413" />

---

## 项目结构

<p align="left">
  <img width="314" height="178" alt="image" src="https://github.com/user-attachments/assets/b050b161-a18b-4ffa-8525-8fd230c4c97f" />
</p>

重要：存放项目的文件路径中不要有中文

| 目录 | 说明 |
| ---- | ---- |
| view | 前端项目 |
| api | 后端项目 |
| persistent_chat | 数据库文件 |
