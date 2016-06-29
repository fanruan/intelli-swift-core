package com.finebi.cube.structure.property;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeIntegerReaderWrapper;
import com.finebi.cube.data.output.ICubeIntegerWriterWrapper;
import com.finebi.cube.location.ICubeResourceLocation;

/**
 * Created by 小灰灰 on 2016/6/28.
 */
public abstract class BICubeIntegerProperty extends BICubeProperty<ICubeIntegerReaderWrapper, ICubeIntegerWriterWrapper> {
    public BICubeIntegerProperty(ICubeResourceLocation currentLocation, ICubeResourceDiscovery discovery) {
        super(currentLocation, discovery);
    }


    @Override
    protected void setReaderType(ICubeResourceLocation rowCountLocation) {
        rowCountLocation.setIntegerTypeWrapper();
    }

    @Override
    protected void setWriterType(ICubeResourceLocation rowCountLocation) {
        rowCountLocation.setIntegerTypeWrapper();
    }
}
