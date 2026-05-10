package com.metahrms.employee_management.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.metahrms.employee_management.dto.request.Contract.ContractCreateDto;
import com.metahrms.employee_management.dto.request.Contract.ContractFilterDto;
import com.metahrms.employee_management.dto.request.Contract.ContractUpdateDto;
import com.metahrms.employee_management.dto.response.Contract.ContractResponse;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.entity.Contract;
import com.metahrms.employee_management.entity.ContractType;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.ContractRepository;
import com.metahrms.employee_management.repository.ContractTypeRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.specification.ContractSpecification;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ContractService {
    ContractRepository contractRepository;
    EmployeeRepository employeeRepository;
    CloudinaryService cloudinaryService;
    ContractTypeRepository contractTypeRepository;

    /**
     * Get contracts with filtering and pagination
     */
    @Transactional(readOnly = true)
    public PagedResponse<ContractResponse> getContracts(ContractFilterDto filterDto) {
        log.info("Fetching contracts with filters: {}", filterDto);

        // Build specification for filtering
        Specification<Contract> spec = ContractSpecification.filterContracts(
            filterDto.getStatus(),
            filterDto.getContractTypeId(),
            filterDto.getEmpId(),
            filterDto.getStartDate(),
            filterDto.getEndDate()
        );

        // Create pageable with sorting by createdAt descending
        Pageable pageable = PageRequest.of(
            filterDto.getPage(),
            filterDto.getPageSize(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Contract> contractPage = contractRepository.findAll(spec, pageable);

        List<ContractResponse> contractResponses = contractPage.getContent().stream()
            .map(this::toContractResponse)
            .collect(Collectors.toList());

        return PagedResponse.<ContractResponse>builder()
            .content(contractResponses)
            .currentPage(contractPage.getNumber())
            .pageSize(contractPage.getSize())
            .totalElements(contractPage.getTotalElements())
            .totalPages(contractPage.getTotalPages())
            .hasNext(contractPage.hasNext())
            .hasPrevious(contractPage.hasPrevious())
            .build();
    }

    /**
     * Get contract by ID
     */
    @Transactional(readOnly = true)
    public ContractResponse getContractById(Integer id) {
        log.info("Fetching contract with id: {}", id);

        Contract contract = contractRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));

        if (Boolean.TRUE.equals(contract.getIsDeleted())) {
            throw new ResourceNotFoundException("Contract has been deleted");
        }

        return toContractResponse(contract);
    }

    /**
     * Create contract with file upload
     */
    @Transactional
    public ContractResponse createContract(ContractCreateDto createDto, MultipartFile file) 
            throws IOException {
        log.info("Creating contract for employee: {}", createDto.getEmpId());

        ContractType contractType = contractTypeRepository
    .findById(createDto.getContractTypeId())
    .orElseThrow(() -> new ResourceNotFoundException(
        "Contract type not found: " + createDto.getContractTypeId()));

        Employee employee = employeeRepository.findById(createDto.getEmpId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Employee not found with id: " + createDto.getEmpId()));

        // ✅ Upload file với method mới - trả về đầy đủ thông tin
        String fileUrl = null;
        String fileKey = null;
        String previewUrl = null;
        String fileFormat = null;
        boolean previewable = false;
        

        if (file != null && !file.isEmpty()) {
            CloudinaryService.UploadResult uploadResult = 
                cloudinaryService.uploadContractFile(file);
            
            fileUrl = uploadResult.getFileUrl();
            fileKey = uploadResult.getPublicId();
            previewUrl = uploadResult.getPreviewUrl();
            fileFormat = uploadResult.getFormat();
            previewable = uploadResult.isPreviewable();
            
            log.info("File uploaded: url={}, previewUrl={}, previewable={}", 
                fileUrl, previewUrl, previewable);

                
        }

        Contract contract = Contract.builder()
            .empId(createDto.getEmpId())
            .contractType(contractType)
            .startDate(createDto.getStartDate())
            .endDate(createDto.getEndDate())
            .fileUrl(fileUrl)
            .fileKey(fileKey)
            .previewUrl(previewUrl)      // ✅ Lưu preview URL
            .fileFormat(fileFormat)       // ✅ Lưu format
            .previewable(previewable)    // ✅ Lưu flag
            .status(createDto.getStatus() != null 
                ? createDto.getStatus() : ContractStatus.ACTIVE)
            .build();

        contract.setIsDeleted(false);
        Contract savedContract = contractRepository.save(contract);
        
        log.info("Contract created with id: {}", savedContract.getId());
        return toContractResponse(savedContract);
    }

    /**
     * Update contract (including file replacement)
     */
    @Transactional
    public ContractResponse updateContract(Integer id, ContractUpdateDto updateDto, MultipartFile file) throws IOException {
        log.info("Updating contract with id: {}", id);

        Contract contract = contractRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));

        if (Boolean.TRUE.equals(contract.getIsDeleted())) {
            throw new IllegalStateException("Cannot update deleted contract");
        }

        // ✅ Update basic fields
        if (updateDto.getContractTypeId() != null) {
            ContractType newType = contractTypeRepository
                .findById(updateDto.getContractTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Contract type not found: " + updateDto.getContractTypeId()));
            contract.setContractType(newType);
        }
        if (updateDto.getStartDate() != null) {
            contract.setStartDate(updateDto.getStartDate());
        }
        if (updateDto.getEndDate() != null) {
            contract.setEndDate(updateDto.getEndDate());
        }
        if (updateDto.getStatus() != null) {
            contract.setStatus(updateDto.getStatus());
        }

        // ✅ Update file (nếu có file mới)
        if (file != null && !file.isEmpty()) {
            // Xóa file cũ trên Cloudinary (nếu có)
            if (contract.getFileKey() != null && !contract.getFileKey().isEmpty()) {
                try {
                    cloudinaryService.deleteFile(contract.getFileKey());
                    log.info("Old file deleted: {}", contract.getFileKey());
                } catch (IOException e) {
                    log.warn("Failed to delete old file: {}", e.getMessage());
                }
            }

            // Upload file mới
            String newFileUrl = cloudinaryService.uploadFile(file);
            String newFileKey = cloudinaryService.extractPublicId(newFileUrl);

            contract.setFileUrl(newFileUrl);
            contract.setFileKey(newFileKey);
            log.info("New file uploaded: {}", newFileUrl);
        }

        Contract updatedContract = contractRepository.save(contract);
        log.info("Contract updated successfully: {}", id);

        return toContractResponse(updatedContract);
    }

    /**
     * Soft delete contract (and delete file from Cloudinary)
     */
    @Transactional
    public void deleteContract(Integer id) throws IOException {
        log.info("Deleting contract with id: {}", id);

        Contract contract = contractRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));

        // ✅ Xóa file trên Cloudinary (nếu có)
        if (contract.getFileKey() != null && !contract.getFileKey().isEmpty()) {
            try {
                cloudinaryService.deleteFile(contract.getFileKey());
                log.info("File deleted from Cloudinary: {}", contract.getFileKey());
            } catch (IOException e) {
                log.error("Failed to delete file from Cloudinary: {}", e.getMessage());
                // Vẫn tiếp tục soft delete contract
            }
        }

        // ✅ Soft delete contract
        contract.setIsDeleted(true);
        contractRepository.save(contract);
        log.info("Contract soft deleted successfully: {}", id);
    }

    
    /**
     * Convert Contract entity to ContractResponse DTO
     */
    private ContractResponse toContractResponse(Contract contract) {
        // Get employee information
        String employeeName = null;
        if (contract.getEmpId() != null) {
            employeeName = employeeRepository.findById(contract.getEmpId())
                .map(Employee::getFullName)
                .orElse(null);
        }

        return ContractResponse.builder()
            .id(contract.getId())
            .empId(contract.getEmpId())
            .employeeName(employeeName)
            .contractType(contract.getContractType() != null? contract.getContractType().getTypeName(): null)
            .contractTypeId(contract.getContractType() != null? contract.getContractType().getId(): null)
            .startDate(contract.getStartDate())
            .endDate(contract.getEndDate())
            .fileUrl(contract.getFileUrl())  // ✅ Trả về URL trực tiếp từ Cloudinary
            .fileKey(contract.getFileKey())  // ✅ Thêm fileKey cho frontend
            .status(contract.getStatus() != null ? contract.getStatus().name() : null)
            .createdAt(contract.getCreatedAt())
            .build();
    }
}