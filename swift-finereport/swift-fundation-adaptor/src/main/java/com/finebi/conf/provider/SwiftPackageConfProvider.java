package com.finebi.conf.provider;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.service.engine.pack.AbstractEnginePackageManager;
import com.finebi.common.structure.config.pack.PackageInfo;
import com.finebi.conf.exception.FinePackageAbsentException;
import com.fr.swift.conf.dashboard.DashboardPackageTableService;
import com.fr.swift.conf.dashboard.store.DashboardConfManager;
import com.fr.swift.conf.updateInfo.TableUpdateInfoConfigService;

import java.util.Arrays;

/**
 * This class created on 2018-1-23 13:58:01
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */

public class SwiftPackageConfProvider extends AbstractEnginePackageManager {

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }

    @Override
    protected PackageInfo getPackageInfoById(String packageId) {
        PackageInfo info = super.getPackageInfoById(packageId);
        if (null == info) {
            info = DashboardConfManager.getManager().getPackageSession().getPackageById(packageId);
        }
        return info;
    }

    @Override
    public boolean removePackage(String packageId) throws FinePackageAbsentException {
        TableUpdateInfoConfigService.getService().removePackageInfo(Arrays.asList(packageId));
        return super.removePackage(packageId);
    }
}
