package com.metahrms.employee_management.controller;

<<<<<<< HEAD
public class AuthController {
    
=======
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metahrms.employee_management.dto.request.User.LoginDto;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.AuthResponse;
import com.metahrms.employee_management.dto.response.User.UserResponse;
import com.metahrms.employee_management.service.AuthService;
import com.metahrms.employee_management.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Tag(name = "Authentication", description = "APIs for user authentication and authorization")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;
    UserService userService;

    @Operation(
        summary = "Login",
        description = "Authenticate user with email and password. Returns JWT token in HttpOnly cookie (access_token) with 5 days expiry."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Login successful",
        content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    @PostMapping("/login")
    public ApiResponse<AuthResponse> authenticate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Login credentials (email and password)",
                required = true,
                content = @Content(schema = @Schema(implementation = LoginDto.class))
            )
            @RequestBody LoginDto request,
            HttpServletResponse response) {
        String token = authService.authenticate(request);

        Cookie jwtCookie = new Cookie("access_token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60 * 24 * 5);
        jwtCookie.setAttribute("SameSite", "Strict");

        response.addCookie(jwtCookie);

        ApiResponse<AuthResponse> apiResponse = new ApiResponse<AuthResponse>();

        apiResponse.setStatus("success");
        apiResponse.setMessage("Login successfilly");
        return apiResponse;
    }

    @Operation(
        summary = "Logout",
        description = "Logout user by clearing the access_token cookie. Sets cookie maxAge to 0 to delete it from browser."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Logout successful",
        content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("access_token", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        jwtCookie.setAttribute("SameSite", "Strict");

        response.addCookie(jwtCookie);

        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setMessage("Logout successfully");
        return apiResponse;
    }

    @Operation(
        summary = "Get current user",
        description = "Get the current authenticated user's information from the JWT token in the cookie"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "User information retrieved successfully",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUserById(HttpServletRequest request) {
        UserResponse userResponse = userService.getUserInfo(request);
        return ResponseEntity.ok(userResponse);
    }


    // @Operation(
    //     summary = "Forgot password",
    //     description = "Reset user password by providing email and new password"
    // )
    // @io.swagger.v3.oas.annotations.responses.ApiResponse(
    //     responseCode = "200",
    //     description = "Password changed successfully",
    //     content = @Content(schema = @Schema(implementation = ApiResponse.class))
    // )
    // @PostMapping("/forgot_password")
    // public ApiResponse<Void> forgotPassword(
    //         @io.swagger.v3.oas.annotations.parameters.RequestBody(
    //             description = "Forgot password request (email and new password)",
    //             required = true,
    //             content = @Content(schema = @Schema(implementation = ForgotPasswordDto.class))
    //         )
    //         @RequestBody ForgotPasswordDto input) {
    //     try {
    //         authService.forgotPassword(input);

    //         ApiResponse<Void> successResponse = new ApiResponse<>();
    //         successResponse.setStatus("success");
    //         successResponse.setMessage("Change Password Successfully");

    //         return successResponse;
    //     } catch (RuntimeException e) {
    //         ApiResponse<Void> errorResponse = new ApiResponse<>();
    //         errorResponse.setStatus("failed");
    //         errorResponse.setMessage(e.getMessage());

    //         return errorResponse;
    //     }
    // }

>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
}
