package com.metahrms.employee_management.controller.CV;


import com.metahrms.employee_management.dto.request.CV.InterviewCreateRequest;
import com.metahrms.employee_management.dto.request.CV.InterviewRescheduleRequest;
import com.metahrms.employee_management.dto.request.CV.InterviewUpdateResultRequest;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.CV.InterviewResponse;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.service.CV.InterviewService;
import com.metahrms.employee_management.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/recruitment/interviews")
@RequiredArgsConstructor
@Tag(name = "HR - Interviews", description = "API quản lý lịch phỏng vấn")
public class InterviewController {

    private final InterviewService interviewService;
    private final EmployeeRepository employeeRepository;

    // ========== TẠO LỊCH PHỎNG VẤN ==========

    @PostMapping
    @Operation(summary = "Tạo lịch phỏng vấn cho ứng viên")
    public ResponseEntity<ApiResponse<InterviewResponse>> create(
            @Valid @RequestBody InterviewCreateRequest request
    ) {
        Integer employeeId = getCurrentEmployeeId();
        log.info("[INTERVIEW] POST /recruitment/interviews - candidate: {}", request.getCandidateId());

        InterviewResponse response = interviewService.createInterview(request, employeeId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(response, "Tạo lịch phỏng vấn thành công. Email mời đã được gửi.")
        );
    }

    // ========== ĐÁNH GIÁ KẾT QUẢ ==========

    @PutMapping("/{id}/result")
    @Operation(summary = "Đánh giá kết quả phỏng vấn")
    public ResponseEntity<ApiResponse<InterviewResponse>> updateResult(
            @PathVariable("id") Integer id,
            @Valid @RequestBody InterviewUpdateResultRequest request
    ) {
        Integer employeeId = getCurrentEmployeeId();

        InterviewResponse response = interviewService.updateResult(id, request, employeeId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Đánh giá phỏng vấn thành công")
        );
    }

    // ========== ĐỔI LỊCH ==========

    @PutMapping("/{id}/reschedule")
    @Operation(summary = "Đổi lịch phỏng vấn")
    public ResponseEntity<ApiResponse<InterviewResponse>> reschedule(
            @PathVariable("id") Integer id,
            @Valid @RequestBody InterviewRescheduleRequest request
    ) {
        Integer employeeId = getCurrentEmployeeId();

        InterviewResponse response = interviewService.reschedule(id, request, employeeId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Đổi lịch phỏng vấn thành công. Email thông báo đã được gửi.")
        );
    }

    // ========== HỦY PHỎNG VẤN ==========

    @DeleteMapping("/{id}")
    @Operation(summary = "Hủy phỏng vấn")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @PathVariable("id") Integer id,
            @RequestParam(required = false) String reason
    ) {
        Integer employeeId = getCurrentEmployeeId();

        interviewService.cancelInterview(id, reason, employeeId);

        return ResponseEntity.ok(
                ApiResponse.successMessage("Hủy phỏng vấn thành công")
        );
    }

    // ========== QUERY ==========

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết phỏng vấn")
    public ResponseEntity<ApiResponse<InterviewResponse>> getById(
            @PathVariable("id") Integer id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(interviewService.getById(id), "OK")
        );
    }

    @GetMapping("/candidate/{candidateId}")
    @Operation(summary = "Lịch sử phỏng vấn của ứng viên")
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> getByCandidateId(
            @PathVariable("candidateId") Integer candidateId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(interviewService.getByCandidateId(candidateId), "OK")
        );
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Danh sách phỏng vấn sắp tới")
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> getUpcoming(
            @RequestParam(name = "days", defaultValue = "7") Integer days
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(interviewService.getUpcoming(days),
                        "Lấy danh sách phỏng vấn sắp tới thành công")
        );
    }

    @GetMapping("/my-schedule")
    @Operation(summary = "Lịch phỏng vấn của tôi (interviewer)")
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> getMySchedule(
            @RequestParam(name="days", defaultValue = "30") Integer days
    ) {
        Integer employeeId = getCurrentEmployeeId();

        return ResponseEntity.ok(
                ApiResponse.success(interviewService.getByInterviewer(employeeId, days),
                        "Lấy lịch phỏng vấn thành công")
        );
    }

    // ========== HELPER ==========

    private Integer getCurrentEmployeeId() {
        Integer userId = SecurityUtils.getCurrentUserId();
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found for userId: " + userId));
        return employee.getId();
    }
}