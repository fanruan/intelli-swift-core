package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.LongWriter;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2017/11/11
 */
public class LongDictColumn extends BaseDictColumn<Long> {
    private LongWriter keyWriter;
    private LongReader keyReader;

    public LongDictColumn(IResourceLocation parent, Comparator<Long> keyComparator) {
        super(parent, keyComparator);
    }

    private void initKeyWriter() {
        if (keyWriter != null) {
            return;
        }
        IResourceLocation keyLocation = parent.buildChildLocation(KEY);
        keyWriter = DISCOVERY.getWriter(keyLocation, new BuildConf(IoType.WRITE, DataType.LONG));
    }

    private void initKeyReader() {
        if (keyReader != null) {
            return;
        }
        IResourceLocation keyLocation = parent.buildChildLocation(KEY);
        keyReader = DISCOVERY.getReader(keyLocation, new BuildConf(IoType.READ, DataType.LONG));
    }

    @Override
    public Long getValue(int index) {
        if (index < 1) {
            return null;
        }
        initKeyReader();
        return keyReader.get(index);
    }

    @Override
    public Long convertValue(Object value) {
        return ((Number) value).longValue();
    }

    @Override
    public void putValue(int index, Long val) {
        initKeyWriter();
        keyWriter.put(index, val);
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

}
