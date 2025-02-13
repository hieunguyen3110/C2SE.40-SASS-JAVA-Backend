package com.capstone1.sasscapstone1.service.DocumentSharesService;

import com.capstone1.sasscapstone1.entity.DocumentShares;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DocumentSharesService {
    ResponseEntity<DocumentShares> shareDocument(Long documentId, Long folderId, String email, String shareUrl);
    ResponseEntity<List<DocumentShares>> getSharesByEmail(String email);
    ResponseEntity<List<DocumentShares>> getSharesByDocument(Long documentId);
}
