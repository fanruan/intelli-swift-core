package com.fr.swift.cube.io.impl.fineio.input;

import com.fineio.accessor.FineIOAccessor;
import com.fineio.accessor.buffer.LongBuf;
import com.fineio.accessor.file.IReadFile;
import com.fineio.accessor.impl.BaseModel;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.input.LongReader;

import java.net.URI;

/**
 * @author anchore
 */
public class LongFineIoReader extends BaseFineIoReader<LongBuf> implements LongReader {
    private LongFineIoReader(URI uri, Connector connector) {
        readFile = (IReadFile<LongBuf>) FineIOAccessor.INSTANCE.createFile(connector, uri, BaseModel.ofLong().asRead());
    }

    public static LongReader build(URI location) {
        return new LongFineIoReader(
                location,
                ConnectorManager.getInstance().getConnector()
        );
    }

    @Override
    public long get(long pos) {
        return FineIOAccessor.INSTANCE.getLong(readFile, pos);
    }
}