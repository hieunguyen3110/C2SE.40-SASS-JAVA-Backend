package com.capstone1.sasscapstone1.controller.DocumentSearchController;

import com.capstone1.sasscapstone1.dto.DocumentDetailDto.DocumentDetailDto;
import com.capstone1.sasscapstone1.dto.DocumentSearchDto.DocumentSearchDto;
import com.capstone1.sasscapstone1.entity.Documents;
import com.capstone1.sasscapstone1.exception.DocumentSearchException;
import com.capstone1.sasscapstone1.service.DocumentSearchService.DocumentSearchService;
import com.capstone1.sasscapstone1.service.FolderService.FolderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class DocumentSearchController {

    private final DocumentSearchService documentSearchService;

    public DocumentSearchController(DocumentSearchService documentSearchService) {
        this.documentSearchService = documentSearchService;
    }

    // Tìm kiếm theo tên tài liệu
    @GetMapping("/title")
    public ResponseEntity<List<DocumentSearchDto>> searchByTitle(@RequestParam String title) {
        return documentSearchService.searchDocByTitle(title);
    }

    // Tìm kiếm theo môn học
    @GetMapping("/subject")
    public ResponseEntity<?> searchBySubject(@RequestParam String subject) {
        return documentSearchService.searchBySubject(subject);
    }

    // Tìm kiếm theo tên thư mục
    @GetMapping("/folder")
    public ResponseEntity<List<DocumentSearchDto>> searchDocByFolderName(@RequestParam String folderName) {
        return documentSearchService.searchDocByFolderName(folderName);
    }

    // Tìm kiếm theo tên khoa
    @GetMapping("/faculty")
    public ResponseEntity<?> searchByFacultyName(@RequestParam String facultyName) {
        return documentSearchService.searchByFacultyName(facultyName);
    }

    // Tìm kiếm theo môn học
    @GetMapping("/doc/subject")
    public ResponseEntity<List<DocumentSearchDto>> searchDocBySubject(@RequestParam String subject) {
        return documentSearchService.searchDocBySubject(subject);
    }

    // Tìm kiếm theo tên khoa
    @GetMapping("/doc/faculty")
    public ResponseEntity<List<DocumentSearchDto>> searchDocByFacultyName(@RequestParam String facultyName) {
        return documentSearchService.searchDocByFacultyName(facultyName);
    }

    @GetMapping("/{folderId}/search-documents")
    public Page<DocumentDetailDto> searchDocumentsInFolderByUser(
            @PathVariable Long folderId,
            @RequestParam Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            // Call the service method
            return documentSearchService.searchDocumentsInFolder(folderId, userId, keyword, page, size).getBody();
        } catch (DocumentSearchException e) {
            throw new RuntimeException("Folder validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while searching documents: " + e.getMessage());
        }
    }
}
