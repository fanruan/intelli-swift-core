package com.finebi.cube.structure.property;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeLongReaderWrapper;
import com.finebi.cube.data.output.ICubeLongWriterWrapper;
import com.finebi.cube.location.ICubeResourceLocation;

/**
 * This class created on 2016/6/3.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BICubeLongProperty extends BICubeProperty<ICubeLongReaderWrapper, ICubeLongWriterWrapper> {
    public BICubeLongProperty(ICubeResourceLocation currentLocation, ICubeResourceDiscovery discovery) {
        super(currentLocation, discovery);
    }


    @Override
    protected void setReaderType(ICubeResourceLocation rowCountLocation) {
        rowCountLocation.setLongTypeWrapper();
    }

    @Override
    protected void setWriterType(ICubeResourceLocation rowCountLocation) {
        rowCountLocation.setLongTypeWrapper();
    }
}
