package com.apushkin.ai.localaicorechat.model;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Role {
    USER("user") {
        @Override
        Message composeMessage(String prompt) {
            return new UserMessage(prompt);
        }
    },
    ASSISTANT("assistant") {
        @Override
        Message composeMessage(String prompt) {
            return new AssistantMessage(prompt);
        }
    },
    SYSTEM("system") {
        @Override
        Message composeMessage(String prompt) {
            return new SystemMessage(prompt);
        }
    };

    private final String role;

    public String getRole() {
        return role;
    }

    public static Role getRole(String roleName) {
        return Arrays.stream(Role.values()).filter(r -> r.role.equalsIgnoreCase(roleName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + roleName));
    }

    abstract Message composeMessage(String prompt);
}
