package com.metahrms.employee_management.controller;

import com.metahrms.employee_management.dto.ContractRequest;
import com.metahrms.employee_management.dto.ContractResponse;
import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.ContractType;
import com.metahrms.employee_management.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    // 1️⃣ Tạo hợp đồng
    @PostMapping
    public ResponseEntity<ContractResponse> create(
            @Valid @RequestBody ContractRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contractService.create(request));
    }

    // 2️⃣ Lấy chi tiết
    @GetMapping("/{id}")
    public ResponseEntity<ContractResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getById(id));
    }

    // 3️⃣ Lấy theo employee
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(contractService.getByEmployee(employeeId));
    }

    // 4️⃣ Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<ContractResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ContractRequest request) {

        return ResponseEntity.ok(contractService.update(id, request));
    }

    // 5️⃣ Xóa mềm
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contractService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 6️⃣ Pagination
    @GetMapping
    public ResponseEntity<?> getAll(Pageable pageable) {
        return ResponseEntity.ok(contractService.getAll(pageable));
    }

    // 7️⃣ Search nâng cao
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String contractNumber,
            @RequestParam(required = false) ContractStatus status,
            @RequestParam(required = false) ContractType contractType,
            Pageable pageable) {

        return ResponseEntity.ok(
                contractService.search(contractNumber, status, contractType, pageable)
        );
    }
}