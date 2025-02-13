package com.capstone1.sasscapstone1.controller.FolderController;

import com.capstone1.sasscapstone1.dto.FolderDownloadStatsDto.FolderDownloadStatsDto;
import com.capstone1.sasscapstone1.dto.FolderDto.FolderDto;
import com.capstone1.sasscapstone1.dto.FolderRequestDto.FolderRequestDto;
import com.capstone1.sasscapstone1.entity.Folder;
import com.capstone1.sasscapstone1.entity.Account;
import com.capstone1.sasscapstone1.service.FolderService.FolderService;
import com.capstone1.sasscapstone1.service.UserDetailService.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/folder")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    // Tạo folder mới
    @PostMapping("/create")
    public ResponseEntity<?> createFolder(@RequestBody FolderRequestDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Account account = (Account) authentication.getPrincipal();
            FolderDto folderDto = folderService.createFolder(request.getFolderName(), request.getDescription(), account);
            return ResponseEntity.ok(folderDto);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
    }

    @GetMapping("/{folderId}")
    public ResponseEntity<?> getFolderByIdOfUser(@PathVariable("folderId") Long folderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return folderService.getFolderById(folderId);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
    }

    @GetMapping("/{folderId}/by-email")
    public ResponseEntity<?> getFolderByIdOfUserOther(@PathVariable("folderId") Long folderId, @RequestParam("email") String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Account account = (Account) userDetailService.loadUserByUsername(email);
            return folderService.getFolderById(account.getAccountId(),folderId);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
    }

    // Cập nhật folder
    @PutMapping("/update/{folderId}")
    public ResponseEntity<?> updateFolder(@PathVariable Long folderId, @RequestBody FolderRequestDto request) {
        FolderDto folderDto = folderService.updateFolder(folderId, request.getFolderName(), request.getDescription());
        return ResponseEntity.ok(folderDto);
    }

    // Xóa folder
    @DeleteMapping("/delete/{folderId}")
    public ResponseEntity<String> deleteFolder(@PathVariable Long folderId) {
        folderService.deleteFolder(folderId);
        return ResponseEntity.ok("Folder deleted successfully");
    }

    // Lấy tất cả folder của người dùng
    @GetMapping("/all")
    public ResponseEntity<List<FolderDto>> getAllFolders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Account account = (Account) authentication.getPrincipal();
            List<FolderDto> folders = folderService.getAllFolders(account);
            return ResponseEntity.ok(folders);
        }
        return ResponseEntity.status(403).body(null);
    }

    @GetMapping("/top-folders")
    public ResponseEntity<Page<FolderDownloadStatsDto>> getTopFoldersByDownloadCount(@RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "3") int size) {
        Page<FolderDownloadStatsDto> topFolders = folderService.getTopFoldersByDownloadCount(page, size);
        return ResponseEntity.ok(topFolders);
    }
}