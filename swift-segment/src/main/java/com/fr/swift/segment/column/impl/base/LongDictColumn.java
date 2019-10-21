package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.LongWriter;
import com.fr.swift.source.ColumnTypeConstants;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2017/11/11
 */
public class LongDictColumn extends BaseDictColumn<Long, LongReader> {

    public LongDictColumn(IResourceLocation parent, Comparator<Long> keyComparator) {
        super(parent, keyComparator);
    }

    @Override
    void initKeyReader() {
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
    public ColumnTypeConstants.ClassType getType() {
        return ColumnTypeConstants.ClassType.LONG;
    }

    @Override
    public Putter<Long> putter() {
        return putter != null ? putter : (putter = new LongPutter());
    }

    class LongPutter extends BasePutter<LongWriter> {
        @Override
        void initKeyWriter() {
            if (keyWriter != null) {
                return;
            }
            IResourceLocation keyLocation = parent.buildChildLocation(KEY);
            keyWriter = DISCOVERY.getWriter(keyLocation, new BuildConf(IoType.WRITE, DataType.LONG));
        }

        @Override
        public void putValue(int index, Long val) {
            initKeyWriter();
            keyWriter.put(index, val == null ? IOConstant.NULL_LONG : val);
        }
    }
}
