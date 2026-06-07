package com.applepie.ai.robot.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/v4/ai")
@SuppressWarnings("all")
public class GlmController {
    @Resource
    private ZhiPuAiChatModel zhiPuAiChatModel;

    // 存储聊天对话
    private Map<String, List<Message>> chatMemoryStore = new ConcurrentHashMap<>();

    @GetMapping("/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "你是谁？") String message,
                           @RequestParam(value = "chatId") String chatId){
        // 根据 chatId 获取对话记录
        List<Message> messages = chatMemoryStore.get(chatId);
        // 若不存在，则初始化一份
        if (CollectionUtils.isEmpty(messages)) {
            messages = new ArrayList<>();
            chatMemoryStore.put(chatId, messages);
        }

        // 添加 “用户角色消息” 到聊天记录中
        messages.add(new UserMessage(message));

        // 构建提示词
        Prompt prompt = new Prompt(messages);
        // 一次性返回结果
        String responseText = zhiPuAiChatModel.call(prompt).getResult().getOutput().getText();

        // 添加 “助手角色消息” 到聊天记录中
        messages.add(new AssistantMessage(responseText));

        return responseText;
    }
    /**
     * 流式返回
     */
    @GetMapping(value = "/generateStream",produces = "text/html;charset=utf-8")
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "你是谁？") String message){
        Prompt prompt = new Prompt(new UserMessage(message));

        return zhiPuAiChatModel.stream(prompt)
                .mapNotNull(response -> response.getResult().getOutput().getText());
    }
}
