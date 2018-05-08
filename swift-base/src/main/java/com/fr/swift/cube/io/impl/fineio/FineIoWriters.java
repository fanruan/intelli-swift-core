package com.fr.swift.cube.io.impl.fineio;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.Types.WriteType;
import com.fr.swift.cube.io.impl.fineio.output.BitMapFineIoWriter;
import com.fr.swift.cube.io.impl.fineio.output.ByteArrayFineIoWriter;
import com.fr.swift.cube.io.impl.fineio.output.ByteFineIoWriter;
import com.fr.swift.cube.io.impl.fineio.output.DoubleFineIoWriter;
import com.fr.swift.cube.io.impl.fineio.output.IntFineIoWriter;
import com.fr.swift.cube.io.impl.fineio.output.LongArrayFineIoWriter;
import com.fr.swift.cube.io.impl.fineio.output.LongFineIoWriter;
import com.fr.swift.cube.io.impl.fineio.output.StringFineIoWriter;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.Writer;
import com.fr.swift.util.Crasher;

/**
 * @author anchore
 */
public final class FineIoWriters {
    public static Writer build(IResourceLocation location, BuildConf conf) {
        if (IoType.WRITE == conf.ioType && StoreType.FINE_IO == location.getStoreType()) {
            boolean isOverwrite = WriteType.OVERWRITE == conf.writeType;
            switch (conf.dataType) {
                case BYTE:
                    return ByteFineIoWriter.build(location, isOverwrite);
                case INT:
                    return IntFineIoWriter.build(location, isOverwrite);
                case LONG:
                    return LongFineIoWriter.build(location, isOverwrite);
                case DOUBLE:
                    return DoubleFineIoWriter.build(location, isOverwrite);
                case BYTE_ARRAY:
                    return ByteArrayFineIoWriter.build(location, isOverwrite);
                case STRING:
                    return StringFineIoWriter.build(location, isOverwrite);
                case BITMAP:
                    return BitMapFineIoWriter.build(location, isOverwrite);
                case LONG_ARRAY:
                    return LongArrayFineIoWriter.build(location, isOverwrite);
                default:
            }
        }
        return Crasher.crash(String.format("illegal cube build conf: %s\nlocation: %s", conf, location));
    }
}