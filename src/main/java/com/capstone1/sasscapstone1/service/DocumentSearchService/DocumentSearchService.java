package com.capstone1.sasscapstone1.service.DocumentSearchService;

import com.capstone1.sasscapstone1.dto.DocumentDetailDto.DocumentDetailDto;
import com.capstone1.sasscapstone1.dto.DocumentSearchDto.DocumentSearchDto;
import com.capstone1.sasscapstone1.entity.Documents;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DocumentSearchService {
    ResponseEntity<List<DocumentSearchDto>> searchDocByTitle(String title);
    ResponseEntity<?> searchBySubject(String subject);
    ResponseEntity<?> searchByFacultyName(String facultyName);
    ResponseEntity<List<DocumentSearchDto>> searchDocBySubject(String subjectName);
    ResponseEntity<List<DocumentSearchDto>> searchDocByFolderName(String folderName);
    ResponseEntity<List<DocumentSearchDto>> searchDocByFacultyName(String facultyName);
    ResponseEntity<Page<DocumentDetailDto>> searchDocumentsInFolder(Long folderId, Long userId, String keyword, int page, int size);
}
