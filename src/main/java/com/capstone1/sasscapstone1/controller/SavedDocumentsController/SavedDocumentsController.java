package com.capstone1.sasscapstone1.controller.SavedDocumentsController;

import com.capstone1.sasscapstone1.dto.SavedDocumentsDto.SavedDocumentsDto;
import com.capstone1.sasscapstone1.service.SavedDocumentsService.SavedDocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/saved-documents")
public class SavedDocumentsController {

    private final SavedDocumentsService savedDocumentsService;

    @Autowired
    public SavedDocumentsController(SavedDocumentsService savedDocumentsService) {
        this.savedDocumentsService = savedDocumentsService;
    }

    // API lưu tài liệu
    @PostMapping("/save")
    public ResponseEntity<String> saveDocument(@RequestParam Long docId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Long accountId = getAccountIdFromAuthentication(authentication);

            savedDocumentsService.saveDocument(docId, accountId);
            return ResponseEntity.ok("Document saved successfully.");
        } else {
            return ResponseEntity.status(401).body("You are not authorized to perform this action.");
        }
    }

    // API hiển thị danh sách tài liệu đã lưu
    @GetMapping("/list")
    public ResponseEntity<Page<SavedDocumentsDto>> listSavedDocuments(@RequestParam int page, @RequestParam int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Long accountId = getAccountIdFromAuthentication(authentication);

            Page<SavedDocumentsDto> savedDocuments = savedDocumentsService.listSavedDocuments(accountId, page, size);
            return ResponseEntity.ok(savedDocuments);
        } else {
            return ResponseEntity.status(401).body(null);
        }
    }

    // API xóa tài liệu đã lưu
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteSavedDocument(@RequestParam Long docId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Long accountId = getAccountIdFromAuthentication(authentication);

            savedDocumentsService.deleteSavedDocument(docId, accountId);
            return ResponseEntity.ok("Document removed successfully from saved list.");
        } else {
            return ResponseEntity.status(401).body("You are not authorized to perform this action.");
        }
    }

    // Helper method để lấy accountId từ Authentication
    private Long getAccountIdFromAuthentication(Authentication authentication) {
        // Trích xuất accountId từ principal (nếu principal là kiểu Account)
        return ((com.capstone1.sasscapstone1.entity.Account) authentication.getPrincipal()).getAccountId();
    }
}
