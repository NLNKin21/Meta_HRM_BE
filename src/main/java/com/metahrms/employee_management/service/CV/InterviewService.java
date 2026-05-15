package com.metahrms.employee_management.service.CV;


import com.metahrms.employee_management.dto.request.CV.InterviewCreateRequest;
import com.metahrms.employee_management.dto.request.CV.InterviewRescheduleRequest;
import com.metahrms.employee_management.dto.request.CV.InterviewUpdateResultRequest;
import com.metahrms.employee_management.dto.response.CV.InterviewResponse;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.CV.Candidate;
import com.metahrms.employee_management.entity.CV.Interview;
import com.metahrms.employee_management.entity.CV.RecruitmentHistory;
import com.metahrms.employee_management.enums.CandidateStatus;
import com.metahrms.employee_management.enums.InterviewResult;
import com.metahrms.employee_management.exception.BusinessException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.mapper.CV.InterviewMapper;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.CV.CandidateRepository;
import com.metahrms.employee_management.repository.CV.InterviewRepository;
import com.metahrms.employee_management.repository.CV.RecruitmentHistoryRepository;
import com.metahrms.employee_management.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final CandidateRepository candidateRepository;
    private final EmployeeRepository employeeRepository;
    private final RecruitmentHistoryRepository historyRepository;
    private final InterviewMapper interviewMapper;
    private final EmailService emailService;

    private static final DateTimeFormatter DT_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ========================================================
    // TẠO LỊCH PHỎNG VẤN
    // ========================================================

    @Transactional
    public InterviewResponse createInterview(
            InterviewCreateRequest request,
            Integer createdByEmployeeId
    ) {
        log.info("[INTERVIEW] Creating interview for candidate: {}", request.getCandidateId());

        // 1. Validate candidate
        Candidate candidate = candidateRepository.findByIdAndIsDeletedFalse(request.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy ứng viên với ID: " + request.getCandidateId()));

        // Kiểm tra trạng thái hợp lệ
        if (candidate.getStatus() != CandidateStatus.REVIEWING
                && candidate.getStatus() != CandidateStatus.NEW) {
            throw new BusinessException(
                    "Chỉ có thể lên lịch phỏng vấn cho ứng viên ở trạng thái 'Mới nộp' hoặc 'Đang xem xét'. " +
                    "Trạng thái hiện tại: " + candidate.getStatus()
            );
        }

        // 2. Validate interviewer
        Employee interviewer = employeeRepository.findByIdAndIsDeletedFalse(request.getInterviewerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người phỏng vấn với ID: " + request.getInterviewerId()));

        // 3. Parse ngày giờ
        LocalDateTime interviewDate = parseDateTime(request.getInterviewDate());

        // Ngày phỏng vấn phải trong tương lai
        if (interviewDate.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Ngày giờ phỏng vấn phải trong tương lai");
        }

        // 4. Kiểm tra interviewer có trống lịch không
        int duration = request.getDurationMinutes() != null ? request.getDurationMinutes() : 60;
        LocalDateTime endTime = interviewDate.plusMinutes(duration);

        if (interviewRepository.hasConflict(request.getInterviewerId(), interviewDate, endTime)) {
            throw new BusinessException(
                    "Người phỏng vấn " + interviewer.getFullName() +
                    " đã có lịch vào thời gian này. Vui lòng chọn thời gian khác."
            );
        }

        // 5. Tạo interview
        Interview interview = Interview.builder()
                .candidate(candidate)
                .interviewDate(interviewDate)
                .durationMinutes(duration)
                .interviewType(request.getInterviewType())
                .location(request.getLocation())
                .interviewerId(request.getInterviewerId())
                .interviewerName(interviewer.getFullName())
                .result(InterviewResult.PENDING)
                .note(request.getNote())
                .createdBy(createdByEmployeeId)
                .emailSent(false)
                .build();

        interview.setIsDeleted(false);
        Interview saved = interviewRepository.save(interview);

        // 6. Cập nhật trạng thái candidate
        CandidateStatus oldStatus = candidate.getStatus();
        candidate.setStatus(CandidateStatus.INTERVIEW_SCHEDULED);
        candidateRepository.save(candidate);

        // 7. Ghi lịch sử
        logHistory(candidate, oldStatus, CandidateStatus.INTERVIEW_SCHEDULED,
                "INTERVIEW_SCHEDULED", createdByEmployeeId,
                "Lên lịch PV: " + interviewDate.format(DT_FORMATTER) +
                " - PV viên: " + interviewer.getFullName());

        // 8. Gửi email mời phỏng vấn
        sendInterviewInvitationEmail(candidate, saved, interviewer);

        log.info("[INTERVIEW] Created interview id={} for candidate={}, date={}",
                saved.getId(), candidate.getId(), interviewDate);

        return interviewMapper.toResponse(saved);
    }

    // ========================================================
    // ĐÁNH GIÁ KẾT QUẢ
    // ========================================================

    @Transactional
    public InterviewResponse updateResult(
            Integer interviewId,
            InterviewUpdateResultRequest request,
            Integer performedByEmployeeId
    ) {
        log.info("[INTERVIEW] Updating result for interview: {}", interviewId);

        Interview interview = findInterviewOrThrow(interviewId);

        // Chỉ đánh giá khi PENDING
        if (interview.getResult() != InterviewResult.PENDING) {
            throw new BusinessException(
                    "Buổi phỏng vấn này đã được đánh giá: " + interview.getResult()
            );
        }

        // Cập nhật kết quả
        interview.setResult(request.getResult());
        interview.setScore(request.getScore());
        interview.setFeedback(request.getFeedback());
        interview.setStrengths(request.getStrengths());
        interview.setWeaknesses(request.getWeaknesses());

        if (request.getNote() != null) {
            interview.setNote(request.getNote());
        }

        Interview saved = interviewRepository.save(interview);

        // Cập nhật candidate status
        Candidate candidate = interview.getCandidate();
        CandidateStatus oldStatus = candidate.getStatus();
        candidate.setStatus(CandidateStatus.INTERVIEWED);
        candidateRepository.save(candidate);

        // Ghi lịch sử
        String resultText = request.getResult() == InterviewResult.PASSED ? "ĐẠT" : "KHÔNG ĐẠT";
        String scoreText = request.getScore() != null ? " - Điểm: " + request.getScore() + "/10" : "";

        logHistory(candidate, oldStatus, CandidateStatus.INTERVIEWED,
                "INTERVIEW_EVALUATED", performedByEmployeeId,
                "Kết quả PV: " + resultText + scoreText);

        log.info("[INTERVIEW] Interview {} result: {}, score: {}",
                interviewId, request.getResult(), request.getScore());

        return interviewMapper.toResponse(saved);
    }

    // ========================================================
    // ĐỔI LỊCH PHỎNG VẤN
    // ========================================================

    @Transactional
    public InterviewResponse reschedule(
            Integer interviewId,
            InterviewRescheduleRequest request,
            Integer performedByEmployeeId
    ) {
        log.info("[INTERVIEW] Rescheduling interview: {}", interviewId);

        Interview interview = findInterviewOrThrow(interviewId);

        if (interview.getResult() != InterviewResult.PENDING) {
            throw new BusinessException("Không thể đổi lịch phỏng vấn đã có kết quả");
        }

        LocalDateTime oldDate = interview.getInterviewDate();

        // Parse ngày mới
        LocalDateTime newDate = parseDateTime(request.getInterviewDate());
        if (newDate.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Ngày giờ phỏng vấn mới phải trong tương lai");
        }

        // Xác định interviewer
        Integer interviewerId = request.getInterviewerId() != null
                ? request.getInterviewerId()
                : interview.getInterviewerId();

        int duration = request.getDurationMinutes() != null
                ? request.getDurationMinutes()
                : interview.getDurationMinutes();

        // Kiểm tra xung đột lịch
        LocalDateTime endTime = newDate.plusMinutes(duration);
        if (interviewRepository.hasConflict(interviewerId, newDate, endTime)) {
            throw new BusinessException("Người phỏng vấn đã có lịch vào thời gian này");
        }

        // Cập nhật
        interview.setInterviewDate(newDate);
        interview.setDurationMinutes(duration);

        if (request.getInterviewType() != null) {
            interview.setInterviewType(request.getInterviewType());
        }
        if (request.getLocation() != null) {
            interview.setLocation(request.getLocation());
        }
        if (request.getInterviewerId() != null) {
            Employee newInterviewer = employeeRepository.findByIdAndIsDeletedFalse(request.getInterviewerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người phỏng vấn"));
            interview.setInterviewerId(newInterviewer.getId());
            interview.setInterviewerName(newInterviewer.getFullName());
        }

        // Reset email sent
        interview.setEmailSent(false);

        Interview saved = interviewRepository.save(interview);

        // Ghi lịch sử
        Candidate candidate = interview.getCandidate();
        logHistory(candidate, null, null,
                "INTERVIEW_RESCHEDULED", performedByEmployeeId,
                "Đổi lịch PV: " + oldDate.format(DT_FORMATTER) + " → " + newDate.format(DT_FORMATTER) +
                (request.getReason() != null ? " | Lý do: " + request.getReason() : ""));

        // Gửi email thông báo đổi lịch
        Employee interviewer = employeeRepository.findByIdAndIsDeletedFalse(interview.getInterviewerId())
                .orElse(null);
        sendRescheduleEmail(candidate, saved, interviewer, oldDate);

        log.info("[INTERVIEW] Rescheduled interview {} from {} to {}",
                interviewId, oldDate, newDate);

        return interviewMapper.toResponse(saved);
    }

    // ========================================================
    // HỦY PHỎNG VẤN
    // ========================================================

    @Transactional
    public void cancelInterview(Integer interviewId, String reason, Integer performedByEmployeeId) {
        log.info("[INTERVIEW] Cancelling interview: {}", interviewId);

        Interview interview = findInterviewOrThrow(interviewId);

        if (interview.getResult() != InterviewResult.PENDING) {
            throw new BusinessException("Không thể hủy phỏng vấn đã có kết quả");
        }

        interview.setIsDeleted(true);
        interviewRepository.save(interview);

        // Trả candidate về REVIEWING
        Candidate candidate = interview.getCandidate();
        CandidateStatus oldStatus = candidate.getStatus();
        candidate.setStatus(CandidateStatus.REVIEWING);
        candidateRepository.save(candidate);

        logHistory(candidate, oldStatus, CandidateStatus.REVIEWING,
                "INTERVIEW_CANCELLED", performedByEmployeeId,
                "Hủy PV" + (reason != null ? ": " + reason : ""));

        log.info("[INTERVIEW] Cancelled interview {}", interviewId);
    }

    // ========================================================
    // QUERY
    // ========================================================
    
    @Transactional(readOnly = true)
    public InterviewResponse getById(Integer id) {
        return interviewMapper.toResponse(findInterviewOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> getByCandidateId(Integer candidateId) {
        return interviewRepository.findByCandidateIdAndIsDeletedFalse(candidateId)
                .stream()
                .map(interviewMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<InterviewResponse> getUpcoming(Integer days) {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.plusDays(days != null ? days : 7);

        return interviewRepository.findUpcomingInterviews(from, to)
                .stream()
                .map(interviewMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> getByInterviewer(Integer interviewerId, Integer days) {
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = from.plusDays(days != null ? days : 30);

        return interviewRepository.findByInterviewerAndDateRange(interviewerId, from, to)
                .stream()
                .map(interviewMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ========================================================
    // PRIVATE HELPERS
    // ========================================================

    private Interview findInterviewOrThrow(Integer id) {
        return interviewRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy lịch phỏng vấn với ID: " + id));
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DT_FORMATTER);
        } catch (Exception e) {
            throw new BusinessException(
                    "Định dạng ngày giờ không hợp lệ: '" + dateTimeStr + "'. " +
                    "Vui lòng dùng định dạng dd/MM/yyyy HH:mm"
            );
        }
    }

    private void logHistory(Candidate candidate, CandidateStatus from, CandidateStatus to,
                            String action, Integer performedBy, String note) {
        String performedByName = null;
        if (performedBy != null) {
            performedByName = employeeRepository.findByIdAndIsDeletedFalse(performedBy)
                    .map(Employee::getFullName)
                    .orElse("Unknown");
        }

        RecruitmentHistory history = RecruitmentHistory.builder()
                .candidate(candidate)
                .action(action)
                .fromStatus(from)
                .toStatus(to)
                .performedBy(performedBy)
                .performedByName(performedByName)
                .note(note)
                .createdAt(LocalDateTime.now())
                .build();

        historyRepository.save(history);
    }

    // ========================================================
    // EMAIL
    // ========================================================

    private void sendInterviewInvitationEmail(Candidate candidate, Interview interview, Employee interviewer) {
        try {
            String typeLabel = interview.getInterviewType() == com.metahrms.employee_management.enums.InterviewType.ONLINE
                    ? "Trực tuyến (Online)" : "Trực tiếp (Offline)";

            String locationInfo = interview.getLocation() != null
                    ? interview.getLocation()
                    : "Sẽ được thông báo sau";

            String subject = "Thư mời phỏng vấn - Vị trí " + candidate.getDesiredPosition() + " | MetaHRM";

            String body = String.format("""
                Chào %s,
                
                Cảm ơn bạn đã ứng tuyển vị trí "%s" tại công ty chúng tôi.
                
                Sau khi xem xét hồ sơ, chúng tôi trân trọng mời bạn tham gia buổi phỏng vấn 
                với thông tin chi tiết như sau:
                
                ═══════════════════════════════════
                📅 Ngày giờ:      %s
                ⏱ Thời lượng:     %d phút
                📋 Hình thức:     %s
                📍 Địa điểm:      %s
                👤 Người PV:      %s
                ═══════════════════════════════════
                
                Lưu ý:
                • Vui lòng đến trước giờ hẹn 10-15 phút
                • Mang theo CMND/CCCD và bản in CV
                • Nếu không thể tham dự, vui lòng phản hồi email này sớm nhất
                
                Nếu có bất kỳ thắc mắc nào, xin đừng ngần ngại liên hệ với chúng tôi.
                
                Chúc bạn buổi phỏng vấn thành công!
                
                Trân trọng,
                Phòng Nhân sự - MetaHRM
                """,
                    candidate.getFullName(),
                    candidate.getDesiredPosition(),
                    interview.getInterviewDate().format(DT_FORMATTER),
                    interview.getDurationMinutes(),
                    typeLabel,
                    locationInfo,
                    interviewer.getFullName()
            );

            emailService.sendEmail(candidate.getEmail(), subject, body);

            // Cập nhật trạng thái gửi email
            interview.setEmailSent(true);
            interview.setEmailSentAt(LocalDateTime.now());
            interviewRepository.save(interview);

            log.info("[INTERVIEW] Invitation email sent to: {}", candidate.getEmail());

        } catch (Exception e) {
            log.error("[INTERVIEW] Failed to send invitation email to: {}", candidate.getEmail(), e);
        }
    }

    private void sendRescheduleEmail(Candidate candidate, Interview interview,
                                     Employee interviewer, LocalDateTime oldDate) {
        try {
            String typeLabel = interview.getInterviewType() == com.metahrms.employee_management.enums.InterviewType.ONLINE
                    ? "Trực tuyến (Online)" : "Trực tiếp (Offline)";

            String subject = "Thông báo thay đổi lịch phỏng vấn | MetaHRM";

            String body = String.format("""
                Chào %s,
                
                Chúng tôi xin thông báo lịch phỏng vấn của bạn đã được thay đổi:
                
                ❌ Lịch cũ:   %s
                ✅ Lịch mới:  %s
                
                📋 Hình thức: %s
                📍 Địa điểm:  %s
                👤 Người PV:  %s
                ⏱ Thời lượng: %d phút
                
                Chúng tôi xin lỗi vì sự bất tiện này.
                Nếu thời gian mới không phù hợp, vui lòng phản hồi email này.
                
                Trân trọng,
                Phòng Nhân sự - MetaHRM
                """,
                    candidate.getFullName(),
                    oldDate.format(DT_FORMATTER),
                    interview.getInterviewDate().format(DT_FORMATTER),
                    typeLabel,
                    interview.getLocation() != null ? interview.getLocation() : "Sẽ thông báo sau",
                    interviewer != null ? interviewer.getFullName() : "Sẽ thông báo sau",
                    interview.getDurationMinutes()
            );

            emailService.sendEmail(candidate.getEmail(), subject, body);

            interview.setEmailSent(true);
            interview.setEmailSentAt(LocalDateTime.now());
            interviewRepository.save(interview);

            log.info("[INTERVIEW] Reschedule email sent to: {}", candidate.getEmail());

        } catch (Exception e) {
            log.error("[INTERVIEW] Failed to send reschedule email", e);
        }
    }
}