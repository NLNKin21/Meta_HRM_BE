package com.metahrms.employee_management.dto.response.CV;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.metahrms.employee_management.enums.CandidateStatus;
import com.metahrms.employee_management.enums.Gender;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Thông tin ứng viên")
public class CandidateResponse {

    Integer id;

    // Thông tin cá nhân
    String fullName;
    String email;
    String phoneNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate dob;

    Gender gender;
    String address;

    // Vị trí ứng tuyển
    String desiredPosition;
    Integer departmentId;
    String departmentName;
    String expectedSalary;

    // Hồ sơ
    String cvFileUrl;
    String cvFileName;
    String coverLetter;

    // Trạng thái
    CandidateStatus status;
    String statusLabel;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime appliedAt;

    // Kết quả
    String rejectReason;
    String note;

    // Liên kết
    Integer createdUserId;
    Integer createdEmployeeId;

    // HR xử lý
    Integer reviewedBy;
    String reviewedByName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime reviewedAt;

    Integer approvedBy;
    String approvedByName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime approvedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime createdAt;
}