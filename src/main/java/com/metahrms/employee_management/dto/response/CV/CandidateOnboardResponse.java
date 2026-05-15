package com.metahrms.employee_management.dto.response.CV;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Kết quả onboard ứng viên")
public class CandidateOnboardResponse {

    @Schema(description = "Thông tin ứng viên")
    CandidateResponse candidate;

    @Schema(description = "User ID được tạo")
    Integer createdUserId;

    @Schema(description = "Username được tạo")
    String createdUsername;

    @Schema(description = "Email đăng nhập")
    String createdEmail;

    @Schema(description = "Mật khẩu đã được gửi qua email")
    Boolean passwordSentViaEmail;

    @Schema(description = "Thông báo")
    String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime onboardedAt;
}