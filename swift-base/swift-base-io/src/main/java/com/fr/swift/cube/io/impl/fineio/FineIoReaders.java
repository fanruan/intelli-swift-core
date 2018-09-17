package com.fr.swift.cube.io.impl.fineio;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.impl.fineio.input.BitMapFineIoReader;
import com.fr.swift.cube.io.impl.fineio.input.ByteArrayFineIoReader;
import com.fr.swift.cube.io.impl.fineio.input.ByteFineIoReader;
import com.fr.swift.cube.io.impl.fineio.input.DoubleFineIoReader;
import com.fr.swift.cube.io.impl.fineio.input.IntFineIoReader;
import com.fr.swift.cube.io.impl.fineio.input.LongArrayFineIoReader;
import com.fr.swift.cube.io.impl.fineio.input.LongFineIoReader;
import com.fr.swift.cube.io.impl.fineio.input.StringFineIoReader;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.util.Crasher;

import java.net.URI;

/**
 * @author anchore
 */
public final class FineIoReaders {
    public static Reader build(URI location, BuildConf conf) {
        if (IoType.READ == conf.getIoType()) {
            switch (conf.getDataType()) {
                case BYTE:
                    return ByteFineIoReader.build(location);
                case INT:
                    return IntFineIoReader.build(location);
                case LONG:
                    return LongFineIoReader.build(location);
                case DOUBLE:
                    return DoubleFineIoReader.build(location);
                case BYTE_ARRAY:
                    return ByteArrayFineIoReader.build(location);
                case STRING:
                    return StringFineIoReader.build(location);
                case BITMAP:
                    return BitMapFineIoReader.build(location);
                case LONG_ARRAY:
                    return LongArrayFineIoReader.build(location);
                default:
            }
        }
        return Crasher.crash(String.format("illegal cube build config: %s\nlocation: %s", conf, location));
    }
}