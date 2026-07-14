package com.ikhefuhr.ikhefu.leave.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveBalanceResponse {
    private Long leaveTypeId;
    private String leaveTypeName;
    private int maxDaysAllowed;
    private long daysUsed;
    private long daysRemaining;
}