package com.apushkin.ai.localaicorechat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LocalAiCoreChatApplication {

    static void main(String[] args) {
        ChatClient chatClient = SpringApplication.run(LocalAiCoreChatApplication.class, args).getBean(ChatClient.class);
        System.out.println(chatClient.prompt()
                .user("Дай мне первую строчку богемской рапсодии")
                .call().content());
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
