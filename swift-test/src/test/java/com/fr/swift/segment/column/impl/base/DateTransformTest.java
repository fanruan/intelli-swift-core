package com.fr.swift.segment.column.impl.base;


import com.fr.swift.segment.column.impl.DateType;
import com.fr.swift.segment.column.impl.MixDateType;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2018/3/28
 */
public class DateTransformTest {
    @Test
    public void testTransform() {
        Calendar c = Calendar.getInstance();
        int year = 2011, month = Calendar.AUGUST, day = 9,
                hour = 11, minute = 11, second = 11;
        c.set(year, month, day, hour, minute, second);
        Calendar c1 = (Calendar) c.clone();
        long millis = c.getTimeInMillis();

        assertEquals(year, DateType.YEAR.from(c));
        assertEquals(month + 1, DateType.MONTH.from(c));
        assertEquals(day, DateType.DAY.from(c));
        assertEquals(hour, DateType.HOUR.from(c));
        assertEquals(minute, DateType.MINUTE.from(c));
        assertEquals(second, DateType.SECOND.from(c));


        c.set(Calendar.MILLISECOND, 0);
        c1.setTimeInMillis(millis);
        assertEquals(c.getTimeInMillis(), MixDateType.Y_M_D_H_M_S.from(c1));

        c.set(year, month, day, hour, minute, 0);
        c.set(Calendar.MILLISECOND, 0);
        c1.setTimeInMillis(millis);
        assertEquals(c.getTimeInMillis(), MixDateType.Y_M_D_H_M.from(c1));

        c.set(year, month, day, hour, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        c1.setTimeInMillis(millis);
        assertEquals(c.getTimeInMillis(), MixDateType.Y_M_D_H.from(c1));

        c.set(year, month, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        c1.setTimeInMillis(millis);
        assertEquals(c.getTimeInMillis(), MixDateType.Y_M_D.from(c1));

        c.set(year, month, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        c1.setTimeInMillis(millis);
        assertEquals(c.getTimeInMillis(), MixDateType.Y_M.from(c1));

        c.set(year, Calendar.JULY, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        c1.setTimeInMillis(millis);
        assertEquals(c.getTimeInMillis(), MixDateType.Y_Q.from(c1));

        c.set(year, month, 8, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        c1.setTimeInMillis(millis);
        assertEquals(c.getTimeInMillis(), MixDateType.Y_W.from(c1));
    }
}