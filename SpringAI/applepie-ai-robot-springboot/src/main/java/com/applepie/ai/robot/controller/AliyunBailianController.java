package com.applepie.ai.robot.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Objects;

@RestController
@RequestMapping("/v6/ai")
@SuppressWarnings("all")
public class AliyunBailianController {

    @Resource
    private OpenAiChatModel openAiChatModel;

    @GetMapping("/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "你是谁？") String message){
        return openAiChatModel.call(message);
    }
    /**
     * 流式返回
     */
    @GetMapping(value = "/generateStream",produces = "text/html;charset=utf-8")
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "你是谁？") String message){
        Prompt prompt = new Prompt(new UserMessage(message));
        return openAiChatModel.stream(prompt)
                .mapNotNull(response -> {
                    Generation generation = response.getResult();
                    return Objects.nonNull(generation) ? generation.getOutput().getText() : null;
                });
    }
}
