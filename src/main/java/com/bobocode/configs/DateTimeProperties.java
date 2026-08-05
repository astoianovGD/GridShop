package com.bobocode.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.datetime")
public class DateTimeProperties {

    private LocalDate launchDate;
    private LocalTime launchTime;
    private LocalDateTime launchTimestamp;
    private Duration sessionTimeout;

}