package com.capstone1.sasscapstone1.service.DocumentSearchService;

import com.capstone1.sasscapstone1.dto.DocumentDetailDto.DocumentDetailDto;
import com.capstone1.sasscapstone1.dto.DocumentDto.DocumentDto;
import com.capstone1.sasscapstone1.dto.DocumentSearchDto.DocumentSearchDto;
import com.capstone1.sasscapstone1.dto.FacultyDto.FacultyDto;
import com.capstone1.sasscapstone1.dto.SubjectDto.SubjectDto;
import com.capstone1.sasscapstone1.entity.Documents;
import com.capstone1.sasscapstone1.entity.Faculty;
import com.capstone1.sasscapstone1.entity.Folder;
import com.capstone1.sasscapstone1.entity.Subject;
import com.capstone1.sasscapstone1.exception.DocumentSearchException;
import com.capstone1.sasscapstone1.exception.FolderException;
import com.capstone1.sasscapstone1.repository.Documents.DocumentsRepository;
import com.capstone1.sasscapstone1.repository.Faculty.FacultyRepository;
import com.capstone1.sasscapstone1.repository.Folder.FolderRepository;
import com.capstone1.sasscapstone1.repository.Subject.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentSearchServiceImpl implements DocumentSearchService {

    private final DocumentsRepository documentsRepository;
    private final SubjectRepository subjectRepository;
    private final FacultyRepository facultyRepository;
    private final FolderRepository folderRepository;

    private DocumentSearchDto mapToDto(Documents document) {
        return DocumentSearchDto.builder()
                .docId(document.getDocId())
                .title(document.getTitle())
                .description(document.getDescription())
                .content(document.getContent())
                .type(document.getType())
                .subjectName(document.getSubject() != null ? document.getSubject().getSubjectName() : null)
                .facultyName(document.getFaculty() != null ? document.getFaculty().getFacultyName() : null)
                .build();
    }

    private DocumentDetailDto mapDocumentToDto(Documents document) {
        return DocumentDetailDto.builder()
                .title(document.getTitle())
                .docId(document.getDocId())
                .filePath(document.getFilePath())
                .build();
    }

    @Override
    public ResponseEntity<List<DocumentSearchDto>>  searchDocByTitle(String title) {
        try {
            List<Documents> documents = documentsRepository.findByTitleContainingIgnoreCaseAndIsActiveIsTrue(title);
            List<DocumentSearchDto> dtos = documents.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            throw new DocumentSearchException("Error searching documents by title: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> searchBySubject(String subjectName) {
        try {
            List<Subject> subjects = subjectRepository.findAllBySubjectNameContainingIgnoreCase(subjectName.toUpperCase());
            List<SubjectDto> subjectDtos = new ArrayList<>();
            for (Subject subject : subjects) {
                SubjectDto subjectDto = SubjectDto.builder()
                        .subjectCode(subject.getSubjectCode())
                        .subjectName(subject.getSubjectName())
                        .build();
                subjectDtos.add(subjectDto);
            }
            return ResponseEntity.status(HttpStatus.OK).body(subjectDtos);
        } catch (Exception e) {
            throw new DocumentSearchException("Error searching subject by subject name: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> searchByFacultyName(String facultyName) {
        try {
            List<Faculty> faculties = facultyRepository.findAllByFacultyNameContainingIgnoreCase(facultyName.toUpperCase());
            List<FacultyDto> facultyDtos = new ArrayList<>();
            for (Faculty faculty : faculties) {
                FacultyDto facultyDto = FacultyDto.builder()
                        .facultyId(faculty.getFacultyId())
                        .facultyName(faculty.getFacultyName())
                        .build();
                facultyDtos.add(facultyDto);
            }
            return ResponseEntity.status(HttpStatus.OK).body(facultyDtos);
        } catch (Exception e) {
            throw new DocumentSearchException("Error searching documents by faculty name: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<DocumentSearchDto>> searchDocBySubject(String subject) {
        List<Documents> documents = documentsRepository.findBySubject_SubjectNameContainingIgnoreCaseAndIsActiveIsTrue(subject);
        List<DocumentSearchDto> dtos = documents.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<List<DocumentSearchDto>> searchDocByFolderName(String folderName) {
        List<Documents> documents = documentsRepository.findByFolderName(folderName);
        List<DocumentSearchDto> dtos = documents.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<List<DocumentSearchDto>> searchDocByFacultyName(String facultyName) {
        List<Documents> documents = documentsRepository.findByFacultyName(facultyName);
        List<DocumentSearchDto> dtos = documents.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<Page<DocumentDetailDto>> searchDocumentsInFolder(Long folderId, Long userId, String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            // Check if the folder belongs to the user
            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new FolderException("Folder not found with ID: " + folderId));

            if (!Long.valueOf(folder.getAccount().getAccountId()).equals(userId)) {
                throw new FolderException("Folder does not belong to the user.");
            }

            // Search documents in the folder
            Page<Documents> documentsPage = keyword != null && !keyword.isBlank()
                    ? documentsRepository.findByFolder_FolderIdAndTitleContainingIgnoreCaseAndIsActiveIsTrue(folderId, keyword, pageable)
                    : documentsRepository.findByFolder_FolderIdAndIsActiveIsTrue(folderId, pageable);

            return ResponseEntity.ok(documentsPage.map(this::mapDocumentToDto));

        } catch (Exception e) {
            throw new RuntimeException("Error searching documents in folder: " + e.getMessage(), e);
        }
    }
}

