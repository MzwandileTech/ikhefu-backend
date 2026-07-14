package com.ikhefuhr.ikhefu.holiday.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalHolidayDto {
    private LocalDate date;
    private String name; // Holds the English name (e.g., "Christmas Day")
    private String localName; // Holds local name (e.g., "Krismasi")
}