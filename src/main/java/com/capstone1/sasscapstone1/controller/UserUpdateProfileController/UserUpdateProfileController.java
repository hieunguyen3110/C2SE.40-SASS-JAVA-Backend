package com.capstone1.sasscapstone1.controller.UserUpdateProfileController;

import com.capstone1.sasscapstone1.entity.Account;
import com.capstone1.sasscapstone1.dto.UpdateUserProfileRequestDto.UpdateUserProfileRequest;
import com.capstone1.sasscapstone1.service.UserUpdateProfileService.UserUpdateProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserUpdateProfileController {

    private final UserUpdateProfileService userProfileService;

    @Autowired
    public UserUpdateProfileController(UserUpdateProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PutMapping("update-profile")
    public ResponseEntity<?> updateUserProfile(
            @ModelAttribute UpdateUserProfileRequest request,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Account account = (Account) authentication.getPrincipal();
            try {
                return userProfileService.updateUserProfile(account, request, profilePicture);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error updating profile: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("You are not authorized to perform this action.");
        }
    }

    @DeleteMapping("/delete-profile-picture")
    public ResponseEntity<String> deleteProfilePicture() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Account account = (Account) authentication.getPrincipal();
            Long accountId = account.getAccountId();

            userProfileService.deleteProfilePicture(accountId);

            return ResponseEntity.ok("Profile picture deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to perform this action.");
        }
    }
}

