package com.metahrms.employee_management.entity.Leave;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "holidays")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "holiday_date", nullable = false, unique = true)
    private LocalDate holidayDate;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String type = "NATIONAL";  // NATIONAL hoặc COMPANY

    @Column(nullable = false)
    private Boolean active;
}