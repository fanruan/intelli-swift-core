package com.finebi.cube.structure.property;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeReader;
import com.finebi.cube.data.output.ICubeWriter;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/5/18.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BICubeProperty<R extends ICubeReader, W extends ICubeWriter> implements Release {

    protected W writer;
    protected R reader;

    protected ICubeResourceDiscovery discovery;
    protected ICubeResourceLocation currentLocation;


    public BICubeProperty(ICubeResourceLocation currentLocation, ICubeResourceDiscovery discovery) {
        this.currentLocation = currentLocation.copy();
        this.discovery = discovery;
    }

    protected abstract String getPropertyName();

    private void initialReader() throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(getPropertyName());
        setWriterType(rowCountLocation);
        rowCountLocation.setReaderSourceLocation();
        reader = (R) discovery.getCubeReader(rowCountLocation);
    }

    private void initialWriter() throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(getPropertyName());
        setReaderType(rowCountLocation);
        rowCountLocation.setWriterSourceLocation();
        writer = (W) discovery.getCubeWriter(rowCountLocation);
    }

    protected abstract void setReaderType(ICubeResourceLocation rowCountLocation);

    protected abstract void setWriterType(ICubeResourceLocation rowCountLocation);


    protected boolean isReaderAvailable() {
        return reader != null;
    }

    protected boolean isWriterAvailable() {
        return writer != null;
    }

    public R getReader() {
        try {
            if (!isReaderAvailable()) {
                initialReader();
            }
            return reader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public W getWriter() {
        try {
            if (!isWriterAvailable()) {
                initialWriter();
            }
            return writer;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    protected void resetWriter() {
        if (isWriterAvailable()) {
            writer.clear();
            writer = null;
        }
    }

    protected void resetReader() {
        if (isReaderAvailable()) {
            reader.clear();
            reader = null;
        }
    }

    @Override
    public void clear() {
        resetReader();
        resetWriter();
    }

    public void forceRelease() {
        if (isWriterAvailable()) {
            writer.forceRelease();
        }
        if (isReaderAvailable()) {
            reader.forceRelease();
        }
        clear();
    }
}
