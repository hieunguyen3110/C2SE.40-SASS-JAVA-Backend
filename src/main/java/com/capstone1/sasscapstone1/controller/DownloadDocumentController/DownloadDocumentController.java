package com.capstone1.sasscapstone1.controller.DownloadDocumentController;

import com.capstone1.sasscapstone1.service.DownloadDocumentService.DownloadDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/download")
public class DownloadDocumentController {

    private final DownloadDocumentService downloadDocumentService;

    @Autowired
    public DownloadDocumentController(DownloadDocumentService downloadDocumentService) {
        this.downloadDocumentService = downloadDocumentService;
    }

    // API tải tài liệu và ghi nhận lượt tải
    @GetMapping("/{documentId}")
    public ResponseEntity<String> downloadDocument(@PathVariable Long documentId) throws IOException {
        // Lấy username từ thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return downloadDocumentService.downloadDocument(documentId, username);
    }
}
