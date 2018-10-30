package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.LongWriter;

import java.util.Date;

/**
 * @author anchore
 * @date 2018/10/24
 */
public class DateDetailColumn extends BaseDetailColumn<Date, LongWriter, LongReader> {

    public DateDetailColumn(IResourceLocation parent) {
        super(parent);
    }

    @Override
    public void put(int pos, Date val) {
        initDetailWriter();
        detailWriter.put(pos, val == null ? IOConstant.NULL_LONG : val.getTime());
    }

    @Override
    public Date get(int pos) {
        initDetailReader();
        return new Date(detailReader.get(pos));
    }

    private void initDetailWriter() {
        if (detailWriter == null) {
            detailWriter = DISCOVERY.getWriter(location, new BuildConf(IoType.WRITE, DataType.LONG));
        }
    }

    @Override
    protected void initDetailReader() {
        if (detailReader == null) {
            detailReader = DISCOVERY.getReader(location, new BuildConf(IoType.READ, DataType.LONG));
        }
    }
}