package com.finebi.cube.conf.field;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/5/26.
 *
 * @author Connery
 * @since 4.0
 */
public class BusinessFieldHelper {
    public static BusinessField getBusinessFieldSource(BIFieldID fieldID) {
        BINonValueUtils.checkNull(fieldID);
        BINonValueUtils.checkNull(fieldID.getIdentity());
        try {
            return BICubeConfigureCenter.getDataSourceManager().getBusinessField(fieldID);
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }
}
