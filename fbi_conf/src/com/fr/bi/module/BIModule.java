package com.fr.bi.module;

import com.finebi.cube.api.ICubeDataLoaderCreator;
import com.finebi.cube.conf.BIAliasManagerProvider;
import com.finebi.cube.conf.BIDataSourceManagerProvider;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.stable.fun.Service;

import java.util.Collection;
import java.util.Locale;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
public interface BIModule {
    void start();

    String getModuleName();

    //模块公用管理员的配置
    boolean isAllAdmin();

    BIDataSourceManagerProvider getDataSourceManagerProvider();
    //TODO Connery Analysis 这种大模块间的就别继承了。改动代价远大于写几行代码

    BISystemPackageConfigurationProvider getBusiPackManagerProvider();

    BIAliasManagerProvider getAliasManagerProvider();

    ICubeDataLoaderCreator getCubeDataLoaderCreator();

    Service[] service4Register();

    void loadResources(Locale[] locale);

    Collection<BIPackageID> getAvailablePackID(long userId);
}