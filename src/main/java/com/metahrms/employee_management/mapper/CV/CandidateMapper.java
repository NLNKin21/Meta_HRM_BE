package com.metahrms.employee_management.mapper.CV;

import com.metahrms.employee_management.dto.response.CV.CandidateResponse;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.CV.Candidate;
import com.metahrms.employee_management.enums.CandidateStatus;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CandidateMapper {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public CandidateResponse toResponse(Candidate candidate) {
        if (candidate == null) return null;

        String departmentName = null;
        if (candidate.getDepartmentId() != null) {
            departmentName = departmentRepository.findById(candidate.getDepartmentId())
                    .map(Department::getDeptName)
                    .orElse(null);
        }

        String reviewedByName = null;
        if (candidate.getReviewedBy() != null) {
            reviewedByName = employeeRepository.findByIdAndIsDeletedFalse(candidate.getReviewedBy())
                    .map(e -> e.getFullName())
                    .orElse(null);
        }

        String approvedByName = null;
        if (candidate.getApprovedBy() != null) {
            approvedByName = employeeRepository.findByIdAndIsDeletedFalse(candidate.getApprovedBy())
                    .map(e -> e.getFullName())
                    .orElse(null);
        }

        return CandidateResponse.builder()
                .id(candidate.getId())
                .fullName(candidate.getFullName())
                .email(candidate.getEmail())
                .phoneNumber(candidate.getPhoneNumber())
                .dob(candidate.getDob())
                .gender(candidate.getGender())
                .address(candidate.getAddress())
                .desiredPosition(candidate.getDesiredPosition())
                .departmentId(candidate.getDepartmentId())
                .departmentName(departmentName)
                .expectedSalary(candidate.getExpectedSalary())
                .cvFileUrl(candidate.getCvFileUrl())
                .cvFileName(candidate.getCvFileName())
                .coverLetter(candidate.getCoverLetter())
                .status(candidate.getStatus())
                .statusLabel(getStatusLabel(candidate.getStatus()))
                .appliedAt(candidate.getAppliedAt())
                .rejectReason(candidate.getRejectReason())
                .note(candidate.getNote())
                .createdUserId(candidate.getCreatedUserId())
                .createdEmployeeId(candidate.getCreatedEmployeeId())
                .reviewedBy(candidate.getReviewedBy())
                .reviewedByName(reviewedByName)
                .reviewedAt(candidate.getReviewedAt())
                .approvedBy(candidate.getApprovedBy())
                .approvedByName(approvedByName)
                .approvedAt(candidate.getApprovedAt())
                .createdAt(candidate.getCreatedAt())
                .build();
    }

    private String getStatusLabel(CandidateStatus status) {
        if (status == null) return null;
        return switch (status) {
            case NEW -> "Mới nộp";
            case REVIEWING -> "Đang xem xét";
            case INTERVIEW_SCHEDULED -> "Đã lên lịch PV";
            case INTERVIEWED -> "Đã phỏng vấn";
            case APPROVED -> "Đã duyệt";
            case ONBOARDED -> "Đã tạo tài khoản";
            case REJECTED -> "Từ chối";
        };
    }

    public String getEmployeeName(Integer employeeId) {
        if (employeeId == null) return null;
        return employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .map(e -> e.getFullName())
                .orElse("Unknown");
    }
}