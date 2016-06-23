package com.fr.bi.test.module;

import com.finebi.cube.api.ICubeDataLoaderCreator;
import com.finebi.cube.conf.BIAliasManagerProvider;
import com.finebi.cube.conf.BIDataSourceManagerProvider;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.bi.module.AbstractModule;
import com.fr.bi.test.DemoService;
import com.fr.stable.DebugAssistant;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.fun.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

/**
 * Created by daniel on 2016/6/23.
 */
public class BIDebugModule extends AbstractModule {
    @Override
    public void start() {
        StableFactory.registerMarkedObject(DebugAssistant.class.getName(), new DebugAssistant() {
            @Override
            public boolean requireDebug() {
                return true;
            }
        });
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
        return new Service[] {
                new DemoService()
        };
    }

    @Override
    public void loadResources(Locale[] locale) {

    }

    @Override
    public Collection<BIPackageID> getAvailablePackID(long userId) {
        return new HashSet<BIPackageID>();
    }
}
