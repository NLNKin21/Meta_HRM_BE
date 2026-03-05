package com.metahrms.employee_management.dto.request.User;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.metahrms.employee_management.enums.UserStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Filter criteria for querying users with pagination")
public class UserFilterDto {

    @Schema(
        description = "Page number (zero-based)",
        example = "0",
        defaultValue = "0"
    )
    Integer page = 0;

    @Schema(
        description = "Number of items per page",
        example = "10",
        defaultValue = "10"
    )
    Integer pageSize = 10;

    @Schema(
        description = "Filter by user status",
        example = "ACTIVE"
    )
    UserStatus status;

    @Schema(
        description = "Filter by department ID",
        example = "1"
    )
    Integer deptId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(
        description = "Filter by date of birth",
        example = "01/01/1990",
        type = "string",
        pattern = "dd/MM/yyyy"
    )
    LocalDate dob;

    @Schema(
        description = "Search keyword to filter users by username, email, or full name (case-insensitive)",
        example = "john"
    )
    String search;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer page = 0;
        private Integer pageSize = 10;
        private UserStatus status;
        private Integer deptId;
        private LocalDate dob;
        private String search;

        public Builder page(Integer page) {
            this.page = page;
            return this;
        }

        public Builder pageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public Builder deptId(Integer deptId) {
            this.deptId = deptId;
            return this;
        }

        public Builder dob(LocalDate dob) {
            this.dob = dob;
            return this;
        }

        public Builder search(String search) {
            this.search = search;
            return this;
        }

        public UserFilterDto build() {
            UserFilterDto dto = new UserFilterDto();
            dto.page = this.page;
            dto.pageSize = this.pageSize;
            dto.status = this.status;
            dto.deptId = this.deptId;
            dto.dob = this.dob;
            dto.search = this.search;
            return dto;
        }
    }

}
