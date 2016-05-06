package com.finebi.cube.structure;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeIntegerReaderWrapper;
import com.finebi.cube.data.output.ICubeIntegerWriterWrapper;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.net.URISyntaxException;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeVersion implements ICubeVersion {

    private ICubeIntegerReaderWrapper versionReader;
    private ICubeIntegerWriterWrapper versionWriter;
    private ICubeResourceLocation currentLocation;
    private ICubeResourceDiscovery resourceDiscovery;
    private static String CUBE_VERSION = "version.fbi";

    public BICubeVersion(ICubeResourceDiscovery discovery, ICubeResourceLocation location) {
        try {
            this.currentLocation = location.buildChildLocation(CUBE_VERSION);
            this.resourceDiscovery = discovery;
        } catch (URISyntaxException e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    @Override
    public int getVersion() {
        try {
            return getVersionReader().getSpecificValue(0);
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return 0;
    }

    private void initVersionReader() {
        try {
            currentLocation.setIntegerType();
            currentLocation.setReaderSourceLocation();
            versionReader = (ICubeIntegerReaderWrapper) resourceDiscovery.getCubeReader(currentLocation);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    private void initVersionWriter() {
        try {
            currentLocation.setIntegerType();
            currentLocation.setWriterSourceLocation();
            versionWriter = (ICubeIntegerWriterWrapper) resourceDiscovery.getCubeWriter(currentLocation);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    public ICubeIntegerReaderWrapper getVersionReader() {
        if (!isVersionReaderAvailable()) {
            initVersionReader();
        }
        return versionReader;
    }

    public ICubeIntegerWriterWrapper getVersionWriter() {
        if (!isVersionWriterAvailable()) {
            initVersionWriter();
        }
        return versionWriter;
    }

    @Override
    public void addVersion(int version) {
        getVersionWriter().recordSpecificValue(0, version);
    }

    protected boolean isVersionReaderAvailable() {
        return versionReader != null;
    }

    protected void resetVersionReader() {
        if (isVersionReaderAvailable()) {
            versionReader.clear();
            versionReader = null;
        }
    }

    protected boolean isVersionWriterAvailable() {
        return versionWriter != null;
    }

    protected void resetVersionWriter() {
        if (isVersionWriterAvailable()) {
            versionWriter.clear();
            versionWriter = null;
        }
    }

    @Override
    public void clear() {
        resetVersionReader();
        resetVersionWriter();
    }
}
