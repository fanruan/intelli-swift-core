package com.fr.swift.segment.column.impl.base;

import com.fr.stable.StringUtils;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.ObjectReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.ObjectWriter;
import com.fr.swift.source.ColumnTypeConstants;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2017/11/7
 */
public class StringDictColumn extends BaseDictColumn<String, ObjectReader<String>> {
    public StringDictColumn(IResourceLocation parent, Comparator<String> keyComparator) {
        super(parent, keyComparator);
    }

    private void initKeyReader() {
        if (keyReader != null) {
            return;
        }
        IResourceLocation keyLocation = parent.buildChildLocation(KEY);
        keyReader = DISCOVERY.getReader(keyLocation, new BuildConf(IoType.READ, DataType.STRING));
    }

    @Override
    public String getValue(int index) {
        if (index < 1) {
            return null;
        }
        initKeyReader();
        return keyReader.get(index);
    }

    @Override
    public int getIndex(Object value) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return 0;
        }
        return super.getIndex(value);
    }

    @Override
    public ColumnTypeConstants.ClassType getType() {
        return ColumnTypeConstants.ClassType.STRING;
    }

    @Override
    public Putter<String> putter() {
        return putter != null ? putter : (putter = new StringPutter());
    }

    class StringPutter extends BasePutter<ObjectWriter<String>> {
        private void initKeyWriter() {
            if (keyWriter != null) {
                return;
            }
            IResourceLocation keyLocation = parent.buildChildLocation(KEY);
            keyWriter = DISCOVERY.getWriter(keyLocation, new BuildConf(IoType.WRITE, DataType.STRING));
        }

        @Override
        public void putValue(int index, String val) {
            initKeyWriter();
            keyWriter.put(index, val == null ? IOConstant.NULL_STRING : val);
        }
    }

}
