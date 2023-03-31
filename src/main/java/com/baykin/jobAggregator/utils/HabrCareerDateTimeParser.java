package com.baykin.jobAggregator.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String parse) {
        DateTimeFormatter formatterForParse = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        LocalDateTime dateTime = LocalDateTime.parse(parse, formatterForParse);
        DateTimeFormatter formatterOutput = DateTimeFormatter.BASIC_ISO_DATE;
        dateTime.format(formatterOutput);
        return dateTime;
    }
}
