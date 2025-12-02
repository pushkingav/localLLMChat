package com.apushkin.ai.localaicorechat.service;

import com.apushkin.ai.localaicorechat.model.Chat;
import com.apushkin.ai.localaicorechat.model.ChatEntry;
import com.apushkin.ai.localaicorechat.model.Role;
import com.apushkin.ai.localaicorechat.repository.ChatRepository;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    @SneakyThrows
    private static void proceedToken(ChatResponse chatResponse, SseEmitter emitter, StringBuilder answerBuilder) {
        AssistantMessage token = chatResponse.getResult().getOutput();
        emitter.send(token);
        answerBuilder.append(token.getText());
    }

    public SseEmitter proceedInteractionWithStreaming(Long chatId, String prompt) {
        myProxy.addChatEntry(chatId, prompt, Role.USER);
        SseEmitter emitter = new SseEmitter(0L);
        StringBuilder answerBuilder = new StringBuilder();
        chatClient.prompt().user(prompt).stream()
                .chatResponse()
                .subscribe(chatResponse -> proceedToken(chatResponse, emitter, answerBuilder),
                        emitter::completeWithError, () -> {
                            myProxy.addChatEntry(chatId, answerBuilder.toString(), Role.ASSISTANT);
                        }
                );
        return emitter;
    }
}
