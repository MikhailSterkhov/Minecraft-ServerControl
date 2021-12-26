package org.stonlexx.servercontrol.api.utility;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@UtilityClass
public class DateUtil {

    public static final Date DATE_FORMATTER = new Date();

    public static final String DEFAULT_DATETIME_PATTERN = ("dd.MM.yyyy h:mm:ss a");
    public static final String DEFAULT_DATE_PATTERN     = ("EEE, MMM d, yyyy");
    public static final String DEFAULT_TIME_PATTERN     = ("h:mm a");


    public String formatPattern(@NonNull String pattern) {
        return createDateFormat(pattern).format(DATE_FORMATTER);
    }

    public String formatTime(long millis, @NonNull String pattern) {
        return createDateFormat(pattern).format(new Date(millis));
    }

    @SneakyThrows
    public long parsePatternToMillis(@NonNull String pattern, @NonNull String formattedPattern) {
        return createDateFormat(pattern).parse(formattedPattern).getTime();
    }

    private DateFormat createDateFormat(@NonNull String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, new Locale("ru"));
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

        return dateFormat;
    }


    @SneakyThrows
    public Date parseDate(@NonNull String datePattern,
                          @NonNull String formattedDate) {

        return createDateFormat(datePattern).parse(formattedDate);
    }

}
