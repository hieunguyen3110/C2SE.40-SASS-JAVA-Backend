package com.capstone1.sasscapstone1.service.DownloadDocumentService;

import com.capstone1.sasscapstone1.exception.DownloadDocumentException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface DownloadDocumentService {
    ResponseEntity<String> downloadDocument(Long documentId, String username) throws DownloadDocumentException, IOException;
}
