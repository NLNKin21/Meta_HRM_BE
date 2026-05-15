package com.metahrms.employee_management.dto.request.CV;

import com.metahrms.employee_management.enums.Gender;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Form ứng tuyển - Public, không cần đăng nhập")
public class CandidateApplyRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 150, message = "Họ tên tối đa 150 ký tự")
    @Schema(description = "Họ và tên ứng viên", example = "Nguyễn Văn An", required = true)
    String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Schema(description = "Email liên hệ", example = "nguyenvanan@gmail.com", required = true)
    String email;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải từ 10-11 chữ số")
    @Schema(description = "Số điện thoại", example = "0901234567")
    String phoneNumber;

    @Schema(description = "Ngày sinh (dd/MM/yyyy)", example = "15/06/1998")
    String dob;

    @Schema(description = "Giới tính", example = "MALE")
    Gender gender;

    @Schema(description = "Địa chỉ", example = "123 Nguyễn Huệ, Q1, TP.HCM")
    String address;

    @NotBlank(message = "Vị trí ứng tuyển không được để trống")
    @Size(max = 200, message = "Vị trí ứng tuyển tối đa 200 ký tự")
    @Schema(description = "Vị trí muốn ứng tuyển", example = "Frontend Developer", required = true)
    String desiredPosition;

    @Schema(description = "Phòng ban muốn ứng tuyển (ID)", example = "3")
    Integer departmentId;

    @Schema(description = "Mức lương mong muốn", example = "15-20 triệu")
    String expectedSalary;

    @Size(max = 2000, message = "Thư giới thiệu tối đa 2000 ký tự")
    @Schema(description = "Thư giới thiệu / lý do ứng tuyển")
    String coverLetter;
}