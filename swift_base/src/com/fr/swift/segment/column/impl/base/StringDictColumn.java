package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.StringReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.StringWriter;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2017/11/7
 */
public class StringDictColumn extends BaseDictColumn<String> {

    private StringWriter keyWriter;
    private StringReader keyReader;

    public StringDictColumn(IResourceLocation parent, Comparator<String> keyComparator) {
        super(parent, keyComparator);
    }

    @Override
    void initKeyWriter() {
        if (keyWriter != null) {
            return;
        }
        IResourceLocation keyLocation = parent.buildChildLocation(KEY);
        keyWriter = (StringWriter) DISCOVERY.getWriter(keyLocation, new BuildConf(IoType.WRITE, DataType.STRING));
    }

    @Override
    void initKeyReader() {
        if (keyReader != null) {
            return;
        }
        IResourceLocation keyLocation = parent.buildChildLocation(KEY);
        keyReader = (StringReader) DISCOVERY.getReader(keyLocation, new BuildConf(IoType.READ, DataType.STRING));
    }

    @Override
    public String getValue(int index) {
        initKeyReader();
        return keyReader.get(index);
    }

    @Override
    public void putValue(int index, String val) {
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
