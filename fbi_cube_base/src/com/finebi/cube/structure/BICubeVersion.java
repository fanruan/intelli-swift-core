package com.finebi.cube.structure;

import com.finebi.cube.data.ICubePrimitiveResourceDiscovery;
import com.finebi.cube.data.input.primitive.ICubeIntegerReader;
import com.finebi.cube.data.output.primitive.ICubeIntegerWriter;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeVersion implements ICubeVersion {

    private ICubeIntegerReader versionReader;
    private ICubeIntegerWriter versionWriter;

    public BICubeVersion(ICubeResourceLocation location) {
        try {

            ICubeResourceLocation currentLocation = location.buildChildLocation("version.fbi");
            currentLocation.setIntegerType();
            currentLocation.setReaderSourceLocation();
            versionReader = (ICubeIntegerReader) BIFactoryHelper.getObject(ICubePrimitiveResourceDiscovery.class).getCubeReader(currentLocation);
            currentLocation.setWriterSourceLocation();
            versionWriter = (ICubeIntegerWriter) BIFactoryHelper.getObject(ICubePrimitiveResourceDiscovery.class).getCubeWriter(currentLocation);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    @Override
    public int getVersion() {
        try {
            return versionReader.getSpecificValue(0);
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public void addVersion(int version) {
        versionWriter.recordSpecificPositionValue(0, version);

    }
}
