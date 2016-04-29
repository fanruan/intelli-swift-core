package com.finebi.cube.structure;

import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

/**
 * 通过ID定位Cube的基本部分。
 * This class created on 2016/3/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeComponentID implements ICubeComponentID {
    String componentID;

    public BICubeComponentID(String componentID) {
        BINonValueUtils.checkNull(componentID);
        this.componentID = componentID;
    }

    @Override
    public String getComponentID() {
        return componentID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BICubeComponentID)) {
            return false;
        }

        BICubeComponentID that = (BICubeComponentID) o;

        return ComparatorUtils.equals(componentID, (that.componentID));

    }

    @Override
    public int hashCode() {
        return componentID.hashCode();
    }
}
