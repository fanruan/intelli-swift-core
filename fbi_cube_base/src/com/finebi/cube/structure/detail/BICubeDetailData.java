package com.finebi.cube.structure.detail;

import com.finebi.cube.CubeVersion;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeReader;
import com.finebi.cube.data.output.ICubeWriter;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.ICubeDetailDataService;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/3/11.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BICubeDetailData<T> implements ICubeDetailDataService<T> {
    protected ICubeWriter<T> cubeWriter;
    protected ICubeReader cubeReader;
    protected ICubeResourceLocation currentLocation;
    private ICubeResourceDiscovery discovery;

    public BICubeDetailData(ICubeResourceDiscovery discovery, ICubeResourceLocation superLocation) {
        try {
            this.discovery = discovery;
            currentLocation = superLocation.buildChildLocation("detail.fbi");
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }


    protected abstract ICubeResourceLocation setDetailType(boolean isIncrease);


    @Override
    public void addDetailDataValue(int rowNumber, T originalValue) {
        // 加多一层保障,防止其他像etl-union那样只能是double类型的数据
        getCubeWriter().recordSpecificValue(rowNumber, originalValue);
        //if(BICollectionUtils.isCubeNullKey(originalValue)){
        //    getCubeWriter().recordSpecificValue(rowNumber, getCubeNullValue());
        //}else{
        //}
    }

    @Override
    public void increaseAddDetailDataValue(int rowNumber, T originalValue) {
        getCubeIncreaseWriter().recordSpecificValue(rowNumber, originalValue);
    }

    protected boolean isCubeWriterAvailable() {
        return cubeWriter != null;
    }

    protected boolean isCubeReaderAvailable() {
        return cubeReader != null;
    }

    public ICubeWriter<T> getCubeWriter() {
        initCubeWriter();
        return cubeWriter;
    }

    public ICubeWriter<T> getCubeIncreaseWriter() {
        initCubeIncreaseWriter();
        return cubeWriter;
    }

    public void initCubeWriter() {
        if (!isCubeWriterAvailable()) {
            buildCubeWriter(false);
        }
    }

    public void initCubeIncreaseWriter() {
        if (!isCubeWriterAvailable()) {
            buildCubeWriter(true);
        }
    }

    public ICubeReader getCubeReader() {
        initCubeReader();
        return cubeReader;
    }

    public void initCubeReader() {
        if (!isCubeReaderAvailable()) {
            buildCubeReader();
        }
    }

    public void buildStructure() {
//        initCubeReader();
        initCubeWriter();
        forceReleaseWriter();
    }

    @Override
    public void forceReleaseWriter() {
        if (cubeWriter != null) {
            cubeWriter.forceRelease();
            cubeWriter = null;
        }
    }

    @Override
    public void forceReleaseReader() {
        if (cubeReader != null) {
            cubeReader.forceRelease();
            cubeReader = null;
        }
    }

    private void buildCubeReader() {
        try {
            currentLocation = setDetailType(false);
            ICubeResourceDiscovery resourceDiscovery = discovery;
            currentLocation.setReaderSourceLocation();
            cubeReader = resourceDiscovery.getCubeReader(currentLocation);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    private void buildCubeWriter(boolean isIncrease) {
        try {
            currentLocation = setDetailType(isIncrease);
            ICubeResourceDiscovery resourceDiscovery = discovery;
            currentLocation.setWriterSourceLocation();
            cubeWriter = resourceDiscovery.getCubeWriter(currentLocation);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    protected void resetCubeWriter() {
        if (isCubeWriterAvailable()) {
            cubeWriter.clear();
            cubeWriter = null;
        }
    }

    protected void resetCubeReader() {
        if (isCubeReaderAvailable()) {
            cubeReader.clear();
        }
    }

    @Override
    public void clear() {
        resetCubeReader();
        resetCubeWriter();
    }

    @Override
    public int getClassType() {
        return 0;
    }

    @Override
    public CubeVersion getVersion() {
        return null;
    }

    @Override
    public void recordVersion(CubeVersion version) {

    }
}
