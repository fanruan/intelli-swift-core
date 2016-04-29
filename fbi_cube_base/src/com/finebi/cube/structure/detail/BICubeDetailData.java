package com.finebi.cube.structure.detail;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeReader;
import com.finebi.cube.data.output.ICubeWriter;
import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.exception.BIBuildWriterException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.ICubeDetailDataService;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/3/11.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BICubeDetailData<T> implements ICubeDetailDataService<T> {
    protected ICubeWriter<T> cubeWriter;
    protected ICubeReader<T> cubeReader;
    protected ICubeResourceLocation currentLocation;

    public BICubeDetailData(ICubeResourceLocation superLocation) {
        try {
            currentLocation = superLocation.buildChildLocation("detail.fbi");
            initial();
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }


    protected void initial() throws IllegalCubeResourceLocationException, BIBuildWriterException, BIBuildReaderException {
        currentLocation.setWriterSourceLocation();
        currentLocation = setDetailType();
        ICubeResourceDiscovery resourceDiscovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
        cubeWriter = resourceDiscovery.getCubeWriter(currentLocation);
        currentLocation.setReaderSourceLocation();
        cubeReader = resourceDiscovery.getCubeReader(currentLocation);
    }

    protected abstract ICubeResourceLocation setDetailType();

    @Override
    public void addDetailDataValue(int rowNumber, T originalValue) {
        cubeWriter.recordSpecificValue(rowNumber, originalValue);
    }

    @Override
    public void releaseDetailDataWriter() {
        if (cubeWriter != null) {
            cubeWriter.clear();
        }
    }

    @Override
    public T getOriginalValueByRow(int rowNumber) {
        try {
            return cubeReader.getSpecificValue(rowNumber);
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;

    }

    @Override
    public void clear() {
        cubeWriter.clear();
        cubeReader.clear();
    }
}
