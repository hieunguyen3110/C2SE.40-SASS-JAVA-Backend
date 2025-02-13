package com.capstone1.sasscapstone1.controller.DocumentSharesController;

import com.capstone1.sasscapstone1.entity.DocumentShares;
import com.capstone1.sasscapstone1.service.DocumentSharesService.DocumentSharesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/shares")
public class DocumentSharesController {

    @Autowired
    private DocumentSharesService documentShareService;

    @PostMapping("/email")
    public ResponseEntity<DocumentShares> shareDocument(@RequestParam Long documentId,
                                                       @RequestParam(required = false) Long folderId,
                                                       @RequestParam String email,
                                                       @RequestParam String shareUrl) {
        return documentShareService.shareDocument(documentId, folderId, email, shareUrl);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<DocumentShares>> getSharesByEmail(@PathVariable String email) {
        return documentShareService.getSharesByEmail(email);
    }

    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<DocumentShares>> getSharesByDocument(@PathVariable Long documentId) {
        return documentShareService.getSharesByDocument(documentId);
    }
}
