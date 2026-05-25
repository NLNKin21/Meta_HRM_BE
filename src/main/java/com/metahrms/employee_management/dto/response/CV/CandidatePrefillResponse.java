package com.metahrms.employee_management.dto.response.CV;

import com.metahrms.employee_management.enums.CandidateStatus;
import com.metahrms.employee_management.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidatePrefillResponse {
    private Integer candidateId;
    private Integer userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate dob;
    private Gender gender;
    private String address;
    private Integer departmentId;
    private String desiredPosition;
    private String expectedSalary;
    private CandidateStatus status;
}