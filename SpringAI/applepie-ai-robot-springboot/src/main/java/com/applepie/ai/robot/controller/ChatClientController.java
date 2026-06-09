package com.applepie.ai.robot.controller;

import com.applepie.ai.robot.tools.DateTimeTools;
import com.applepie.ai.robot.tools.WeatherTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v2/ai")
public class ChatClientController {

    @Autowired
    private ChatClient chatClient;

    @GetMapping(value = "/generateStream",produces = "text/html;charset=utf-8")
//    public Flux<String> generate(@RequestParam(value = "message", defaultValue = "你是谁？") String message){
//        return chatClient.prompt().user(message).call().content(); //返回字符串
//        return chatClient.prompt().user(message).stream().content(); //返回流式输出
//    }
    public String generate(@RequestParam(value = "message", defaultValue = "你是谁？") String message,
                           @RequestParam(value = "chatId") String chatId){
        return chatClient.prompt()
                //.system("你是一名有着十年开发经验的全栈开发工程师苹果派派")
                .tools(new DateTimeTools(), new WeatherTools())
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID,chatId))
                .call()
                .content(); //返回字符串
//        return chatClient.prompt().user(message).stream().content(); //返回流式输出
    }
}
