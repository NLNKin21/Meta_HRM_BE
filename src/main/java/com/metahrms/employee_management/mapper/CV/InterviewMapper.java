package com.metahrms.employee_management.mapper.CV;

import com.metahrms.employee_management.dto.response.CV.InterviewResponse;
import com.metahrms.employee_management.entity.CV.Candidate;
import com.metahrms.employee_management.entity.CV.Interview;
import com.metahrms.employee_management.enums.InterviewResult;
import com.metahrms.employee_management.enums.InterviewType;
import com.metahrms.employee_management.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterviewMapper {

    private final EmployeeRepository employeeRepository;

    public InterviewResponse toResponse(Interview interview) {
        if (interview == null) return null;

        Candidate candidate = interview.getCandidate();

        // Lấy tên interviewer
        String interviewerName = null;
        if (interview.getInterviewerName() != null) {
            interviewerName = interview.getInterviewerName();
        } else if (interview.getInterviewerId() != null) {
            interviewerName = employeeRepository.findByIdAndIsDeletedFalse(interview.getInterviewerId())
                    .map(e -> e.getFullName())
                    .orElse("Unknown");
        }

        return InterviewResponse.builder()
                // Ứng viên
                .id(interview.getId())
                .candidateId(candidate != null ? candidate.getId() : null)
                .candidateName(candidate != null ? candidate.getFullName() : null)
                .candidateEmail(candidate != null ? candidate.getEmail() : null)
                .candidatePhone(candidate != null ? candidate.getPhoneNumber() : null)
                .desiredPosition(candidate != null ? candidate.getDesiredPosition() : null)
                // Lịch
                .interviewDate(interview.getInterviewDate())
                .durationMinutes(interview.getDurationMinutes())
                .interviewType(interview.getInterviewType())
                .interviewTypeLabel(getInterviewTypeLabel(interview.getInterviewType()))
                .location(interview.getLocation())
                // Người PV
                .interviewerId(interview.getInterviewerId())
                .interviewerName(interviewerName)
                // Kết quả
                .result(interview.getResult())
                .resultLabel(getResultLabel(interview.getResult()))
                .score(interview.getScore())
                .feedback(interview.getFeedback())
                .strengths(interview.getStrengths())
                .weaknesses(interview.getWeaknesses())
                // Meta
                .note(interview.getNote())
                .emailSent(interview.getEmailSent())
                .emailSentAt(interview.getEmailSentAt())
                .createdAt(interview.getCreatedAt())
                .updatedAt(interview.getUpdatedAt())
                .build();
    }

    private String getInterviewTypeLabel(InterviewType type) {
        if (type == null) return null;
        return switch (type) {
            case ONLINE -> "Trực tuyến";
            case OFFLINE -> "Trực tiếp";
        };
    }

    private String getResultLabel(InterviewResult result) {
        if (result == null) return null;
        return switch (result) {
            case PENDING -> "Chờ đánh giá";
            case PASSED -> "Đạt";
            case FAILED -> "Không đạt";
        };
    }
}