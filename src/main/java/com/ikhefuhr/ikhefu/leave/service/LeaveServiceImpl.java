package com.ikhefuhr.ikhefu.leave.service;

import com.ikhefuhr.ikhefu.entity.LeaveRequest;
import com.ikhefuhr.ikhefu.entity.LeaveType;
import com.ikhefuhr.ikhefu.entity.PublicHoliday;
import com.ikhefuhr.ikhefu.entity.User;
import com.ikhefuhr.ikhefu.enums.LeaveStatus;
import com.ikhefuhr.ikhefu.holiday.service.HolidaySyncService;
import com.ikhefuhr.ikhefu.leave.dto.request.ApplyLeaveRequest;
import com.ikhefuhr.ikhefu.leave.dto.request.LeaveReviewRequest;
import com.ikhefuhr.ikhefu.leave.dto.response.LeaveBalanceResponse;
import com.ikhefuhr.ikhefu.leave.dto.response.LeaveResponse;
import com.ikhefuhr.ikhefu.leave.mapper.LeaveMapper;
import com.ikhefuhr.ikhefu.repository.LeaveRequestRepository;
import com.ikhefuhr.ikhefu.repository.LeaveTypeRepository;
import com.ikhefuhr.ikhefu.repository.PublicHolidayRepository;
import com.ikhefuhr.ikhefu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveMapper leaveMapper;

    private final PublicHolidayRepository publicHolidayRepository;
    private final HolidaySyncService holidaySyncService;

    // --- Employee Actions ---

    @Override
    public LeaveResponse applyForLeave(ApplyLeaveRequest request, String employeeEmail) {
        // 1. Basic date defensive validation
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }

        // 2. Calculate the working days for this new request (ignoring weekends/holidays)
        long workingDays = calculateWorkingDays(request.getStartDate(), request.getEndDate());
        if (workingDays == 0) {
            throw new IllegalArgumentException("The requested leave period does not contain any working days.");
        }

        // 3. LEAVE BALANCE ENFORCEMENT
        // Find the leave type to get its allowed days limit (e.g., Annual Leave = 21 days)
        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found with ID: " + request.getLeaveTypeId()));

        int maxDaysAllowed = leaveType.getDaysAllowed();
        int currentYear = request.getStartDate().getYear();

        // Fetch all of this employee's historical requests
        List<LeaveRequest> allRequests = leaveRequestRepository.findByEmployeeEmailOrderBySubmittedAtDesc(employeeEmail);

        // Calculate how many APPROVED working days they have already used this year for this leave type
        long daysAlreadyUsed = allRequests.stream()
                .filter(lr -> lr.getLeaveType().getId().equals(request.getLeaveTypeId()))
                .filter(lr -> lr.getStatus() == LeaveStatus.APPROVED)
                .filter(lr -> lr.getStartDate().getYear() == currentYear)
                .mapToLong(lr -> calculateWorkingDays(lr.getStartDate(), lr.getEndDate()))
                .sum();

        long remainingBalance = maxDaysAllowed - daysAlreadyUsed;

        // Check if the new request exceeds their available balance
        if (workingDays > remainingBalance) {
            throw new IllegalArgumentException(String.format(
                    "Leave request denied. You requested %d days of %s, but you only have %d days remaining for %d (Max: %d, Used: %d).",
                    workingDays, leaveType.getName(), remainingBalance, currentYear, maxDaysAllowed, daysAlreadyUsed
            ));
        }

        // 4. Active leave overlap barrier check
        List<LeaveStatus> activeStatuses = Arrays.asList(LeaveStatus.PENDING, LeaveStatus.APPROVED);
        boolean hasOverlap = leaveRequestRepository.hasOverlappingLeave(
                employeeEmail,
                request.getStartDate(),
                request.getEndDate(),
                activeStatuses
        );

        if (hasOverlap) {
            throw new IllegalStateException("You already have an active or pending leave request that overlaps with this period.");
        }

        // 5. Proceed with normal booking if all validations pass
        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new RuntimeException("Employee not found with email: " + employeeEmail));

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employee(employee)
                .leaveType(leaveType)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .status(LeaveStatus.PENDING)
                .build();

        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);
        return leaveMapper.toResponse(savedRequest);
    }


    @Override
    @Transactional(readOnly = true)
    public List<LeaveResponse> getEmployeeLeaveHistory(String employeeEmail) {
        // Switch from OrderByCreatedAtDesc to OrderBySubmittedAtDesc
        List<LeaveRequest> history = leaveRequestRepository.findByEmployeeEmailOrderBySubmittedAtDesc(employeeEmail);
        return leaveMapper.toResponseList(history);
    }

    @Override
    public void cancelPendingLeave(Long leaveId, String employeeEmail) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found with ID: " + leaveId));

        // 1. Ownership check (Security)
        if (!leaveRequest.getEmployee().getEmail().equals(employeeEmail)) {
            throw new IllegalStateException("You are not authorized to cancel this leave request.");
        }

        // 2. State check (Business Rule)
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Only PENDING leave requests can be cancelled.");
        }

        // 3. Soft Delete: Change status and persist instead of calling .delete()
        leaveRequest.setStatus(LeaveStatus.CANCELLED);
        leaveRequestRepository.save(leaveRequest); // Saves the audit trail!
    }

    // --- Administrative Actions ---

    @Override
    @Transactional(readOnly = true)
    public List<LeaveResponse> getAllLeaveRequests() {
        List<LeaveRequest> requests = leaveRequestRepository.findAll();
        return leaveMapper.toResponseList(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveResponse getLeaveRequestById(Long leaveId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found with ID: " + leaveId));
        return leaveMapper.toResponse(leaveRequest);
    }

    @Override
    public LeaveResponse approveLeave(Long leaveId, LeaveReviewRequest reviewRequest) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found with ID: " + leaveId));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Cannot approve a request that is already " + leaveRequest.getStatus());
        }

        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setReviewedAt(LocalDateTime.now());
        leaveRequest.setAdminComment(reviewRequest.getAdminComment());

        LeaveRequest updatedRequest = leaveRequestRepository.save(leaveRequest);
        return leaveMapper.toResponse(updatedRequest);
    }

    @Override
    public LeaveResponse rejectLeave(Long leaveId, LeaveReviewRequest reviewRequest) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found with ID: " + leaveId));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Cannot reject a request that is already " + leaveRequest.getStatus());
        }

        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setReviewedAt(LocalDateTime.now());
        leaveRequest.setAdminComment(reviewRequest.getAdminComment());

        LeaveRequest updatedRequest = leaveRequestRepository.save(leaveRequest);
        return leaveMapper.toResponse(updatedRequest);
    }

    // --- Business Logic Utilities ---

    private long calculateWorkingDays(LocalDate start, LocalDate end) {
        long workingDaysCount = 0;
        LocalDate current = start;

        // 2. Auto-verify and load the target holidays for the start date's year
        holidaySyncService.ensureHolidaysAreLoaded(start.getYear());

        // If the leave request spans across New Year's (e.g., Dec 2026 to Jan 2027), sync the next year too!
        if (start.getYear() != end.getYear()) {
            holidaySyncService.ensureHolidaysAreLoaded(end.getYear());
        }

        // 3. Fetch the holiday dates falling inside the request's range from our local DB cache
        List<LocalDate> holidayDates = publicHolidayRepository.findByDateBetween(start, end)
                .stream()
                .map(PublicHoliday::getDate)
                .toList();

        // 4. Calculate working days
        while (!current.isAfter(end)) {
            DayOfWeek dayOfWeek = current.getDayOfWeek();

            if (dayOfWeek != DayOfWeek.SATURDAY &&
                    dayOfWeek != DayOfWeek.SUNDAY &&
                    !holidayDates.contains(current)) {
                workingDaysCount++;
            }
            current = current.plusDays(1);
        }

        return workingDaysCount;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> getEmployeeLeaveBalances(String employeeEmail) {
        int currentYear = LocalDate.now().getYear();

        // 1. Fetch all leave types
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();

        // 2. Fetch all historical leave requests for this employee
        List<LeaveRequest> allRequests = leaveRequestRepository.findByEmployeeEmailOrderBySubmittedAtDesc(employeeEmail);

        // 3. Map each leave type to its respective balances
        return leaveTypes.stream().map(type -> {
            // Calculate total approved working days used this year for this specific leave type
            long daysUsed = allRequests.stream()
                    .filter(lr -> lr.getLeaveType().getId().equals(type.getId()))
                    .filter(lr -> lr.getStatus() == LeaveStatus.APPROVED)
                    .filter(lr -> lr.getStartDate().getYear() == currentYear)
                    .mapToLong(lr -> calculateWorkingDays(lr.getStartDate(), lr.getEndDate()))
                    .sum();

            long remaining = type.getDaysAllowed() - daysUsed;

            return LeaveBalanceResponse.builder()
                    .leaveTypeId(type.getId())
                    .leaveTypeName(type.getName())
                    .maxDaysAllowed(type.getDaysAllowed())
                    .daysUsed(daysUsed)
                    .daysRemaining(remaining < 0 ? 0 : remaining) // Protect against negative balance edges
                    .build();
        }).toList();
    }
}