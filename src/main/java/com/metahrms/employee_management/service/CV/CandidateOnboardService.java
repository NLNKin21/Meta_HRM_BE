package com.metahrms.employee_management.service.CV;

import com.metahrms.employee_management.dto.request.CV.CandidateApproveRequest;
import com.metahrms.employee_management.dto.response.CV.CandidateOnboardResponse;
import com.metahrms.employee_management.dto.response.CV.CandidateResponse;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.CV.Candidate;
import com.metahrms.employee_management.entity.CV.RecruitmentHistory;
import com.metahrms.employee_management.entity.User;
import com.metahrms.employee_management.enums.CandidateStatus;
import com.metahrms.employee_management.enums.InterviewResult;
import com.metahrms.employee_management.enums.UserRole;
import com.metahrms.employee_management.enums.UserStatus;
import com.metahrms.employee_management.exception.BusinessException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.mapper.CV.CandidateMapper;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.CV.CandidateRepository;
import com.metahrms.employee_management.repository.CV.InterviewRepository;
import com.metahrms.employee_management.repository.CV.RecruitmentHistoryRepository;
import com.metahrms.employee_management.repository.UserRepository;
import com.metahrms.employee_management.service.EmailService;
import com.metahrms.employee_management.util.PasswordGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateOnboardService {

    private final CandidateRepository candidateRepository;
    private final InterviewRepository interviewRepository;
    private final RecruitmentHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final CandidateMapper candidateMapper;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ========================================================
    // BƯỚC 1: DUYỆT ỨNG VIÊN (INTERVIEWED → APPROVED)
    // ========================================================

    @Transactional
    public CandidateResponse approve(
            Integer candidateId,
            CandidateApproveRequest request,
            Integer performedByEmployeeId
    ) {
        log.info("[ONBOARD] Approving candidate: {}", candidateId);

        Candidate candidate = findCandidateOrThrow(candidateId);

        // Validate trạng thái
        if (candidate.getStatus() != CandidateStatus.INTERVIEWED) {
            throw new BusinessException(
                    "Chỉ có thể duyệt ứng viên đã phỏng vấn xong. " +
                    "Trạng thái hiện tại: " + candidate.getStatus()
            );
        }

        // Kiểm tra kết quả phỏng vấn phải PASSED
        boolean hasPassed = interviewRepository.findByCandidateIdAndIsDeletedFalse(candidateId)
                .stream()
                .anyMatch(i -> i.getResult() == InterviewResult.PASSED);

        if (!hasPassed) {
            throw new BusinessException(
                    "Ứng viên chưa có kết quả phỏng vấn ĐẠT. " +
                    "Vui lòng đánh giá phỏng vấn trước khi duyệt."
            );
        }

        // Cập nhật trạng thái
        CandidateStatus oldStatus = candidate.getStatus();
        candidate.setStatus(CandidateStatus.APPROVED);
        candidate.setApprovedBy(performedByEmployeeId);
        candidate.setApprovedAt(LocalDateTime.now());

        if (request.getNote() != null && !request.getNote().isBlank()) {
            candidate.setNote(appendNote(candidate.getNote(), request.getNote(), performedByEmployeeId));
        }

        candidateRepository.save(candidate);

        // Ghi lịch sử
        logHistory(candidate, oldStatus, CandidateStatus.APPROVED,
                "APPROVED", performedByEmployeeId, request.getNote());

        log.info("[ONBOARD] Candidate {} approved", candidateId);

        return candidateMapper.toResponse(candidate);
    }

    // ========================================================
    // BƯỚC 2: TẠO TÀI KHOẢN (APPROVED → ONBOARDED)
    // ========================================================

    @Transactional
    public CandidateOnboardResponse onboard(
            Integer candidateId,
            CandidateApproveRequest request,
            Integer performedByEmployeeId
    ) {
        log.info("[ONBOARD] Onboarding candidate: {}", candidateId);

        Candidate candidate = findCandidateOrThrow(candidateId);

        // Validate trạng thái
        if (candidate.getStatus() != CandidateStatus.APPROVED) {
            throw new BusinessException(
                    "Chỉ có thể tạo tài khoản cho ứng viên đã được duyệt. " +
                    "Trạng thái hiện tại: " + candidate.getStatus()
            );
        }

        // Kiểm tra email đã có user chưa
        if (userRepository.existsByEmail(candidate.getEmail())) {
            throw new BusinessException(
                    "Email " + candidate.getEmail() + " đã tồn tại trong hệ thống. " +
                    "Vui lòng kiểm tra lại hoặc liên hệ quản trị."
            );
        }

        // 1. Generate username từ email
        String username = generateUsername(candidate.getEmail(), candidate.getFullName());

        // 2. Generate password
        String rawPassword = PasswordGenerator.generate();

        // 3. Xác định role
        UserRole role = parseUserRole(request.getUserRole());

        // 4. Tạo User
        User newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .email(candidate.getEmail())
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("[ONBOARD] User created: id={}, username={}, email={}",
                savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());

        // 5. Cập nhật candidate
        CandidateStatus oldStatus = candidate.getStatus();
        candidate.setStatus(CandidateStatus.ONBOARDED);
        candidate.setCreatedUserId(savedUser.getId());

        if (request.getNote() != null && !request.getNote().isBlank()) {
            candidate.setNote(appendNote(candidate.getNote(), request.getNote(), performedByEmployeeId));
        }

        candidateRepository.save(candidate);

        // 6. Ghi lịch sử
        logHistory(candidate, oldStatus, CandidateStatus.ONBOARDED,
                "ONBOARDED", performedByEmployeeId,
                "Tạo tài khoản: " + username + " (userId=" + savedUser.getId() + ")");

        // 7. Gửi email chào mừng + tài khoản
        boolean emailSent = sendOnboardEmail(candidate, savedUser, rawPassword);

        log.info("[ONBOARD] Candidate {} onboarded successfully. userId={}",
                candidateId, savedUser.getId());

        return CandidateOnboardResponse.builder()
                .candidate(candidateMapper.toResponse(candidate))
                .createdUserId(savedUser.getId())
                .createdUsername(savedUser.getUsername())
                .createdEmail(savedUser.getEmail())
                .passwordSentViaEmail(emailSent)
                .message("Tạo tài khoản thành công! " +
                        (emailSent ? "Email đã được gửi tới " + candidate.getEmail()
                                : "Không gửi được email, vui lòng cung cấp tài khoản thủ công."))
                .onboardedAt(LocalDateTime.now())
                .build();
    }

    // ========================================================
    // DUYỆT + TẠO TÀI KHOẢN 1 BƯỚC (cho trường hợp nhanh)
    // ========================================================

    @Transactional
    public CandidateOnboardResponse approveAndOnboard(
            Integer candidateId,
            CandidateApproveRequest request,
            Integer performedByEmployeeId
    ) {
        log.info("[ONBOARD] Approve + Onboard candidate: {}", candidateId);

        Candidate candidate = findCandidateOrThrow(candidateId);

        // Nếu đang INTERVIEWED → approve trước
        if (candidate.getStatus() == CandidateStatus.INTERVIEWED) {
            approve(candidateId, request, performedByEmployeeId);
        }

        // Reload sau khi approve
        candidate = findCandidateOrThrow(candidateId);

        // Nếu đang APPROVED → onboard
        if (candidate.getStatus() == CandidateStatus.APPROVED) {
            return onboard(candidateId, request, performedByEmployeeId);
        }

        throw new BusinessException(
                "Không thể duyệt và tạo tài khoản. " +
                "Trạng thái hiện tại: " + candidate.getStatus()
        );
    }

    // ========================================================
    // TỪ CHỐI ỨNG VIÊN
    // ========================================================

    @Transactional
    public CandidateResponse reject(
            Integer candidateId,
            String reason,
            Integer performedByEmployeeId
    ) {
        log.info("[ONBOARD] Rejecting candidate: {}", candidateId);

        Candidate candidate = findCandidateOrThrow(candidateId);

        // Không reject ONBOARDED
        if (candidate.getStatus() == CandidateStatus.ONBOARDED) {
            throw new BusinessException("Không thể từ chối ứng viên đã tạo tài khoản");
        }

        // Không reject đã reject
        if (candidate.getStatus() == CandidateStatus.REJECTED) {
            throw new BusinessException("Ứng viên này đã bị từ chối trước đó");
        }

        CandidateStatus oldStatus = candidate.getStatus();
        candidate.setStatus(CandidateStatus.REJECTED);
        candidate.setRejectReason(reason);
        candidateRepository.save(candidate);

        // Ghi lịch sử
        logHistory(candidate, oldStatus, CandidateStatus.REJECTED,
                "REJECTED", performedByEmployeeId,
                "Từ chối: " + (reason != null ? reason : "Không có lý do"));

        // Gửi email thông báo từ chối
        sendRejectionEmail(candidate);

        log.info("[ONBOARD] Candidate {} rejected", candidateId);

        return candidateMapper.toResponse(candidate);
    }

    // ========================================================
    // PRIVATE HELPERS
    // ========================================================

    private Candidate findCandidateOrThrow(Integer id) {
        return candidateRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy ứng viên với ID: " + id));
    }

    /**
     * Generate username từ email hoặc tên
     * nguyenvanan@gmail.com → nguyenvanan
     * Nếu trùng thì thêm số: nguyenvanan1, nguyenvanan2...
     */
    private String generateUsername(String email, String fullName) {
        // Lấy phần trước @ của email
        String base = email.split("@")[0]
                .toLowerCase()
                .replaceAll("[^a-z0-9_]", "");

        if (base.isEmpty()) {
            // Fallback: dùng tên không dấu
            base = removeVietnameseAccents(fullName)
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]", "")
                    .replaceAll("\\s+", "_");
        }

        // Kiểm tra trùng
        String username = base;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = base + counter;
            counter++;
        }

        return username;
    }

    private UserRole parseUserRole(String roleStr) {
        if (roleStr == null || roleStr.isBlank()) {
            return UserRole.EMPLOYEE;
        }
        try {
            return UserRole.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("[ONBOARD] Invalid role: {}, defaulting to EMPLOYEE", roleStr);
            return UserRole.EMPLOYEE;
        }
    }

    private String appendNote(String existing, String newNote, Integer employeeId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String employeeName = getEmployeeName(employeeId);

        return (existing != null ? existing + "\n" : "")
                + "[" + timestamp + " - " + employeeName + "] " + newNote;
    }

    private String getEmployeeName(Integer employeeId) {
        if (employeeId == null) return "System";
        return employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .map(Employee::getFullName)
                .orElse("Unknown");
    }

    private void logHistory(Candidate candidate, CandidateStatus from, CandidateStatus to,
                            String action, Integer performedBy, String note) {
        String performedByName = getEmployeeName(performedBy);

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

    /**
     * Bỏ dấu tiếng Việt
     */
    private String removeVietnameseAccents(String str) {
        if (str == null) return "";
        String normalized = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replace("đ", "d")
                .replace("Đ", "D");
    }

    // ========================================================
    // EMAIL
    // ========================================================

    private boolean sendOnboardEmail(Candidate candidate, User user, String rawPassword) {
        try {
            String subject = "🎉 Chúc mừng bạn đã trúng tuyển! Thông tin tài khoản | MetaHRM";

            String body = String.format("""
                Chào %s,
                
                Chúng tôi rất vui thông báo bạn đã TRÚNG TUYỂN vị trí "%s" 
                tại công ty chúng tôi! 🎉
                
                ═══════════════════════════════════════
                THÔNG TIN TÀI KHOẢN HỆ THỐNG
                ═══════════════════════════════════════
                🌐 Hệ thống:    MetaHRM
                👤 Username:    %s
                📧 Email:       %s
                🔑 Mật khẩu:    %s
                ═══════════════════════════════════════
                
                ⚠️ LƯU Ý QUAN TRỌNG:
                • Vui lòng đổi mật khẩu ngay sau lần đăng nhập đầu tiên
                • Không chia sẻ thông tin tài khoản cho người khác
                • Nếu gặp vấn đề đăng nhập, liên hệ Phòng Nhân sự
                
                📋 CÁC BƯỚC TIẾP THEO:
                1. Đăng nhập hệ thống bằng tài khoản trên
                2. Cập nhật thông tin cá nhân
                3. Phòng Nhân sự sẽ liên hệ để hoàn tất thủ tục nhận việc
                
                Chúc bạn có những trải nghiệm tuyệt vời!
                
                Trân trọng,
                Phòng Nhân sự - MetaHRM
                """,
                    candidate.getFullName(),
                    candidate.getDesiredPosition(),
                    user.getUsername(),
                    user.getEmail(),
                    rawPassword
            );

            emailService.sendEmail(candidate.getEmail(), subject, body);
            log.info("[ONBOARD] Onboard email sent to: {}", candidate.getEmail());
            return true;

        } catch (Exception e) {
            log.error("[ONBOARD] Failed to send onboard email to: {}", candidate.getEmail(), e);
            return false;
        }
    }

    private void sendRejectionEmail(Candidate candidate) {
        try {
            String subject = "Thông báo kết quả ứng tuyển - MetaHRM";

            String body = String.format("""
                Chào %s,
                
                Cảm ơn bạn đã quan tâm và ứng tuyển vị trí "%s" 
                tại công ty chúng tôi.
                
                Sau khi xem xét kỹ lưỡng hồ sơ và kết quả phỏng vấn, 
                chúng tôi rất tiếc phải thông báo rằng hồ sơ của bạn 
                chưa phù hợp với yêu cầu tuyển dụng lần này.
                
                %s
                
                Chúng tôi sẽ lưu giữ hồ sơ của bạn và liên hệ nếu có 
                vị trí phù hợp trong tương lai.
                
                Chúc bạn nhiều thành công trên con đường sự nghiệp!
                
                Trân trọng,
                Phòng Nhân sự - MetaHRM
                """,
                    candidate.getFullName(),
                    candidate.getDesiredPosition(),
                    candidate.getRejectReason() != null
                            ? "Lý do: " + candidate.getRejectReason()
                            : ""
            );

            emailService.sendEmail(candidate.getEmail(), subject, body);
            log.info("[ONBOARD] Rejection email sent to: {}", candidate.getEmail());

        } catch (Exception e) {
            log.error("[ONBOARD] Failed to send rejection email to: {}", candidate.getEmail(), e);
        }
    }
}