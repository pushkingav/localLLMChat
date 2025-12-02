package com.apushkin.ai.localaicorechat.controller;

import com.apushkin.ai.localaicorechat.model.Chat;
import com.apushkin.ai.localaicorechat.service.ChatService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {
    public static final String CHATS = "chats";
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/")
    public String index(ModelMap model) {
        model.addAttribute(CHATS, chatService.getAllChats());
        return "chat";
    }

    @GetMapping("/chat/{chatId}")
    public String showChat(@PathVariable Long chatId, ModelMap model) {
        model.addAttribute(CHATS, chatService.getAllChats());
        model.addAttribute("chat", chatService.getChat(chatId));
        return "chat";
    }

    @PostMapping("/chat/new")
    public String newChat(@RequestParam String title, ModelMap model) {
        Chat chat = chatService.createNewChat(title);
        return "redirect:/chat/" + chat.getId();
    }

    @PostMapping("/chat/{chatId}/delete")
    public String deleteChat(@PathVariable Long chatId) {
        chatService.deleteChat(chatId);
        return "redirect:/";
    }

//    @PostMapping("/chat/{chatId}/entry")
    public String talkToModel(@PathVariable Long chatId, @RequestParam String prompt) {
        chatService.proceedInteraction(chatId, prompt);
        return "redirect:/chat/" + chatId;
    }
}
