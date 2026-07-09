package com.ikhefuhr.ikhefu.repository;

import com.ikhefuhr.ikhefu.entity.LeaveRequest;
import com.ikhefuhr.ikhefu.entity.User;
import com.ikhefuhr.ikhefu.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployee(User employee);

    List<LeaveRequest> findByStatus(LeaveStatus status);

    List<LeaveRequest> findByEmployeeAndStatus(User employee, LeaveStatus status);
}
