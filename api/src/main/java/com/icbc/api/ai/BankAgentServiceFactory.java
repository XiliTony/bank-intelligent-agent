package com.icbc.api.ai;

import com.icbc.api.ai.provider.MyChatMemoryProvider;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 银行智能体客服工厂配置类
 */
@Configuration
public class BankAgentServiceFactory {

    @Resource
    private ChatModel myQwenChatModel;

    @Resource
    private ContentRetriever contentRetriever;

    @Resource
    private MyChatMemoryProvider myChatMemoryProvider;

    @Resource
    private McpToolProvider mcpToolProvider;

    @Resource
    private StreamingChatModel qwenStreamingChatModel;

    /**
     * 创建并注册 {@link BankAgentService} 的代理实现 Bean
     *
     * @return 配置完整的 {@link BankAgentService} 代理实例
     */
    @Bean
    public BankAgentService bankAgentService() {
        // 构造AI Service
        BankAgentService bankAgentService = AiServices.builder(BankAgentService.class)
                .chatModel(myQwenChatModel)
                .streamingChatModel(qwenStreamingChatModel) // 流式输出模型
                .contentRetriever(contentRetriever) // RAG 检索增强生成
                .chatMemoryProvider(myChatMemoryProvider) // MySQL 持久化存储，每个会话独立存储
                .toolProvider(mcpToolProvider) // MCP 工具调用
                .build();
        return bankAgentService;
    }
}
