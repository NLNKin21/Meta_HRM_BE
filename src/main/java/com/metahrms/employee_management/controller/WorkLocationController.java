package com.metahrms.employee_management.controller;

import com.metahrms.employee_management.dto.request.common.ApiResponse;
import com.metahrms.employee_management.entity.Attendance.WorkLocation;
import com.metahrms.employee_management.repository.Attendance.WorkLocationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class WorkLocationController {

    private final WorkLocationRepository workLocationRepository;

    /**
     * GET /api/locations - Lấy tất cả locations đang active
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkLocation>>> getAllLocations() {
        log.info("[LOCATIONS] Getting all active work locations");

        List<WorkLocation> locations = workLocationRepository.findByIsActiveTrue();

        log.info("[LOCATIONS] Found {} location(s)", locations.size());

        return ResponseEntity.ok(
            ApiResponse.success(locations, "Retrieved " + locations.size() + " location(s)")
        );
    }

    /**
     * GET /api/locations/{id} - Lấy location theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkLocation>> getLocationById(@PathVariable Integer id) {
        log.info("[LOCATIONS] Getting location id={}", id);

        WorkLocation location = workLocationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Location not found: " + id));

        return ResponseEntity.ok(
            ApiResponse.success(location, "Location found")
        );
    }

    /**
     * GET /api/locations/nearby - Tìm locations gần tọa độ
     */
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<WorkLocation>>> getNearbyLocations(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "1000") Integer radius
    ) {
        log.info("[LOCATIONS] Finding nearby locations: lat={}, lng={}, radius={}m",
            latitude, longitude, radius);

        List<WorkLocation> locations = workLocationRepository.findNearbyLocations(
            latitude, longitude, radius
        );

        log.info("[LOCATIONS] Found {} nearby location(s)", locations.size());

        return ResponseEntity.ok(
            ApiResponse.success(locations, "Found " + locations.size() + " nearby location(s)")
        );
    }

    /**
     * GET /api/locations/search - Tìm kiếm locations
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<WorkLocation>>> searchLocations(
            @RequestParam String keyword
    ) {
        log.info("[LOCATIONS] Searching locations: keyword={}", keyword);

        List<WorkLocation> locations = workLocationRepository.searchByKeyword(keyword);

        return ResponseEntity.ok(
            ApiResponse.success(locations, "Found " + locations.size() + " location(s)")
        );
    }
}