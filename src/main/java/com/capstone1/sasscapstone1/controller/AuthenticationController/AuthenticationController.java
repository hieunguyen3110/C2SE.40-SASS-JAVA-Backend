package com.capstone1.sasscapstone1.controller.AuthenticationController;


import com.capstone1.sasscapstone1.dto.RegisterDto.RegisterDto;
import com.capstone1.sasscapstone1.entity.Account;
import com.capstone1.sasscapstone1.request.*;
import com.capstone1.sasscapstone1.service.AuthenticationService.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws Exception {
        return authenticationService.login(loginRequest,response);
    }
    @PostMapping("/register")
    public ResponseEntity<?> handleRegister(@RequestBody RegisterRequest registerRequest) throws Exception {
        return authenticationService.register(registerRequest);
    }
    @GetMapping("/refresh-token")
    public ResponseEntity<?> handleRefreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return authenticationService.refreshToken(request,response);
    }
    @GetMapping("/autoLogin")
    ResponseEntity<?> autoLogin(HttpServletRequest httpServletRequest) throws Exception {
        return authenticationService.autoLogin(httpServletRequest);
    }
    @GetMapping("/logout")
    public ResponseEntity<?> handleLogout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return authenticationService.logout(request,response);
    }
    @PostMapping("/validate/reset-password")
    public ResponseEntity<?> handleValidateResetPassword(@RequestBody SendOTPRequest request) throws Exception {
        return authenticationService.validateResetPassword(request);
    }

    @PostMapping("/update/new-password")
    public ResponseEntity<?> handleSaveNewPassword(@RequestBody ResetPasswordRequest request) throws Exception {
        return authenticationService.resetPassword(request);
    }

    @PostMapping("/delete/clear-token")
    public ResponseEntity<?> handleClearToken(@RequestBody ClearTokenRequest request) throws Exception{
        return authenticationService.clearToken(request);
    }
    @PutMapping("/change-password")
    public ResponseEntity<?> handleChangePassword(@RequestBody ChangePasswordRequest request) throws Exception {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            Account account= (Account) authentication.getPrincipal();
            return authenticationService.changePassword(request,account);
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You must be login");
        }
    }
}
