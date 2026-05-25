package com.metahrms.employee_management.controller.CV;

import com.metahrms.employee_management.dto.request.CV.CandidateApproveRequest;
import com.metahrms.employee_management.dto.request.CV.CandidateFilterRequest;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.CV.CandidateOnboardResponse;
import com.metahrms.employee_management.dto.response.CV.CandidatePrefillResponse;
import com.metahrms.employee_management.dto.response.CV.CandidateResponse;
import com.metahrms.employee_management.dto.response.CV.RecruitmentHistoryResponse;
import com.metahrms.employee_management.dto.response.CV.RecruitmentStatsResponse;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.CV.RecruitmentHistory;
import com.metahrms.employee_management.enums.CandidateStatus;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.CV.RecruitmentHistoryRepository;
import com.metahrms.employee_management.service.CV.CandidateService;
import com.metahrms.employee_management.util.SecurityUtils;

import com.metahrms.employee_management.dto.request.CV.CandidateApproveRequest;
import com.metahrms.employee_management.dto.response.CV.CandidateOnboardResponse;
import com.metahrms.employee_management.service.CV.CandidateOnboardService;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/recruitment")
@RequiredArgsConstructor
@Tag(name = "HR - Recruitment", description = "API quản lý tuyển dụng cho HR")
public class RecruitmentController {

    private final CandidateService candidateService;
    private final EmployeeRepository employeeRepository;
    private final RecruitmentHistoryRepository historyRepository;
    private final CandidateOnboardService onboardService;
    // ========== DANH SÁCH ==========

    @GetMapping("/candidates")
    @Operation(summary = "Danh sách ứng viên (có filter + phân trang)")
    public ResponseEntity<ApiResponse<Page<CandidateResponse>>> getCandidates(
            @RequestParam(name="status", required = false) CandidateStatus status,
            @RequestParam(name="departmentId", required = false) Integer departmentId,
            @RequestParam(name="keyword", required = false) String keyword,
            @RequestParam(name="page", defaultValue = "0") Integer page,
            @RequestParam(name="size", defaultValue = "20") Integer size
    ) {
        CandidateFilterRequest filter = CandidateFilterRequest.builder()
                .status(status)
                .departmentId(departmentId)
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();

        Page<CandidateResponse> candidates = candidateService.getCandidates(filter);

        return ResponseEntity.ok(
                ApiResponse.success(candidates, "Lấy danh sách ứng viên thành công")
        );
    }

    // ========== CHI TIẾT ==========

    @GetMapping("/candidates/{id}")
    @Operation(summary = "Chi tiết ứng viên")
    public ResponseEntity<ApiResponse<CandidateResponse>> getCandidateById(
            @PathVariable("id") Integer id
    ) {
        CandidateResponse candidate = candidateService.getCandidateById(id);
        return ResponseEntity.ok(
                ApiResponse.success(candidate, "Lấy thông tin ứng viên thành công")
        );
    }

    // ========== ĐỔI TRẠNG THÁI ==========

    @PutMapping("/candidates/{id}/status")
    @Operation(summary = "Đổi trạng thái ứng viên")
    public ResponseEntity<ApiResponse<CandidateResponse>> updateStatus(
            @PathVariable("id") Integer id,
            @RequestParam("status") CandidateStatus status,
            @RequestParam(name = "note", required = false) String note
    ) {
        Integer employeeId = getCurrentEmployeeId();

        CandidateResponse updated = candidateService.updateStatus(id, status, note, employeeId);

        return ResponseEntity.ok(
                ApiResponse.success(updated, "Cập nhật trạng thái ứng viên thành công")
        );
    }

    // ========== THÊM GHI CHÚ ==========

    @PostMapping("/candidates/{id}/note")
    @Operation(summary = "Thêm ghi chú cho ứng viên")
    public ResponseEntity<ApiResponse<CandidateResponse>> addNote(
            @PathVariable("id") Integer id,
            @RequestBody String note
    ) {
        Integer employeeId = getCurrentEmployeeId();

        CandidateResponse updated = candidateService.addNote(id, note, employeeId);

        return ResponseEntity.ok(
                ApiResponse.success(updated, "Thêm ghi chú thành công")
        );
    }

    // ========== THỐNG KÊ ==========

    @GetMapping("/stats")
    @Operation(summary = "Thống kê tuyển dụng")
    public ResponseEntity<ApiResponse<RecruitmentStatsResponse>> getStats() {
        RecruitmentStatsResponse stats = candidateService.getStats();
        return ResponseEntity.ok(
                ApiResponse.success(stats, "Lấy thống kê tuyển dụng thành công")
        );
    }

    // ========== HELPER ==========

    private Integer getCurrentEmployeeId() {
        Integer userId = SecurityUtils.getCurrentUserId();
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found for userId: " + userId));
        return employee.getId();
    }

    @GetMapping("/candidates/{id}/history")
    @Operation(summary = "Lịch sử xử lý ứng viên (timeline)")
    public ResponseEntity<ApiResponse<List<RecruitmentHistoryResponse>>> getCandidateHistory(
            @PathVariable("id") Integer id
    ) {
        List<RecruitmentHistory> histories = historyRepository
                .findByCandidateIdOrderByCreatedAtDesc(id);

        List<RecruitmentHistoryResponse> response = histories.stream()
                .map(this::toHistoryResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(response, "Lấy lịch sử ứng viên thành công")
        );
    }

    private RecruitmentHistoryResponse toHistoryResponse(RecruitmentHistory h) {
        return RecruitmentHistoryResponse.builder()
                .id(h.getId())
                .action(h.getAction())
                .fromStatus(h.getFromStatus())
                .toStatus(h.getToStatus())
                .performedBy(h.getPerformedBy())
                .performedByName(h.getPerformedByName())
                .note(h.getNote())
                .createdAt(h.getCreatedAt())
                .build();
    }

        // ========== DUYỆT ỨNG VIÊN ==========

        @PutMapping("/candidates/{id}/approve")
        @Operation(summary = "Duyệt ứng viên (INTERVIEWED → APPROVED)")
        public ResponseEntity<ApiResponse<CandidateResponse>> approve(
                @PathVariable("id") Integer id,
                @RequestBody(required = false) CandidateApproveRequest request
        ) {
        Integer employeeId = getCurrentEmployeeId();

        if (request == null) {
                request = CandidateApproveRequest.builder().build();
        }

        CandidateResponse response = onboardService.approve(id, request, employeeId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Duyệt ứng viên thành công")
        );
        }

        // ========== TẠO TÀI KHOẢN (APPROVED → ONBOARDED) ==========

        @PostMapping("/candidates/{id}/onboard")
        @Operation(summary = "Tạo tài khoản cho ứng viên đã duyệt (APPROVED → ONBOARDED)")
        public ResponseEntity<ApiResponse<CandidateOnboardResponse>> onboard(
                @PathVariable("id") Integer id,
                @RequestBody(required = false) CandidateApproveRequest request
        ) {
        Integer employeeId = getCurrentEmployeeId();

        if (request == null) {
                request = CandidateApproveRequest.builder().build();
        }

        CandidateOnboardResponse response = onboardService.onboard(id, request, employeeId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(response, response.getMessage())
        );
        }

        // ========== DUYỆT + TẠO TÀI KHOẢN 1 BƯỚC ==========

        @PostMapping("/candidates/{id}/approve-and-onboard")
        @Operation(summary = "Duyệt + Tạo tài khoản 1 bước (INTERVIEWED → ONBOARDED)")
        public ResponseEntity<ApiResponse<CandidateOnboardResponse>> approveAndOnboard(
                @PathVariable("id") Integer id,
                @RequestBody(required = false) CandidateApproveRequest request
        ) {
        Integer employeeId = getCurrentEmployeeId();

        if (request == null) {
                request = CandidateApproveRequest.builder().build();
        }

        CandidateOnboardResponse response = onboardService.approveAndOnboard(id, request, employeeId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(response, response.getMessage())
        );
        }

        // ========== TỪ CHỐI ỨNG VIÊN ==========

        @PutMapping("/candidates/{id}/reject")
        @Operation(summary = "Từ chối ứng viên")
        public ResponseEntity<ApiResponse<CandidateResponse>> reject(
                @PathVariable("id") Integer id,
                @RequestParam(required = false) String reason
        ) {
        Integer employeeId = getCurrentEmployeeId();

        CandidateResponse response = onboardService.reject(id, reason, employeeId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Từ chối ứng viên thành công")
        );
        }

        @GetMapping("/candidates/by-user/{userId}")
        @Operation(summary = "Lấy thông tin ứng viên theo userId để prefill tạo employee")
        public ResponseEntity<ApiResponse<CandidatePrefillResponse>> getCandidateByUserId(
                @PathVariable("userId") Integer userId
        ) {
        CandidatePrefillResponse response = candidateService.getCandidatePrefillByUserId(userId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Lấy thông tin ứng viên thành công")
        );
        }
}