package com.finebi.cube.structure;

import com.finebi.cube.data.ICubePrimitiveResourceDiscovery;
import com.finebi.cube.data.input.primitive.ICubeIntegerReader;
import com.finebi.cube.data.output.primitive.ICubeIntegerWriter;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.common.factory.BIFactoryHelper;
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

    private ICubeIntegerReader versionReader;
    private ICubeIntegerWriter versionWriter;
    private ICubeResourceLocation currentLocation;
    private static String CUBE_VERSION = "version.fbi";

    public BICubeVersion(ICubeResourceLocation location) {
        try {
            this.currentLocation = location.buildChildLocation(CUBE_VERSION);
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
            versionReader = (ICubeIntegerReader) BIFactoryHelper.getObject(ICubePrimitiveResourceDiscovery.class).getCubeReader(currentLocation);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    private void initVersionWriter() {
        try {
            currentLocation.setIntegerType();
            currentLocation.setWriterSourceLocation();
            versionWriter = (ICubeIntegerWriter) BIFactoryHelper.getObject(ICubePrimitiveResourceDiscovery.class).getCubeWriter(currentLocation);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    public ICubeIntegerReader getVersionReader() {
        if (!isVersionReaderAvailable()) {
            initVersionReader();
        }
        return versionReader;
    }

    public ICubeIntegerWriter getVersionWriter() {
        if (!isVersionWriterAvailable()) {
            initVersionWriter();
        }
        return versionWriter;
    }

    @Override
    public void addVersion(int version) {
        getVersionWriter().recordSpecificPositionValue(0, version);
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
