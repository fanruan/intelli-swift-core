package com.fr.bi.module;

import com.finebi.cube.api.ICubeDataLoaderCreator;
import com.finebi.cube.conf.*;
import com.finebi.cube.conf.datasource.BIDataSourceManager;
import com.finebi.cube.conf.pack.imp.BISystemPackageConfigurationManager;
import com.finebi.cube.conf.relation.BISystemTableRelationManager;
import com.finebi.cube.conf.singletable.SingleTableUpdateManager;
import com.finebi.cube.conf.timer.UpdateFrequencyManager;
import com.finebi.cube.conf.trans.BIAliasManager;
import com.fr.base.FRContext;
import com.fr.bi.DemoService;
import com.fr.bi.cal.BICubeManager;
import com.fr.bi.cal.log.BILogManager;
import com.fr.bi.cluster.ClusterAdapter;
import com.fr.bi.cluster.manager.ClusterManager;
import com.fr.bi.cluster.manager.EmptyClusterManager;
import com.fr.bi.cluster.utils.ClusterEnv;
import com.fr.bi.conf.base.auth.BISystemAuthorityManager;
import com.fr.bi.conf.base.cube.BISystemCubeConfManager;
import com.fr.bi.conf.base.login.BISystemUserLoginInformationManager;
import com.fr.bi.conf.manager.excelview.BIExcelViewManager;
import com.fr.bi.conf.manager.update.BIUpdateSettingManager;
import com.fr.bi.conf.provider.*;
import com.fr.bi.fs.BIReportNodeLockDAO;
import com.fr.bi.fs.BITableMapper;
import com.fr.bi.fs.HSQLBIReportDAO;
import com.fr.bi.fs.TableDataBIReportDAO;
import com.fr.bi.resource.ResourceConstants;
import com.fr.bi.resource.ResourceHelper;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.web.base.Service4BIBase;
import com.fr.bi.web.conf.Service4BIConfigure;
import com.fr.bi.web.dezi.mobile.Service4BIMobile;
import com.fr.bi.web.dezi.web.Service4BIDezi;
import com.fr.bi.web.report.Service4BIReport;
import com.fr.cluster.rpc.RPC;
import com.fr.data.core.db.DBUtils;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dialect.DialectFactory;
import com.fr.data.core.db.dml.Select;
import com.fr.data.core.db.dml.Table;
import com.fr.data.core.db.tableObject.Column;
import com.fr.data.core.db.tableObject.ColumnSize;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.data.pool.MemoryConnection;
import com.fr.file.DatasourceManager;
import com.fr.fs.control.dao.hsqldb.HSQLDBDAOControl;
import com.fr.fs.control.dao.tabledata.TableDataDAOControl;
import com.fr.fs.dao.FSDAOManager;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.fun.Service;
import com.fr.web.core.db.PlatformDB;

import java.sql.*;
import java.util.Iterator;

/**
 * Created by 小灰灰 on 2015/12/15.
 */
public class BICoreModule extends AbstractModule {
    @Override
    public void start() {
        registProviders();
        initDataSourcePool();
        registerClusterIfNeed();
        registerSystemManager();
        registDAO();
        registerResources();
        registerTableAddColumn();
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
    public ICubeDataLoaderCreator getCubeDataLoaderCreator() {
        return StableFactory.getMarkedObject(ICubeDataLoaderCreator.XML_TAG, ICubeDataLoaderCreator.class);
    }


    private void registProviders() {
        StableFactory.registerMarkedObject(BIUpdateFrequencyManagerProvider.XML_TAG, new BIUpdateSettingManager());
        StableFactory.registerMarkedObject(BISystemPackageConfigurationProvider.XML_TAG, getPackManagerProvider());
        StableFactory.registerMarkedObject(BIAuthorityManageProvider.XML_TAG, new BISystemAuthorityManager());
        StableFactory.registerMarkedObject(ICubeDataLoaderCreator.XML_TAG, com.finebi.cube.api.BICubeManager.getInstance());
        StableFactory.registerMarkedObject(BIDataSourceManagerProvider.XML_TAG, getSourceManagerProvider());
        StableFactory.registerMarkedObject(BIAliasManagerProvider.XML_TAG, getTransManagerProvider());
        StableFactory.registerMarkedObject(BITableRelationConfigurationProvider.XML_TAG, getConnectionManagerProvider());
        StableFactory.registerMarkedObject(BICubeManagerProvider.XML_TAG, getCubeManagerProvider());
        StableFactory.registerMarkedObject(BILogManagerProvider.XML_TAG, new BILogManager());
        StableFactory.registerMarkedObject(BIUserLoginInformationProvider.XML_TAG, new BISystemUserLoginInformationManager());
        StableFactory.registerMarkedObject(BIExcelViewManagerProvider.XML_TAG, new BIExcelViewManager());
        StableFactory.registerMarkedObject(BICubeConfManagerProvider.XML_TAG, new BISystemCubeConfManager());
        StableFactory.registerMarkedObject(UpdateFrequencyManager.XML_TAG, new UpdateFrequencyManager());
        StableFactory.registerMarkedObject(SingleTableUpdateManager.XML_TAG, new SingleTableUpdateManager());


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
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BICubeManager provider = new BICubeManager();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BICubeManagerProvider) RPC.getProxy(BICubeManager.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new BICubeManager();
        }

    }

    private BITableRelationConfigurationProvider generateConnectionManager() {

        if (ClusterEnv.isCluster()) {
//            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
//                BITableRelationConfigurationProvider provider = new BIConnectionManager();
//                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
//                return provider;
//            } else {
//                return (BITableRelationConfigurationProvider) RPC.getProxy(BIConnectionManager.class,
//                        ClusterAdapter.getManager().getHostManager().getIp(),
//                        ClusterAdapter.getManager().getHostManager().getPort());
//            }
            return null;
        } else {
            return new BISystemTableRelationManager();
        }
    }

    private BISystemPackageConfigurationProvider generateBusiPackManager() {
        if (ClusterEnv.isCluster()) {
//            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
//                BIBusiPackManager provider = new BIBusiPackManager();
//                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
//                return provider;
//            } else {
//                return (BISystemPackageConfigurationProvider) RPC.getProxy(BIBusiPackManager.class,
//                        ClusterAdapter.getManager().getHostManager().getIp(),
//                        ClusterAdapter.getManager().getHostManager().getPort());
//            }
            return null;
        } else {

            return new BISystemPackageConfigurationManager();
        }

    }

    private BIDataSourceManagerProvider generateDataSourceManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BIDataSourceManager provider = new BIDataSourceManager();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIDataSourceManagerProvider) RPC.getProxy(BIDataSourceManager.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new BIDataSourceManager();
        }

    }

    private BIAliasManagerProvider generateTransManager() {
        if (ClusterEnv.isCluster()) {
            if (ClusterAdapter.getManager().getHostManager().isSelf()) {
                BIAliasManager provider = new BIAliasManager();
                RPC.registerSkeleton(provider, ClusterAdapter.getManager().getHostManager().getPort());
                return provider;
            } else {
                return (BIAliasManagerProvider) RPC.getProxy(BIAliasManager.class,
                        ClusterAdapter.getManager().getHostManager().getIp(),
                        ClusterAdapter.getManager().getHostManager().getPort());
            }
        } else {
            return new BIAliasManager();
        }
    }


    private void initDataSourcePool() {
        synchronized (DatasourceManager.getInstance()) {
            Iterator<String> iterator = DatasourceManager.getProviderInstance().getConnectionNameIterator();
            while (iterator.hasNext()) {
                String name = iterator.next();
                JDBCDatabaseConnection connection = DatasourceManager.getProviderInstance().getConnection(name, JDBCDatabaseConnection.class);
                BIDBUtils.dealWithJDBCConnection(connection);
            }
            try {
                MemoryConnection.getConnectionMap().clear();
                FRContext.getCurrentEnv().writeResource(DatasourceManager.getInstance());
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }


    private void registerClusterIfNeed() {
        if (ClusterEnv.isCluster()) {
            try {
                ClusterManager.getInstance().initClusterEnv();
                ClusterAdapter.registerBIClusterManagerInterface(ClusterManager.getInstance());
            } catch (Exception ex) {
                BILogger.getLogger().error(ex.getMessage(), ex);
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
                BILogger.getLogger().error(e.getMessage(), e);
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

    private void registDAO() {
        StableFactory.registerMarkedObject(HSQLDBDAOControl.class.getName(), HSQLBIReportDAO.getInstance());
        StableFactory.registerMarkedObject(TableDataDAOControl.class.getName(), TableDataBIReportDAO.getInstance());
        StableFactory.registerMarkedObject(BIReportNodeLockDAO.class.getName(), BIReportNodeLockDAO.getInstance());
    }

    private void registerResources() {
        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_BASE_JS, ResourceHelper.getBaseJs());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_BASE_CSS, ResourceHelper.getBaseCss());

        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_DATA_JS, ResourceHelper.getDataJS(), ResourceHelper.DataTransmitter);

        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_CONF_JS, ResourceHelper.getConfJs());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_CONF_CSS, ResourceHelper.getConfCss());

        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_DESIGN_JS, ResourceHelper.getDeziJs());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_DEZI_CSS, ResourceHelper.getDeziCss());

        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_SHOW_JS, ResourceHelper.getShowJs());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_SHOW_CSS, ResourceHelper.getShowCss());

        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_MODULE_JS, ResourceHelper.getCommonJs());
        StableFactory.registerStyleFiles(ResourceConstants.DEFAULT_MODULE_CSS, ResourceHelper.getCommonCss());

        StableFactory.registerJavaScriptFiles(ResourceConstants.DEFAULT_FORMULA_JS, ResourceHelper.getFormulaCollectionJS(), ResourceHelper.FormulaTransmitter);
    }

    private void registerSystemManager() {
    }

    @Override
    public Service[] service4Register() {
        return new Service[]{
                new Service4BIConfigure(),
                new Service4BIReport(),
                new Service4BIDezi(),
                new Service4BIMobile(),
                new Service4BIBase(),

                new DemoService()
        };
    }


}
