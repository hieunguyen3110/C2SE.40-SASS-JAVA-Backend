package com.capstone1.sasscapstone1.controller.UserProfileController;

import com.capstone1.sasscapstone1.dto.UserProfileResponseDTO.UserProfileResponse;
import com.capstone1.sasscapstone1.entity.Account;
import com.capstone1.sasscapstone1.service.UserProfileService.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Kiểm tra người dùng đã đăng nhập
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Account account = (Account) authentication.getPrincipal();
            UserProfileResponse profile = userProfileService.getUserProfile(account.getEmail());
            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/profile/email")
    public ResponseEntity<?> getUserProfile(@RequestParam("email") String email) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Account account= (Account) authentication.getPrincipal();
            UserProfileResponse profile = userProfileService.getUserProfile(email,account);
            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
