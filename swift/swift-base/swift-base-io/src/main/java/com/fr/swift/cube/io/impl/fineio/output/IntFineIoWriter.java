package com.fr.swift.cube.io.impl.fineio.output;

import com.fineio.FineIO;
import com.fineio.FineIO.MODEL;
import com.fineio.io.IntBuffer;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.output.IntWriter;

import java.net.URI;

/**
 * @author anchore
 */
public class IntFineIoWriter extends BaseFineIoWriter<IntBuffer> implements IntWriter {
    private IntFineIoWriter(URI uri, Connector connector, boolean isOverwrite) {
        if (isOverwrite) {
            ioFile = FineIO.createIOFile(connector, uri, MODEL.WRITE_INT, true);
        } else {
            ioFile = FineIO.createIOFile(connector, uri, MODEL.APPEND_INT, true);
        }
    }

    public static IntWriter build(URI location, boolean isOverwrite) {
        return new IntFineIoWriter(
                location,
                ConnectorManager.getInstance().getConnector(),
                isOverwrite
        );
    }

    @Override
    public void put(long pos, int val) {
        FineIO.put(ioFile, pos, val);
    }
}