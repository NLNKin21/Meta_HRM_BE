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
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.User;
import com.metahrms.employee_management.enums.UserRole;
import com.metahrms.employee_management.enums.UserStatus;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.UserRepository;
import com.metahrms.employee_management.specification.UserSpecification;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    EmployeeRepository employeeRepository;
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

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());

        // Use role from DTO, default to USER if not provided
        user.setRole(dto.getRole() != null ? dto.getRole() : UserRole.EMPLOYEE);

        // Use status from DTO, default to ACTIVE if not provided
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : UserStatus.ACTIVE);

        User saved = userRepository.save(user);
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
        try {
            employee = employeeRepository.findByUserId(user.getId()).orElse(null);
        } catch (Exception e) {
            // Data corruption: Multiple employees found for one user. Ignore employee details to prevent crash.
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                // Map additional fields from the Employee entity if it exists
                .fullName(employee != null ? employee.getFullName() : null)
                .gender(employee != null && employee.getGender() != null ? employee.getGender().name() : null)
                .dob(employee != null ? employee.getDob() : null)
                .country(employee != null?employee.getAddress():null)
                .phoneNumber(employee != null ? employee.getPhoneNumber() : null)
                .hireDate(employee != null ? employee.getHireDate() : null)
                // .positionName(employee != null ? employee.getPosition().getPositionName() : null)
                .deptName(employee != null ? employee.getDeptId() != null ? employee.getDeptId().toString() : null : null)
                .roleInDept(employee != null ?employee.getRoleInDept()!=null?employee.getRoleInDept():null:null)
                .DeptId(employee!=null?employee.getDeptId():null)
                .build();
    }
}
