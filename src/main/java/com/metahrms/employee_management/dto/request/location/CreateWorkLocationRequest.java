package com.metahrms.employee_management.dto.request.location;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkLocationRequest {

    @NotBlank(message = "Location name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    @Size(max = 50, message = "Code must not exceed 50 characters")
    private String code;

    @Size(max = 1000, message = "Address must not exceed 1000 characters")
    private String address;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private BigDecimal longitude;

    @Min(value = 10, message = "Radius must be at least 10 meters")
    @Max(value = 10000, message = "Radius must not exceed 10000 meters")
    private Integer radius = 100;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 100)
    private String contactPerson;

    @Size(max = 20)
    private String contactPhone;
}