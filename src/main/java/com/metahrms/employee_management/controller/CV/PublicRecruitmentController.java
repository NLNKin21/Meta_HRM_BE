package com.metahrms.employee_management.controller.CV;

import com.metahrms.employee_management.dto.request.CV.CandidateApplyRequest;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.CV.CandidateResponse;
import com.metahrms.employee_management.dto.response.Department.DepartmentResponse;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.service.CV.CandidateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/public/recruitment")
@RequiredArgsConstructor
@Tag(name = "Public - Recruitment", description = "API công khai cho ứng viên nộp đơn")
public class PublicRecruitmentController {

    private final CandidateService candidateService;
    private final DepartmentRepository departmentRepository;

    @GetMapping("/departments")
    @Operation(summary = "Lấy danh sách phòng ban (cho form ứng tuyển)")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getDepartments() {
        List<Department> departments = departmentRepository.findAll().stream()
                .filter(d -> !Boolean.TRUE.equals(d.getIsDeleted()))
                .collect(Collectors.toList());

        List<DepartmentResponse> response = departments.stream()
                .map(d -> DepartmentResponse.builder()
                        .id(d.getId())
                        .deptName(d.getDeptName())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(response, "OK")
        );
    }

    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Nộp đơn ứng tuyển", description = "Ứng viên nộp form + CV (không cần đăng nhập)")
    public ResponseEntity<ApiResponse<CandidateResponse>> apply(
            @RequestPart("data") @Valid CandidateApplyRequest request,
            @RequestPart(value = "cvFile", required = false) MultipartFile cvFile
    ) {
        log.info("[RECRUIT] POST /public/recruitment/apply - {}", request.getEmail());

        try {
            CandidateResponse response = candidateService.apply(request, cvFile);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.<CandidateResponse>builder()
                            .code(201)
                            .status("success")
                            .message("Nộp đơn ứng tuyển thành công! Vui lòng kiểm tra email.")
                            .data(response)
                            .build()
            );
        } catch (Exception e) {
            log.error("[RECRUIT] Apply failed", e);
            return ResponseEntity.badRequest().body(
                    ApiResponse.<CandidateResponse>builder()
                            .code(400)
                            .status("error")
                            .message(e.getMessage())
                            .build()
            );
        }
    }
}