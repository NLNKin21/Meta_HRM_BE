package com.metahrms.employee_management.service.CV;


import com.metahrms.employee_management.dto.request.CV.CandidateApplyRequest;
import com.metahrms.employee_management.dto.request.CV.CandidateFilterRequest;
import com.metahrms.employee_management.dto.response.CV.CandidateResponse;
import com.metahrms.employee_management.dto.response.CV.RecruitmentStatsResponse;
import com.metahrms.employee_management.entity.CV.Candidate;
import com.metahrms.employee_management.entity.CV.RecruitmentHistory;
import com.metahrms.employee_management.enums.CandidateStatus;
import com.metahrms.employee_management.exception.BusinessException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.mapper.CV.CandidateMapper;
import com.metahrms.employee_management.repository.CV.CandidateRepository;
import com.metahrms.employee_management.repository.CV.RecruitmentHistoryRepository;
import com.metahrms.employee_management.service.CloudinaryService;
import com.metahrms.employee_management.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final RecruitmentHistoryRepository historyRepository;
    private final CandidateMapper candidateMapper;
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;

    // ========================================================
    // PUBLIC: Ứng viên nộp đơn
    // ========================================================

    @Transactional
    public CandidateResponse apply(
            CandidateApplyRequest request,
            MultipartFile cvFile
    ) throws IOException {

        log.info("[RECRUIT] New application: {} - {}", request.getFullName(), request.getEmail());

        // 1. Kiểm tra email đã ứng tuyển chưa (đang active)
        if (candidateRepository.existsByEmailAndActiveApplication(request.getEmail())) {
            throw new BusinessException(
                    "Email " + request.getEmail() + " đã có đơn ứng tuyển đang xử lý. " +
                    "Vui lòng chờ kết quả hoặc liên hệ HR."
            );
        }

        // 2. Upload CV lên Cloudinary
        String cvFileUrl = null;
        String cvFileKey = null;
        String cvFileName = null;

        if (cvFile != null && !cvFile.isEmpty()) {
            // Validate file
            validateCvFile(cvFile);

            cvFileUrl = cloudinaryService.uploadFile(cvFile);
            cvFileKey = cloudinaryService.extractPublicId(cvFileUrl);
            cvFileName = cvFile.getOriginalFilename();
            log.info("[RECRUIT] CV uploaded: {}", cvFileUrl);
        }

        // 3. Parse ngày sinh
        LocalDate dob = null;
        if (request.getDob() != null && !request.getDob().isBlank()) {
            try {
                dob = LocalDate.parse(request.getDob(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception e) {
                log.warn("[RECRUIT] Invalid dob format: {}", request.getDob());
            }
        }

        // 4. Tạo Candidate
        Candidate candidate = Candidate.builder()
                .fullName(request.getFullName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phoneNumber(request.getPhoneNumber())
                .dob(dob)
                .gender(request.getGender())
                .address(request.getAddress())
                .desiredPosition(request.getDesiredPosition().trim())
                .departmentId(request.getDepartmentId())
                .expectedSalary(request.getExpectedSalary())
                .cvFileUrl(cvFileUrl)
                .cvFileKey(cvFileKey)
                .cvFileName(cvFileName)
                .coverLetter(request.getCoverLetter())
                .status(CandidateStatus.NEW)
                .appliedAt(LocalDateTime.now())
                .build();

        candidate.setIsDeleted(false);
        Candidate saved = candidateRepository.save(candidate);

        log.info("[RECRUIT] Candidate created: id={}, email={}", saved.getId(), saved.getEmail());

        // 5. Ghi lịch sử
        logHistory(saved, null, CandidateStatus.NEW, "APPLIED", null, "Ứng viên nộp đơn");

        // 6. Gửi email xác nhận cho ứng viên
        sendApplicationConfirmationEmail(saved);

        return candidateMapper.toResponse(saved);
    }

    // ========================================================
    // HR: Xem danh sách ứng viên
    // ========================================================
    @Transactional(readOnly = true)
    public Page<CandidateResponse> getCandidates(CandidateFilterRequest filter) {
        log.info("[RECRUIT] Fetching candidates - status: {}, keyword: {}, dept: {}",
                filter.getStatus(), filter.getKeyword(), filter.getDepartmentId());

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());

        Page<Candidate> page = candidateRepository.findWithFilters(
                filter.getStatus(),
                filter.getDepartmentId(),
                filter.getKeyword(),
                pageable
        );

        return page.map(candidateMapper::toResponse);
    }

    // ========================================================
    // HR: Xem chi tiết ứng viên
    // ========================================================
    @Transactional(readOnly = true)
    public CandidateResponse getCandidateById(Integer id) {
        Candidate candidate = findCandidateOrThrow(id);
        return candidateMapper.toResponse(candidate);
    }

    // ========================================================
    // HR: Đổi trạng thái ứng viên
    // ========================================================

    @Transactional
    public CandidateResponse updateStatus(
            Integer candidateId,
            CandidateStatus newStatus,
            String note,
            Integer performedByEmployeeId
    ) {
        Candidate candidate = findCandidateOrThrow(candidateId);
        CandidateStatus oldStatus = candidate.getStatus();

        // Validate chuyển trạng thái hợp lệ
        validateStatusTransition(oldStatus, newStatus);

        candidate.setStatus(newStatus);

        // Cập nhật thông tin theo trạng thái
        switch (newStatus) {
            case REVIEWING -> {
                candidate.setReviewedBy(performedByEmployeeId);
                candidate.setReviewedAt(LocalDateTime.now());
            }
            case REJECTED -> {
                candidate.setRejectReason(note);
            }
            case APPROVED -> {
                candidate.setApprovedBy(performedByEmployeeId);
                candidate.setApprovedAt(LocalDateTime.now());
            }
            default -> {}
        }

        if (note != null && !note.isBlank()) {
            candidate.setNote(note);
        }

        Candidate saved = candidateRepository.save(candidate);

        // Ghi lịch sử
        String performedByName = getEmployeeName(performedByEmployeeId);
        logHistory(saved, oldStatus, newStatus, "STATUS_CHANGE",
                performedByEmployeeId, performedByName, note);

        // Gửi email thông báo cho ứng viên
        sendStatusUpdateEmail(saved, oldStatus, newStatus);

        log.info("[RECRUIT] Candidate {} status: {} → {}", candidateId, oldStatus, newStatus);

        return candidateMapper.toResponse(saved);
    }

    // ========================================================
    // HR: Thêm ghi chú
    // ========================================================

    @Transactional
    public CandidateResponse addNote(Integer candidateId, String note, Integer performedByEmployeeId) {
        Candidate candidate = findCandidateOrThrow(candidateId);

        String existingNote = candidate.getNote();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String performedByName = getEmployeeName(performedByEmployeeId);

        String newNote = (existingNote != null ? existingNote + "\n" : "")
                + "[" + timestamp + " - " + performedByName + "] " + note;

        candidate.setNote(newNote);
        Candidate saved = candidateRepository.save(candidate);

        logHistory(saved, null, null, "NOTE_ADDED",
                performedByEmployeeId, performedByName, note);

        return candidateMapper.toResponse(saved);
    }

    // ========================================================
    // HR: Thống kê
    // ========================================================
    @Transactional(readOnly = true)
    public RecruitmentStatsResponse getStats() {
        Long totalNew = candidateRepository.countByStatusAndIsDeletedFalse(CandidateStatus.NEW);
        Long totalReviewing = candidateRepository.countByStatusAndIsDeletedFalse(CandidateStatus.REVIEWING);
        Long totalInterviewScheduled = candidateRepository.countByStatusAndIsDeletedFalse(CandidateStatus.INTERVIEW_SCHEDULED);
        Long totalInterviewed = candidateRepository.countByStatusAndIsDeletedFalse(CandidateStatus.INTERVIEWED);
        Long totalApproved = candidateRepository.countByStatusAndIsDeletedFalse(CandidateStatus.APPROVED);
        Long totalOnboarded = candidateRepository.countByStatusAndIsDeletedFalse(CandidateStatus.ONBOARDED);
        Long totalRejected = candidateRepository.countByStatusAndIsDeletedFalse(CandidateStatus.REJECTED);

        return RecruitmentStatsResponse.builder()
                .totalNew(totalNew)
                .totalReviewing(totalReviewing)
                .totalInterviewScheduled(totalInterviewScheduled)
                .totalInterviewed(totalInterviewed)
                .totalApproved(totalApproved)
                .totalOnboarded(totalOnboarded)
                .totalRejected(totalRejected)
                .totalAll(totalNew + totalReviewing + totalInterviewScheduled
                        + totalInterviewed + totalApproved + totalOnboarded + totalRejected)
                .build();
    }

    // ========================================================
    // PRIVATE HELPERS
    // ========================================================

    private Candidate findCandidateOrThrow(Integer id) {
        return candidateRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ứng viên với ID: " + id));
    }

    private void validateCvFile(MultipartFile file) {
        // Chỉ cho phép PDF
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new BusinessException("Chỉ chấp nhận file CV định dạng PDF");
        }

        // Giới hạn 10MB
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new BusinessException("File CV không được vượt quá 10MB");
        }
    }

    private void validateStatusTransition(CandidateStatus from, CandidateStatus to) {
        boolean valid = switch (from) {
            case NEW -> to == CandidateStatus.REVIEWING || to == CandidateStatus.REJECTED;
            case REVIEWING -> to == CandidateStatus.INTERVIEW_SCHEDULED || to == CandidateStatus.REJECTED;
            case INTERVIEW_SCHEDULED -> to == CandidateStatus.INTERVIEWED || to == CandidateStatus.REJECTED;
            case INTERVIEWED -> to == CandidateStatus.APPROVED || to == CandidateStatus.REJECTED;
            case APPROVED -> to == CandidateStatus.ONBOARDED;
            case ONBOARDED, REJECTED -> false;
        };

        if (!valid) {
            throw new BusinessException(
                    "Không thể chuyển trạng thái từ '" + from + "' sang '" + to + "'"
            );
        }
    }

    private void logHistory(Candidate candidate, CandidateStatus from, CandidateStatus to,
                            String action, Integer performedBy, String note) {
        logHistory(candidate, from, to, action, performedBy, null, note);
    }

    private void logHistory(Candidate candidate, CandidateStatus from, CandidateStatus to,
                            String action, Integer performedBy, String performedByName, String note) {
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

    private String getEmployeeName(Integer employeeId) {
        if (employeeId == null) return "System";
        return candidateMapper.getEmployeeName(employeeId);
    }

    // ========================================================
    // EMAIL
    // ========================================================

    private void sendApplicationConfirmationEmail(Candidate candidate) {
        try {
            String subject = "Xác nhận đã nhận hồ sơ ứng tuyển - MetaHRM";
            String body = String.format("""
                Chào %s,
                
                Cảm ơn bạn đã ứng tuyển vị trí "%s" tại công ty chúng tôi.
                
                Chúng tôi đã nhận được hồ sơ của bạn và sẽ xem xét trong thời gian sớm nhất.
                Nếu hồ sơ phù hợp, chúng tôi sẽ liên hệ để sắp xếp phỏng vấn.
                
                Thông tin đơn ứng tuyển:
                - Họ tên: %s
                - Email: %s
                - Vị trí: %s
                - Ngày nộp: %s
                
                Trân trọng,
                Phòng Nhân sự - MetaHRM
                """,
                    candidate.getFullName(),
                    candidate.getDesiredPosition(),
                    candidate.getFullName(),
                    candidate.getEmail(),
                    candidate.getDesiredPosition(),
                    candidate.getAppliedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );

            emailService.sendEmail(candidate.getEmail(), subject, body);
            log.info("[RECRUIT] Confirmation email sent to: {}", candidate.getEmail());
        } catch (Exception e) {
            log.error("[RECRUIT] Failed to send confirmation email to: {}", candidate.getEmail(), e);
        }
    }

    private void sendStatusUpdateEmail(Candidate candidate, CandidateStatus oldStatus, CandidateStatus newStatus) {
        try {
            if (newStatus == CandidateStatus.REJECTED) {
                String subject = "Thông báo kết quả ứng tuyển - MetaHRM";
                String body = String.format("""
                    Chào %s,
                    
                    Cảm ơn bạn đã quan tâm và ứng tuyển vị trí "%s" tại công ty chúng tôi.
                    
                    Sau khi xem xét kỹ lưỡng, chúng tôi rất tiếc phải thông báo rằng 
                    hồ sơ của bạn chưa phù hợp với yêu cầu tuyển dụng lần này.
                    
                    Chúng tôi sẽ lưu giữ hồ sơ của bạn và liên hệ nếu có vị trí phù hợp 
                    trong tương lai.
                    
                    Chúc bạn thành công!
                    
                    Trân trọng,
                    Phòng Nhân sự - MetaHRM
                    """,
                        candidate.getFullName(),
                        candidate.getDesiredPosition()
                );

                emailService.sendEmail(candidate.getEmail(), subject, body);
                log.info("[RECRUIT] Rejection email sent to: {}", candidate.getEmail());
            }
        } catch (Exception e) {
            log.error("[RECRUIT] Failed to send status update email", e);
        }
    }
}