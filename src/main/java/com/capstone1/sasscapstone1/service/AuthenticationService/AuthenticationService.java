package com.capstone1.sasscapstone1.service.AuthenticationService;

import com.capstone1.sasscapstone1.entity.Account;
import com.capstone1.sasscapstone1.request.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {
    ResponseEntity<?> login(LoginRequest loginRequest, HttpServletResponse response) throws Exception;
    ResponseEntity<?> autoLogin(HttpServletRequest request) throws Exception;
    ResponseEntity<?> refreshToken(HttpServletRequest request,HttpServletResponse response) throws Exception;
    ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) throws Exception;
    ResponseEntity<?> register(RegisterRequest registerRequest) throws Exception;
    ResponseEntity<?> validateResetPassword(SendOTPRequest request) throws Exception;
    ResponseEntity<?> resetPassword(ResetPasswordRequest request) throws Exception;
    ResponseEntity<?> clearToken(ClearTokenRequest request) throws Exception;
    ResponseEntity<?> allowActiveAccount(String email) throws Exception;
    ResponseEntity<?> changePassword(ChangePasswordRequest request, Account account) throws Exception;
    Account getCurrentUser();
}
