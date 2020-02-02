package com.aveng.vnapp.service.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * @author apaydin
 */
public class DateUtil {

    public static OffsetDateTime convertInstantUTCtoOffsetDateTime(Instant instant) {
        return Optional.ofNullable(instant)
            .map(i -> OffsetDateTime.from(i.atOffset(ZoneOffset.UTC)))
            .orElse(null);
    }

    public static Instant convertOffsetDateTimeToUTCInstant(OffsetDateTime offsetDateTime) {
        return Optional.ofNullable(offsetDateTime)
            .map(OffsetDateTime::toInstant)
            .orElse(null);
    }
}
