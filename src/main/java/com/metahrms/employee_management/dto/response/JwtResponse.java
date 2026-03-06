package com.metahrms.employee_management.dto.response;

<<<<<<< HEAD
import com.metahrms.employee_management.enums.Role;
=======
import com.metahrms.employee_management.enums.UserRole;
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private Long userId;
    private String username;
    private String email;
<<<<<<< HEAD
    private Role role;
=======
    private UserRole role;
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
    private Long employeeId;
    private String employeeName;
}