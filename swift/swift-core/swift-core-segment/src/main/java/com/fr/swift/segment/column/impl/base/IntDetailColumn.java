package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.IntWriter;

/**
 * @author anchore
 * @date 2017/11/7
 */
public class IntDetailColumn extends BaseDetailColumn<Integer, IntWriter, IntReader> {
    public IntDetailColumn(IResourceLocation parent) {
        super(parent);
    }

    @Override
    public long getLong(int pos) {
        return getInt(pos);
    }

    @Override
    public double getDouble(int pos) {
        return getInt(pos);
    }

    @Override
    public int getInt(int pos) {
        initDetailReader();
        return detailReader.get(pos);
    }

    @Override
    public void put(int pos, Integer val) {
        initDetailWriter();
        detailWriter.put(pos, val == null ? IOConstant.NULL_INT : val);
    }

    @Override
    public Integer get(int pos) {
        return getInt(pos);
    }

    private void initDetailWriter() {
        if (detailWriter == null) {
            detailWriter = DISCOVERY.getWriter(location, new BuildConf(IoType.WRITE, DataType.INT));
        }
    }

    @Override
    protected void initDetailReader() {
        if (detailReader == null) {
            detailReader = DISCOVERY.getReader(location, new BuildConf(IoType.READ, DataType.INT));
        }
    }
}