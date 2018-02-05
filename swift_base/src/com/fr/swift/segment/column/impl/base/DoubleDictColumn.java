package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.DoubleReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.DoubleWriter;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2017/11/11
 */
public class DoubleDictColumn extends BaseDictColumn<Double> {

    private DoubleWriter keyWriter;
    private DoubleReader keyReader;

    public DoubleDictColumn(IResourceLocation parent, Comparator<Double> keyComparator) {
        super(parent, keyComparator);
    }

    @Override
    void initKeyWriter() {
        if (keyWriter != null) {
            return;
        }
        IResourceLocation keyLocation = parent.buildChildLocation(KEY);
        keyWriter = (DoubleWriter) DISCOVERY.getWriter(keyLocation, new BuildConf(IoType.WRITE, DataType.DOUBLE));
    }

    @Override
    void initKeyReader() {
        if (keyReader != null) {
            return;
        }
        IResourceLocation keyLocation = parent.buildChildLocation(KEY);
        keyReader = (DoubleReader) DISCOVERY.getReader(keyLocation, new BuildConf(IoType.READ, DataType.DOUBLE));
    }

    @Override
    public void flush() {
        super.flush();
        if (keyWriter != null) {
            keyWriter.flush();
        }
    }

    @Override
    public void release() {
        super.release();
        if (keyWriter != null) {
            keyWriter.release();
            keyWriter = null;
        }
        if (keyReader != null) {
            keyReader.release();
            keyReader = null;
        }
    }

    @Override
    public Double getValue(int index) {
        initKeyReader();
        return keyReader.get(index);
    }

    @Override
    public void putValue(int index, Double val) {
        initKeyWriter();
        keyWriter.put(index, val);
    }
}
