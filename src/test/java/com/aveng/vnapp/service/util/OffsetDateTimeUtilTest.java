package com.aveng.vnapp.service.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.Test;

public class OffsetDateTimeUtilTest {


    @Test
    public void convertInstantUTCtoOffsetDateTime_success() {

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Instant instant = OffsetDateTimeUtil.convertOffsetDateTimeToUTCInstant(now);
        assertNotNull(instant);
        assertEquals(instant.atOffset(ZoneOffset.UTC), now);
    }

    @Test
    public void convertOffsetDateTimeToUTCInstant() {
        Instant now = Instant.now();
        OffsetDateTime convertedOffsetDateTime = OffsetDateTimeUtil.convertInstantUTCtoOffsetDateTime(now);
        assertEquals(convertedOffsetDateTime.toInstant(), now);
    }
}