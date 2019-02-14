package com.fr.swift.query.filter.match;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.column.impl.DateType;

/**
 * Created by pony on 2018/5/22.
 */
public class MatchConverterFactory {
    public static MatchConverter createConvertor(GroupType groupType) {
        switch (groupType) {
            case Y_Q:
                return new MixDateConverter(new DateType[]{DateType.YEAR, DateType.QUARTER});
            case M_D:
                return new MixDateConverter(new DateType[]{DateType.MONTH, DateType.DAY});
            case Y_W:
                return new MixDateConverter(new DateType[]{DateType.YEAR, DateType.WEEK});
            case Y_D:
                return new MixDateConverter(new DateType[]{DateType.YEAR, DateType.DAY});
            case Y_M:
                return new MixDateConverter(new DateType[]{DateType.YEAR, DateType.MONTH});
            case Y_M_D:
                return new MixDateConverter(new DateType[]{DateType.YEAR, DateType.MONTH, DateType.DAY});
            case Y_M_D_H:
                return new MixDateConverter(new DateType[]{DateType.YEAR, DateType.MONTH, DateType.DAY, DateType.HOUR});
            case Y_M_D_H_M:
                return new MixDateConverter(new DateType[]{DateType.YEAR, DateType.MONTH, DateType.DAY, DateType.HOUR, DateType.MINUTE});
            case Y_M_D_H_M_S:
                return new MixDateConverter(new DateType[]{DateType.YEAR, DateType.MONTH, DateType.DAY, DateType.HOUR, DateType.MINUTE, DateType.SECOND});
            default:
                return new ToStringConverter();
        }
    }
}
