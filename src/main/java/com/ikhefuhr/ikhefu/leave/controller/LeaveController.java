package com.ikhefuhr.ikhefu.leave.controller;

import com.ikhefuhr.ikhefu.leave.dto.request.ApplyLeaveRequest;
import com.ikhefuhr.ikhefu.leave.dto.request.LeaveReviewRequest;
import com.ikhefuhr.ikhefu.leave.dto.response.LeaveBalanceResponse;
import com.ikhefuhr.ikhefu.leave.dto.response.LeaveResponse;
import com.ikhefuhr.ikhefu.leave.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <-- Import this!
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // --- Employee Endpoints (Keep open to any authenticated user) ---

    @PostMapping("/apply")
    public ResponseEntity<LeaveResponse> applyForLeave(
            @Valid @RequestBody ApplyLeaveRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        LeaveResponse response = leaveService.applyForLeave(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my-history")
    public ResponseEntity<List<LeaveResponse>> getMyLeaveHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<LeaveResponse> history = leaveService.getEmployeeLeaveHistory(userDetails.getUsername());
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelLeave(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        leaveService.cancelPendingLeave(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-balances")
    public ResponseEntity<List<LeaveBalanceResponse>> getMyBalances(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<LeaveBalanceResponse> balances = leaveService.getEmployeeLeaveBalances(userDetails.getUsername());
        return ResponseEntity.ok(balances);
    }

    // --- Administrative Endpoints (SECURED) ---

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // <-- Only admins can view all requests!
    public ResponseEntity<List<LeaveResponse>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveService.getAllLeaveRequests());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <-- Only admins can view specific requests by ID!
    public ResponseEntity<LeaveResponse> getLeaveById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.getLeaveRequestById(id));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')") // <-- Only admins can approve requests!
    public ResponseEntity<LeaveResponse> approveLeave(
            @PathVariable Long id,
            @Valid @RequestBody LeaveReviewRequest reviewRequest) {
        return ResponseEntity.ok(leaveService.approveLeave(id, reviewRequest));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')") // <-- Only admins can reject requests!
    public ResponseEntity<LeaveResponse> rejectLeave(
            @PathVariable Long id,
            @Valid @RequestBody LeaveReviewRequest reviewRequest) {
        return ResponseEntity.ok(leaveService.rejectLeave(id, reviewRequest));
    }
}