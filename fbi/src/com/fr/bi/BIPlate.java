package com.fr.bi;


import com.finebi.ProductConstants;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.BITableRelationConfigurationProvider;
import com.fr.base.ClassUtils;
import com.fr.base.FRContext;
import com.fr.bi.cal.generate.BackUpUtils;
import com.fr.bi.cal.report.BIActor;
import com.fr.bi.cal.report.db.DialectCreatorImpl;
import com.fr.bi.cal.report.db.HiveDialectCreatorImpl;
import com.fr.bi.cal.report.db.KylinDialectCreatorImpl;
import com.fr.bi.cal.report.db.PostgreDialectCreatorImpl;
import com.fr.bi.conf.VT4FBI;
import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.utils.BIModuleManager;
import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.fs.BISharedReportNode;
import com.fr.bi.fs.BITableMapper;
import com.fr.bi.fs.entry.BIReportEntry;
import com.fr.bi.fs.entry.BIReportEntryDAO;
import com.fr.bi.fs.entry.EntryConstants;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.module.BICoreModule;
import com.fr.bi.module.BIModule;
import com.fr.bi.resource.FsResourceHelper;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.web.base.utils.BIWebUtils;
import com.fr.data.core.db.DBUtils;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dialect.DialectFactory;
import com.fr.data.core.db.tableObject.Column;
import com.fr.data.core.db.tableObject.ColumnSize;
import com.fr.data.dao.DAOUtils;
import com.fr.data.dao.FieldColumnMapper;
import com.fr.data.dao.MToMRelationFCMapper;
import com.fr.data.dao.ObjectTableMapper;
import com.fr.data.dao.RelationFCMapper;
import com.fr.fs.AbstractFSPlate;
import com.fr.fs.FSConfig;
import com.fr.fs.base.entity.User;
import com.fr.fs.basic.SystemAttr;
import com.fr.fs.basic.SystemStyle;
import com.fr.fs.control.EntryControl;
import com.fr.fs.control.EntryPoolFactory;
import com.fr.fs.control.UserControl;
import com.fr.fs.control.dao.tabledata.TableDataDAOControl.ColumnColumn;
import com.fr.fs.dao.EntryDAO;
import com.fr.fs.dao.FSDAOManager;
import com.fr.fs.schedule.entry.FolderEntry;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.general.FRLogger;
import com.fr.general.GeneralContext;
import com.fr.general.GeneralUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.plugin.ExtraClassManager;
import com.fr.stable.ActorConstants;
import com.fr.stable.ActorFactory;
import com.fr.stable.ArrayUtils;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.StableUtils;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.fun.Service;
import com.fr.stable.plugin.PluginSimplify;
import com.fr.web.core.db.PlatformDB;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * BI模块启动时做的一些初始化工作，通过反射调用
 */
public class BIPlate extends AbstractFSPlate {
    private static BILogger LOGGER = BILoggerFactory.getLogger(BIPlate.class);
    private final static String BI_REPORT_SUFFIX = ".fbi";

    @Override
    public void initData() {
        try {
            FRContext.getCurrentEnv().setBuildFilePath("bibuild.txt");
            LOGGER.info("FINE BI :" + GeneralUtils.readBuildNO());
            initModules();
            super.initData();
            startModules();
            initPlugin();
            registerEntrySomething();
            initOOMKillerForLinux();
            loadMemoryData();
            //backupWhenStart();
            addBITableColumn4NewConnection();
            addSharedTableColumn4NewConnection();

            //兼容FR工程中可能存在BID这一列的情况
            dropColumnBID();
            //兼容FR工程中可能存在PARENTID类型是整型的情况
            notifyColumnParentIdType();

            migrateBIReport();
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    // BI模板迁移
    private void migrateBIReport() {
        try {
            String reportsPath = FRContext.getCurrentEnv().getPath() + File.separator + "biReport";
            File reportsFile = new File(reportsPath);
            File[] files = reportsFile.listFiles();
            if (files == null) {
                return;
            }
            for (int i = 0; i < files.length; i++) {
                File singleUserFile = files[i];
                String fileName = singleUserFile.getName();     // 用户id
                long userId = GeneralUtils.string2Number(fileName).longValue();
                User user = UserControl.getInstance().getUser(userId);
                if (user == null) {
                    continue;
                }
                // 读migrate中所有.fbi文件
                String basePath = singleUserFile.getAbsolutePath();
                File migrate = new File(basePath + File.separator + "migrate");
                File[] migrateFiles = migrate.listFiles();
                if (migrateFiles == null) {
                    continue;
                }
                for (int j = 0; j < migrateFiles.length; j++) {
                    File mFile = migrateFiles[j];
                    String mFileName = mFile.getName();
                    if (!mFileName.endsWith(BI_REPORT_SUFFIX)) {
                        continue;
                    }
                    // 复制到biReport 并执行添加模板操作
                    String newFile = DateUtils.getDate2AllIncludeSSS(new Date()) + "_" + new Random().nextInt(1000);
                    File migrateFile = new File(basePath, newFile + BI_REPORT_SUFFIX);
                    boolean result = mFile.renameTo(migrateFile);
                    if (result) {
                        BIReportNode node = new BIReportNode(userId, BIReportConstant.REPORTS_ROOT_NODE, newFile, File.separator + newFile + BI_REPORT_SUFFIX, null);
                        BIDAOUtils.getBIDAOManager().saveOrUpDate(node, userId);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void backupWhenStart() {
        if (PerformancePlugManager.getInstance().isBackupWhenStart()) {
            BackUpUtils.backup();
        }
    }

    public void loadMemoryData() {
        try {
            loadResources();
            BICubeConfigureCenter.getAliasManager().getTransManager(UserControl.getInstance().getSuperManagerID());
            BIConnectionManager.getBIConnectionManager();
            BICubeConfigureCenter.getTableRelationManager().getAllTablePath(UserControl.getInstance().getSuperManagerID());
            BICubeConfigureCenter.getDataSourceManager().getAllBusinessTable();
            BIConfigureManagerCenter.getUpdateFrequencyManager().getUpdateSettings(UserControl.getInstance().getSuperManagerID());

            /// TODO: 2017/6/2 目前通过维护packageManager中的Map来提供分析可用资源的获取，真正解决需要从cube配置中获取到当前Cube中存在的资源
            BICubeConfigureCenter.getPackageManager().getAllPackages(UserControl.getInstance().getSuperManagerID());
            BICubeConfigureCenter.getPackageManager().initialAnalysisResourceMap(UserControl.getInstance().getSuperManagerID());
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    private void loadResources() {
        if (!StableUtils.isDebug()) {
            Locale[] locales = new Locale[]{Locale.CHINA, Locale.US};
            for (Locale locale : locales) {
                try {
                    com.fr.web.ResourceHelper.createDefaultJs(locale);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            try {
                com.fr.web.ResourceHelper.createDefaultCss();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            for (BIModule module : BIModuleManager.getModules()) {
                module.loadResources(locales);
            }
        }
    }

    private void registerEntrySomething() {
        EntryPoolFactory.registerEntryDAO(EntryConstants.BIREPORT, BIReportEntryDAO.getInstance());
        EntryPoolFactory.registerEntry("bireport", BIReportEntry.class);
        EntryPoolFactory.registerEntryTableNames(new String[]{BIReportEntry.TABLE_NAME});
        EntryPoolFactory.registerMobileEntryTableNames(new String[]{BIReportEntry.TABLE_NAME});
    }

    private void addBITableColumn4NewConnection() {
        Connection cn = null;
        String tableName = BIReportEntry.TABLE_NAME;
        try {
            cn = PlatformDB.getDB().createConnection();
            try {
                cn.setAutoCommit(false);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            Dialect dialect = DialectFactory.generateDialect(cn, PlatformDB.getDB().getDriver());
            FSDAOManager.addTableColumn(cn, dialect,
                    new Column("createBy", Types.BIGINT, new ColumnSize(10)), tableName);
            cn.commit();
        } catch (Exception e) {
            if (cn != null) {
                try {
                    cn.rollback();
                } catch (SQLException e1) {
                    LOGGER.error(e1.getMessage(), e1);
                }
            }
        } finally {
            DBUtils.closeConnection(cn);
        }
    }

    private static void addSharedTableColumn4NewConnection() {
        Connection cn = null;
        String tableName = "FR_T_" + DAOUtils.getClassNameWithOutPath(BISharedReportNode.class);
        try {
            cn = PlatformDB.getDB().createConnection();
            try {
                cn.setAutoCommit(false);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            Dialect dialect = DialectFactory.generateDialect(cn, PlatformDB.getDB().getDriver());
            FSDAOManager.addTableColumn(cn, dialect,
                    new Column("createByName", Types.VARCHAR, new ColumnSize(50)), tableName);
            FSDAOManager.addTableColumn(cn, dialect,
                    new Column("shareToName", Types.VARCHAR, new ColumnSize(50)), tableName);
            cn.commit();
        } catch (Exception e) {
            if (cn != null) {
                try {
                    cn.rollback();
                } catch (SQLException e1) {
                    FRContext.getLogger().error(e1.getMessage(), e1);
                }
            }

            FRContext.getLogger().info("Add" + tableName + "Column Action Failed!");
            FRContext.getLogger().info(e.getMessage());
        } finally {
            DBUtils.closeConnection(cn);
        }
    }

    private static void dropColumnBID() {
        Connection cn = null;
        String tableName = "FR_T_" + DAOUtils.getClassNameWithOutPath(BIReportNode.class);
        try {
            cn = PlatformDB.getDB().createConnection();
            Statement st = cn.createStatement();
            st.execute("ALTER TABLE " + tableName + " DROP BID ");
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        } finally {
            DBUtils.closeConnection(cn);
        }
    }

    private static void notifyColumnParentIdType() {
        Connection cn = null;
        String tableName = "FR_T_" + DAOUtils.getClassNameWithOutPath(BIReportNode.class);
        try {
            cn = PlatformDB.getDB().createConnection();
            Statement st = cn.createStatement();
            st.execute("ALTER TABLE " + tableName + "  ALTER COLUMN PARENTID VARCHAR (255)");
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        } finally {
            DBUtils.closeConnection(cn);
        }
    }

    private void initOOMKillerForLinux() {
        String os = System.getProperty("os.name");
        LOGGER.info("OS:" + os);
        if (os.toUpperCase().contains("LINUX")) {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String pid = name.split("@")[0];
            try {
                String cmd = "echo -17 > /proc/" + pid + "/oom_adj";
                LOGGER.info("execute command:" + cmd);
                Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
                String cmd2 = "echo -1000 > /proc/" + pid + "/oom_score_adj";
                LOGGER.info("execute command:" + cmd2);
                Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd2});
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }

        }
    }

    private void startModules() {
        for (BIModule module : BIModuleManager.getModules()) {
            module.start();
        }
    }

    private void initModules() {
        BIModuleManager.registModule(new BICoreModule());
        Set<Class<?>> set = ClassUtils.getClasses("com.fr.bi.module");
        set.addAll(ClassUtils.getClasses("com.fr.bi.test"));
        for (Class c : set) {
            if (BIModule.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers())) {
                try {
                    BIModule module = (BIModule) c.newInstance();
                    BIModuleManager.registModule(module);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    private static void changeSystemEnv() {
        StableFactory.getMarkedObject(BISystemPackageConfigurationProvider.XML_TAG, BISystemPackageConfigurationProvider.class).envChanged();
        StableFactory.getMarkedObject(BITableRelationConfigurationProvider.XML_TAG, BITableRelationConfigurationProvider.class).envChanged();

    }

    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                synchronized (BIPlate.class) {
                    changeSystemEnv();
                }
            }
        });
    }

    private void initPlugin() {
        try {
            ExtraClassManager.getInstance().addMutable(DialectCreatorImpl.XML_TAG, new DialectCreatorImpl(), PluginSimplify.create("bi", "com.fr.bi.plugin.db.ads"));
            ExtraClassManager.getInstance().addMutable(KylinDialectCreatorImpl.XML_TAG, new KylinDialectCreatorImpl(), PluginSimplify.create("bi", "com.fr.bi.plugin.db.kylin"));
            ExtraClassManager.getInstance().addMutable(HiveDialectCreatorImpl.XML_TAG, new HiveDialectCreatorImpl(), PluginSimplify.create("bi", "com.fr.bi.plugin.db.hive"));
            ExtraClassManager.getInstance().addMutable(PostgreDialectCreatorImpl.XML_TAG, new PostgreDialectCreatorImpl(), PluginSimplify.create("bi", "com.fr.bi.plugin.db.postgre"));
        } catch (Exception e) {
            FRLogger.getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public String[] getPlateStyleFiles4WebClient() {
        return ArrayUtils.addAll(FsResourceHelper.getFsCss(), new String[]{
                "/com/fr/bi/web/cross/css/bi.toolbar.add.css",
                "/com/fr/bi/web/cross/css/bi.shared.table.css",

                "/com/fr/bi/web/cross/css/theme/bi.chartpreview.css",
                "/com/fr/bi/web/cross/css/theme/bi.stylesetting.css",
                "/com/fr/bi/web/cross/css/theme/bi.theme.css",
                "/com/fr/bi/web/cross/css/usermanager/bi.usermanager.css",

                "/com/fr/bi/web/cross/css/reporthangout/hangoutreport.plate.css",
        });
    }

    /**
     * 启动时BI加载的js文件
     *
     * @return 路径地址
     */
    @Override
    public String[] getPlateJavaScriptFiles4WebClient() {
        return (String[]) ArrayUtils.addAll(FsResourceHelper.getFsJs(), new String[]{
                "/com/fr/bi/web/cross/js/usermanager/bi.usermanager.js",
                "/com/fr/bi/web/cross/js/bi.user.manager.js",
                "/com/fr/bi/web/cross/js/bi.share.js",
                "/com/fr/bi/web/cross/js/theme/bi.chartpreview.js",
                "/com/fr/bi/web/cross/js/theme/bi.stylesetting.js",
                "/com/fr/bi/web/cross/js/theme/bi.theme.js",
                "/com/fr/bi/web/cross/js/theme/bi.widget.newanalysis.js",
                "/com/fr/bi/web/cross/js/bi.toolbar.add.js",
                "/com/fr/bi/web/cross/js/bi.directory.edit.js",
                "/com/fr/bi/web/cross/js/reporthangout/hangoutreport.plate.js",
                "/com/fr/bi/web/cross/js/reporthangout/node/node.multilayer.arrow.plate.js",
                "/com/fr/bi/web/cross/js/reporthangout/bireportdialog.js",
                "/com/fr/bi/web/cross/js/configauth/bi.config.authmanage.js"
        });
    }

    static {
        VT4FBI.dealWithLic();
    }

    @Override
    public String[] getLocaleFile() {
        return new String[]{"com/fr/bi/web/locale/fbi"};
    }

    static {
        ActorFactory.registerActor(ActorConstants.TYPE_BI, new BIActor());
    }


    /**
     * 注册使用的需要service
     *
     * @return 所有需要注册的服务
     */
    @Override
    public Service[] service4Register() {
        List<Service> list = new ArrayList<Service>();
        for (BIModule module : BIModuleManager.getModules()) {
            Service[] service = module.service4Register();
            if (service != null) {
                list.addAll(Arrays.asList(service));
            }
        }
        return list.toArray(new Service[list.size()]);
    }

    /**
     * 注册getRelationClass对象的表结构
     *
     * @return 所有需要注册的表结构
     */
    @Override
    public ObjectTableMapper[] mappers4Register() {
        return new ObjectTableMapper[]{
                BITableMapper.BI_REPORT_NODE.TABLE_MAPPER,
                BITableMapper.BI_SHARED_REPORT_NODE.TABLE_MAPPER,
                BITableMapper.BI_CREATED_TEMPLATE_FOLDER.TABLE_MAPPER,
                BITableMapper.BI_REPORT_NODE_LOCK.TABLE_MAPPER,
                BITableMapper.BI_CONF_TABLE_LOCK.TABLE_MAPPER,
                BITableMapper.BI_BUSINESS_PACK_CONFIG_LOCK.TABLE_MAPPER,
                BIReportEntry.TABLE_MAPPER
        };
    }

    @Override
    public Class<?> getRelationClass() {
        return null;
    }

    /**
     * 注册Company角色表权限关系映射到对象到company表的内容中
     *
     * @return 模块和公司角色对应的权限关系
     */
    @Override
    public FieldColumnMapper[] columnMappers4Company() {
        return new FieldColumnMapper[]{};
    }

    /**
     * 注册Custom角色权限表关系映射到对象到custom表的内容中
     *
     * @return 模块和自定义角色对应的权限关系
     */
    @Override
    public FieldColumnMapper[] columnMappers4Custom() {
        return new FieldColumnMapper[]{};
    }


    @Override
    public RelationFCMapper getRelationFCMapper4Custom() {
        return new MToMRelationFCMapper("bisPrivileges", getRelationClass());
    }

    @Override
    public RelationFCMapper getRelationFCMapper4Company() {
        return new MToMRelationFCMapper("bisPrivileges", getRelationClass());
    }

    /**
     * 创建id为 id的plate getRelationClass所对应的对象
     *
     * @param id 对象的ID
     * @return 创建的对象(dao)
     */
    @Override
    public Object createPrivilegeObject(long id) {
        return null;
    }

    @Override
    public List<String> getAllPrivilegesID() {
        return new ArrayList<String>();
    }

    @Override
    public ColumnColumn[] getTableDataColumns() {
        return new ColumnColumn[0];
    }


    /**
     * 释放一些东东
     */
    @Override
    public void release() {
        // do nothing
    }

    /**
     * 目前主要做兼容的一些东西
     */
    @Override
    public void refreshManager() {
        // do nothing
    }

    /**
     * 注册文件是否支持此模块
     *
     * @return 产品是否支持此模块
     */
    @Override
    public boolean isSupport() {
        return true;
    }

    /**
     * 是否需要权限控制
     *
     * @return 模块是否需要权限支持
     */
    @Override
    public boolean needPrivilege() {
        return false;
    }

    @Override
    public List<EntryDAO> getEntryDaoAccess() {
        List<EntryDAO> daoList = new ArrayList<EntryDAO>();
        daoList.add(BIReportEntryDAO.getInstance());
        return daoList;
    }

    @Override
    public String getBuildNO() {
        return GeneralUtils.readBuildNO();
    }

    @Override
    public String getVersion() {
        return ProductConstants.RELEASE_VERSION;
    }

    @Override
    public boolean shouldOverrideEntrance() {
        return true;
    }

    /**
     * 重写平台页面入口
     *
     * @param req
     * @param res
     * @param map
     * @throws Exception
     */
    @Override
    public void overrideOperate(HttpServletRequest req, HttpServletResponse res, Map<String, Object> map) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        loadStyleSettings(map);
        loadIcons(map, userId);
        loadFavoriteReports(map, userId);
        initAnalysisConfigAuth(map, userId);
        WebUtils.writeOutTemplate(searchTemplatePath(req), res, map);
    }

    private void loadStyleSettings(Map<String, Object> map) throws Exception {
        JSONObject styleSettings = JSONObject.create();
        SystemAttr systemAttr = FSConfig.getProviderInstance().getSystemAttr();
        SystemStyle systemStyle = FSConfig.getProviderInstance().getSystemStyle();
        /*
        * BI-9466 框架变了，老配色不适合新框架
        * */
        if (updateToVersion41()) {
            useDefalutStyles(systemAttr, systemStyle);
        }
        boolean isLoginImg = systemAttr.isLoginPageImg();
        styleSettings.put("isLoginImg", isLoginImg);
        if (isLoginImg) {
            styleSettings.put("loginImg", systemAttr.getLoginImageID4FS());
        } else {
            styleSettings.put("loginUrl", systemAttr.getLoginUrl4FS());
        }
        styleSettings.put("loginTitle", systemAttr.getLoginTitle4FS());
        styleSettings.put("logoType", systemAttr.getBannerType().toInt());
        styleSettings.put("logoImg", systemAttr.getLogoImageID4FS());
        boolean isBackgroundImg = systemAttr.isBackgroundImg();
        styleSettings.put("isBackgroundImg", isBackgroundImg);
        styleSettings.put("backgroundImg", systemAttr.getBgImageID4FS());
        styleSettings.put("customBackgroundColor", systemAttr.getCustomBackgroundColor());
        if (!isBackgroundImg) {
            styleSettings.put("backgroundColorIndex", systemAttr.getBackgroundColor());
        }
        styleSettings.put("colorSchema", systemStyle.getColorScheme());
        styleSettings.put("customColors", systemStyle.getCustomColorsAsArray());
        map.put("styleSettings", styleSettings);
    }

    private boolean updateToVersion41() {
        //todo 等Lucifer判断版本的方法过来后，直接调用
        return false;
    }

    private void useDefalutStyles(SystemAttr systemAttr, SystemStyle systemStyle) throws JSONException {
        systemAttr.setBackgroundColor(0);
        systemAttr.setCustomBackgroundColor("rgb(38,77,132)");
        systemStyle.setColorScheme(0);
        systemStyle.setCustomColors("[]");
    }

    private void loadIcons(Map<String, Object> result, long userId) throws Exception {
        FolderEntry[] folders = EntryControl.getInstance().getRootNode().getShowFolderEntrys(userId);
        JSONArray iconInfo = JSONArray.create();
        for (FolderEntry folder : folders) {
            JSONObject f = JSONObject.create();
            f.put("text", folder.getDisplayName());
            f.put("id", folder.getId());
            f.put("value", FSConfig.getProviderInstance().getSystemStyle().getFolderIconById(folder.getId()));
            iconInfo.put(f);
        }
        result.put("icons", iconInfo);
    }

    private void initAnalysisConfigAuth(Map<String, Object> map, long userId) throws Exception {
        int auth = BIWebUtils.getUserEditViewAuth(userId);
        map.put("analysisAuth", ComparatorUtils.equals(auth, BIReportConstant.REPORT_AUTH.EDIT));
        map.put("configAuth", BIWebUtils.showDataConfig(userId));
    }

    private void loadFavoriteReports(Map<String, Object> result, long userId) throws Exception {
        result.put("favoriteReports", UserControl.getInstance().getFavoriteNodesInfo(userId, com.fr.fs.web.platform.entry.EntryConstants.MOBILECONFIG.PC));
    }

    private String searchTemplatePath(HttpServletRequest req) {
        if ("true".equals(WebUtils.getHTTPRequestParameter(req, "debug"))) {
            return "/com/fr/bi/web/html/index_debug.html";
        } else {
            return "/com/fr/bi/web/html/index.html";
        }
    }
}
