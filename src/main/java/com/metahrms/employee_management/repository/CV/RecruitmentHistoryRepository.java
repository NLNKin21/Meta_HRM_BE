package com.metahrms.employee_management.repository.CV;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.CV.RecruitmentHistory;

import java.util.List;

@Repository
public interface RecruitmentHistoryRepository extends JpaRepository<RecruitmentHistory, Integer> {

    @Query("""
        SELECT rh FROM RecruitmentHistory rh
        WHERE rh.candidate.id = :candidateId
        ORDER BY rh.createdAt DESC
    """)
    List<RecruitmentHistory> findByCandidateIdOrderByCreatedAtDesc(
            @Param("candidateId") Integer candidateId
    );

    List<RecruitmentHistory> findByCandidateId(Integer candidateId);
}