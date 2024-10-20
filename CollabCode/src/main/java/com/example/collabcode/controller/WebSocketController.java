package com.example.collabcode.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class WebSocketController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MessageMapping("/code") // The endpoint to receive messages
    @SendTo("/topic/code") // Where to send messages back
    public void handleTextMessage(String message) {
        try {
            // Parse the incoming JSON message
            Map<String, Object> parsedMessage = objectMapper.readValue(message, Map.class);
            String action = (String) parsedMessage.get("action");

            switch (action) {
                case "EDIT":
                    handleEdit(parsedMessage);
                    break;
                case "SAVE":
                    handleSave(parsedMessage);
                    break;
                case "EXECUTE":
                    handleExecute(parsedMessage);
                    break;
                case "COMMENT":
                    handleComment(parsedMessage);
                    break;
                // Add more cases as needed
                default:
                    // Handle unknown action
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle parsing errors
        }
    }

    private void handleEdit(Map<String, Object> message) {
        String content = (String) message.get("content");
        String username = (String) message.get("username");
        String timestamp = (String) message.get("timestamp");
        // Implement logic to handle the edit action
        System.out.println(username + " edited the code at " + timestamp + ": " + content);
    }

    private void handleSave(Map<String, Object> message) {
        String fileId = (String) message.get("fileId");
        String username = (String) message.get("username");
        String timestamp = (String) message.get("timestamp");
        // Implement logic to handle the save action
        System.out.println(username + " saved file " + fileId + " at " + timestamp);
    }

    private void handleExecute(Map<String, Object> message) {
        String fileId = (String) message.get("fileId");
        String language = (String) message.get("language");
        String username = (String) message.get("username");
        String timestamp = (String) message.get("timestamp");
        // Implement logic to handle the execute action
        System.out.println(username + " executed file " + fileId + " in " + language + " at " + timestamp);
    }

    private void handleComment(Map<String, Object> message) {
        String fileId = (String) message.get("fileId");
        String comment = (String) message.get("comment");
        String username = (String) message.get("username");
        String timestamp = (String) message.get("timestamp");
        // Implement logic to handle the comment action
        System.out.println(username + " commented on file " + fileId + ": " + comment + " at " + timestamp);
    }
}
