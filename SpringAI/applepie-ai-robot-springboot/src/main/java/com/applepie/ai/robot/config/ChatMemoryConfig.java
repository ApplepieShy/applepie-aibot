package com.applepie.ai.robot.config;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;

public class ChatMemoryConfig {

    @Resource
    private ChatMemoryRepository chatMemoryRepository;

    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.builder()
                .maxMessages(50)//10~50，默认20
                .chatMemoryRepository(chatMemoryRepository)
                .build();
    }
}
