package com.ikhefuhr.ikhefu.leave.dto.response;

import com.ikhefuhr.ikhefu.enums.LeaveStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveResponse {
    private Long id;
    private Long employeeId;
    private String employeeEmail;
    private String employeeName;
    private String leaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private String adminComment;
}