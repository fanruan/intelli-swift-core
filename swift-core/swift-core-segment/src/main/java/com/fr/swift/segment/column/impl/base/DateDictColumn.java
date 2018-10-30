package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.LongWriter;
import com.fr.swift.source.ColumnTypeConstants.ClassType;

import java.util.Comparator;
import java.util.Date;

/**
 * @author anchore
 * @date 2018/10/24
 */
public class DateDictColumn extends BaseDictColumn<Date, LongReader> {

    public DateDictColumn(IResourceLocation parent, Comparator<Date> keyComparator) {
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
    public Date getValue(int index) {
        initKeyReader();
        return new Date(keyReader.get(index));
    }

    @Override
    public ClassType getType() {
        return ClassType.DATE;
    }

    @Override
    public Putter<Date> putter() {
        return putter != null ? putter : (putter = new DatePutter());
    }

    class DatePutter extends BasePutter<LongWriter> {
        @Override
        void initKeyWriter() {
            if (keyWriter != null) {
                return;
            }
            IResourceLocation keyLocation = parent.buildChildLocation(KEY);
            keyWriter = DISCOVERY.getWriter(keyLocation, new BuildConf(IoType.WRITE, DataType.LONG));
        }

        @Override
        public void putValue(int index, Date val) {
            initKeyWriter();
            keyWriter.put(index, val == null ? IOConstant.NULL_LONG : val.getTime());
        }
    }
}