package com.capstone1.sasscapstone1.service.ChatbotService;

import com.capstone1.sasscapstone1.request.SendMessageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ChatbotService {
    ResponseEntity<?> sendMessage(SendMessageRequest request) throws Exception;
}
