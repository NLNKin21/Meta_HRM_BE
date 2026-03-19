package com.metahrms.employee_management.dto.response.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.LocalDate;

import com.metahrms.employee_management.enums.UserRole;
import com.metahrms.employee_management.enums.UserStatus;


import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Response object containing user details")
public class UserResponse {

  @Schema(description = "Unique identifier of the user", example = "1")
  Integer id;

  @Schema(description = "Username", example = "johndoe")
  String username;

  @Schema(description = "Email address", example = "john.doe@example.com")
  String email;

  @Schema(description = "User's gender", example = "MALE")
  String gender;

  @Schema(description = "User's country", example = "Vietnam")
  String country;

  @Schema(description = "URL or path to user's profile picture", example = "https://example.com/profiles/user123.jpg")
  String profilePicImage;

  @Schema(description = "User's full name", example = "John Doe")
  String fullName;

  @Schema(description = "User role in the system", example = "USER")
  UserRole role;

  @Schema(description = "User account status", example = "ACTIVE")
  UserStatus status;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  @Schema(
    description = "Date of birth",
    example = "15/03/1985",
    type = "string",
    pattern = "dd/MM/yyyy"
  )
  LocalDate dob;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  @Schema(
    description = "Date when the user account was created",
    example = "28/11/2025",
    type = "string",
    pattern = "dd/MM/yyyy"
  )
  LocalDateTime createdAt;
}
