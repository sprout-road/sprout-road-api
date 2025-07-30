package com.sprout.api.file.util;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class TimeUtil {

	private final Clock clock;

	public TimeUtil() {
		this(Clock.systemDefaultZone());
	}

	public TimeUtil(Clock clock) {
		this.clock = clock;
	}

	public Date getDate() {
		return Date.from(Instant.now(clock));
	}

	public Date getDate(long dateTime) {
		return new Date(dateTime);
	}

	public long getCurrentTimeMillis() {
		return Instant.now(clock).toEpochMilli();
	}

	public String getFormattedDate(String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return now().format(formatter);
	}

	public LocalDateTime now() {
		return LocalDateTime.now(clock);
	}
}
