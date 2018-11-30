package com.fr.swift.cube.io.impl.fineio.output;

import com.fineio.FineIO;
import com.fineio.FineIO.MODEL;
import com.fineio.io.ByteBuffer;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.output.ByteWriter;

import java.net.URI;

/**
 * @author anchore
 */
public class ByteFineIoWriter extends BaseFineIoWriter<ByteBuffer> implements ByteWriter {
    private ByteFineIoWriter(URI uri, Connector connector, boolean isOverwrite) {
        if (isOverwrite) {
            ioFile = FineIO.createIOFile(connector, uri, MODEL.WRITE_BYTE, true);
        } else {
            ioFile = FineIO.createIOFile(connector, uri, MODEL.APPEND_BYTE, true);
        }
    }

    public static ByteWriter build(URI location, boolean isOverwrite) {
        return new ByteFineIoWriter(
                location,
                ConnectorManager.getInstance().getConnector(),
                isOverwrite
        );
    }

    @Override
    public void put(long pos, byte val) {
        FineIO.put(ioFile, pos, val);
    }
}