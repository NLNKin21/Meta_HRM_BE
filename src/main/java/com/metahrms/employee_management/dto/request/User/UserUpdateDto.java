package com.metahrms.employee_management.dto.request.User;

import java.time.LocalDate;

import com.metahrms.employee_management.enums.UserRole;
import com.metahrms.employee_management.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Data transfer object for updating user information. All fields are optional for partial updates.")
public class UserUpdateDto {

    @Size(min = 3, message = "Username must be at least 3 characters")
    @Schema(
        description = "Updated username (minimum 3 characters)",
        example = "johndoe_updated",
        minLength = 3
    )
    String username;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(
        description = "Current password for verification when changing password",
        example = "currentPass123",
        minLength = 8
    )
    String currentPassword;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(
        description = "New password (minimum 8 characters)",
        example = "newPass123",
        minLength = 8
    )
    String password;

    @Schema(
        description = "User's gender",
        example = "MALE"
    )
    String gender;

    @Schema(
        description = "User's country",
        example = "Vietnam"
    )
    String country;

    @Schema(
        description = "User's full name",
        example = "John Doe"
    )
    String fullName;

    @Schema(
        description = "URL or path to user's profile picture",
        example = "https://example.com/profiles/user123.jpg"
    )
    String profilePicImage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(
        description = "Date of birth",
        example = "15/03/1985",
        type = "string",
        pattern = "dd/MM/yyyy"
    )
    LocalDate dob;

    @Schema(
        description = "User role (admin only)",
        example = "USER"
    )
    UserRole role;

    @Schema(
        description = "User account status (admin only)",
        example = "ACTIVE"
    )
    UserStatus status;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String currentPassword;
        private String password;
        private String gender;
        private String country;
        private String fullName;
        private String profilePicImage;
        private LocalDate dob;
        private UserRole role;
        private UserStatus status;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder currentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
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

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder profilePicImage(String profilePicImage) {
            this.profilePicImage = profilePicImage;
            return this;
        }

        public Builder dob(LocalDate dob) {
            this.dob = dob;
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

        public UserUpdateDto build() {
            UserUpdateDto dto = new UserUpdateDto();
            dto.username = this.username;
            dto.currentPassword = this.currentPassword;
            dto.password = this.password;
            dto.gender = this.gender;
            dto.country = this.country;
            dto.fullName = this.fullName;
            dto.profilePicImage = this.profilePicImage;
            dto.dob = this.dob;
            dto.role = this.role;
            dto.status = this.status;
            return dto;
        }
    }

}
