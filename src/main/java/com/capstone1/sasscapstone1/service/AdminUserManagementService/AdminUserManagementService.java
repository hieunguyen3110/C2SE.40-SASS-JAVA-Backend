package com.capstone1.sasscapstone1.service.AdminUserManagementService;

import com.capstone1.sasscapstone1.dto.UpdateUserProfileRequestDto.UpdateUserProfileRequest;
import com.capstone1.sasscapstone1.dto.UserProfileResponseDTO.UserProfileResponse;
import com.capstone1.sasscapstone1.dto.UserListDto.UserListDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminUserManagementService {
    Page<UserListDto> listUsers(int page, int size);
    UserProfileResponse getUserDetails(Long accountId);
    void softDeleteAccounts(List<Long> accountIds);
    ResponseEntity<String> approveNewUsers(List<Long> accountIds);
    ResponseEntity<?>adminDeleteUserProfilePicture(Long accountId);
    void deleteProfilePicture(Long accountId);
}
