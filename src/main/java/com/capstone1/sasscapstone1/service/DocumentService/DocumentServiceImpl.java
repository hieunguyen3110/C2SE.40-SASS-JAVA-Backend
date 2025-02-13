package com.capstone1.sasscapstone1.service.DocumentService;

import com.capstone1.sasscapstone1.dto.AdminDocumentDto.AdminDocumentDto;
import com.capstone1.sasscapstone1.dto.DocumentDetailDto.DocumentDetailDto;
import com.capstone1.sasscapstone1.dto.PopularDocumentDto.PopularDocumentDto;
import com.capstone1.sasscapstone1.entity.*;
import com.capstone1.sasscapstone1.exception.DocumentException;
import com.capstone1.sasscapstone1.repository.Documents.DocumentsRepository;
import com.capstone1.sasscapstone1.repository.Faculty.FacultyRepository;
import com.capstone1.sasscapstone1.repository.Folder.FolderRepository;
import com.capstone1.sasscapstone1.repository.History.HistoryRepository;
import com.capstone1.sasscapstone1.repository.Subject.SubjectRepository;
import com.capstone1.sasscapstone1.request.TrainDocumentRequest;
import com.capstone1.sasscapstone1.service.FirebaseService.FirebaseService;
import com.capstone1.sasscapstone1.service.KafkaService.KafkaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import com.capstone1.sasscapstone1.dto.DocumentDto.DocumentDto;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {


    private final DocumentsRepository documentsRepository;
    private final FolderRepository folderRepository;
    private final FacultyRepository facultyRepository;
    private final FirebaseService firebaseService;
    private final SubjectRepository subjectRepository;
    private final HistoryRepository historyRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    private final RestTemplate restTemplate;


    @Autowired
    public DocumentServiceImpl(DocumentsRepository documentsRepository, FolderRepository folderRepository,
                               FacultyRepository facultyRepository, FirebaseService firebaseService,
                               SubjectRepository subjectRepository, EntityManager entityManager,
                               KafkaService kafkaService, RestTemplate restTemplate, HistoryRepository historyRepository) {
        this.documentsRepository = documentsRepository;
        this.folderRepository = folderRepository;
        this.facultyRepository = facultyRepository;
        this.firebaseService = firebaseService;
        this.subjectRepository = subjectRepository;
        this.entityManager = entityManager;
        this.restTemplate = restTemplate;
        this.historyRepository = historyRepository;
    }

    @Override
    public ResponseEntity<String> uploadDocument(MultipartFile file, String title, String description, String content, String type, String subjectCode, String facultyName, String folderId, Account account) throws IOException {
        try {
            if (file == null || title == null || title.isBlank() || subjectCode == null ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required fields: file, title, subjectName, or facultyName.");
            }

            String originalFileName = file.getOriginalFilename();

            if (originalFileName == null || originalFileName.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file name.");
            }
            String extension= FilenameUtils.getExtension(originalFileName);
            if (!extension.equalsIgnoreCase("pdf")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only pdf files are accepted.");
            }


            String lowerCaseType = type.toLowerCase();
            if (!lowerCaseType.equals("trắc nghiệm") && !lowerCaseType.equals("tự luận")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid document type. Only 'trắc nghiệm' and 'tự luận' are accepted.");
            }
            Faculty faculty=null;
            if(facultyName!=null){
                faculty = facultyRepository.findByFacultyName(facultyName)
                        .orElseThrow(() -> new RuntimeException("Faculty not found"));
            }
            Subject subject = subjectRepository.findBySubjectCode(subjectCode)
                    .orElseThrow(() -> new RuntimeException("Subject not found"));


            Account managedAccount = entityManager.merge(account);

            CompletableFuture<String> fileUploadFuture = firebaseService.save(file, originalFileName);
            String filePath;
            try {
                filePath = fileUploadFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file to Firebase: " + e.getMessage());
            }

            Folder folder = null;
            if (folderId != null) {
                folder = folderRepository.findByFolderIdAndAccountId(Long.parseLong(folderId), managedAccount.getAccountId())
                        .orElseThrow(() -> new RuntimeException("Folder not found or does not belong to this user."));
            }

            // Lưu tài liệu vào cơ sở dữ liệu
            Documents document = new Documents();
            document.setTitle(title);
            document.setDescription(description);
            document.setContent(content);
            document.setType(lowerCaseType);
            document.setFilePath(filePath);
            document.setFileSize((int) file.getSize());
            document.setFileName(originalFileName);
            document.setSubject(subject);
            if(faculty!=null) document.setFaculty(faculty);
            document.setAccount(managedAccount);
            if (folder != null) document.setFolder(folder);
            document.setIsActive(false);
            documentsRepository.save(document);

            return ResponseEntity.ok("Document uploaded successfully! ID: " + document.getDocId() + ", URL: " + filePath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving document: " + e.getMessage());
        }
    }


    @Override
    public Page<DocumentDto> getAllDocuments(Pageable pageable) {
        try {
            return documentsRepository.findAll(pageable).map(this::mapToDocumentDto);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all documents: " + e.getMessage(), e);
        }
    }

    @Override
    public ResponseEntity<?> getDocumentByFolderId(Long folderId, int pageNum, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<Documents> documentsPage = documentsRepository.findByFolder_FolderIdOrderByCreatedAtDesc(folderId, pageable);
            List<DocumentDto> documentDtos = documentsPage.map(this::mapToDocumentDto).toList();
            return ResponseEntity.ok(documentDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching documents by folder ID: " + e.getMessage());
        }
    }

    @Override
    public DocumentDetailDto getDocumentById(Long docId) {
        try {
            Documents document = documentsRepository.findById(docId)
                    .orElseThrow(() -> new RuntimeException("Document not found"));
            return mapToDocumentDetailDto(document);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching document by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public ResponseEntity<?> trainDocument(TrainDocumentRequest request) {
        try {
            String uri = "http://127.0.0.1:5000/api/upload-file";
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            List<Object> response = new ArrayList<>();
            HttpEntity<TrainDocumentRequest> entity = new HttpEntity<>(request, headers);
            response.add(restTemplate.postForEntity(uri, entity, Object.class).getBody());
            Optional<Documents> findDocByFilePath= documentsRepository.findByFilePath(request.getFilePath());
            if(findDocByFilePath.isPresent()){
                Documents documents= findDocByFilePath.get();
                documents.setIsTrain(true);
                documentsRepository.save(documents);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new DocumentException("Error during document training.");
        }// Logic for training document...
    }

    @Override
    public ResponseEntity<?> findAllByAccount(Account account, int pageNum, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<Documents> documentsPage = documentsRepository.findAllByAccountAndIsActiveIsTrue(account, pageable);
            List<DocumentDto> documentDtos = documentsPage.map(this::mapToDocumentDto).toList();
            return ResponseEntity.ok(documentDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching documents by account: " + e.getMessage());
        }
    }

    @Override
    public void updateDocument(Long docId, AdminDocumentDto documentDto) {
        try {
            // Tìm tài liệu theo docId
            Documents document = documentsRepository.findById(docId)
                    .orElseThrow(() -> new RuntimeException("Document not found"));

            // Cập nhật các trường thông tin cơ bản
            document.setTitle(documentDto.getTitle());
            document.setDescription(documentDto.getDescription());
            document.setType(documentDto.getType());

            // Cập nhật môn học
            if (documentDto.getSubjectName() != null) {
                Subject subject = subjectRepository.findBySubjectName(documentDto.getSubjectName())
                        .orElse(null); // Trả về null nếu không tìm thấy
                document.setSubject(subject);
            }

            // Cập nhật khoa
            if (documentDto.getFacultyName() != null) {
                Faculty faculty = facultyRepository.findByFacultyName(documentDto.getFacultyName())
                        .orElse(null); // Trả về null nếu không tìm thấy
                document.setFaculty(faculty);
            }

            // Cập nhật thư mục theo folderId
            if (documentDto.getFolderId() != null) {
                Folder folder = folderRepository.findById(documentDto.getFolderId())
                        .orElseThrow(() -> new RuntimeException("Folder not found with ID: " + documentDto.getFolderId()));
                document.setFolder(folder);
            } else {
                document.setFolder(null); // Gán null nếu không có folderId
            }

            // Lưu lại tài liệu
            documentsRepository.save(document);
        } catch (Exception e) {
            throw new RuntimeException("Error updating document: " + e.getMessage(), e);
        }
    }

    private DocumentDto mapToDocumentDto(Documents document) {
        DocumentDto dto = new DocumentDto();
        dto.setDocId(document.getDocId());
        dto.setTitle(document.getTitle());
        dto.setDescription(document.getDescription());
        dto.setFilePath(document.getFilePath());
        return dto;
    }

    private DocumentDetailDto mapToDocumentDetailDto(Documents document) {
        DocumentDetailDto dto = new DocumentDetailDto();
        dto.setDocId(document.getDocId());
        dto.setTitle(document.getTitle());
        dto.setFilePath(document.getFilePath());
        dto.setFolderName(document.getFolder() != null ? document.getFolder().getFolderName() : null);
        dto.setSubjectName(document.getSubject().getSubjectName());
        dto.setFacultyName(document.getFaculty().getFacultyName());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setAuthorName(document.getAccount().getFirstName() + " " + document.getAccount().getLastName());
        dto.setProfilePicture(document.getAccount().getProfilePicture());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PopularDocumentDto> getPopularDocuments(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return historyRepository.findAllByOrderByDownloadCountDesc(pageable).map(history -> {
                Documents document = history.getDocument();
                String authorName = document.getAccount() != null
                        ? document.getAccount().getFirstName() + " " + document.getAccount().getLastName()
                        : "Unknown";
                String profilePicture = document.getAccount() != null
                        ? document.getAccount().getProfilePicture()
                        : "Unknown";
                return new PopularDocumentDto(
                        document.getDocId(),
                        document.getTitle(),
                        document.getDescription(),
                        document.getFilePath(),
                        document.getSubject() != null ? document.getSubject().getSubjectName() : null,
                        document.getFaculty() != null ? document.getFaculty().getFacultyName() : null,
                        authorName,
                        history.getDownloadCount(),
                        profilePicture
                );
            });
        } catch (Exception e) {
            throw new RuntimeException("Error fetching popular documents: " + e.getMessage(), e);
        }
    }
}
