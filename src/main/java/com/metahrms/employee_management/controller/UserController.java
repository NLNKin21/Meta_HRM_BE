package com.metahrms.employee_management.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.metahrms.employee_management.dto.request.User.RegisterUserDto;
import com.metahrms.employee_management.dto.request.User.UserFilterDto;
import com.metahrms.employee_management.dto.request.User.UserUpdateDto;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.dto.response.User.UserResponse;
import com.metahrms.employee_management.enums.UserStatus;
import com.metahrms.employee_management.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Tag(name = "User", description = "APIs for managing users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @Operation(
        summary = "Get all users",
        description = "Retrieve a paginated list of users with optional filtering by status, department, date of birth, and search term"
    )
    @GetMapping
    public ApiResponse<PagedResponse<UserResponse>> getUsers(
            @Parameter(description = "Page number (zero-based)", example = "0") @RequestParam(name="page",required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(name = "pageSize",required = false, defaultValue = "10") Integer pageSize,
            @Parameter(description = "Filter by user status") @RequestParam(name = "status",required = false) UserStatus status,
            @Parameter(description = "Filter by department ID") @RequestParam(name = "deptId",required = false) Integer deptId,
            @Parameter(description = "Filter by date of birth (format: dd/MM/yyyy)", example = "01/01/1990") @RequestParam(name = "dob",required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dob,
            @Parameter(description = "Search in user fields (case-insensitive)") @RequestParam(name = "search",required = false) String search
    ) {
        UserFilterDto filterDto = UserFilterDto.builder()
                .page(page)
                .pageSize(pageSize)
                .status(status)
                .deptId(deptId)
                .dob(dob)
                .search(search)
                .build();

        PagedResponse<UserResponse> users = userService.getUser(filterDto);

        ApiResponse<PagedResponse<UserResponse>> response = new ApiResponse<>();
        response.setStatus("success");
        response.setMessage("Get users successfully");
        response.setData(users);
        return response;
    }

    @Operation(
        summary = "Get user by ID",
        description = "Retrieve a single user by their ID"
    )
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable Integer id) {
        UserResponse user = userService.getUserById(id);

        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setStatus("success");
        response.setMessage("Get user successfully");
        response.setData(user);
        return response;
    }

    @Operation(
        summary = "Create user",
        description = "Create a new user account"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User registration data",
                required = true,
                content = @Content(schema = @Schema(implementation = RegisterUserDto.class))
            )
            @Valid @RequestBody RegisterUserDto dto) {
        UserResponse user = userService.createUser(dto);

        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setStatus("success");
        response.setMessage("User created successfully");
        response.setData(user);
        return response;
    }

    @Operation(
        summary = "Update user",
        description = "Update an existing user. All fields are optional for partial updates."
    )
    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User update data",
                required = true,
                content = @Content(schema = @Schema(implementation = UserUpdateDto.class))
            )
            @Valid @RequestBody UserUpdateDto dto) {
        UserResponse user = userService.updateUser(id, dto);

        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setStatus("success");
        response.setMessage("User updated successfully");
        response.setData(user);
        return response;
    }

    @Operation(
        summary = "Delete user",
        description = "Delete a user account"
    )
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable Integer id) {
        userService.deleteUser(id);

        ApiResponse<Void> response = new ApiResponse<>();
        response.setStatus("success");
        response.setMessage("User deleted successfully");
        return response;
    }

    @Operation(
        summary = "Get users not linked to employees",
        description = "Retrieve a list of users that are not linked to any employee record. Useful for creating new employee records."
    )
    @GetMapping("/unlinked")
    public ApiResponse<List<UserResponse>> getUsersNotLinkedToEmployee() {
        List<UserResponse> users = userService.getUsersNotLinkedToEmployee();

        ApiResponse<List<UserResponse>> response = new ApiResponse<>();
        response.setStatus("success");
        response.setMessage("Get unlinked users successfully");
        response.setData(users);
        return response;
    }
}
