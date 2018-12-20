package com.fr.swift.cube.io.impl.fineio.output;

import com.fineio.FineIO;
import com.fineio.FineIO.MODEL;
import com.fineio.io.DoubleBuffer;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.output.DoubleWriter;

import java.net.URI;

/**
 * @author anchore
 */
public class DoubleFineIoWriter extends BaseFineIoWriter<DoubleBuffer> implements DoubleWriter {
    private DoubleFineIoWriter(URI uri, Connector connector, boolean isOverwrite) {
        if (isOverwrite) {
            ioFile = FineIO.createIOFile(connector, uri, MODEL.WRITE_DOUBLE, true);
        } else {
            ioFile = FineIO.createIOFile(connector, uri, MODEL.APPEND_DOUBLE, true);
        }
    }

    public static DoubleWriter build(URI location, boolean isOverwrite) {
        return new DoubleFineIoWriter(
                location,
                ConnectorManager.getInstance().getConnector(),
                isOverwrite
        );
    }

    @Override
    public void put(long pos, double val) {
        FineIO.put(ioFile, pos, val);
    }
}