package com.apushkin.ai.localaicorechat.service;

import com.apushkin.ai.localaicorechat.model.Chat;
import com.apushkin.ai.localaicorechat.model.ChatEntry;
import com.apushkin.ai.localaicorechat.model.Role;
import com.apushkin.ai.localaicorechat.repository.ChatRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatClient chatClient;

    @Autowired
    private ChatService myProxy;


    public ChatService(ChatRepository chatRepository, ChatClient chatClient) {
        this.chatRepository = chatRepository;
        this.chatClient = chatClient;
    }

    public List<Chat> getAllChats() {
        return chatRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Chat getChat(Long chatId) {
        return chatRepository.findById(chatId).orElseThrow();
    }

    public Chat createNewChat(String title) {
        Chat chat = Chat.builder().title(title).build();
        return chatRepository.save(chat);
    }

    public void deleteChat(Long chatId) {
        chatRepository.deleteById(chatId);
    }

    @Transactional
    public void proceedInteraction(Long chatId, String prompt) {
        //  Use self-injection to ensure transactional methods are properly proxied
        myProxy.addChatEntry(chatId, prompt, Role.USER);

        String answer = chatClient.prompt().user(prompt).call().content();

        myProxy.addChatEntry(chatId, answer, Role.ASSISTANT);
    }

    public void addChatEntry(Long chatId, String prompt, Role role) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        chat.addChatEntry(ChatEntry.builder()
                .content(prompt)
                .role(role).build());
    }
}
