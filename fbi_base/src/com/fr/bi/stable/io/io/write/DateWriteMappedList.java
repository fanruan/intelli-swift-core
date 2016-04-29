package com.fr.bi.stable.io.io.write;

import com.fr.bi.stable.constant.DateConstant;
import com.fr.bi.stable.io.io.ListWriter;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.write.LongNIOWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DateWriteMappedList implements NIOWriter<Long> {

    private LongNIOWriter lml;

    private String path;
    private transient String corrPath;
    private long maxDate = 0;
    private long minDate = 0;
    private boolean firstValue = true;

    public DateWriteMappedList(String path, String corrPath) {
        this.path = path;
        this.corrPath = corrPath;
        lml = new LongNIOWriter(new File(path));
    }

    @Override
    public void add(long row, Long value) {
        if (value == null) {
            lml.add(row, new Long(DateConstant.DATEMAP.NULLDATE));

        } else {
            if (firstValue) {
                firstValue = false;
                maxDate = value;
                minDate = value;
            } else {
                maxDate = Math.max(maxDate, value);
                minDate = Math.min(minDate, value);
            }
            lml.add(row, value);
        }
    }

    private void writeOtherValue() {
        File f = new File(corrPath);
        List<String> v = new ArrayList<String>();
        v.add(String.valueOf(maxDate));
        v.add(String.valueOf(minDate));
        ListWriter.writeValueListToFile(v, f);
    }

    @Override
    public void clear() {
        if (lml != null) {
            lml.clear();
            lml = null;
        }
    }

    public void checkMValue(long max, long min) {
        maxDate = Math.max(maxDate, max);
        minDate = Math.min(minDate, min);
    }

    @Override
    public void save() {
        writeOtherValue();
    }

    @Override
    public void setPos(long pos) {

    }

    public File getOtherValueBaseFile() {
        return new File(corrPath);
    }

    public void setMaxMin(long min, long max) {
        this.minDate = min;
        this.maxDate = max;
    }
}