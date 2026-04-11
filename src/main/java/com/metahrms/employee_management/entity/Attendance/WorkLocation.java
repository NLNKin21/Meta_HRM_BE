package com.metahrms.employee_management.entity.Attendance;

import java.math.BigDecimal;

import com.metahrms.employee_management.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "work_locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkLocation extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "code", unique = true, length = 50)
    private String code;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "radius")
    private Integer radius = 100; // meters

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    // ⭐ Helper method: Calculate distance to another location
    public double calculateDistanceTo(BigDecimal lat, BigDecimal lng) {
        // Haversine formula
        final int EARTH_RADIUS_KM = 6371;

        double lat1 = Math.toRadians(this.latitude.doubleValue());
        double lat2 = Math.toRadians(lat.doubleValue());
        double dLat = Math.toRadians(lat.doubleValue() - this.latitude.doubleValue());
        double dLon = Math.toRadians(lng.doubleValue() - this.longitude.doubleValue());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c * 1000; // Convert to meters
    }

    public boolean isWithinGeofence(BigDecimal lat, BigDecimal lng) {
        double distance = calculateDistanceTo(lat, lng);
        return distance <= this.radius;
    }
}