package com.ikhefuhr.ikhefu.repository;

import com.ikhefuhr.ikhefu.entity.PublicHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PublicHolidayRepository extends JpaRepository<PublicHoliday, Long> {

    // Finds all cached holidays within our leave request date range
    List<PublicHoliday> findByDateBetween(LocalDate startDate, LocalDate endDate);
}