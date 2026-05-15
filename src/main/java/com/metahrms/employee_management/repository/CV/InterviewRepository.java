package com.metahrms.employee_management.repository.CV;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.CV.Interview;
import com.metahrms.employee_management.enums.InterviewResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Integer> {

    Optional<Interview> findByIdAndIsDeletedFalse(Integer id);

    // Tìm interview theo candidate
    @Query("""
        SELECT i FROM Interview i
        JOIN FETCH i.candidate
        WHERE i.candidate.id = :candidateId
          AND i.isDeleted = false
        ORDER BY i.interviewDate DESC
    """)
    List<Interview> findByCandidateIdAndIsDeletedFalse(
            @Param("candidateId") Integer candidateId
    );

    // Tìm interview mới nhất của candidate
    @Query("""
        SELECT i FROM Interview i
        WHERE i.candidate.id = :candidateId
          AND i.isDeleted = false
        ORDER BY i.interviewDate DESC
        LIMIT 1
    """)
    Optional<Interview> findLatestByCandidateId(@Param("candidateId") Integer candidateId);

    // Lịch phỏng vấn sắp tới
    @Query("""
        SELECT i FROM Interview i
        JOIN FETCH i.candidate          
        WHERE i.isDeleted = false
          AND i.interviewDate BETWEEN :from AND :to
          AND i.result = 'PENDING'
        ORDER BY i.interviewDate ASC
    """)
    List<Interview> findUpcomingInterviews(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // Lịch phỏng vấn của 1 interviewer
    @Query("""
        SELECT i FROM Interview i
        JOIN FETCH i.candidate
        WHERE i.isDeleted = false
          AND i.interviewerId = :interviewerId
          AND i.interviewDate BETWEEN :from AND :to
        ORDER BY i.interviewDate ASC
    """)
    List<Interview> findByInterviewerAndDateRange(
            @Param("interviewerId") Integer interviewerId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // Kiểm tra interviewer có trống lịch không
    @Query("""
        SELECT COUNT(i) > 0
        FROM Interview i
        WHERE i.isDeleted = false
          AND i.interviewerId = :interviewerId
          AND i.interviewDate BETWEEN :from AND :to
    """)
    boolean hasConflict(
            @Param("interviewerId") Integer interviewerId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // Đếm theo kết quả
    Long countByResultAndIsDeletedFalse(InterviewResult result);
}