package com.icbc.api.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

/**
 * 银行智能体客服服务接口
 */
public interface BankAgentService {

    /**
     * 流式输出
     *
     * @param memoryId 会话 id
     * @param message 用户输入的问题
     * @return 生成回答的字符串流
     */
    @SystemMessage(fromResource = "system-prompt.txt") // 从资源中加载提示模板
    Flux<String> chatStream(@MemoryId Integer memoryId, @UserMessage String message);

}
