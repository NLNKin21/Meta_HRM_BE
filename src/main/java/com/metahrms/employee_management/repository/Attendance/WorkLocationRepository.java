package com.metahrms.employee_management.repository.Attendance;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Attendance.WorkLocation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho WorkLocation entity
 */
@Repository
public interface WorkLocationRepository extends JpaRepository<WorkLocation, Integer> {
    
    /**
     * Tìm location theo code (unique)
     */
    Optional<WorkLocation> findByCode(String code);
    
    /**
     * Tìm tất cả locations đang active
     */
    List<WorkLocation> findByIsActiveTrue();
    
    /**
     * Tìm locations theo name (có thể trùng)
     */
    List<WorkLocation> findByName(String name);
    
    /**
     * Kiểm tra code đã tồn tại chưa
     */
    boolean existsByCode(String code);
    
    /**
     * Kiểm tra code đã tồn tại (exclude id - dùng khi update)
     */
    @Query("SELECT CASE WHEN COUNT(wl) > 0 THEN true ELSE false END " +
           "FROM WorkLocation wl WHERE wl.code = :code AND wl.id != :id")
    boolean existsByCodeAndIdNot(@Param("code") String code, @Param("id") Integer id);
    
    /**
     * Search locations by name, code, or address
     */
    @Query("SELECT wl FROM WorkLocation wl " +
           "WHERE (LOWER(wl.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(wl.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(wl.address) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND wl.isActive = true")
    List<WorkLocation> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * Tìm locations trong một bán kính từ tọa độ cho trước
     * Sử dụng Haversine formula
     * 
     * Formula: distance = acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon2 - lon1)) * R
     * R = Earth radius = 6371 km
     */
    @Query(value = 
        "SELECT * FROM work_locations wl " +
        "WHERE wl.is_active = true " +
        "AND (6371000 * acos(" +
        "    cos(radians(:latitude)) * cos(radians(wl.latitude)) * " +
        "    cos(radians(wl.longitude) - radians(:longitude)) + " +
        "    sin(radians(:latitude)) * sin(radians(wl.latitude))" +
        ")) <= :radiusMeters",
        nativeQuery = true
    )
    List<WorkLocation> findNearbyLocations(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radiusMeters") Integer radiusMeters
    );
    
    /**
     * Tìm location gần nhất với tọa độ cho trước
     */
    @Query(value = 
        "SELECT *, " +
        "(6371000 * acos(" +
        "    cos(radians(:latitude)) * cos(radians(latitude)) * " +
        "    cos(radians(longitude) - radians(:longitude)) + " +
        "    sin(radians(:latitude)) * sin(radians(latitude))" +
        ")) AS distance " +
        "FROM work_locations " +
        "WHERE is_active = true " +
        "ORDER BY distance ASC " +
        "LIMIT 1",
        nativeQuery = true
    )
    WorkLocation findNearestLocation(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude
    );
    
    /**
     * Tìm locations theo radius (ví dụ: tất cả locations có radius >= 100m)
     */
    @Query("SELECT wl FROM WorkLocation wl WHERE wl.radius >= :minRadius AND wl.isActive = true")
    List<WorkLocation> findByMinRadius(@Param("minRadius") Integer minRadius);
    
    /**
     * Đếm số attendance records tại một location
     */
    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar " +
           "WHERE ar.checkInLocationId = :locationId")
    long countAttendanceByLocationId(@Param("locationId") Integer locationId);
    
    /**
     * Tìm locations có contact person
     */
    @Query("SELECT wl FROM WorkLocation wl WHERE wl.contactPerson IS NOT NULL AND wl.isActive = true")
    List<WorkLocation> findLocationsWithContactPerson();
    
    /**
     * Tìm locations theo contact phone
     */
    List<WorkLocation> findByContactPhone(String contactPhone);
    
    /**
     * Validate GPS coordinates trong location
     * Kiểm tra tọa độ có nằm trong bán kính cho phép không
     */
    @Query(value = 
        "SELECT CASE WHEN " +
        "(6371000 * acos(" +
        "    cos(radians(:checkLat)) * cos(radians(wl.latitude)) * " +
        "    cos(radians(wl.longitude) - radians(:checkLon)) + " +
        "    sin(radians(:checkLat)) * sin(radians(wl.latitude))" +
        ")) <= wl.radius " +
        "THEN true ELSE false END " +
        "FROM work_locations wl " +
        "WHERE wl.id = :locationId",
        nativeQuery = true
    )
    Boolean isWithinRadius(
        @Param("locationId") Integer locationId,
        @Param("checkLat") Double checkLat,
        @Param("checkLon") Double checkLon
    );
    
    /**
     * Tính khoảng cách từ tọa độ đến location
     */
    @Query(value = 
        "SELECT " +
        "(6371000 * acos(" +
        "    cos(radians(:checkLat)) * cos(radians(wl.latitude)) * " +
        "    cos(radians(wl.longitude) - radians(:checkLon)) + " +
        "    sin(radians(:checkLat)) * sin(radians(wl.latitude))" +
        ")) AS distance " +
        "FROM work_locations wl " +
        "WHERE wl.id = :locationId",
        nativeQuery = true
    )
    Double calculateDistanceToLocation(
        @Param("locationId") Integer locationId,
        @Param("checkLat") Double checkLat,
        @Param("checkLon") Double checkLon
    );
    
    /**
     * Tìm tất cả locations trong một khu vực (bounding box)
     */
    @Query("SELECT wl FROM WorkLocation wl " +
           "WHERE wl.latitude BETWEEN :minLat AND :maxLat " +
           "AND wl.longitude BETWEEN :minLon AND :maxLon " +
           "AND wl.isActive = true")
    List<WorkLocation> findInBoundingBox(
        @Param("minLat") BigDecimal minLat,
        @Param("maxLat") BigDecimal maxLat,
        @Param("minLon") BigDecimal minLon,
        @Param("maxLon") BigDecimal maxLon
    );

    /**
     * Tìm theo ID và chưa bị xoá - dùng trong Admin update/delete
     */
    Optional<WorkLocation> findByIdAndIsDeletedFalse(Integer id);

    /**
     * Tìm theo code và chưa bị xoá - dùng khi validate unique code lúc create
     */
    Optional<WorkLocation> findByCodeAndIsDeletedFalse(String code);

    /**
     * Kiểm tra code đã tồn tại (chưa xoá) - dùng khi create
     */
    boolean existsByCodeAndIsDeletedFalse(String code);

    /**
     * Kiểm tra code đã tồn tại (chưa xoá, trừ chính nó) - dùng khi update
     */
    boolean existsByCodeAndIdNotAndIsDeletedFalse(String code, Integer id);

    /**
     * Danh sách có phân trang + filter - dùng trong Admin list
     */
    @Query("SELECT wl FROM WorkLocation wl WHERE wl.isDeleted = false " +
           "AND (:isActive IS NULL OR wl.isActive = :isActive) " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "     LOWER(wl.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "     LOWER(wl.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "     LOWER(wl.address) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<WorkLocation> findAllWithFilters(
        @Param("isActive") Boolean isActive,
        @Param("keyword") String keyword,
        Pageable pageable
    );
}