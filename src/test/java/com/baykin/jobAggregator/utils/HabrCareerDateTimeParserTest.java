package com.baykin.jobAggregator.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class HabrCareerDateTimeParserTest {

    @Test
    void localDateParserTest() {
        String outText = "2023-02-22T17:27:02+03:00";
        String expected = "2023-02-22T17:27:02";
        LocalDateTime localDateTime = new HabrCareerDateTimeParser().parse(outText);
        assertThat(expected).isEqualTo(localDateTime.toString());
    }
}