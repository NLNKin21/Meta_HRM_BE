package com.metahrms.employee_management.dto.request.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Data transfer object for user login authentication")
public class LoginDto {

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Schema(
    description = "User's email address for authentication",
    example = "hr.manager@company.com",
    required = true
  )
  String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  @Schema(
    description = "User's password (minimum 8 characters)",
    example = "12345678",
    required = true,
    minLength = 8
  )
  String password;

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String email;
    private String password;

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public LoginDto build() {
      LoginDto dto = new LoginDto();
      dto.email = this.email;
      dto.password = this.password;
      return dto;
    }
  }
}
