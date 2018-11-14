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
    public static final WriteType DEFAULT_WRITE_TYPE = WriteType.APPEND;

    private final IoType ioType;
    private final DataType dataType;
    private final WriteType writeType;

    public BuildConf(IoType ioType, DataType dataType) {
        this(ioType, dataType, DEFAULT_WRITE_TYPE);
    }

    public BuildConf(IoType ioType, DataType dataType, WriteType writeType) {
        this.ioType = ioType;
        this.dataType = dataType;
        this.writeType = writeType;
    }

    public IoType getIoType() {
        return ioType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public WriteType getWriteType() {
        return writeType;
    }

    @Override
    public String toString() {
        return "{" + ioType + ", " + dataType + ", " + writeType + "}";
    }
}