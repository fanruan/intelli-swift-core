package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.DoubleReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.DoubleWriter;
import com.fr.swift.source.ColumnTypeConstants;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2017/11/11
 */
public class DoubleDictColumn extends BaseDictColumn<Double, DoubleReader> {
    public DoubleDictColumn(IResourceLocation parent, Comparator<Double> keyComparator) {
        super(parent, keyComparator);
    }

    @Override
    void initKeyReader() {
        if (keyReader != null) {
            return;
        }
        IResourceLocation keyLocation = parent.buildChildLocation(KEY);
        keyReader = DISCOVERY.getReader(keyLocation, new BuildConf(IoType.READ, DataType.DOUBLE));
    }

    @Override
    public Double getValue(int index) {
        if (index < 1) {
            return null;
        }

        initKeyReader();
        return keyReader.get(index);
    }

    @Override
    public ColumnTypeConstants.ClassType getType() {
        return ColumnTypeConstants.ClassType.DOUBLE;
    }

    @Override
    public Putter<Double> putter() {
        return putter != null ? putter : (putter = new DoublePutter());
    }

    class DoublePutter extends BasePutter<DoubleWriter> {
        @Override
        void initKeyWriter() {
            if (keyWriter != null) {
                return;
            }
            IResourceLocation keyLocation = parent.buildChildLocation(KEY);
            keyWriter = DISCOVERY.getWriter(keyLocation, new BuildConf(IoType.WRITE, DataType.DOUBLE));
        }

        @Override
        public void putValue(int index, Double val) {
            initKeyWriter();
            keyWriter.put(index, val == null ? IOConstant.NULL_DOUBLE : val);
        }
    }
}
