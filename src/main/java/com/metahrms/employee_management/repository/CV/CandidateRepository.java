package com.metahrms.employee_management.repository.CV;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.CV.Candidate;
import com.metahrms.employee_management.enums.CandidateStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Integer>,
        JpaSpecificationExecutor<Candidate> {

    // ========== TÌM KIẾM CƠ BẢN ==========

    Optional<Candidate> findByIdAndIsDeletedFalse(Integer id);

    List<Candidate> findByStatusAndIsDeletedFalse(CandidateStatus status);

    // ========== GIỚI HẠN 1 EMAIL / VỊ TRÍ ==========

    @Query("""
        SELECT COUNT(c) > 0
        FROM Candidate c
        WHERE c.email = :email
          AND c.desiredPosition = :position
          AND c.isDeleted = false
          AND c.status NOT IN ('REJECTED')
    """)
    boolean existsByEmailAndPosition(
            @Param("email") String email,
            @Param("position") String position
    );

    // Kiểm tra email đã ứng tuyển bất kỳ vị trí nào chưa (đang active)
    @Query("""
        SELECT COUNT(c) > 0
        FROM Candidate c
        WHERE c.email = :email
          AND c.isDeleted = false
          AND c.status NOT IN ('REJECTED', 'ONBOARDED')
    """)
    boolean existsByEmailAndActiveApplication(@Param("email") String email);

    // ========== DANH SÁCH CHO HR ==========

    @Query("""
        SELECT c FROM Candidate c
        WHERE c.isDeleted = false
        ORDER BY c.appliedAt DESC
    """)
    Page<Candidate> findAllActive(Pageable pageable);

    @Query("""
        SELECT c FROM Candidate c
        WHERE c.isDeleted = false
          AND c.status = :status
        ORDER BY c.appliedAt DESC
    """)
    Page<Candidate> findByStatus(
            @Param("status") CandidateStatus status,
            Pageable pageable
    );

    @Query("""
        SELECT c FROM Candidate c
        WHERE c.isDeleted = false
          AND (
            LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.desiredPosition) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        ORDER BY c.appliedAt DESC
    """)
    Page<Candidate> searchByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // ========== FILTER NÂNG CAO ==========

    @Query("""
        SELECT c FROM Candidate c
        WHERE c.isDeleted = false
          AND (:status IS NULL OR c.status = :status)
          AND (:departmentId IS NULL OR c.departmentId = :departmentId)
          AND (:keyword IS NULL OR
               LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.desiredPosition) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        ORDER BY c.appliedAt DESC
    """)
    Page<Candidate> findWithFilters(
            @Param("status") CandidateStatus status,
            @Param("departmentId") Integer departmentId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // ========== THỐNG KÊ ==========

    Long countByStatusAndIsDeletedFalse(CandidateStatus status);

    @Query("""
        SELECT COUNT(c)
        FROM Candidate c
        WHERE c.isDeleted = false
          AND c.appliedAt BETWEEN :from AND :to
    """)
    Long countByAppliedAtBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
        SELECT c.status, COUNT(c)
        FROM Candidate c
        WHERE c.isDeleted = false
        GROUP BY c.status
    """)
    List<Object[]> countByStatusGrouped();
}