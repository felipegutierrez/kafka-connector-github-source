package com.github.felipegutierrez.kafka.connector.github.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateUtilsTest {

    @Test
    public void maxInstant() {
        Instant i1 = ZonedDateTime.now().toInstant();
        Instant i2 = i1.plusSeconds(1);
        assertEquals(DateUtils.MaxInstant(i1, i2), i2);
        assertEquals(DateUtils.MaxInstant(i2, i1), i2);
    }
}