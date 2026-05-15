package com.metahrms.employee_management.dto.response.CV;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecruitmentStatsResponse {
    Long totalNew;
    Long totalReviewing;
    Long totalInterviewScheduled;
    Long totalInterviewed;
    Long totalApproved;
    Long totalOnboarded;
    Long totalRejected;
    Long totalAll;
}