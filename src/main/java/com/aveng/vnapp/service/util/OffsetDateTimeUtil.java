package com.aveng.vnapp.service.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * Handles all {@link OffsetDateTime} {@link Instant} conversions
 *
 * @author apaydin
 */
public class OffsetDateTimeUtil {

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
