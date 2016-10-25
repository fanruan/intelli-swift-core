package com.finebi.cube.data.disk.writer;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.BICubeReleaseRecorder;
import com.finebi.cube.data.disk.NIOHandlerManager;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wang on 2016/9/30.
 * 应该只能有一个线程在操作写，
 */
public class WriterHandlerManager implements NIOHandlerManager<ICubePrimitiveWriter> {
    private ICubePrimitiveWriter writer = null;
    protected AtomicInteger countOfWriters = new AtomicInteger(0);
    protected volatile boolean isForceReleased = false;
    private ICubeResourceLocation resourceLocation = null;
    private CopyOnWriteArrayList<String> queryRecorder = new CopyOnWriteArrayList<String>();

    public WriterHandlerManager(ICubePrimitiveWriter writer) {
        this.writer = writer;
    }

    public WriterHandlerManager(ICubePrimitiveWriter writer, ICubeResourceLocation resourceLocation) {
        this.writer = writer;
        this.resourceLocation = resourceLocation;
    }

    @Override
    public ICubePrimitiveWriter queryHandler() {
        synchronized (this) {
            countOfWriters.getAndIncrement();
            BILoggerFactory.getLogger().debug("query writer " + resourceLocation.getAbsolutePath() + " " + countOfWriters.get());
            return writer;
        }
    }

    @Override
    public void releaseHandler(String handlerKey) {
        synchronized (this) {
            int currentCountOfWriters = -1;
            if (queryRecorder.contains(handlerKey)){
                queryRecorder.remove(handlerKey);
                countOfWriters.decrementAndGet();
            }else {
                BILoggerFactory.getLogger().debug(handlerKey+" writer has been released========== "+ resourceLocation.getAbsolutePath());
            }
            currentCountOfWriters = countOfWriters.get();
            if (currentCountOfWriters == 0) {
                try {
                    isForceReleased = true;
                    writer.releaseSource();
                    countOfWriters.set(0);
                    queryRecorder.clear();
                    BILoggerFactory.getLogger().debug("=0 release writer " + resourceLocation.getAbsolutePath() + " " + countOfWriters.get());
                } catch (Exception e) {
                    throw BINonValueUtils.beyondControl(e);
                } finally {
                    isForceReleased = false;
                }
            } else if (currentCountOfWriters < 0) {
                countOfWriters.set(0);
                BILoggerFactory.getLogger().debug("<0 release writer " + resourceLocation.getAbsolutePath() + " " + countOfWriters.get());
            }
        }
    }
    @Override
    public void registerHandlerKey(String handlerKey){
        queryRecorder.add(handlerKey);
    }
    @Override
    public void destroyHandler() {
        synchronized (this) {
            isForceReleased = true;
            writer.destoryResource();
            countOfWriters.set(0);
            queryRecorder.clear();
            BILoggerFactory.getLogger().debug("force release writer " + resourceLocation.getAbsolutePath() + " " + countOfWriters.get());
        }
    }

    @Override
    public boolean isHandlerEmpty() {
        return countOfWriters.get() == 0;
    }

    @Override
    public boolean isForceReleased() {
        return isForceReleased;
    }
}
