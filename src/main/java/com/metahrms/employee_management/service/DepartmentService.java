package com.metahrms.employee_management.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metahrms.employee_management.dto.request.Department.DepartmentDto;
import com.metahrms.employee_management.dto.response.Department.DepartmentResponse;
import com.metahrms.employee_management.dto.response.Department.DepartmentSummaryDto;
import com.metahrms.employee_management.dto.response.Department.TeamMemberResponse;
import com.metahrms.employee_management.dto.response.Department.TeamWorkloadResponse;
import com.metahrms.employee_management.dto.response.Employee.EmployeeSummaryDto;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.User;
import com.metahrms.employee_management.entity.Task.Task;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.UserRepository;
import com.metahrms.employee_management.repository.Task.TaskRepository;
import com.metahrms.employee_management.enums.RoleInDepartment;
import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DepartmentService {

    DepartmentRepository departmentRepository;
    EmployeeRepository employeeRepository;
    TaskRepository taskRepository;
    UserRepository userRepository;

    // ==================== EXISTING METHODS ====================

    /**
     * Tạo phòng ban mới
     */
    public DepartmentResponse createDepartment(DepartmentDto createDto) {
        departmentRepository.findByDeptNameAndIsDeletedFalse(createDto.getDeptName())
            .ifPresent(d -> {
                throw new RuntimeException("Tên phòng ban đã tồn tại: " + createDto.getDeptName());
            });

        Department department = new Department();
        department.setDeptName(createDto.getDeptName());
        department.setIsDeleted(false);

        Department saved = departmentRepository.save(department);
        return toDepartmentResponse(saved);
    }

    /**
     * Cập nhật phòng ban
     */
    public DepartmentResponse updateDepartment(Integer id, DepartmentDto updateDto) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban với id: " + id));

        if (Boolean.TRUE.equals(department.getIsDeleted())) {
            throw new RuntimeException("Không thể cập nhật phòng ban đã bị xóa");
        }

        departmentRepository.findByDeptNameAndIsDeletedFalse(updateDto.getDeptName())
            .filter(d -> !d.getId().equals(id))
            .ifPresent(d -> {
                throw new RuntimeException("Tên phòng ban đã tồn tại: " + updateDto.getDeptName());
            });

        department.setDeptName(updateDto.getDeptName());
        Department updated = departmentRepository.save(department);
        return toDepartmentResponse(updated);
    }

    /**
     * Xóa mềm phòng ban
     */
    public void deleteDepartment(Integer id) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban với id: " + id));

        if (Boolean.TRUE.equals(department.getIsDeleted())) {
            throw new RuntimeException("Phòng ban đã bị xóa trước đó");
        }

        department.setIsDeleted(true);
        departmentRepository.save(department);
    }

    /**
     * Lấy chi tiết phòng ban theo ID
     */
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Integer id) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban với id: " + id));

        if (Boolean.TRUE.equals(department.getIsDeleted())) {
            throw new ResourceNotFoundException("Phòng ban đã bị xóa");
        }

        return toDepartmentResponse(department);
    }

    /**
     * Lấy danh sách nhân viên theo phòng ban (sắp xếp theo cấp bậc)
     */
    @Transactional(readOnly = true)
    public List<EmployeeSummaryDto> getEmployeesByDepartmentId(Integer deptId) {
        Department department = departmentRepository.findById(deptId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban với id: " + deptId));

        if (Boolean.TRUE.equals(department.getIsDeleted())) {
            throw new ResourceNotFoundException("Phòng ban đã bị xóa");
        }

        List<Employee> employees = employeeRepository.findByDeptIdAndIsDeletedFalseOrderByPositionLevel(deptId);

        if (employees.isEmpty()) {
            return Collections.emptyList();
        }

        Integer managerId = employeeRepository
            .findFirstByDeptIdAndRoleInDept(deptId, RoleInDepartment.HEAD)
            .map(Employee::getId)
            .orElse(null);

        return employees.stream()
            .map(emp -> toEmployeeSummaryDto(emp, managerId))
            .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách tổng hợp phòng ban
     */
    @Transactional(readOnly = true)
    public List<DepartmentSummaryDto> getDepartmentSummaries() {
        return departmentRepository.findByIsDeletedFalse().stream()
            .map(dept -> {
                Long count = employeeRepository.countByDeptId(dept.getId());
                
                String managerName = employeeRepository
                    .findFirstByDeptIdAndRoleInDept(dept.getId(), RoleInDepartment.HEAD)
                    .map(Employee::getFullName)
                    .orElse(null);

                return new DepartmentSummaryDto(
                    dept.getId(),
                    dept.getDeptName(),
                    managerName,
                    count,
                    dept.getCreatedAt()
                );
            })
            .collect(Collectors.toList());
    }

    // ==================== NEW METHODS FOR TEAM MANAGEMENT ====================

    /**
     * Lấy danh sách thành viên phòng ban (cho Manager view)
     */
    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getDepartmentMembers(Integer departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban với id: " + departmentId));

        if (Boolean.TRUE.equals(department.getIsDeleted())) {
            throw new ResourceNotFoundException("Phòng ban đã bị xóa");
        }

        List<Employee> employees = employeeRepository.findActiveByDepartmentId(
            departmentId,
            EmployeeStatus.ACTIVE
        );

        if (employees.isEmpty()) {
            return Collections.emptyList();
        }

        return employees.stream()
            .map(this::mapToTeamMemberResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy thống kê workload của team
     */
    @Transactional(readOnly = true)
    public List<TeamWorkloadResponse> getTeamWorkload(Integer departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban với id: " + departmentId));

        if (Boolean.TRUE.equals(department.getIsDeleted())) {
            throw new ResourceNotFoundException("Phòng ban đã bị xóa");
        }

        List<Employee> employees = employeeRepository.findActiveByDepartmentId(
            departmentId,
            EmployeeStatus.ACTIVE
        );

        if (employees.isEmpty()) {
            return Collections.emptyList();
        }

        return employees.stream()
            .map(this::mapToTeamWorkloadResponse)
            .collect(Collectors.toList());
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Map Employee to TeamMemberResponse
     */
    private TeamMemberResponse mapToTeamMemberResponse(Employee employee) {
        // Lấy tasks của nhân viên
        List<Task> tasks = taskRepository.findByAssigneeIdAndIsDeletedFalse(employee.getId());
        
        int totalTasks = tasks.size();
        
        long completedCount = tasks.stream()
            .filter(this::isCompletedTask)
            .count();
        
        long inProgressCount = tasks.stream()
            .filter(this::isInProgressTask)
            .count();
        
        long overdueCount = tasks.stream()
            .filter(t -> Boolean.TRUE.equals(t.getIsLate()))
            .count();

        // Lấy thông tin position
        String positionName = null;
        if (employee.getPosition() != null) {
            positionName = employee.getPosition().getPositionName();
        }

        // Lấy email từ User
        String email = null;
        String avatar = null;
        if (employee.getUserId() != null) {
            User user = userRepository.findById(employee.getUserId()).orElse(null);
            if (user != null) {
                email = user.getEmail();
                avatar =null; // Hoặc tên field khác
            }
        }

        return TeamMemberResponse.builder()
            .id(employee.getId())
            .fullName(employee.getFullName())
            .email(email)
            .avatar(avatar)
            .position(positionName)
            .roleInDept(employee.getRoleInDept() != null ? employee.getRoleInDept().name() : null)
            .status(employee.getStatus() != null ? employee.getStatus().name() : null)
            .taskCount(totalTasks)
            .completedTasks((int) completedCount)
            .inProgressTasks((int) inProgressCount)
            .overdueTasks((int) overdueCount)
            .phoneNumber(employee.getPhoneNumber())
            .gender(employee.getGender() != null ? employee.getGender().name() : null)
            .hireDate(employee.getHireDate() != null ? employee.getHireDate().toString() : null)
            .build();
    }

    /**
     * Map Employee to TeamWorkloadResponse
     */
    private TeamWorkloadResponse mapToTeamWorkloadResponse(Employee employee) {
        List<Task> tasks = taskRepository.findByAssigneeIdAndIsDeletedFalse(employee.getId());
        
        int totalTasks = tasks.size();
        
        long completedCount = tasks.stream()
            .filter(this::isCompletedTask)
            .count();
        
        long inProgressCount = tasks.stream()
            .filter(this::isInProgressTask)
            .count();
        
        long pendingCount = tasks.stream()
            .filter(this::isPendingTask)
            .count();
        
        long overdueCount = tasks.stream()
            .filter(t -> Boolean.TRUE.equals(t.getIsLate()))
            .count();
        
        int completionRate = totalTasks > 0 
            ? (int) ((completedCount * 100) / totalTasks) 
            : 0;
        
        double avgCompletionTime = calculateAvgCompletionTime(tasks);
        
        String workloadLevel = calculateWorkloadLevel(totalTasks, (int) overdueCount);

        String positionName = null;
        if (employee.getPosition() != null) {
            positionName = employee.getPosition().getPositionName();
        }

        // Lấy avatar từ User
        String avatar = null;
        if (employee.getUserId() != null) {
            User user = userRepository.findById(employee.getUserId()).orElse(null);
            if (user != null) {
                avatar = null;
            }
        }

        return TeamWorkloadResponse.builder()
            .userId(employee.getId())
            .fullName(employee.getFullName())
            .avatar(avatar)
            .position(positionName)
            .totalTasks(totalTasks)
            .completedTasks((int) completedCount)
            .inProgressTasks((int) inProgressCount)
            .pendingTasks((int) pendingCount)
            .overdueTasks((int) overdueCount)
            .completionRate(completionRate)
            .avgCompletionTime(Math.round(avgCompletionTime * 10.0) / 10.0)
            .workloadLevel(workloadLevel)
            .build();
    }

    /**
     * Kiểm tra task đã hoàn thành
     */
    private boolean isCompletedTask(Task task) {
        if (task.getStatus() == null) return false;
        String statusName = task.getStatus().getStatusName().toLowerCase();
        return statusName.contains("completed") || 
               statusName.contains("hoàn thành") ||
               statusName.contains("done");
    }

    /**
     * Kiểm tra task đang thực hiện
     */
    private boolean isInProgressTask(Task task) {
        if (task.getStatus() == null) return false;
        String statusName = task.getStatus().getStatusName().toLowerCase();
        return statusName.contains("in_progress") || 
               statusName.contains("in progress") ||
               statusName.contains("đang") ||
               statusName.contains("processing");
    }

    /**
     * Kiểm tra task đang chờ
     */
    private boolean isPendingTask(Task task) {
        if (task.getStatus() == null) return false;
        String statusName = task.getStatus().getStatusName().toLowerCase();
        return statusName.contains("todo") || 
               statusName.contains("pending") ||
               statusName.contains("chờ") ||
               statusName.contains("new") ||
               statusName.contains("mới");
    }

    /**
     * Tính thời gian hoàn thành trung bình
     */
    private double calculateAvgCompletionTime(List<Task> tasks) {
        return tasks.stream()
            .filter(t -> t.getCompletedAt() != null && t.getCreatedAt() != null)
            .mapToLong(t -> {
                LocalDate createdDate = t.getCreatedAt().toLocalDate();
                LocalDate completedDate = t.getCompletedAt().toLocalDate();
                return ChronoUnit.DAYS.between(createdDate, completedDate);
            })
            .average()
            .orElse(0.0);
    }

    /**
     * Xác định mức độ workload
     */
    private String calculateWorkloadLevel(int totalTasks, int overdueTasks) {
        if (overdueTasks >= 3) return "HIGH";
        if (totalTasks >= 10) return "HIGH";
        if (totalTasks >= 5) return "MEDIUM";
        return "LOW";
    }

    // ==================== EXISTING HELPER METHODS ====================

    private DepartmentResponse toDepartmentResponse(Department department) {
        Long employeeCount = employeeRepository.countByDeptId(department.getId());

        String managerName = employeeRepository
            .findFirstByDeptIdAndRoleInDept(department.getId(), RoleInDepartment.HEAD)
            .map(Employee::getFullName)
            .orElse(null);

        Integer managerId = employeeRepository
            .findFirstByDeptIdAndRoleInDept(department.getId(), RoleInDepartment.HEAD)
            .map(Employee::getId)
            .orElse(null);

        return DepartmentResponse.builder()
            .id(department.getId())
            .deptName(department.getDeptName())
            .managerName(managerName)
            .managerId(managerId)
            .employeeCount(employeeCount)
            .createdAt(department.getCreatedAt())
            .build();
    }

    private EmployeeSummaryDto toEmployeeSummaryDto(Employee employee, Integer managerId) {
        Integer positionLevel = null;
        String positionName = null;
        Integer positionId = null;
        
        if (employee.getPosition() != null) {
            positionId = employee.getPosition().getId();
            positionName = employee.getPosition().getPositionName();
            positionLevel = employee.getPosition().getLevelOrder();
        }
        
        return EmployeeSummaryDto.builder()
            .id(employee.getId())
            .fullName(employee.getFullName())
            .gender(employee.getGender())
            .phoneNumber(employee.getPhoneNumber())
            .hireDate(employee.getHireDate())
            .status(employee.getStatus())
            .positionId(positionId)
            .positionName(positionName)
            .positionLevel(positionLevel)
            .roleInDept(employee.getRoleInDept())
            .isManager(employee.getId().equals(managerId))
            .build();
    }
}