package com.capstone1.sasscapstone1.service.DownloadDocumentService;

import com.capstone1.sasscapstone1.entity.Documents;
import com.capstone1.sasscapstone1.entity.History;
import com.capstone1.sasscapstone1.exception.DownloadDocumentException;
import com.capstone1.sasscapstone1.repository.Documents.DocumentsRepository;
import com.capstone1.sasscapstone1.repository.History.HistoryRepository;
import com.capstone1.sasscapstone1.service.HistoryService.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Service
public class DownloadDocumentServiceImpl implements DownloadDocumentService {

    private final DocumentsRepository documentsRepository;
    private final HistoryService historyService;

    @Autowired
    public DownloadDocumentServiceImpl(DocumentsRepository documentsRepository, HistoryService historyService) {
        this.documentsRepository = documentsRepository;
        this.historyService = historyService;
    }

    @Override
    public ResponseEntity<String> downloadDocument(Long documentId, String username) {
        try {
            // Kiểm tra tài liệu có tồn tại không
            Documents document = documentsRepository.findById(documentId)
                    .orElseThrow(() -> new DownloadDocumentException("Document not found"));

            historyService.trackDownload(documentId, username);

            // Trả về filePath của tài liệu
            String filePath = document.getFilePath();

            if (filePath != null && !filePath.isEmpty()) {
                return ResponseEntity.ok(filePath);
            } else {
                throw new DownloadDocumentException("File path is invalid or not found.");
            }
        } catch (DownloadDocumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}
