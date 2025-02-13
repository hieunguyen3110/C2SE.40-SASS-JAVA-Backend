package com.capstone1.sasscapstone1.service.DocumentSharesService;

import com.capstone1.sasscapstone1.entity.DocumentShares;
import com.capstone1.sasscapstone1.entity.Documents;
import com.capstone1.sasscapstone1.entity.Folder;
import com.capstone1.sasscapstone1.repository.DocumentShares.DocumentSharesRepository;
import com.capstone1.sasscapstone1.repository.Documents.DocumentsRepository;
import com.capstone1.sasscapstone1.repository.Folder.FolderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DocumentSharesServiceImpl implements DocumentSharesService {

    private final DocumentSharesRepository documentShareRepository;
    private final DocumentsRepository documentsRepository;
    private final FolderRepository foldersRepository;

    public DocumentSharesServiceImpl(DocumentSharesRepository documentShareRepository,
                                     DocumentsRepository documentsRepository,
                                     FolderRepository foldersRepository) {
        this.documentShareRepository = documentShareRepository;
        this.documentsRepository = documentsRepository;
        this.foldersRepository = foldersRepository;
    }

    @Override
    public ResponseEntity<DocumentShares> shareDocument(Long documentId, Long folderId, String email, String shareUrl) {
        try {
            Documents document = documentsRepository.findById(documentId)
                    .orElseThrow(() -> new RuntimeException("Document not found"));

            Folder folder = folderId != null ? foldersRepository.findById(folderId)
                    .orElseThrow(() -> new RuntimeException("Folder not found")) : null;

            DocumentShares documentShare = new DocumentShares();
            documentShare.setDocument(document);
            documentShare.setFolderId(folder);
            documentShare.setEmail(email);
            documentShare.setShareUrl(shareUrl);
            documentShareRepository.save(documentShare);

            return ResponseEntity.ok(documentShare);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @Override
    public ResponseEntity<List<DocumentShares>> getSharesByEmail(String email) {
        try {
            List<DocumentShares> shares = documentShareRepository.findByEmail(email);
            return ResponseEntity.ok(shares);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @Override
    public ResponseEntity<List<DocumentShares>> getSharesByDocument(Long documentId) {
        try {
            List<DocumentShares> shares = documentShareRepository.findByDocument_DocId(documentId);
            return ResponseEntity.ok(shares);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
