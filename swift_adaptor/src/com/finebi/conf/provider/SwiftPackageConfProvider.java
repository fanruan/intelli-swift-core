package com.finebi.conf.provider;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.service.engine.pack.AbstractEnginePackageManager;
import com.fr.swift.driver.SwiftDriverRegister;

/**
 * This class created on 2018-1-23 13:58:01
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */

public class SwiftPackageConfProvider extends AbstractEnginePackageManager {

    public SwiftPackageConfProvider() {
        SwiftDriverRegister.register();
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }

}
