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
public class LongDetailColumn extends BaseDetailColumn<Long> {
    private LongWriter detailWriter;
    private LongReader detailReader;

    public LongDetailColumn(IResourceLocation parent) {
        super(parent);
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

    private void initDetailReader() {
        if (detailReader == null) {
            detailReader = DISCOVERY.getReader(location, new BuildConf(IoType.READ, DataType.LONG));
        }
    }

    @Override
    public void flush() {
        if (detailWriter != null) {
            detailWriter.flush();
        }
    }

    @Override
    public void release() {
        if (detailWriter != null) {
            detailWriter.release();
            detailWriter = null;
        }
        if (detailReader != null) {
            detailReader.release();
            detailReader = null;
        }
    }
}