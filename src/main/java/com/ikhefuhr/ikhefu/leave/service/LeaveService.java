package com.ikhefuhr.ikhefu.leave.service;

import com.ikhefuhr.ikhefu.leave.dto.request.ApplyLeaveRequest;
import com.ikhefuhr.ikhefu.leave.dto.request.LeaveReviewRequest;
import com.ikhefuhr.ikhefu.leave.dto.response.LeaveBalanceResponse;
import com.ikhefuhr.ikhefu.leave.dto.response.LeaveResponse;
import java.util.List;

public interface LeaveService {

    // --- Employee Workflows ---

    /**
     * Submits a fresh leave request for the authenticated employee.
     */
    LeaveResponse applyForLeave(ApplyLeaveRequest request, String employeeEmail);

    /**
     * Retrieves the entire leave application history for an individual employee.
     */
    List<LeaveResponse> getEmployeeLeaveHistory(String employeeEmail);

    /**
     * Cancels an existing request, provided its status is still PENDING.
     */
    void cancelPendingLeave(Long leaveId, String employeeEmail);

    // --- Administrative Workflows ---

    /**
     * Fetches every leave request across the entire system.
     */
    List<LeaveResponse> getAllLeaveRequests();

    /**
     * Pulls details for a specific leave application by its unique ID.
     */
    LeaveResponse getLeaveRequestById(Long leaveId);

    /**
     * Transitions a request status to APPROVED and records administrative notes.
     */
    LeaveResponse approveLeave(Long leaveId, LeaveReviewRequest reviewRequest);

    /**
     * Transitions a request status to REJECTED and records administrative notes.
     */

    LeaveResponse rejectLeave(Long leaveId, LeaveReviewRequest reviewRequest);

    /**
     * Calculates and returns the remaining leave balances for all active leave types
     * for an individual employee in the current calendar year.
     */
    List<LeaveBalanceResponse> getEmployeeLeaveBalances(String employeeEmail);
}