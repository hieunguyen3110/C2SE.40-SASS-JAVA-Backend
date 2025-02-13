package com.capstone1.sasscapstone1.service.UserUpdateProfileService;

import com.capstone1.sasscapstone1.dto.UpdateUserProfileRequestDto.UpdateUserProfileRequest;
import com.capstone1.sasscapstone1.entity.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserUpdateProfileService {
    ResponseEntity<?> updateUserProfile(Account account, UpdateUserProfileRequest request, MultipartFile profilePicture);
    void deleteProfilePicture(Long accountId);
}
