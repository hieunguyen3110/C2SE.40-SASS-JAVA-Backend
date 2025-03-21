package com.capstone1.sasscapstone1.service.HistoryService;

import com.capstone1.sasscapstone1.dto.FolderDownloadStatsDto.FolderDownloadStatsDto;
import com.capstone1.sasscapstone1.dto.PopularDocumentDto.PopularDocumentDto;
import com.capstone1.sasscapstone1.entity.Documents;
import com.capstone1.sasscapstone1.entity.History;
import com.capstone1.sasscapstone1.exception.HistoryException;
import com.capstone1.sasscapstone1.repository.Documents.DocumentsRepository;
import com.capstone1.sasscapstone1.repository.History.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final DocumentsRepository documentsRepository;

    @Autowired
    public HistoryServiceImpl(HistoryRepository historyRepository, DocumentsRepository documentsRepository) {
        this.historyRepository = historyRepository;
        this.documentsRepository = documentsRepository;
    }

    @Override
    public void trackDownload(Long documentId, String username) {
        try {
            // Kiểm tra xem tài liệu có tồn tại không
            Documents document = documentsRepository.findById(documentId)
                    .orElseThrow(() -> new HistoryException("Document not found with ID: " + documentId));

            // Tìm bản ghi history hiện tại
            Optional<History> existingHistory = historyRepository.findByDocument_DocId(documentId);

            History history;
            if (existingHistory.isPresent()) {
                // Nếu đã tồn tại, tăng số lượt tải
                history = existingHistory.get();
                history.setDownloadCount(history.getDownloadCount() + 1);
            } else {
                // Nếu chưa tồn tại, tạo mới với số lượt tải là 1
                history = new History(document, 1);
            }

            // Lưu lại bản ghi
            historyRepository.save(history);

        } catch (Exception e) {
            throw new RuntimeException("Error tracking download: " + e.getMessage(), e);
        }
    }



}

