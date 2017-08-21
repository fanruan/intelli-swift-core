package com.finebi.cube.location.meta;

import com.finebi.common.resource.ResourceItem;

/**
 * Created by wang on 2017/6/15.
 */
public interface BILocationInfo extends ResourceItem {
    String getCubeFolder();

    String getLogicFolder();

    String getChild();

    String getRealFolder();

    String getRealPath();
}
