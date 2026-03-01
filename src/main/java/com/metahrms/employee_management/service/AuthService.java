package com.metahrms.employee_management.service;

import com.metahrms.employee_management.dto.request.LoginRequest;
import com.metahrms.employee_management.dto.response.JwtResponse;

public interface AuthService {

    JwtResponse login(LoginRequest request);
    
    void logout(String token);
    
    JwtResponse refreshToken(String refreshToken);
    
    void forgotPassword(String email);
    
    void resetPassword(String token, String newPassword);
    
    void changePassword(Long userId, String oldPassword, String newPassword);
}