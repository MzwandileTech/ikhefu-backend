package com.ikhefuhr.ikhefu.leave.mapper;

import com.ikhefuhr.ikhefu.entity.LeaveRequest;
import com.ikhefuhr.ikhefu.leave.dto.response.LeaveResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring") // Tells Spring to manage this as an injectable Bean
public interface LeaveMapper {

    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "employee.email", target = "employeeEmail")
    // Combines firstName and lastName dynamically if your User entity supports it
    @Mapping(target = "employeeName", expression = "java(request.getEmployee().getFirstName() + \" \" + request.getEmployee().getLastName())")
    @Mapping(source = "leaveType.name", target = "leaveTypeName")
    LeaveResponse toResponse(LeaveRequest request);

    List<LeaveResponse> toResponseList(List<LeaveRequest> requests);
}