package com.metahrms.employee_management.dto.response.CV;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.metahrms.employee_management.enums.CandidateStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecruitmentHistoryResponse {

    Integer id;
    String action;
    CandidateStatus fromStatus;
    CandidateStatus toStatus;
    Integer performedBy;
    String performedByName;
    String note;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime createdAt;
}