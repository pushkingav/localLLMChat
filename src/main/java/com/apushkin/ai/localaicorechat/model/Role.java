package com.apushkin.ai.localaicorechat.model;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Role {
    USER("user"),
    ASSISTANT("assistant"),
    SYSTEM("system");

    private final String role;

    public String getRole() {
        return role;
    }

    public static Role getRole(String roleName) {
        return Arrays.stream(Role.values()).filter(r -> r.role.equalsIgnoreCase(roleName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + roleName));
    }
}
