package com.fr.bi.module;

import com.finebi.cube.api.ICubeDataLoaderCreator;
import com.fr.bi.conf.provider.BIDataSourceManagerProvider;
import com.fr.bi.conf.provider.BISystemPackageConfigurationProvider;
import com.fr.stable.fun.Service;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
public interface BIModule {
    void start();

    String getModuleName();

    //模块公用管理员的配置
    boolean isAllAdmin();

    BIDataSourceManagerProvider getDataSourceManagerProvider();

    BISystemPackageConfigurationProvider getBusiPackManagerProvider();

    ICubeDataLoaderCreator getCubeDataLoaderCreator();

    public Service[] service4Register();
}