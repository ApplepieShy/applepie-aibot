package com.applepie.ai.robot.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v3/ai")
@SuppressWarnings("all")
public class QWenTestController {

    @Resource
    private OllamaChatModel ollamaChatModel;

    /**
     * 同步返回
     */
    @GetMapping("/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "你是谁？") String message){
        ChatResponse chatResponse = ollamaChatModel.call(new Prompt(message));
        /**
         *  // 构建提示词，调用大模型，不动用yml文件配置的话可以这样自由配置
         *  ChatResponse chatResponse = chatModel.call(new Prompt(
         *          message,
         *          OllamaOptions.builder()
         *                  .model("qwen3:14b") // 模型名称
         *                  .temperature(0.4) // 温度值
         *                  .build()
         *  ));
          */
        return chatResponse.getResult().getOutput().getText();
    }
    /**
     * 流式返回
     */

    @GetMapping(value = "/generateStream",produces = "text/html;charset=utf-8")
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "你是谁？") String message){
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.ollamaChatModel.stream(prompt).mapNotNull(chatResponse -> {
            String text = chatResponse.getResult().getOutput().getText();
            return text != null ? text.replace("\n", "<br>") : text;
                }
            );
    }
}
