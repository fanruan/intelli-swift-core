package com.fr.swift.cube.io;

import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Types.WriteType;

/**
 * @author anchore
 * @date 2017/11/24
 */
public class BuildConf {
    /**
     * 默认覆写
     */
    public static final WriteType DEFAULT_WRITE_TYPE = WriteType.OVERWRITE;

    public final IoType ioType;
    public final DataType dataType;
    public final WriteType writeType;

    public BuildConf(IoType ioType, DataType dataType) {
        this(ioType, dataType, DEFAULT_WRITE_TYPE);
    }

    public BuildConf(IoType ioType, DataType dataType, WriteType writeType) {
        this.ioType = ioType;
        this.dataType = dataType;
        this.writeType = writeType;
    }

    @Override
    public String toString() {
        return "{" + ioType + ", " + dataType + ", " + writeType + "}";
    }
}