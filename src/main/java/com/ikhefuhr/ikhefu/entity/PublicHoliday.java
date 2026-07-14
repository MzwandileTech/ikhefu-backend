package com.ikhefuhr.ikhefu.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "public_holidays", indexes = {
        @Index(name = "idx_holiday_date", columnList = "holiday_date", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "holiday_date", nullable = false, unique = true)
    private LocalDate date;

    @NotNull
    @Column(nullable = false)
    private String name;
}