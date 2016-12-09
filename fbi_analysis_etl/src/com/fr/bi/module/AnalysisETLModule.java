package com.fr.bi.module;

import com.finebi.cube.api.ICubeDataLoaderCreator;
import com.finebi.cube.conf.BIAliasManagerProvider;
import com.finebi.cube.conf.BIDataSourceManagerProvider;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cluster.ClusterAdapter;
import com.fr.bi.cluster.utils.ClusterEnv;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.data.AnalysisBaseTableSource;
import com.fr.bi.etl.analysis.data.AnalysisETLTableSource;
import com.fr.bi.etl.analysis.manager.*;
import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.NumberBottomNFilter;
import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.NumberLargeOrEqualsCLFilter;
import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.NumberSmallOrEqualsCLFilter;
import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.NumberTopNFilter;
import com.fr.bi.field.filtervalue.BIFilterValueMap;
import com.fr.bi.resource.ResourceConstants;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.web.service.Service4AnalysisETL;
import com.fr.bi.web.service.action.PartCubeDataLoader;
import com.fr.cluster.rpc.RPC;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.fun.Service;
import com.fr.web.ResourceHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


/**
 * Created by 小灰灰 on 2015/12/11.
 */
public class AnalysisETLModule extends AbstractModule {
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
        StableFactory.registerJavaScriptFiles(ETLResourcesHelper.DEFAULT_JS, ETLResourcesHelper.getDefaultJs());
        StableFactory.registerStyleFiles(ETLResourcesHelper.DEFAULT_CSS, ETLResourcesHelper.getDefaultCss());
        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_DESIGN_JS, ETLResourcesHelper.getDefaultJs());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_DESIGN_CSS, ETLResourcesHelper.getDefaultCss());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_CONF_CSS, ETLResourcesHelper.getAnimateCss());
    }

    public void loadResources(Locale[] locales) {
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

    @Override
    public void clearAnalysisETLCache(long userId) {
        for (BusinessTable table : BIAnalysisETLManagerCenter.getDataSourceManager().getAllBusinessTable()) {
            int tableType = table.getTableSource().getType();
            if (tableType == Constants.TABLE_TYPE.BASE) {
                ((AnalysisBaseTableSource) table.getTableSource()).clearUserBaseTableMap();
            }
            if (tableType == Constants.TABLE_TYPE.ETL) {
                ((AnalysisETLTableSource) table.getTableSource()).clearUserBaseTableMap();
            }
        }
        PartCubeDataLoader.clearAll();
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
        return BIAnalysisETLManagerCenter.getDataSourceManager();
    }


    @Override
    public BISystemPackageConfigurationProvider getBusiPackManagerProvider() {
        return BIAnalysisETLManagerCenter.getBusiPackManager();
    }

    @Override
    public BIAliasManagerProvider getAliasManagerProvider() {
        return BIAnalysisETLManagerCenter.getAliasManagerProvider();
    }

    @Override
    public ICubeDataLoaderCreator getCubeDataLoaderCreator() {
        return StableFactory.getMarkedObject(UserETLCubeDataLoaderCreator.class.getName(), ICubeDataLoaderCreator.class);
    }


    private void registerManager() {
        StableFactory.registerMarkedObject(BIAnalysisBusiPackManagerProvider.XML_TAG, getBusiPackProvider());
        StableFactory.registerMarkedObject(BIAnalysisDataSourceManagerProvider.XML_TAG, getDataSourceProvider());
        StableFactory.registerMarkedObject(UserETLCubeManager.class.getName(), new UserETLCubeManager());
        StableFactory.registerMarkedObject(UserETLCubeManagerProvider.class.getName(), getUserETLCubeManagerProvider());
        StableFactory.registerMarkedObject(BIAliasManagerProvider.class.getName(),/* new BIAnalysisETLAliasManager()*/getBIAliasManagerProvider());
        StableFactory.registerMarkedObject(UserETLCubeDataLoaderCreator.class.getName(), UserETLCubeDataLoaderCreator.getInstance());
    }
    private BIAliasManagerProvider getBIAliasManagerProvider() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BIAnalysisETLAliasManager provider = new BIAnalysisETLAliasManager();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIAliasManagerProvider) RPC.getProxy(BIAnalysisETLAliasManager.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new BIAnalysisETLAliasManager();
        }
    }
    private UserETLCubeManagerProvider getUserETLCubeManagerProvider() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                UserETLCubeManager provider = new UserETLCubeManager();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (UserETLCubeManagerProvider) RPC.getProxy(UserETLCubeManager.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new UserETLCubeManager();
        }
    }
    private BIAnalysisBusiPackManagerProvider getBusiPackProvider() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                AnalysisBusiPackManager provider = new AnalysisBusiPackManager();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIAnalysisBusiPackManagerProvider) RPC.getProxy(AnalysisBusiPackManager.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new AnalysisBusiPackManager();
        }
    }

    private BIAnalysisDataSourceManagerProvider getDataSourceProvider() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                AnalysisDataSourceManager provider = new AnalysisDataSourceManager();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIAnalysisDataSourceManagerProvider) RPC.getProxy(AnalysisDataSourceManager.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new AnalysisDataSourceManager();
        }
    }

    @Override
    public Service[] service4Register() {
        return new Service[]{
                new Service4AnalysisETL()
        };
    }
}
