package com.applepie.ai.robot.controller;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/v1/ai")
public class DSV1Controller {

    @Resource
    private DeepSeekChatModel chatModel;

    @GetMapping(value = "/generateStream", produces = "text/html;charset=utf-8")
    public Flux<String> generate(@RequestParam(value = "message", defaultValue = "你是谁？") String message){
        Prompt prompt = new Prompt(new UserMessage(message));
        // 使用原子布尔值跟踪分隔线状态（每个请求独立）
        AtomicBoolean needSeparator = new AtomicBoolean(true);
        return chatModel.stream(prompt)
                .mapNotNull(chatResponse -> {
                    // 获取响应内容
                    DeepSeekAssistantMessage deepSeekAssistantMessage = (DeepSeekAssistantMessage) chatResponse.getResult().getOutput();
                    // 推理内容
                    String reasoningContent = deepSeekAssistantMessage.getReasoningContent();
                    // 推理结束后的正式回答
                    String text = deepSeekAssistantMessage.getText();

                    boolean isTextResponse = false;
                    // 若推理内容有值，则响应推理内容，否则，说明推理结束了，响应正式回答
                    String rawContent;
                    if (Objects.isNull(text)) {
                        rawContent = reasoningContent;
                    } else {
                        rawContent = text;
                        isTextResponse = true; // 标记为正式回答
                    }
                    // 处理换行
                    String processed = StringUtils.isNotBlank(rawContent) ? rawContent.replace("\n", "<br>") : rawContent;

                    // 在正式回答内容之前，添加一个分隔线
                    if (isTextResponse
                            && needSeparator.compareAndSet(true, false)) {
                        processed = "<hr>" + processed; // 使用 HTML 的 <hr> 标签实现
                    }

                    return processed;
                });
    }
}
