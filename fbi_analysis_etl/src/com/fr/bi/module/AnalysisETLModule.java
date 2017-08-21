package com.fr.bi.module;

import com.finebi.cube.api.ICubeDataLoaderCreator;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BIAliasManagerProvider;
import com.finebi.cube.conf.BIDataSourceManagerProvider;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cluster.ClusterAdapter;
import com.fr.bi.cluster.utils.ClusterEnv;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.data.AnalysisBaseTableSource;
import com.fr.bi.etl.analysis.data.AnalysisETLTableSource;
import com.fr.bi.etl.analysis.manager.*;
import com.fr.bi.etl.analysis.monitor.web.Service4AnalysisETLMonitor;
import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.NumberBottomNFilter;
import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.NumberLargeOrEqualsCLFilter;
import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.NumberSmallOrEqualsCLFilter;
import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.NumberTopNFilter;
import com.fr.bi.etl.analysis.resource.ETLResourcesHelper;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.field.filtervalue.BIFilterValueMap;
import com.fr.bi.resource.ResourceConstants;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.service.Service4AnalysisETL;
import com.fr.bi.web.service.action.PartCubeDataLoader;
import com.fr.cluster.rpc.RPC;
import com.fr.fs.control.UserControl;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.bridge.event.StableFactoryMessageTransponder;
import com.fr.stable.bridge.event.StableFactoryProducer;
import com.fr.stable.bridge.event.StableFactoryResourceType;
import com.fr.stable.fun.Service;
import com.fr.web.ResourceHelper;

import java.util.*;


/**
 * Created by 小灰灰 on 2015/12/11.
 */
public class AnalysisETLModule extends AbstractModule {
    @Override
    public void start() {
        // TODO BI-3640 集群版本禁用螺旋分析
        if (ClusterEnv.isCluster()) {
            return;
        }
        registerManager();
        registerFilter();
        registerResources();
        dealWithOldVersion();
    }

    private void dealWithOldVersion() {
        BIAnalysisETLManagerCenter.getBusiPackManager().getAllTables(UserControl.getInstance().getSuperManagerID());
    }

    /**
     *
     */
    private void registerResources() {

        StableFactoryMessageTransponder.getInstance().addProducer(new StableFactoryProducer() {

            @Override
            public void reInject(StableFactoryResourceType resourceType) {

                if (StableFactoryResourceType.TYPE_JS.equals(resourceType)) {
                    registerJavaScriptFiles();
                } else if (StableFactoryResourceType.TYPE_CSS.equals(resourceType)) {
                    registerStyleFiles();
                }
            }
        }, new StableFactoryResourceType[]{StableFactoryResourceType.TYPE_CSS, StableFactoryResourceType.TYPE_JS});

        registerJavaScriptFiles();
        registerStyleFiles();

    }

    private void registerStyleFiles() {

        StableFactory.registerStyleFiles(ETLResourcesHelper.DEFAULT_CSS, ETLResourcesHelper.getDefaultCss());

        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_DESIGN_CSS, ETLResourcesHelper.getDefaultCss());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_SHOW_CSS, ETLResourcesHelper.getDefaultCss());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_CONF_CSS, ETLResourcesHelper.getAnimateCss());
        StableFactory.registerJavaScriptFiles(ETLResourcesHelper.MONITOR_JS, ETLResourcesHelper.getMonitorJS());
        StableFactory.registerStyleFiles(ETLResourcesHelper.MONITOR_CSS, ETLResourcesHelper.getMonitorCss());
        StableFactory.registerStyleFiles(ETLResourcesHelper.MONITOR_CSS, ETLResourcesHelper.getAnimateCss());
    }

    private void registerJavaScriptFiles() {

        StableFactory.registerJavaScriptFiles(ETLResourcesHelper.DEFAULT_JS, ETLResourcesHelper.getDefaultJs());
        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_DESIGN_JS, ETLResourcesHelper.getDefaultJs());
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
    public Collection<BIPackageID> getAuthAvailablePackID(long userId) {
        List<BIPackageID> list = new ArrayList<BIPackageID>();
        BISystemPackageConfigurationProvider provider = getBusiPackManagerProvider();
        if (provider == null) {
            return list;
        } else {
            for (IBusinessPackageGetterService pack : provider.getAllPackages(userId)) {
                list.add(pack.getID());
            }
        }
        return list;

    }

    @Override
    public void clearCacheAfterBuildCubeTask(long userId) {
        Map<BusinessTable, CubeTableSource> refreshTables = new HashMap<BusinessTable, CubeTableSource>();
        for (BusinessTable table : BIAnalysisETLManagerCenter.getDataSourceManager().getAllBusinessTable()) {
            CubeTableSource oriSource = table.getTableSource();
            oriSource.refresh();
            table.setSource(oriSource);
            refreshTables.put(table, oriSource);
        }
        refreshAnalysisSources(refreshTables);
        clearAnalysisSourceCaches();
        BIAnalysisETLManagerCenter.getDataSourceManager().persistData(userId);
        BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().refresh();
        PartCubeDataLoader.clearAll();
    }

    private void clearAnalysisSourceCaches() {
        for (BusinessTable table : BIAnalysisETLManagerCenter.getDataSourceManager().getAllBusinessTable()) {
            int tableType = table.getTableSource().getType();
            if (tableType == BIBaseConstant.TABLE_TYPE.BASE) {
                ((AnalysisBaseTableSource) table.getTableSource()).clearUserBaseTableMap();
            }
            if (tableType == BIBaseConstant.TABLE_TYPE.ETL) {
                ((AnalysisETLTableSource) table.getTableSource()).clearUserBaseTableMap();
            }
        }
    }

    private void refreshAnalysisSources(Map<BusinessTable, CubeTableSource> refreshTables) {
        for (BusinessTable table : refreshTables.keySet()) {
            try {
                BIAnalysisETLManagerCenter.getDataSourceManager().addTableSource(table, refreshTables.get(table));
            } catch (BIKeyDuplicateException e) {
                BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            }
        }
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
        return StableFactory.getMarkedObject(UserETLCubeDataLoaderCreator.XML_TAG, ICubeDataLoaderCreator.class);
    }


    private void registerManager() {
        StableFactory.registerMarkedObject(BIAnalysisBusiPackManagerProvider.XML_TAG, getBusiPackProvider());
        StableFactory.registerMarkedObject(BIAnalysisDataSourceManagerProvider.XML_TAG, getDataSourceProvider());
        StableFactory.registerMarkedObject(UserETLCubeManager.class.getName(), new UserETLCubeManager());
        StableFactory.registerMarkedObject(UserETLCubeManagerProvider.class.getName(), getUserETLCubeManagerProvider());
        StableFactory.registerMarkedObject(BIAliasManagerProvider.class.getName(),/* new BIAnalysisETLAliasManager()*/getBIAliasManagerProvider());
        StableFactory.registerMarkedObject(UserETLCubeDataLoaderCreator.XML_TAG, UserETLCubeDataLoaderCreator.getInstance());
    }

    private BIAliasManagerProvider getBIAliasManagerProvider() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BIAnalysisETLAliasManagerWithoutUser provider = new BIAnalysisETLAliasManagerWithoutUser();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIAliasManagerProvider) RPC.getProxy(BIAnalysisETLAliasManagerWithoutUser.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new BIAnalysisETLAliasManagerWithoutUser();
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
                AnalysisDataSourceManagerWithoutUser provider = new AnalysisDataSourceManagerWithoutUser();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIAnalysisDataSourceManagerProvider) RPC.getProxy(AnalysisDataSourceManagerWithoutUser.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new AnalysisDataSourceManagerWithoutUser();
        }
    }

    @Override
    public Service[] service4Register() {
        return new Service[]{
                new Service4AnalysisETL(),
                new Service4AnalysisETLMonitor()
        };
    }
}
