package com.fr.swift.cube.io.impl.fineio.output;

import com.fineio.FineIO;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.file.IOFile;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.DoubleWriter;

import java.net.URI;

/**
 * @author anchore
 */
public class DoubleFineIoWriter extends BaseFineIoWriter implements DoubleWriter {
    private IOFile<DoubleBuffer> ioFile;

    private DoubleFineIoWriter(URI uri, Connector connector, boolean isOverwrite) {
        if (isOverwrite) {
            ioFile = FineIO.createIOFile(connector, uri, FineIO.MODEL.WRITE_DOUBLE);
        } else {
            ioFile = FineIO.createIOFile(connector, uri, FineIO.MODEL.EDIT_DOUBLE);
        }
    }

    public static DoubleWriter build(IResourceLocation location, boolean isOverwrite) {
        return new DoubleFineIoWriter(
                location.getUri(),
                ConnectorManager.getInstance().getConnector(),
                isOverwrite
        );
    }

    @Override
    public void flush() {
        ioFile.close();
    }

    @Override
    public void release() {
        if (ioFile != null) {
            flush();
            ioFile = null;
        }
    }

    @Override
    public void put(long pos, double val) {
        FineIO.put(ioFile, pos, val);
    }
}