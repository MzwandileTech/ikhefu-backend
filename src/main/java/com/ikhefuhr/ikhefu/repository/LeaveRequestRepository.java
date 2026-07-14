package com.ikhefuhr.ikhefu.repository;

import com.ikhefuhr.ikhefu.entity.LeaveRequest;
import com.ikhefuhr.ikhefu.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // 1. Fetch a specific employee's complete leave history using their User ID
    List<LeaveRequest> findByEmployeeIdOrderBySubmittedAtDesc(Long employeeId);

    // 2. Fetch a specific employee's complete leave history using their Email
    List<LeaveRequest> findByEmployeeEmailOrderBySubmittedAtDesc(String email);

    // 3. Let Admins filter all global requests by status (e.g., finding all PENDING items)
    List<LeaveRequest> findByStatusOrderBySubmittedAtAsc(LeaveStatus status);

    /**
     * Finds any overlapping leave requests for an employee that are still active (PENDING or APPROVED)
     */
    @Query("SELECT COUNT(l) > 0 FROM LeaveRequest l WHERE l.employee.email = :email " +
            "AND l.status IN (:activeStatuses) " +
            "AND ((l.startDate <= :endDate AND l.endDate >= :startDate))")
    boolean hasOverlappingLeave(
            @Param("email") String email,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate,
            @Param("activeStatuses") List<LeaveStatus> activeStatuses
    );
}