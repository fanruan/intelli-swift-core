package com.fr.bi.stable.io.io.read;

import com.fr.bi.stable.constant.DateConstant;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.read.LongNIOReader;

import java.util.Date;

public class DateReadMappedList implements NIOReader<Long> {

    private LongNIOReader lml;

    private long rowCount;
    private long maxDate = 0;
    private long minDate = 0;

    public DateReadMappedList(LongNIOReader lml, long rowCount, long maxDate, long minDate) {
        this.rowCount = rowCount;
        this.lml = lml;
        this.maxDate = maxDate;
        this.minDate = minDate;
    }

    /**
     * 清除
     */
    @Override
    public void clear() {
    }

    @Override
    public Long get(long row) {
        return lml.get(row);
    }

    @Override
    public long getLastPos(long rowCount) {
        return rowCount;
    }


    public long getMaxDatelong() {
        return maxDate;
    }

    public long getMinDatelong() {
        return minDate;
    }

    public long getRowCount() {
        return rowCount;
    }

    public Date getDate(long row) {
        long v = lml.get(row);
        return v == DateConstant.DATEMAP.NULLDATE ? null : new Date(v);
    }
}