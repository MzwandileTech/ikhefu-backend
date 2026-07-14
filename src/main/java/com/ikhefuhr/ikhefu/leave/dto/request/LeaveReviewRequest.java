package com.ikhefuhr.ikhefu.leave.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveReviewRequest {

    @Size(max = 500, message = "Admin comment cannot exceed 500 characters")
    private String adminComment;
}