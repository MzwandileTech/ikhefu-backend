package com.ikhefuhr.ikhefu.holiday.service;

import com.ikhefuhr.ikhefu.entity.PublicHoliday;
import com.ikhefuhr.ikhefu.holiday.dto.ExternalHolidayDto;
import com.ikhefuhr.ikhefu.repository.PublicHolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidaySyncService {

    private final PublicHolidayRepository publicHolidayRepository;
    private final RestTemplate restTemplate = new RestTemplate(); // Built-in Spring HTTP Client

    /**
     * Checks if holidays for a given year exist in our database.
     * If not, it fetches them from the free Nager.Date API and caches them.
     */
    public void ensureHolidaysAreLoaded(int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        // 1. Query the database to see if we've already cached this year's holidays
        List<PublicHoliday> existingHolidays = publicHolidayRepository.findByDateBetween(startOfYear, endOfYear);

        // 2. If nothing is found, trigger the API sync
        if (existingHolidays.isEmpty()) {
            log.info("Holidays for the year {} not found in local DB. Syncing with Nager.Date API...", year);
            try {
                // Fetch public holidays for South Africa (ZA)
                String url = "https://date.nager.at/api/v3/PublicHolidays/" + year + "/ZA";
                ExternalHolidayDto[] response = restTemplate.getForObject(url, ExternalHolidayDto[].class);

                if (response != null && response.length > 0) {
                    List<PublicHoliday> holidaysToSave = Arrays.stream(response)
                            .map(dto -> PublicHoliday.builder()
                                    .date(dto.getDate())
                                    .name(dto.getName())
                                    .build())
                            .toList();

                    // Cache them in PostgreSQL
                    publicHolidayRepository.saveAll(holidaysToSave);
                    log.info("Successfully synced and cached {} South African holidays for the year {}.", holidaysToSave.size(), year);
                }
            } catch (Exception e) {
                // If the external API is ever down, we gracefully log the error and fall back
                // so the user's leave application doesn't crash (it will temporarily calculate without holidays)
                log.error("Failed to fetch holidays from public API for year {}. Falling back gracefully. Error: {}", year, e.getMessage());
            }
        }
    }
}