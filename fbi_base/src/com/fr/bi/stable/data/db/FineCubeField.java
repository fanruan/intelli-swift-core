package com.fr.bi.stable.data.db;

import com.finebi.common.resource.ResourceItem;

/**
 * Created by Roy on 2017/6/16.
 */
public interface FineCubeField extends ICubeFieldSource, ResourceItem {
    String getFieldUniqueName();

}
