package com.applepie.ai.robot.controller;

import com.applepie.ai.robot.model.AIResponse;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/v6/ai")
@SuppressWarnings("all")
public class AliyunBailianController {

    @Resource
    private OpenAiChatModel openAiChatModel;

    // 存储聊天对话
    private Map<String, List<Message>> chatMemoryStore = new ConcurrentHashMap<>();


    @GetMapping("/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "你是谁？") String message){
        return openAiChatModel.call(message);
    }
    /**
     * 流式返回
     */
    @GetMapping(value = "/generateStream",produces = {MediaType.TEXT_EVENT_STREAM_VALUE,"text/html;charset=utf-8"})
    public Flux<AIResponse> generateStream(@RequestParam(value = "message", defaultValue = "你是谁？") String message){
        SystemMessage systemMessage = new SystemMessage("你是一名有着十年开发经验的全栈开发工程师苹果派派");
        UserMessage userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(Arrays.asList(systemMessage, userMessage));
        return openAiChatModel.stream(prompt)
                .mapNotNull(chatResponse -> {
                    Generation generation = chatResponse.getResult();
                    String text = generation.getOutput().getText();
                    return AIResponse.builder().v(text).build();
                });
    }
}
