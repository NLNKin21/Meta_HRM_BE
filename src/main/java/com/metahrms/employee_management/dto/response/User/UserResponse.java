package com.metahrms.employee_management.dto.response.User;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.LocalDate;

import com.metahrms.employee_management.enums.UserRole;
import com.metahrms.employee_management.enums.UserStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;

@Data
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

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Integer id;
    private String username;
    private String email;
    private String gender;
    private String country;
    private String profilePicImage;
    private String fullName;
    private UserRole role;
    private UserStatus status;
    private LocalDate dob;
    private LocalDateTime createdAt;

    public Builder id(Integer id) {
      this.id = id;
      return this;
    }

    public Builder username(String username) {
      this.username = username;
      return this;
    }

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder gender(String gender) {
      this.gender = gender;
      return this;
    }

    public Builder country(String country) {
      this.country = country;
      return this;
    }

    public Builder profilePicImage(String profilePicImage) {
      this.profilePicImage = profilePicImage;
      return this;
    }

    public Builder fullName(String fullName) {
      this.fullName = fullName;
      return this;
    }

    public Builder role(UserRole role) {
      this.role = role;
      return this;
    }

    public Builder status(UserStatus status) {
      this.status = status;
      return this;
    }

    public Builder dob(LocalDate dob) {
      this.dob = dob;
      return this;
    }

    public Builder createdAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public UserResponse build() {
      UserResponse response = new UserResponse();
      response.id = this.id;
      response.username = this.username;
      response.email = this.email;
      response.gender = this.gender;
      response.country = this.country;
      response.profilePicImage = this.profilePicImage;
      response.fullName = this.fullName;
      response.role = this.role;
      response.status = this.status;
      response.dob = this.dob;
      response.createdAt = this.createdAt;
      return response;
    }
  }
}
