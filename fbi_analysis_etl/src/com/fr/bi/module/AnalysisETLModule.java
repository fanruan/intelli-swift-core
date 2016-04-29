package com.fr.bi.module;

import com.fr.bi.cluster.ClusterAdapter;
import com.fr.bi.cluster.utils.ClusterEnv;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BIDataSourceManagerProvider;
import com.fr.bi.conf.provider.BISystemPackageConfigurationProvider;
import com.fr.bi.etl.analysis.manager.*;
import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.*;
import com.fr.bi.field.filtervalue.BIFilterValueMap;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.engine.index.AbstractTIPathLoader;
import com.fr.bi.web.service.Service4AnalysisETL;
import com.fr.cluster.rpc.RPC;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.fun.Service;
import com.fr.bi.resource.ResourceConstants;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
public class AnalysisETLModule extends AbstractModule {
    @Override
    public void start() {
        registManager();
        registFilter();
        registResources();
    }

    /**
     *
     */
    private void registResources() {
        StableFactory.registerJavaScriptFiles(ETLResourcesHelper.DEFAULT_JS, ETLResourcesHelper.getDefaultJs());
        StableFactory.registerStyleFiles(ETLResourcesHelper.DEFAULT_CSS, ETLResourcesHelper.getDefaultCss());
        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_DESIGN_JS, ETLResourcesHelper.getDefaultJs());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_DEZI_CSS, ETLResourcesHelper.getDefaultCss());
    }

    /**
     *
     */
    private void registFilter() {
        BIFilterValueMap.ALL_VALUES.put(BIReportConstant.TARGET_FILTER_NUMBER.LARGE_THAN_CAL_LINE, NumberLargeCLFilter.class);
        BIFilterValueMap.ALL_VALUES.put(BIReportConstant.TARGET_FILTER_NUMBER.LARGE_OR_EQUAL_CAL_LINE, NumberLargeOrEqualsCLFilter.class);
        BIFilterValueMap.ALL_VALUES.put(BIReportConstant.TARGET_FILTER_NUMBER.SMALL_THAN_CAL_LINE, NumberSmallCLFilter.class);
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
        return StableFactory.getMarkedObject(BIAnalysisDataSourceManagerProvider.XML_TAG, BIAnalysisDataSourceManagerProvider.class);
    }

    @Override
    public BISystemPackageConfigurationProvider getBusiPackManagerProvider() {
        return BIConfigureManagerCenter.getPackageManager();
    }

    @Override
    public Class<? extends AbstractTIPathLoader> getTIPathLoaderClass() {
        return UserETLCubeTILoader.class;
    }


    private void registManager() {
        StableFactory.registerMarkedObject(BIAnalysisBusiPackManagerProvider.XML_TAG, getBusiPackProvider());
        StableFactory.registerMarkedObject(BIAnalysisDataSourceManagerProvider.XML_TAG, getDataSourceProvider());
        StableFactory.registerMarkedObject(UserETLCubeManager.class.getName(), new UserETLCubeManager());
    }

    private BIAnalysisBusiPackManagerProvider getBusiPackProvider() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                AnalysisBusiPackManager provider = new AnalysisBusiPackManager();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIAnalysisBusiPackManagerProvider) RPC.getProxy(BIAnalysisBusiPackManagerProvider.class,
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
                return (BIAnalysisDataSourceManagerProvider) RPC.getProxy(BIAnalysisDataSourceManagerProvider.class,
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