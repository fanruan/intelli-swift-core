package com.fr.swift.utils;

import com.finebi.conf.internalimp.space.SpaceInfo;
import com.finebi.conf.internalimp.update.UpdateSpaceInfo;
import com.fr.swift.adaptor.space.SwiftSpaceManager;

/**
 * This class created on 2018/5/11
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class UpdateSpaceInfoUtil {

    public static UpdateSpaceInfo getUpdateSpaceInfo(SwiftSpaceManager spaceManager) throws Exception {
        UpdateSpaceInfo updateSpaceInfo = new UpdateSpaceInfo();
        SpaceInfo used = spaceManager.getAllOccupySpaceInfo();
        SpaceInfo total = spaceManager.getDiskSpaceInfo();
        SpaceInfo remain = spaceManager.getUsefulSpaceInfo();

        updateSpaceInfo.setTotal(total.getSpace());
        updateSpaceInfo.setUsed(used.getSpace());
        updateSpaceInfo.setRemaining(remain.getSpace());
        return updateSpaceInfo;
    }
}
