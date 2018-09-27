package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.ObjectReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.ObjectWriter;

/**
 * @author anchore
 * @date 2018/3/18
 */
public class StringDetailColumn extends BaseDetailColumn<String, ObjectWriter<String>, ObjectReader<String>> {
    public StringDetailColumn(IResourceLocation parent) {
        super(parent);
    }

    private void initDetailWriter() {
        if (detailWriter == null) {
            detailWriter = DISCOVERY.getWriter(location, new BuildConf(IoType.WRITE, DataType.STRING));
        }
    }

    @Override
    protected void initDetailReader() {
        if (detailReader == null) {
            detailReader = DISCOVERY.getReader(location, new BuildConf(IoType.READ, DataType.STRING));
        }
    }

    @Override
    public void put(int pos, String val) {
        initDetailWriter();
        detailWriter.put(pos, val == null ? IOConstant.NULL_STRING : val);
    }

    @Override
    public String get(int pos) {
        initDetailReader();
        return detailReader.get(pos);
    }
}