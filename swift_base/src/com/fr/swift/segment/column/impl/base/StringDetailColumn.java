package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.StringReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.StringWriter;

/**
 * @author anchore
 * @date 2018/3/18
 */
public class StringDetailColumn extends BaseDetailColumn<String> {
    private StringWriter detailWriter;
    private StringReader detailReader;

    public StringDetailColumn(IResourceLocation parent) {
        super(parent);
    }

    private void initDetailWriter() {
        if (detailWriter == null) {
            detailWriter = DISCOVERY.getWriter(location, new BuildConf(IoType.WRITE, DataType.STRING));
        }
    }

    private void initDetailReader() {
        if (detailReader == null) {
            detailReader = DISCOVERY.getReader(location, new BuildConf(IoType.READ, DataType.STRING));
        }
    }

    @Override
    public void put(int pos, String val) {
        initDetailWriter();
        detailWriter.put(pos, val);
    }

    @Override
    public String get(int pos) {
        initDetailReader();
        return detailReader.get(pos);
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
        }
        if (detailReader != null) {
            detailReader.release();
        }
    }
}