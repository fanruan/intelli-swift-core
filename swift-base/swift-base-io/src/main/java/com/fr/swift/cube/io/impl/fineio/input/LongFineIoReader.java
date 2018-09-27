package com.fr.swift.cube.io.impl.fineio.input;

import com.fineio.FineIO;
import com.fineio.io.LongBuffer;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.input.LongReader;

import java.net.URI;

/**
 * @author anchore
 */
public class LongFineIoReader extends BaseFineIoReader<LongBuffer> implements LongReader {
    private LongFineIoReader(URI uri, Connector connector) {
        this.ioFile = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_LONG);
    }

    public static LongReader build(URI location) {
        return new LongFineIoReader(
                location,
                ConnectorManager.getInstance().getConnector()
        );
    }

    @Override
    public long get(long pos) {
        return FineIO.getLong(ioFile, pos);
    }
}