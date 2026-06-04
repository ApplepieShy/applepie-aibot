package com.applepie.ai.robot.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v4/ai")
@SuppressWarnings("all")
public class GlmController {
    @Resource
    private ZhiPuAiChatModel zhiPuAiChatModel;

    @GetMapping("/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "你是谁？") String message){
        return zhiPuAiChatModel.call(message);
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
