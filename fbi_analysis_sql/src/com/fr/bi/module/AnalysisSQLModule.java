package com.fr.bi.module;

import com.finebi.cube.api.ICubeDataLoaderCreator;
import com.finebi.cube.conf.BIAliasManagerProvider;
import com.finebi.cube.conf.BIDataSourceManagerProvider;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.bi.cluster.ClusterAdapter;
import com.fr.bi.cluster.utils.ClusterEnv;
import com.fr.bi.field.filtervalue.BIFilterValueMap;
import com.fr.bi.resource.ResourceConstants;
import com.fr.bi.sql.analysis.Constants;
import com.fr.bi.sql.analysis.manager.*;
import com.fr.bi.sql.analysis.report.widget.field.filtervalue.number.NumberBottomNFilter;
import com.fr.bi.sql.analysis.report.widget.field.filtervalue.number.NumberLargeOrEqualsCLFilter;
import com.fr.bi.sql.analysis.report.widget.field.filtervalue.number.NumberSmallOrEqualsCLFilter;
import com.fr.bi.sql.analysis.report.widget.field.filtervalue.number.NumberTopNFilter;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.web.service.Service4AnalysisSQL;
import com.fr.cluster.rpc.RPC;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.fun.Service;
import com.fr.web.ResourceHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Created by 小灰灰 on 2016/7/8.
 */
public class AnalysisSQLModule extends AbstractModule{
    @Override
    public void start() {
        registerManager();
        registerFilter();
        registerResources();
    }

    /**
     *
     */
    private void registerResources() {
        StableFactory.registerJavaScriptFiles(SQLResourcesHelper.DEFAULT_JS, SQLResourcesHelper.getDefaultJs());
        StableFactory.registerStyleFiles(SQLResourcesHelper.DEFAULT_CSS, SQLResourcesHelper.getDefaultCss());
        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_DESIGN_JS, SQLResourcesHelper.getDefaultJs());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_DESIGN_CSS, SQLResourcesHelper.getDefaultCss());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_CONF_CSS, SQLResourcesHelper.getAnimateCss());
    }

    public void loadResources (Locale[] locales) {
        ResourceHelper.forceInitJSCache(ResourceConstants.DEFAULT_DESIGN_JS);
        ResourceHelper.forceInitStyleCache(ResourceConstants.DEFAULT_DESIGN_CSS);
        ResourceHelper.forceInitStyleCache(ResourceConstants.DEFAULT_CONF_CSS);
    }

    @Override
    public Collection<BIPackageID> getAvailablePackID(long userId) {
        List<BIPackageID> list = new ArrayList<BIPackageID>();
        list.add(new BIPackageID(Constants.PACK_ID));
        return list;
    }

    /**
     *
     */
    private void registerFilter() {
        BIFilterValueMap.ALL_VALUES.put(BIReportConstant.TARGET_FILTER_NUMBER.LARGE_OR_EQUAL_CAL_LINE, NumberLargeOrEqualsCLFilter.class);
        BIFilterValueMap.ALL_VALUES.put(BIReportConstant.TARGET_FILTER_NUMBER.SMALL_OR_EQUAL_CAL_LINE, NumberSmallOrEqualsCLFilter.class);
        BIFilterValueMap.ALL_VALUES.put(BIReportConstant.TARGET_FILTER_NUMBER.TOP_N, NumberTopNFilter.class);
        BIFilterValueMap.ALL_VALUES.put(BIReportConstant.TARGET_FILTER_NUMBER.BOTTOM_N, NumberBottomNFilter.class);
    }

    @Override
    public boolean isAllAdmin() {
        return false;
    }

    @Override
    public BIDataSourceManagerProvider getDataSourceManagerProvider() {
        return BIAnalysisSQLManagerCenter.getDataSourceManager();
    }


    @Override
    public BISystemPackageConfigurationProvider getBusiPackManagerProvider() {
        return BIAnalysisSQLManagerCenter.getBusiPackManager();
    }

    @Override
    public BIAliasManagerProvider getAliasManagerProvider() {
        return BIAnalysisSQLManagerCenter.getAliasManagerProvider();
    }

    @Override
    public ICubeDataLoaderCreator getCubeDataLoaderCreator() {
        return StableFactory.getMarkedObject(UserSQLCubeDataLoaderCreator.class.getName(), ICubeDataLoaderCreator.class);
    }


    private void registerManager() {
        StableFactory.registerMarkedObject(BIAnalysisSQLBusiPackManagerProvider.class.getName(), getBusiPackProvider());
        StableFactory.registerMarkedObject(BIAnalysisSQLDataSourceManagerProvider.class.getName(), getDataSourceProvider());
        StableFactory.registerMarkedObject(UserSQLDataManagerProvider.class.getName(), new UserSQLDataManager());
        StableFactory.registerMarkedObject(BIAnalysisSQLAliasManager.class.getName(), new BIAnalysisSQLAliasManager());
        StableFactory.registerMarkedObject(UserSQLCubeDataLoaderCreator.class.getName(), UserSQLCubeDataLoaderCreator.getInstance());
    }

    private BIAnalysisSQLBusiPackManagerProvider getBusiPackProvider() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                AnalysisSQLBusiPackManager provider = new AnalysisSQLBusiPackManager();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIAnalysisSQLBusiPackManagerProvider) RPC.getProxy(BIAnalysisSQLBusiPackManagerProvider.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new AnalysisSQLBusiPackManager();
        }
    }

    private BIAnalysisSQLDataSourceManagerProvider getDataSourceProvider() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                AnalysisSQLDataSourceManager provider = new AnalysisSQLDataSourceManager();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIAnalysisSQLDataSourceManagerProvider) RPC.getProxy(BIAnalysisSQLDataSourceManagerProvider.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new AnalysisSQLDataSourceManager();
        }
    }

    @Override
    public Service[] service4Register() {
        return new Service[]{
                new Service4AnalysisSQL()
        };
    }
}
