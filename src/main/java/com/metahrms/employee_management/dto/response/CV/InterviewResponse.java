package com.metahrms.employee_management.dto.response.CV;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.metahrms.employee_management.enums.InterviewResult;
import com.metahrms.employee_management.enums.InterviewType;

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
@Schema(description = "Thông tin buổi phỏng vấn")
public class InterviewResponse {

    Integer id;

    // Ứng viên
    Integer candidateId;
    String candidateName;
    String candidateEmail;
    String candidatePhone;
    String desiredPosition;

    // Lịch
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime interviewDate;

    Integer durationMinutes;
    InterviewType interviewType;
    String interviewTypeLabel;
    String location;

    // Người phỏng vấn
    Integer interviewerId;
    String interviewerName;

    // Kết quả
    InterviewResult result;
    String resultLabel;
    Integer score;
    String feedback;
    String strengths;
    String weaknesses;

    // Meta
    String note;
    Boolean emailSent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime emailSentAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime updatedAt;
}
