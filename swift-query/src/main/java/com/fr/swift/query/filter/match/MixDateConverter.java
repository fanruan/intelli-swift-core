package com.fr.swift.query.filter.match;

import com.fr.swift.segment.column.impl.DateType;

import java.util.Calendar;

/**
 * Created by pony on 2018/5/22.
 */
public class MixDateConverter implements MatchConverter<String> {

    private static final String[] APPENDED = {"-", "-", " ", ":", ":", " "};
    private DateType[] dateTypes;
    private Calendar c = Calendar.getInstance();

    public MixDateConverter(DateType[] dateTypes) {
        this.dateTypes = dateTypes;
    }

    @Override
    public String convert(Object data) {
        return data == null ? null : convertByTypes((Long) data);
    }

    private String convertByTypes(Long data) {
        c.setTimeInMillis(data);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < dateTypes.length; i++) {
            int value = dateTypes[i].from(c);
            if (value < 10 && i > 0) {
                sb.append(0);
            }
            sb.append(dateTypes[i].from(c));
            sb.append(APPENDED[i]);
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
