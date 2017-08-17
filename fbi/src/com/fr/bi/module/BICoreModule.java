package com.fr.bi.module;

import com.finebi.cube.api.ICubeDataLoaderCreator;
import com.finebi.cube.api.UserAnalysisCubeDataLoaderCreator;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BIAliasManagerProvider;
import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.BIDataSourceManagerProvider;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.BITableRelationConfigurationProvider;
import com.finebi.cube.conf.datasource.BIDataSourceManagerWithoutUser;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.pack.imp.BISystemPackageConfigurationManagerWithoutUser;
import com.finebi.cube.conf.relation.BISystemTableRelationManagerWithoutUser;
import com.finebi.cube.conf.singletable.SingleTableUpdateManager;
import com.finebi.cube.conf.timer.UpdateFrequencyManager;
import com.finebi.cube.conf.trans.BIAliasManagerWithoutUser;
import com.fr.base.FRContext;
import com.fr.bi.cal.BICubeManager;
import com.fr.bi.cal.analyze.cal.index.loader.cache.WidgetDataCacheManager;
import com.fr.bi.cal.generate.timerTask.BICubeTimeTaskCreatorManager;
import com.fr.bi.cal.generate.timerTask.BICubeTimeTaskCreatorProvider;
import com.fr.bi.cluster.ClusterAdapter;
import com.fr.bi.cluster.ClusterManager;
import com.fr.bi.cluster.manager.EmptyClusterManager;
import com.fr.bi.cluster.utils.ClusterEnv;
import com.fr.bi.conf.base.auth.BISystemAuthorityManager;
import com.fr.bi.conf.base.cube.BISystemCubeConfManagerWithoutUser;
import com.fr.bi.conf.base.dataconfig.BISystemDataConfigAuthorityManager;
import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.conf.base.datasource.BIConnectionProvider;
import com.fr.bi.conf.base.login.BISystemUserLoginInformationManager;
import com.fr.bi.conf.log.BILogManagerWithoutUser;
import com.fr.bi.conf.manager.excelview.BIExcelViewManagerWithoutUser;
import com.fr.bi.conf.manager.report.BIPublicReportManager;
import com.fr.bi.conf.manager.update.BIUpdateSettingManagerWithoutUser;
import com.fr.bi.conf.provider.BIAuthorityManageProvider;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BICubeConfManagerProvider;
import com.fr.bi.conf.provider.BICubeTaskRecordProvider;
import com.fr.bi.conf.provider.BIDataConfigAuthorityProvider;
import com.fr.bi.conf.provider.BIExcelViewManagerProvider;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.conf.provider.BIPublicReportManagerProvider;
import com.fr.bi.conf.provider.BIUpdateFrequencyManagerProvider;
import com.fr.bi.conf.provider.BIUserLoginInformationProvider;
import com.fr.bi.conf.records.BICubeTaskRecordManagerWithoutUser;
import com.fr.bi.conf.report.BIFSReportProvider;
import com.fr.bi.conf.tablelock.BIConfTableLock;
import com.fr.bi.conf.tablelock.BIConfTableLockDAO;
import com.fr.bi.fs.BIDAOProvider;
import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportDAO;
import com.fr.bi.fs.BIReportNodeLock;
import com.fr.bi.fs.BIReportNodeLockDAO;
import com.fr.bi.fs.BISuperManagetDAOManager;
import com.fr.bi.fs.BIReportQueryProvider;
import com.fr.bi.fs.BITableMapper;
import com.fr.bi.fs.HSQLBIReportDAO;
import com.fr.bi.fs.TableDataBIReportDAO;
import com.fr.bi.fs.BITableDataDAOProvider;
import com.fr.bi.fs.BITableDataDAOManager;
import com.fr.bi.fs.BIMultiPathProvider;
import com.fr.bi.resource.BaseResourceHelper;
import com.fr.bi.resource.CommonResourceHelper;
import com.fr.bi.resource.ConfResourceHelper;
import com.fr.bi.resource.DeziResourceHelper;
import com.fr.bi.resource.ResourceConstants;
import com.fr.bi.resource.ShowResourceHelper;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.bi.tool.BIReadReportProvider;
import com.fr.bi.util.BIReadReportUtils;
import com.fr.bi.web.base.Service4BIBase;
import com.fr.bi.web.conf.Service4BIConfigure;
import com.fr.bi.web.dezi.web.Service4BIDezi;
import com.fr.bi.web.report.Service4BIPublic;
import com.fr.bi.web.report.Service4BIReport;
import com.fr.bi.web.report.services.finecube.Service4FineCube;
import com.fr.bi.web.report.utils.BIFSReportManager;
import com.fr.cluster.rpc.RPC;
import com.fr.data.core.db.DBUtils;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dialect.DialectFactory;
import com.fr.data.core.db.dml.Select;
import com.fr.data.core.db.dml.Table;
import com.fr.data.core.db.tableObject.Column;
import com.fr.data.core.db.tableObject.ColumnSize;
import com.fr.data.dao.ObjectTableMapper;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.data.pool.MemoryConnection;
import com.fr.file.DatasourceManager;
import com.fr.fs.control.UserControl;
import com.fr.fs.control.dao.hsqldb.HSQLDBDAOControl;
import com.fr.fs.control.dao.tabledata.TableDataDAOControl;
import com.fr.fs.dao.FSDAOManager;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.fun.Service;
import com.fr.web.core.db.PlatformDB;
import com.fr.bi.web.conf.services.BIMultiPathManager;
import com.fr.bi.fs.BIReportQueryManger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by 小灰灰 on 2015/12/15.
 */
public class BICoreModule extends AbstractModule {

    private static boolean isInit = true;
    private static boolean isFirstTimeInit = true;

    @Override
    public void start() {

        if (isInit) {
            isInit = false;
            registerClusterIfNeed();
        }
        registerProviders();
        initDataSourcePool();
        registerSystemManager();
        registerDAO();
        registerResources();
        registerTableAddColumn();
        isFirstTimeInit = false;
    }

    @Override
    public BIDataSourceManagerProvider getDataSourceManagerProvider() {
        return StableFactory.getMarkedObject(BIDataSourceManagerProvider.XML_TAG, BIDataSourceManagerProvider.class);
    }

    @Override
    public BISystemPackageConfigurationProvider getBusiPackManagerProvider() {
        return StableFactory.getMarkedObject(BISystemPackageConfigurationProvider.XML_TAG, BISystemPackageConfigurationProvider.class);
    }

    @Override
    public BIAliasManagerProvider getAliasManagerProvider() {
        return StableFactory.getMarkedObject(BIAliasManagerProvider.XML_TAG, BIAliasManagerProvider.class);
    }

    @Override
    public ICubeDataLoaderCreator getCubeDataLoaderCreator() {
        return StableFactory.getMarkedObject(UserAnalysisCubeDataLoaderCreator.XML_TAG, ICubeDataLoaderCreator.class);
    }


    private void registerProviders() {
        StableFactory.registerMarkedObject(BIFSReportProvider.XML_TAG, getBIFSReportManager());
        StableFactory.registerMarkedObject(BIDAOProvider.XML_TAG, getBIDAO());
        StableFactory.registerMarkedObject(BIReadReportProvider.XML_TAG, getBIReadReport());
        StableFactory.registerMarkedObject(BIReportDAO.class.getName(), getBIReportDAO());
        StableFactory.registerMarkedObject(BIReportQueryProvider.XML_TAG, getBIQueryReportManager());
        StableFactory.registerMarkedObject(BIMultiPathProvider.XML_TAG, getBIMultiPathManager());

        StableFactory.registerMarkedObject(BIUpdateFrequencyManagerProvider.XML_TAG, getBIUpdateSettingManager());
        StableFactory.registerMarkedObject(BISystemPackageConfigurationProvider.XML_TAG, getPackManagerProvider());
        StableFactory.registerMarkedObject(BIAuthorityManageProvider.XML_TAG, getBISystemAuthorityManager());
        StableFactory.registerMarkedObject(UserAnalysisCubeDataLoaderCreator.XML_TAG, UserAnalysisCubeDataLoaderCreator.getInstance());
        StableFactory.registerMarkedObject(BIDataSourceManagerProvider.XML_TAG, getSourceManagerProvider());
        StableFactory.registerMarkedObject(BIAliasManagerProvider.XML_TAG, getTransManagerProvider());
        StableFactory.registerMarkedObject(BITableRelationConfigurationProvider.XML_TAG, getConnectionManagerProvider());
        StableFactory.registerMarkedObject(BICubeTimeTaskCreatorProvider.XML_TAG, new BICubeTimeTaskCreatorManager());
        StableFactory.registerMarkedObject(BICubeManagerProvider.XML_TAG, getCubeManagerProvider());
        StableFactory.registerMarkedObject(BILogManagerProvider.XML_TAG, getBILogManager());
        StableFactory.registerMarkedObject(BIUserLoginInformationProvider.XML_TAG, new BISystemUserLoginInformationManager());
        StableFactory.registerMarkedObject(BIConnectionProvider.XML_TAG, getConnectionManager());
        StableFactory.registerMarkedObject(UpdateFrequencyManager.XML_TAG, new UpdateFrequencyManager());
        StableFactory.registerMarkedObject(BIExcelViewManagerProvider.XML_TAG, getExcelViewManager());
        StableFactory.registerMarkedObject(BICubeConfManagerProvider.XML_TAG, getBICubeConfManager());
        StableFactory.registerMarkedObject(SingleTableUpdateManager.XML_TAG, new SingleTableUpdateManager());
        StableFactory.registerMarkedObject(BICubeTaskRecordProvider.XML_TAG, getBICubeTaskRecordManagerWithoutUser());

        StableFactory.registerMarkedObject(BIDataConfigAuthorityProvider.XML_TAG, new BISystemDataConfigAuthorityManager());
        StableFactory.registerMarkedObject(BITableDataDAOProvider.XML_TAG, getBITableDataDAOManager());

        StableFactory.registerMarkedObject(BIPublicReportManagerProvider.XML_TAG, getBIPublicReportManger());

    }

    public BIMultiPathProvider getBIMultiPathManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BIMultiPathManager provider = BIMultiPathManager.getInstance();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIMultiPathProvider) RPC.getProxy(BIMultiPathManager.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return BIMultiPathManager.getInstance();
        }
    }

    public BIReportQueryProvider getBIQueryReportManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BIReportQueryManger provider = BIReportQueryManger.getInstance();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIReportQueryProvider) RPC.getProxy(BIReportQueryManger.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return BIReportQueryManger.getInstance();
        }
    }

    private BIPublicReportManagerProvider getBIPublicReportManger() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BIPublicReportManager provider = new BIPublicReportManager();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIPublicReportManagerProvider) RPC.getProxy(BIPublicReportManager.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new BIPublicReportManager();
        }
    }

    public BICubeTaskRecordProvider getBICubeTaskRecordManagerWithoutUser() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BICubeTaskRecordManagerWithoutUser provider = new BICubeTaskRecordManagerWithoutUser();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BICubeTaskRecordProvider) RPC.getProxy(BICubeTaskRecordManagerWithoutUser.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new BICubeTaskRecordManagerWithoutUser();
        }
    }

    public BITableDataDAOProvider getBITableDataDAOManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BITableDataDAOManager provider = BITableDataDAOManager.getInstance();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BITableDataDAOProvider) RPC.getProxy(BITableDataDAOManager.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return BITableDataDAOManager.getInstance();
        }
    }

    public BIUpdateFrequencyManagerProvider getBIUpdateSettingManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BIUpdateSettingManagerWithoutUser provider = new BIUpdateSettingManagerWithoutUser();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIUpdateFrequencyManagerProvider) RPC.getProxy(BIUpdateSettingManagerWithoutUser.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new BIUpdateSettingManagerWithoutUser();
        }
    }

    public BILogManagerProvider getBILogManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isBuildCube()) {
                BILogManagerWithoutUser provider = new BILogManagerWithoutUser();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getBuildCubePort());
                return provider;
            } else {
                return (BILogManagerProvider) RPC.getProxy(BILogManagerWithoutUser.class,
                        ClusterAdapter.getManager().getHostManager().getBuildCubeIp(),
                        ClusterAdapter.getManager().getHostManager().getBuildCubePort());
            }
        } else {
            return new BILogManagerWithoutUser();
        }
    }

    public BIReportDAO getBIReportDAO() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BISuperManagetDAOManager provider = BISuperManagetDAOManager.getInstance();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIReportDAO) RPC.getProxy(BISuperManagetDAOManager.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return BISuperManagetDAOManager.getInstance();
        }
    }

    protected BIAuthorityManageProvider getBISystemAuthorityManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BISystemAuthorityManager provider = new BISystemAuthorityManager();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIAuthorityManageProvider) RPC.getProxy(BISystemAuthorityManager.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new BISystemAuthorityManager();
        }
    }

    protected BIReadReportProvider getBIReadReport() {
//        if (ClusterEnv.isCluster()) {
//            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
//                BIReadReportUtils provider = BIReadReportUtils.getInstance();
//                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
//                return provider;
//            } else {
//                return (BIReadReportProvider) RPC.getProxy(BIReadReportUtils.class,
//                        ClusterAdapter.getManager().getHostManager().getIp(),
//                        ClusterAdapter.getManager().getHostManager().getPort());
//            }
//        } else {
        return BIReadReportUtils.getInstance();
//        }
    }

    protected BIDAOProvider getBIDAO() {
//        if (ClusterEnv.isCluster()) {
//            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
//                BIDAOUtils provider = BIDAOUtils.getInstance();
//                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
//                return provider;
//            } else {
//                return (BIDAOProvider) RPC.getProxy(BIDAOUtils.class,
//                        ClusterAdapter.getManager().getHostManager().getIp(),
//                        ClusterAdapter.getManager().getHostManager().getPort());
//            }
//        } else {
        return BIDAOUtils.getInstance();
//        }
    }

    protected BIFSReportProvider getBIFSReportManager() {
//        if (ClusterEnv.isCluster()) {
//            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
//                BIFSReportManager provider = BIFSReportManager.getInstance();
//                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
//                return provider;
//            } else {
//                return (BIFSReportProvider) RPC.getProxy(BIFSReportManager.class,
//                        ClusterAdapter.getManager().getHostManager().getIp(),
//                        ClusterAdapter.getManager().getHostManager().getPort());
//            }
//        } else {
        return BIFSReportManager.getInstance();
//        }
    }

    protected BIConnectionProvider getConnectionManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BIConnectionManager provider = BIConnectionManager.getInstance();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIConnectionProvider) RPC.getProxy(BIConnectionManager.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return BIConnectionManager.getInstance();
        }
    }

    protected SingleTableUpdateManager getSingleTableUpdateManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                SingleTableUpdateManager provider = new SingleTableUpdateManager();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (SingleTableUpdateManager) RPC.getProxy(SingleTableUpdateManager.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new SingleTableUpdateManager();
        }
    }

    protected UpdateFrequencyManager getUpdateFrequencyManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                UpdateFrequencyManager provider = new UpdateFrequencyManager();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (UpdateFrequencyManager) RPC.getProxy(UpdateFrequencyManager.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new UpdateFrequencyManager();
        }
    }

    protected BICubeConfManagerProvider getBICubeConfManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BISystemCubeConfManagerWithoutUser provider = new BISystemCubeConfManagerWithoutUser();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BICubeConfManagerProvider) RPC.getProxy(BISystemCubeConfManagerWithoutUser.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new BISystemCubeConfManagerWithoutUser();
        }
    }

    protected BIExcelViewManagerProvider getExcelViewManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BIExcelViewManagerWithoutUser provider = new BIExcelViewManagerWithoutUser();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIExcelViewManagerProvider) RPC.getProxy(BIExcelViewManagerWithoutUser.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new BIExcelViewManagerWithoutUser();
        }
    }

    protected BICubeManagerProvider getCubeManagerProvider() {
        return generateCubeManager();
    }

    protected BITableRelationConfigurationProvider getConnectionManagerProvider() {
        return generateConnectionManager();
    }

    protected BISystemPackageConfigurationProvider getPackManagerProvider() {
        return generateBusiPackManager();
    }

    protected BIDataSourceManagerProvider getSourceManagerProvider() {
        return generateDataSourceManager();
    }

    protected BIAliasManagerProvider getTransManagerProvider() {
        return generateTransManager();
    }

    private BICubeManagerProvider generateCubeManager() {
        BICubeManager provider = new BICubeManager();
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isBuildCube()) {
                provider.resetCubeGenerationHour(UserControl.getInstance().getSuperManagerID());
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getBuildCubePort());
                return provider;
            } else {
                return (BICubeManagerProvider) RPC.getProxy(BICubeManager.class,
                        ClusterAdapter.getManager().getHostManager().getBuildCubeIp(),
                        ClusterAdapter.getManager().getHostManager().getBuildCubePort());
            }
        } else {
            provider.resetCubeGenerationHour(UserControl.getInstance().getSuperManagerID());
            return provider;
        }

    }

    private BITableRelationConfigurationProvider generateConnectionManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BISystemTableRelationManagerWithoutUser provider = new BISystemTableRelationManagerWithoutUser();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BITableRelationConfigurationProvider) RPC.getProxy(BISystemTableRelationManagerWithoutUser.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
//            return null;
        } else {
            return new BISystemTableRelationManagerWithoutUser();
        }
    }


    private BISystemPackageConfigurationProvider generateBusiPackManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BISystemPackageConfigurationManagerWithoutUser provider = new BISystemPackageConfigurationManagerWithoutUser();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BISystemPackageConfigurationProvider) RPC.getProxy(BISystemPackageConfigurationManagerWithoutUser.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new BISystemPackageConfigurationManagerWithoutUser();
        }

    }

    private BIDataSourceManagerProvider generateDataSourceManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BIDataSourceManagerWithoutUser provider = new BIDataSourceManagerWithoutUser();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIDataSourceManagerProvider) RPC.getProxy(BIDataSourceManagerWithoutUser.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new BIDataSourceManagerWithoutUser();
        }

    }

    private BIAliasManagerProvider generateTransManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BIAliasManagerWithoutUser provider = new BIAliasManagerWithoutUser();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIAliasManagerProvider) RPC.getProxy(BIAliasManagerWithoutUser.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new BIAliasManagerWithoutUser();
        }
    }


    private void initDataSourcePool() {
        synchronized (DatasourceManager.getProviderInstance()) {
            Iterator<String> iterator = DatasourceManager.getProviderInstance().getConnectionNameIterator();
            while (iterator.hasNext()) {
                String name = iterator.next();
                JDBCDatabaseConnection connection = DatasourceManager.getProviderInstance().getConnection(name, JDBCDatabaseConnection.class);
                BIDBUtils.dealWithJDBCConnection(connection);
            }
            try {
                MemoryConnection.getConnectionMap().clear();
                FRContext.getCurrentEnv().writeResource(DatasourceManager.getProviderInstance());
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
    }


    private void registerClusterIfNeed() {
        if (ClusterEnv.isCluster()) {
            try {
                ClusterManager.getInstance().initClusterEnv();
            } catch (Exception ex) {
                BILoggerFactory.getLogger().error(ex.getMessage(), ex);
            }
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BILoggerFactory.getLogger(BICoreModule.class).info("-------------------------master---------------------------");
            } else {
                BILoggerFactory.getLogger(BICoreModule.class).info("-------------------------slaver---------------------------");
            }
        } else {
            ClusterAdapter.registerBIClusterManagerInterface(EmptyClusterManager.getInstance());
        }
    }

    /**
     *
     */
    private void registerTableAddColumn() {
        addTableColumn4NewConnection(BITableMapper.BI_REPORT_NODE.TABLE_NAME, new Column(BITableMapper.BI_REPORT_NODE.FIELD_STATUS, Types.INTEGER, new ColumnSize(10)));
    }

    private static void addTableColumn4NewConnection(String tableName, Column column) {
        Connection cn = null;
        try {
            cn = PlatformDB.getDB().createConnection();

            try {
                cn.setAutoCommit(false);
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
            if (shouldAddTableColumn(cn, BITableMapper.BI_REPORT_NODE.TABLE_NAME, column.getName())) {
                Dialect dialect = DialectFactory.generateDialect(cn, PlatformDB.getDB().getDriver());
                FSDAOManager.addTableColumn(cn, dialect, column, tableName);
            }
            cn.commit();
        } catch (Exception e) {
            if (cn != null) {
                try {
                    cn.rollback();
                } catch (SQLException e1) {
                    FRContext.getLogger().error(e1.getMessage(), e1);
                }
            }

            FRContext.getLogger().error("Add" + tableName + "SortColumn Action Failed!");
            FRContext.getLogger().error(e.getMessage(), e);
        } finally {
            DBUtils.closeConnection(cn);
        }
    }

    private static boolean shouldAddTableColumn(Connection cn, String tableName, String columnName) {
        Table table = new Table(tableName);
        Select select = new Select(table, DialectFactory.generateDialect(cn, PlatformDB.getDB().getDriver()));
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = select.createPreparedStatement(cn);
            rs = ps.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                if (columnName.equalsIgnoreCase(md.getColumnName(i))) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            FRContext.getLogger().error("Check" + tableName + "SortColumn Action Failed!");
            FRContext.getLogger().error(e.getMessage(), e);
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
            }
        }
    }


    private void registerDAO() {
        if ((!ClusterEnv.isCluster())) {
            dropBILockDAOTable(BIReportNodeLock.class);
            dropBILockDAOTable(BIConfTableLock.class);
        }
        if (ClusterAdapter.getManager().getHostManager().isSelf() && isFirstTimeInit) {
            dropBILockDAOTable(BIReportNodeLock.class);
            dropBILockDAOTable(BIConfTableLock.class);
        }

        StableFactory.registerMarkedObject(HSQLDBDAOControl.class.getName(), HSQLBIReportDAO.getInstance());
        StableFactory.registerMarkedObject(TableDataDAOControl.class.getName(), TableDataBIReportDAO.getInstance());
        StableFactory.registerMarkedObject(BIReportNodeLockDAO.class.getName(), BIReportNodeLockDAO.getInstance());
        StableFactory.registerMarkedObject(BIConfTableLockDAO.class.getName(), BIConfTableLockDAO.getInstance());
    }

    private void dropBILockDAOTable(Class Lock) {
        Connection cn = null;
        PreparedStatement ps = null;
        String tableName = ObjectTableMapper.PREFIX_NAME + Lock.getSimpleName();
        try {
            cn = PlatformDB.getDB().createConnection();
            ps = cn.prepareStatement("DROP TABLE " + DialectFactory.generateDialect(cn).column2SQL(tableName));
            ps.execute();
            BILoggerFactory.getLogger().info("Table " + tableName + " has been deleted successfully");
            cn.commit();
        } catch (Exception e) {
            //BILogger.getLogger().error(e.getMessage(), e);
        } finally {
            DBUtils.closeStatement(ps);
            DBUtils.closeConnection(cn);
        }
    }

    private void registerResources() {
        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_THIRD_JS, BaseResourceHelper.getThirdJs());
        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_MAP_JS, BaseResourceHelper.getMapJS(), BaseResourceHelper.MapTransmitter);
        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_BASE_JS, BaseResourceHelper.getBaseJs());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_THIRD_CSS, BaseResourceHelper.getThirdCss());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_BASE_CSS, BaseResourceHelper.getBaseCss());

        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_EXPORT_JS, BaseResourceHelper.getExportJS());

        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_DATA_JS, BaseResourceHelper.getDataJS(), BaseResourceHelper.DataTransmitter);

        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_CONF_JS, ConfResourceHelper.getConfJs());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_CONF_CSS, ConfResourceHelper.getConfCss());

        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_DESIGN_JS, DeziResourceHelper.getDeziJs());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_DESIGN_CSS, DeziResourceHelper.getDeziCss());

        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_DESIGN_CONFIG_JS, DeziResourceHelper.getDeziConfigJs());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_DESIGN_CONFIG_CSS, DeziResourceHelper.getDeziConfigCss());

        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_SHOW_JS, ShowResourceHelper.getShowJs());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_SHOW_CSS, ShowResourceHelper.getShowCss());

        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_MODULE_JS, CommonResourceHelper.getCommonJs());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_MODULE_CSS, CommonResourceHelper.getCommonCss());

        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_FORMULA_JS, BaseResourceHelper.getFormulaCollectionJS(), BaseResourceHelper.FormulaTransmitter);
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_LOCAL_CSS, BaseResourceHelper.getLocalCss());
    }

    public void loadResources(Locale[] locales) {
        com.fr.web.ResourceHelper.forceInitJSCache(ResourceConstants.DEFAULT_THIRD_JS);
        com.fr.web.ResourceHelper.forceInitJSCache(ResourceConstants.DEFAULT_BASE_JS);
        com.fr.web.ResourceHelper.forceInitJSCache(ResourceConstants.DEFAULT_DESIGN_JS);
        com.fr.web.ResourceHelper.forceInitJSCache(ResourceConstants.DEFAULT_CONF_JS);
        com.fr.web.ResourceHelper.forceInitJSCache(ResourceConstants.DEFAULT_DESIGN_JS);
        com.fr.web.ResourceHelper.forceInitJSCache(ResourceConstants.DEFAULT_SHOW_JS);
        com.fr.web.ResourceHelper.forceInitJSCache(ResourceConstants.DEFAULT_MODULE_JS);

        BaseResourceHelper.FormulaTransmitter.transmit(BaseResourceHelper.getFormulaCollectionJS(), locales);

        com.fr.web.ResourceHelper.forceInitStyleCache(ResourceConstants.DEFAULT_THIRD_CSS);
        com.fr.web.ResourceHelper.forceInitStyleCache(ResourceConstants.DEFAULT_BASE_CSS);
        com.fr.web.ResourceHelper.forceInitStyleCache(ResourceConstants.DEFAULT_DESIGN_CSS);
        com.fr.web.ResourceHelper.forceInitStyleCache(ResourceConstants.DEFAULT_CONF_CSS);
        com.fr.web.ResourceHelper.forceInitStyleCache(ResourceConstants.DEFAULT_DESIGN_CSS);
        com.fr.web.ResourceHelper.forceInitStyleCache(ResourceConstants.DEFAULT_SHOW_CSS);
        com.fr.web.ResourceHelper.forceInitStyleCache(ResourceConstants.DEFAULT_MODULE_CSS);
    }

    @Override
    public Collection<BIPackageID> getAvailablePackID(long userId) {
        return BIConfigureManagerCenter.getAuthorityManager().getAuthPackagesByUser(userId);
    }

    @Override
    public Collection<BIPackageID> getAuthAvailablePackID(long userId) {
        return getAvailablePackID(userId);
    }

    @Override
    public void clearCacheAfterBuildCubeTask(long userId) {
        WidgetDataCacheManager.getInstance().clear();
    }

    private void registerSystemManager() {
    }

    @Override
    public Service[] service4Register() {
        return new Service[]{
                new Service4BIConfigure(),
                new Service4BIReport(),
                new Service4BIDezi(),
                new Service4BIBase(),
                new Service4FineCube(),
                new Service4BIPublic(),
        };
    }


}
