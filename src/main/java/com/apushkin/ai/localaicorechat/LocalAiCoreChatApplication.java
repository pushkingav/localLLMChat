package com.apushkin.ai.localaicorechat;

import com.apushkin.ai.localaicorechat.repository.ChatRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LocalAiCoreChatApplication {

    private final ChatRepository chatRepository;

    public LocalAiCoreChatApplication(@Autowired ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    static void main(String[] args) {
        ChatClient chatClient = SpringApplication.run(LocalAiCoreChatApplication.class, args).getBean(ChatClient.class);
        System.out.println(chatClient.prompt()
                .user("Дай мне первую строчку богемской рапсодии")
                .call().content());
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultAdvisors(getAdvisor())
                .build();
    }

    private Advisor getAdvisor() {
       return MessageChatMemoryAdvisor.builder(getChatMemory()).build();
    }

    private ChatMemory getChatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(2)
                .chatMemoryRepository(chatRepository)
                .build();
    }
}
