package com.finebi.cube.data.disk;


import com.finebi.cube.data.ICubePrimitiveResourceDiscovery;
import com.finebi.cube.data.disk.reader.ReaderHandlerManager;
import com.finebi.cube.data.disk.writer.WriterHandlerManager;
import com.finebi.cube.data.disk.reader.primitive.BIPrimitiveNIOReaderManager;
import com.finebi.cube.data.disk.writer.primitive.BIPrimitiveNIOWriterManager;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;
import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.exception.BIBuildWriterException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.location.ICubeResourceLocation;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by wang on 2016/9/30.
 */
public class NIOResourceManager  implements ICubePrimitiveResourceDiscovery {
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private BIPrimitiveNIOWriterManager writerManager = null;
    private BIPrimitiveNIOReaderManager readerManager = null;
    private ReaderHandlerManager readerHandlerManager;
    private WriterHandlerManager writerHandlerManager;
    public NIOResourceManager(){
        writerManager = BIPrimitiveNIOWriterManager.getInstance();
        readerManager = BIPrimitiveNIOReaderManager.getInstance();
    }
    public ReadWriteLock getReadWriteLock(){
        return readWriteLock;
    }
    @Override
    public ICubePrimitiveReader getCubeReader(ICubeResourceLocation resourceLocation) throws IllegalCubeResourceLocationException, BIBuildReaderException {
        synchronized (this) {
            if (isNULL(readerHandlerManager)||(!isNULL(readerHandlerManager)&&(readerHandlerManager.isForceReleased()))) {
                ICubePrimitiveReader reader = readerManager.buildCubeReader(resourceLocation);
                readerHandlerManager = new ReaderHandlerManager(reader);
                reader.setHandlerReleaseHelper(readerHandlerManager);
            }
            return readerHandlerManager.queryHandler();
        }
    }

    @Override
    public ICubePrimitiveWriter getCubeWriter(ICubeResourceLocation resourceLocation) throws IllegalCubeResourceLocationException, BIBuildWriterException {
        synchronized (this) {
            if (isNULL(writerHandlerManager)||(!isNULL(writerHandlerManager)&&writerHandlerManager.isForceReleased())) {
                ICubePrimitiveWriter writer = writerManager.buildCubeWriter(resourceLocation);
                writerHandlerManager = new WriterHandlerManager(writer);
                writer.setHandlerReleaseHelper(writerHandlerManager);
            }
            return writerHandlerManager.queryHandler();
        }
    }

    private boolean isNULL(NIOHandlerManager nioHandler){
        return nioHandler == null;
    }

    public void forceRelease() {
        if (!isNULL(readerHandlerManager)) {
            readerHandlerManager.forceReleaseHandler();
        }
        if (!isNULL(writerHandlerManager)) {
            writerHandlerManager.forceReleaseHandler();
        }
    }
}
