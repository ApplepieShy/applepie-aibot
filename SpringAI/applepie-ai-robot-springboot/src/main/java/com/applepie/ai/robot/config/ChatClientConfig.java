package com.applepie.ai.robot.config;

import com.applepie.ai.robot.advisor.MyAdvisor;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Resource
    private ChatMemory chatMemory;

    @Bean
    public ChatClient chatClient(DeepSeekChatModel deepSeekChatModel){
        return ChatClient.builder(deepSeekChatModel)
                //.defaultSystem("你是一位有着十年开发经验的高级开发工程师")
                .defaultAdvisors(new SimpleLoggerAdvisor(),
//                                    new MyAdvisor()
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
            .build();
    }
}
