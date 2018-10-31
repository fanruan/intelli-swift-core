package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.IntWriter;
import com.fr.swift.source.ColumnTypeConstants;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2017/11/11
 */
public class IntDictColumn extends BaseDictColumn<Integer, IntReader> {

    public IntDictColumn(IResourceLocation parent, Comparator<Integer> keyComparator) {
        super(parent, keyComparator);
    }

    @Override
    void initKeyReader() {
        if (keyReader != null) {
            return;
        }
        IResourceLocation keyLocation = parent.buildChildLocation(KEY);
        keyReader = DISCOVERY.getReader(keyLocation, new BuildConf(IoType.READ, DataType.INT));
    }

    @Override
    public Integer getValue(int index) {
        if (index < 1) {
            return null;
        }
        initKeyReader();
        return keyReader.get(index);
    }

    @Override
    public ColumnTypeConstants.ClassType getType() {
        return ColumnTypeConstants.ClassType.INTEGER;
    }

    @Override
    public Putter<Integer> putter() {
        return putter != null ? putter : (putter = new IntPutter());
    }

    class IntPutter extends BasePutter<IntWriter> {
        @Override
        void initKeyWriter() {
            if (keyWriter != null) {
                return;
            }
            IResourceLocation keyLocation = parent.buildChildLocation(KEY);
            keyWriter = DISCOVERY.getWriter(keyLocation, new BuildConf(IoType.WRITE, DataType.INT));
        }

        @Override
        public void putValue(int index, Integer val) {
            initKeyWriter();
            keyWriter.put(index, val == null ? IOConstant.NULL_INT : val);
        }
    }
}