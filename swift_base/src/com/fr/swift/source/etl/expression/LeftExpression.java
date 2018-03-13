package com.fr.swift.source.etl.expression;

import com.finebi.conf.constant.BIConfConstants;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.etl.utils.DateUtils;

import java.text.ParseException;

/**
 * Created by Handsome on 2018/3/1 0001 15:40
 */
public class LeftExpression implements Expression {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(LeftExpression.class);

    private boolean isUnitLeft = false;
    private Object value;
    private Object tValue;

    public LeftExpression(boolean isUnitLeft, Object value) {
        this.isUnitLeft = isUnitLeft;
        this.value = value;
    }

    @Override
    public Object get(Segment segment, int row, int columnType) {
        return isUnitLeft ? getTransValue(columnType) : null;
    }

    private Object getTransValue(int columnType) {
        if(tValue == null) {
            try {
                if (columnType == BIConfConstants.CONF.COLUMN.DATE) {
                    tValue = DateUtils.parse((String) value).getTime();
                } else if(columnType == BIConfConstants.CONF.COLUMN.NUMBER) {
                    tValue = Double.valueOf((String) value);
                } else {
                    tValue = value;
                }
            } catch(ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return tValue;
    }


}
