package com.fr.swift.cube.io.impl.fineio.output;

import com.fineio.FineIO;
import com.fineio.io.ByteBuffer;
import com.fineio.io.file.IOFile;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.ByteWriter;

import java.net.URI;

/**
 * @author anchore
 */
public class ByteFineIoWriter extends BaseFineIoWriter implements ByteWriter {
    private IOFile<ByteBuffer> ioFile;

    private ByteFineIoWriter(URI uri, Connector connector, boolean isOverwrite) {
        if (isOverwrite) {
            ioFile = FineIO.createIOFile(connector, uri, FineIO.MODEL.WRITE_BYTE);
        } else {
            ioFile = FineIO.createIOFile(connector, uri, FineIO.MODEL.EDIT_BYTE);
        }
    }

    public static ByteWriter build(IResourceLocation location, boolean isOverwrite) {
        return new ByteFineIoWriter(
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
    public void put(long pos, byte val) {
        FineIO.put(ioFile, pos, val);
    }
}