package com.capstone1.sasscapstone1.controller.DocumentController;

import com.capstone1.sasscapstone1.dto.AdminDocumentDto.AdminDocumentDto;
import com.capstone1.sasscapstone1.dto.DocumentDetailDto.DocumentDetailDto;
import com.capstone1.sasscapstone1.dto.DocumentDto.DocumentDto;
import com.capstone1.sasscapstone1.dto.PopularDocumentDto.PopularDocumentDto;
import com.capstone1.sasscapstone1.entity.Account;
import com.capstone1.sasscapstone1.request.TrainDocumentRequest;
import com.capstone1.sasscapstone1.service.DocumentService.DocumentService;
import com.capstone1.sasscapstone1.service.HistoryService.HistoryService;
import com.capstone1.sasscapstone1.service.UserDetailService.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final UserDetailServiceImpl userDetailService;

    // Upload tài liệu với môn học và khoa
    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestPart("file") MultipartFile file,
                                                 @RequestPart("title") String title,
                                                 @RequestPart("description") String description,
                                                 @Nullable @RequestPart("content") String content,
                                                 @RequestPart("type") String type,
                                                 @RequestPart("subjectCode") String subjectCode,
                                                 @Nullable @RequestPart("facultyName") String facultyName,
                                                 @Nullable  @RequestPart("folderId") String folderId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Account account = (Account) authentication.getPrincipal();
            try {
                return documentService.uploadDocument(file, title, description, content, type, subjectCode, facultyName, folderId, account);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to perform this action.");
        }
    }

    // Lấy tất cả tài liệu
    @GetMapping("/all")
    @Transactional
    public ResponseEntity<Page<DocumentDto>> getAllDocuments(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "3") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DocumentDto> documentPage = documentService.getAllDocuments(pageable);
        return ResponseEntity.ok(documentPage);
    }

    @GetMapping("/folder")
    @Transactional
    public ResponseEntity<?> getAllDocumentsByFolderId(@RequestParam("folderId") Long folderId,
                                                       @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
                                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
                                                       ) {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            return documentService.getDocumentByFolderId(folderId,pageNum,pageSize);
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to perform this action.");
        }
    }

    // Lấy tài liệu theo ID
    @GetMapping("/{docId}")
    public ResponseEntity<DocumentDetailDto> getDocumentById(@PathVariable Long docId) {
        try {
            DocumentDetailDto documentDetail = documentService.getDocumentById(docId);
            return ResponseEntity.ok(documentDetail);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    @PostMapping("/train-document")
//    public ResponseEntity<?> trainDocument(@RequestBody List<TrainDocumentRequest> request){
//        try{
//            return documentService.trainDocument(request);
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//    }

    @GetMapping("/account")
    @Transactional
    public ResponseEntity<?> getAllDocumentsByAccount(
                                                      @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        try{
            Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
            if(!(authentication instanceof AnonymousAuthenticationToken)){
                Account account= (Account) authentication.getPrincipal();
                return documentService.findAllByAccount(account,pageNum,pageSize);
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to perform this action.");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/account/email")
    @Transactional
    public ResponseEntity<?> getAllDocumentsByEmail(@RequestParam("email") String email,
                                                       @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
                                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        try{
            Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
            if(!(authentication instanceof AnonymousAuthenticationToken)){
                Account account= (Account) userDetailService.loadUserByUsername(email);
                return documentService.findAllByAccount(account,pageNum,pageSize);
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to perform this action.");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{docId}")
    public ResponseEntity<?> updateDocument(@PathVariable Long docId,
                                            @RequestBody AdminDocumentDto documentDto) {
        documentService.updateDocument(docId, documentDto);
        return ResponseEntity.ok("Document updated successfully.");
    }

    @GetMapping("/popular")
    public ResponseEntity<Page<PopularDocumentDto>> getPopularDocuments(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                        @RequestParam(value = "size", defaultValue = "3") int size) {
        Page<PopularDocumentDto> popularDocuments = documentService.getPopularDocuments(page, size);
        return ResponseEntity.ok(popularDocuments);
    }

}
