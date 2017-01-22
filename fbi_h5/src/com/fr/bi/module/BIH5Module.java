package com.fr.bi.module;

import com.finebi.cube.api.ICubeDataLoaderCreator;
import com.finebi.cube.conf.BIAliasManagerProvider;
import com.finebi.cube.conf.BIDataSourceManagerProvider;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.bi.h5.services.Service4BIH5;
import com.fr.bi.h5.resource.ResourceConstants;
import com.fr.bi.h5.resource.ResourceHelper;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.fun.Service;

import java.util.Collection;
import java.util.Locale;

/**
 * Created by Root on 2016/10/21.
 */
public class BIH5Module extends AbstractModule {
    @Override
    public void start() {
        registerResources();
    }

    @Override
    public BIDataSourceManagerProvider getDataSourceManagerProvider() {
        return null;
    }

    @Override
    public BISystemPackageConfigurationProvider getBusiPackManagerProvider() {
        return null;
    }

    @Override
    public BIAliasManagerProvider getAliasManagerProvider() {
        return null;
    }

    @Override
    public ICubeDataLoaderCreator getCubeDataLoaderCreator() {
        return null;
    }

    @Override
    public Service[] service4Register() {
        return new Service[]{
                new Service4BIH5()
        };
    }

    private void registerResources() {
        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_MOBILE_JS, ResourceHelper.getMobileJs());
    }

    @Override
    public void loadResources(Locale[] locale) {
        com.fr.web.ResourceHelper.forceInitJSCache(ResourceConstants.DEFAULT_MOBILE_JS);
    }

    @Override
    public Collection<BIPackageID> getAvailablePackID(long userId) {
        return null;
    }

    @Override
    public void clearAnalysisETLCache(long userId) {

    }
}
