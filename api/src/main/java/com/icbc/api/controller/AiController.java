package com.icbc.api.controller;

import com.icbc.api.ai.BankAgentService;
import jakarta.annotation.Resource;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Ai 控制器
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private BankAgentService bankAgentService;

    /**
     * 流式聊天接口
     *
     * @param memoryId 会话 id
     * @param message 用户输入的问题
     * @return {@link ServerSentEvent} 的响应流，每个事件包含一段生成的文本
     */
    @GetMapping("/chat")
    public Flux<ServerSentEvent<String>> chat(@RequestParam("memoryId")int memoryId,
                                              @RequestParam("message")String message) {
        return bankAgentService.chatStream(memoryId, message)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }
}
