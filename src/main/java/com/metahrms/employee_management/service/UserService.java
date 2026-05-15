package com.metahrms.employee_management.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.metahrms.employee_management.dto.request.User.RegisterUserDto;
import com.metahrms.employee_management.dto.request.User.UserFilterDto;
import com.metahrms.employee_management.dto.request.User.UserUpdateDto;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.dto.response.Position.PositionResponse;
import com.metahrms.employee_management.dto.response.User.UserResponse;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.User;
import com.metahrms.employee_management.enums.UserRole;
import com.metahrms.employee_management.enums.UserStatus;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.UserRepository;
import com.metahrms.employee_management.specification.UserSpecification;
import com.metahrms.employee_management.util.PasswordGenerator;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    private final EmailService emailService;
    UserRepository userRepository;
    EmployeeRepository employeeRepository;
    DepartmentRepository departmentRepository;
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PagedResponse<UserResponse> getUser(UserFilterDto filterDto) {
        // Build specification for filtering
        Specification<User> spec = UserSpecification.filterUser(
        filterDto.getStatus(),
        filterDto.getDeptId(),
        filterDto.getSearch()
    );

        // Create pageable with sorting by createdAt descending
        Pageable pageable = PageRequest.of(
            filterDto.getPage(),
            filterDto.getPageSize(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<UserResponse> content = userPage.getContent().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());

        return PagedResponse.<UserResponse>builder()
                .content(content)
                .currentPage(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
                .build();

    }

   
    public UserResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (user.getStatus() == UserStatus.DELETED) {
            throw new RuntimeException("User has been deleted");
        }

        return toUserResponse(user);
    }

    public UserResponse createUser(RegisterUserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // ✅ Auto-generate password ngẫu nhiên (bỏ qua password từ DTO)
        String rawPassword = PasswordGenerator.generate();
        log.info("RAW PASSWORD = {}", rawPassword);
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(rawPassword)); // ✅ Hash để lưu DB
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole() != null ? dto.getRole() : UserRole.EMPLOYEE);
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : UserStatus.ACTIVE);

        User saved = userRepository.save(user);

        // ✅ Gửi email sau khi lưu thành công
        // Nếu gửi thất bại → chỉ log, user vẫn được tạo
        emailService.sendWelcomeEmail(saved.getEmail(), saved.getUsername(), rawPassword);

        return toUserResponse(saved);
    }

    public UserResponse updateUser(Integer id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (user.getStatus() == UserStatus.DELETED) {
            throw new RuntimeException("Cannot update deleted user");
        }

        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getPassword() != null) user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getRole() != null) user.setRole(dto.getRole());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());

        User updated = userRepository.save(user);
        return toUserResponse(updated);
    }


    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    public UserResponse getUserInfo(HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);

        if (userId == null) {
            throw new RuntimeException("User ID not found in request");
        }

        User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User Not Found"));

        return toUserResponse(user);
    }

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        Object userObj = request.getAttribute("user");

        if (userObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) userObj;

            Object idObj = user.get("id");
            if (idObj != null) {
                try {
                    return Integer.parseInt(idObj.toString());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public List<UserResponse> getUsersNotLinkedToEmployee() {
        List<User> allUsers = userRepository.findAll();

        List<User> unlinkedUsers = allUsers.stream()
                .filter(user -> {
                    return employeeRepository.findByUserId(user.getId()).isEmpty();
                })
                .filter(user -> user.getStatus() == UserStatus.ACTIVE)
                .filter(user -> user.getRole() != UserRole.ADMIN)
                .collect(Collectors.toList());

        return unlinkedUsers.stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }


    private UserResponse toUserResponse(User user) {
        // Find the corresponding employee for the user
        Employee employee = null;
        String deptName = null;
        try {
            // ✅ Sửa: dùng userId để tìm employee (WHERE e.user_id = ?)
            employee = employeeRepository.findByUserIdWithPosition(user.getId()).orElse(null);
            } catch (Exception e) {
                log.error("Error fetching employee for userId {}: {}", user.getId(), e.getMessage());
            }
        if (employee != null && employee.getDeptId() != null) {
            deptName = departmentRepository.findById(employee.getDeptId())
                    .map(Department::getDeptName)
                    .orElse(null);
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .employeeId(employee != null ?employee.getId():null)
                .email(user.getEmail())
                .role(user.getRole())
                .avatar(employee != null ? employee.getProfilePicImage() : null)
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                // Map additional fields from the Employee entity if it exists
                .fullName(employee != null ? employee.getFullName() : null)
                .gender(employee != null && employee.getGender() != null ? employee.getGender().name() : null)
                .dob(employee != null ? employee.getDob() : null)
                .country(employee != null?employee.getAddress():null)
                .phoneNumber(employee != null ? employee.getPhoneNumber() : null)
                .hireDate(employee != null ? employee.getHireDate() : null)
                .positionName(employee != null && employee.getPosition() != null? employee.getPosition().getPositionName(): null)
                .deptName(deptName)
                .roleInDept(employee != null ?employee.getRoleInDept()!=null?employee.getRoleInDept():null:null)
                .DeptId(employee!=null?employee.getDeptId():null)
                .build();
    }
}
