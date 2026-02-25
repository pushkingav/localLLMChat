package com.apushkin.ai.localaicorechat.repository;

import com.apushkin.ai.localaicorechat.model.Chat;
import com.apushkin.ai.localaicorechat.model.ChatEntry;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long>, ChatMemoryRepository {
    @Override
    default List<String> findConversationIds() {
        return findAll().stream()
                .map(chat -> String.valueOf(chat.getId()))
                .toList();
    }

    @Override
    default List<Message> findByConversationId(String conversationId) {
        Chat chat = findById(Long.valueOf(conversationId)).orElseThrow();
        return chat.getHistory().stream()
                .map(ChatEntry::toMessage)
                .toList();
    }

    @Override
    default void saveAll(String conversationId, List<Message> messages) {
        Chat chat = findById(Long.valueOf(conversationId)).orElseThrow();
        messages.stream().map(ChatEntry::toChatEntry).forEach(chat::addChatEntry);
        save(chat);
    }

    @Override
    default void deleteByConversationId(String conversationId) {
        // not implemented (NEVER)
    }
}
