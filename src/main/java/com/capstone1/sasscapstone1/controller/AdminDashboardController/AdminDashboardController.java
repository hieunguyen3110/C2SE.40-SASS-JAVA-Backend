package com.capstone1.sasscapstone1.controller.AdminDashboardController;

import com.capstone1.sasscapstone1.dto.AdminDashboardStatsDto.StatsDto;
import com.capstone1.sasscapstone1.dto.AdminDocumentDto.AdminDocumentDto;
import com.capstone1.sasscapstone1.dto.DocumentListDto.DocumentListDto;
import com.capstone1.sasscapstone1.dto.UpdateUserProfileRequestDto.UpdateUserProfileRequest;
import com.capstone1.sasscapstone1.dto.UserListDto.UserListDto;
import com.capstone1.sasscapstone1.dto.UserProfileResponseDTO.UserProfileResponse;
import com.capstone1.sasscapstone1.entity.Account;
import com.capstone1.sasscapstone1.exception.DocumentException;
import com.capstone1.sasscapstone1.request.TrainDocumentRequest;
import com.capstone1.sasscapstone1.service.AdminDashboardService.AdminDashboardService;
import com.capstone1.sasscapstone1.service.AdminUserManagementService.AdminUserManagementService;
import com.capstone1.sasscapstone1.service.DocumentCheckService.DocumentCheckService;
import com.capstone1.sasscapstone1.service.DocumentManagementService.DocumentManagementService;
import com.capstone1.sasscapstone1.service.DocumentService.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;
    private final AdminUserManagementService userManagementService;
    private final DocumentManagementService documentManagementService;
    private final DocumentCheckService documentCheckService;
    private final DocumentService documentService;
    private final AdminUserManagementService adminUserManagementService;


    @Autowired
    public AdminDashboardController(AdminDashboardService dashboardService, AdminUserManagementService userManagementService,
                                    DocumentManagementService documentManagementService, DocumentCheckService documentCheckService, DocumentService documentService, AdminUserManagementService adminUserManagementService) {
        this.dashboardService = dashboardService;
        this.userManagementService = userManagementService;
        this.documentManagementService = documentManagementService;
        this.documentCheckService = documentCheckService;
        this.documentService= documentService;
        this.adminUserManagementService = adminUserManagementService;
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsDto> getDashboardStats() {
        StatsDto stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    // List users
    @GetMapping("/users")
    public ResponseEntity<Page<UserListDto>> listUsers(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userManagementService.listUsers(page, size));
    }

    // User details
    @GetMapping("/users/{accountId}")
    public ResponseEntity<UserProfileResponse> getUserDetails(@PathVariable Long accountId) {
        return ResponseEntity.ok(userManagementService.getUserDetails(accountId));
    }

    // Delete users
    @DeleteMapping("/delete-users")
    public ResponseEntity<String> softDeleteUsers(@RequestBody List<Long> accountIds) {
        try {
            userManagementService.softDeleteAccounts(accountIds);
            return ResponseEntity.ok("Accounts successfully soft-deleted.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error soft-deleting accounts: " + e.getMessage());
        }
    }

    // Approve users
    @PostMapping("/users/approve")
    public ResponseEntity<String> approveNewUsers(@RequestBody List<Long> accountIds) {
        return userManagementService.approveNewUsers(accountIds);
    }

    @DeleteMapping("/delete-profile-picture")
    public ResponseEntity<?> adminDeleteUserProfilePicture(@RequestParam Long accountId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                return adminUserManagementService.adminDeleteUserProfilePicture(accountId);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error updating profile: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("You are not authorized to perform this action.");
        }
    }

    @GetMapping("/documents")
    public ResponseEntity<Page<DocumentListDto>> listDocuments(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(documentManagementService.listDocuments(page, size));
    }

    @GetMapping("/documents/search")
    public ResponseEntity<Page<DocumentListDto>> searchDocuments(@RequestParam String keyword,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(documentManagementService.searchDocuments(keyword, page, size));
    }

    @DeleteMapping("/documents")
    public ResponseEntity<String> softDeleteDocuments(@RequestBody List<Long> docIds) {
        documentManagementService.softDeleteDocuments(docIds);
        return ResponseEntity.ok("Documents deleted successfully.");
    }

    @PostMapping("/documents/approve")
    public ResponseEntity<?> approveDocuments(@RequestBody List<Long> docIds) {
        // Lấy thông tin đăng nhập từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to perform this action.");
        }

        // Lấy email của admin từ thông tin đăng nhập
        Account account = (Account) authentication.getPrincipal();
        String adminApprove = account.getFirstName() + " " + account.getLastName();

        try {
            // Gọi service để duyệt danh sách tài liệu và lấy tên admin
            documentManagementService.approveDocuments(docIds, adminApprove);

            return ResponseEntity.ok("Documents approved successfully.");
        } catch (DocumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }


    @PostMapping("/check-document")
    public ResponseEntity<String> checkDocument(@RequestParam Long docId) {
        try {
            Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
            if(!(authentication instanceof AnonymousAuthenticationToken)){
                documentCheckService.checkDocument(docId);
                return ResponseEntity.ok("Document checked successfully.");
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You must login to access");
            }

        } catch (DocumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error checking document: " + e.getMessage());
        }
    }

    @GetMapping("/documents/{docId}")
    public ResponseEntity<?> getDocumentDetails(@PathVariable Long docId) {
        return ResponseEntity.ok(documentManagementService.getDocumentDetails(docId));
    }

    @PutMapping("/documents/{docId}")
    public ResponseEntity<?> updateDocument(@PathVariable Long docId,
                                                 @RequestBody AdminDocumentDto documentDto) {
        documentManagementService.updateDocument(docId, documentDto);
        return ResponseEntity.ok("Document updated successfully.");
    }
    @PostMapping("/train-document")
    public ResponseEntity<?> trainDocument(@RequestBody TrainDocumentRequest request){
        try{
            return documentService.trainDocument(request);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
