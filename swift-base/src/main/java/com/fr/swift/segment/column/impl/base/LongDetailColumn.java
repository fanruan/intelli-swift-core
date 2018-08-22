package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.LongWriter;

/**
 * @author anchore
 * @date 2017/11/9
 */
public class LongDetailColumn extends BaseDetailColumn<Long, LongWriter, LongReader> {
    public LongDetailColumn(IResourceLocation parent) {
        super(parent);
    }

    @Override
    public double getDouble(int pos) {
        return getLong(pos);
    }

    @Override
    public long getLong(int pos) {
        initDetailReader();
        return detailReader.get(pos);
    }

    @Override
    public void put(int pos, Long val) {
        initDetailWriter();
        detailWriter.put(pos, val == null ? IOConstant.NULL_LONG : val);
    }

    @Override
    public Long get(int pos) {
        return getLong(pos);
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