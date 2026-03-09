package com.metahrms.employee_management.entity;

import com.metahrms.employee_management.enums.UserRole;
import com.metahrms.employee_management.enums.UserStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    private String username;

    private String password;

    private String email;

    @Enumerated(EnumType.ORDINAL)  // Lưu bằng số: 0,1,2,3
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Enumerated(EnumType.ORDINAL)  // Lưu bằng số: 0,1,2,3
    @Column(name = "status", nullable = false)
    private UserStatus status;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String password;
        private String email;
        private UserRole role;
        private UserStatus status;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(UserRole role) {
            this.role = role;
            return this;
        }

        public Builder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public User build() {
            User user = new User();
            user.username = this.username;
            user.password = this.password;
            user.email = this.email;
            user.role = this.role;
            user.status = this.status;
            return user;
        }
    }
}