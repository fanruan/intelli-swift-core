package com.fr.swift.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author yee
 * @date 2018-12-20
 */
public class DateUtils {
    public static Date string2Date(String string) {
        if (Strings.isBlank(string)) {
            return null;
        } else if (string.matches("^\\d+$")) {
            Long longValue;
            try {
                longValue = Long.valueOf(string);
            } catch (NumberFormatException var8) {
                if (string.length() <= 3) {
                    throw new RuntimeException("can't cast String to Long! might too long or contain error String value", var8);
                }

                try {
                    longValue = Long.valueOf(string.substring(0, string.length() - 3));
                } catch (NumberFormatException var6) {
                    throw new RuntimeException("can't cast String to Long! might too long or contain error String value", var6);
                }
            }

            return object2Date(longValue);
        } else {
            return null;
        }
    }

    public static Date object2Date(Object obj) {
        Date date = null;
        if (obj instanceof Date) {
            date = (Date) obj;
        } else if (obj instanceof Calendar) {
            date = ((Calendar) obj).getTime();
        } else if (obj instanceof Number) {
            long longValue = ((Number) obj).longValue();
            if (longValue <= 1000000L && longValue > 0L) {
                date = new Date((longValue - 25569L) * 1000L * 24L * 60L * 60L);
            } else {
                date = new Date(longValue);
            }
        }

        return date;
    }
}
