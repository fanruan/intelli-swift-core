package com.fr.bi;


import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.BITableRelationConfigurationProvider;
import com.fr.bi.cal.report.BIActor;
import com.fr.bi.cal.report.db.DialectCreatorImpl;
import com.fr.bi.conf.VT4FBI;
import com.fr.bi.conf.utils.BIModuleManager;
import com.fr.bi.fs.BITableMapper;
import com.fr.bi.module.BICoreModule;
import com.fr.bi.module.BIModule;
import com.fr.bi.resource.ResourceHelper;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BIClassUtils;
import com.fr.data.dao.FieldColumnMapper;
import com.fr.data.dao.MToMRelationFCMapper;
import com.fr.data.dao.ObjectTableMapper;
import com.fr.data.dao.RelationFCMapper;
import com.fr.fs.AbstractFSPlate;
import com.fr.fs.base.entity.PlatformManageModule;
import com.fr.fs.control.dao.tabledata.TableDataDAOControl.ColumnColumn;
import com.fr.fs.dao.BIReportEntryDAO;
import com.fr.fs.dao.EntryDAO;
import com.fr.general.FRLogger;
import com.fr.general.GeneralContext;
import com.fr.general.Inter;
import com.fr.plugin.ExtraClassManager;
import com.fr.stable.ActorConstants;
import com.fr.stable.ActorFactory;
import com.fr.stable.ArrayUtils;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.fun.Service;
import com.fr.stable.plugin.PluginSimplify;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * BI模块启动时做的一些初始化工作，通过反射调用
 */
public class BIPlate extends AbstractFSPlate {

    @Override
    public void initData() {
//        SystemFactoryRegister.systemRegister();
        initModules();
        super.initData();
        startModules();
        initPlugin();
        initOOMKillerForLinux();
        StableFactory.getMarkedObject(BICubeManagerProvider.XML_TAG, BICubeManagerProvider.class).generateCubes();
    }

    private void initOOMKillerForLinux() {
        String os = System.getProperty("os.name");
        BILogger.getLogger().info("OS:" + os);
        if(os.toUpperCase().contains("LINUX")){
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String pid = name.split("@")[0];
            try {
                String cmd = "echo -17 > /proc/" + pid +"/oom_adj";
                BILogger.getLogger().info("execute command:" + cmd);
                Runtime.getRuntime().exec(new String[]{"/bin/sh","-c", cmd});
            } catch (IOException e) {
                BILogger.getLogger().error(e.getMessage(), e);
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
        for (Class c : BIClassUtils.getClasses("com.fr.bi.module")) {
            if (BIModule.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers())) {
                try {
                    BIModule module = (BIModule) c.newInstance();
                    BIModuleManager.registModule(module);
                } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
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
            ExtraClassManager.getInstance().addDialectCreator(new DialectCreatorImpl(), PluginSimplify.create("bi", "bi.db.ads"));
        } catch (Exception e) {
            FRLogger.getLogger().error(e.getMessage(), e);
        }
    }


    @Override
    public String[] getPlateStyleFiles4WebClient() {
        return (String[]) ArrayUtils.addAll(ResourceHelper.getFoundationCss(), new String[]{
                "/com/fr/bi/web/cross/css/bi.toolbar.add.css",
                "/com/fr/bi/web/cross/css/bi.segment.css",
                "/com/fr/bi/web/cross/css/bi.combo.css",
                "/com/fr/bi/web/cross/css/bi.button.css",

                "/com/fr/bi/web/cross/css/bi.button.css",
                "/com/fr/bi/web/cross/css/bi.shared.table.css",
                "/com/fr/bi/web/cross/css/bi.quarter.css",

                "/com/fr/bi/web/cross/css/bi.label.css",
                "/com/fr/bi/web/cross/css/bi.title.css",

                "/com/fr/bi/web/cross/css/bi.extra.dialog.css",
                "/com/fr/bi/web/cross/css/bi.edit.dialog.css",
                "/com/fr/bi/web/cross/css/bi.share.dialog.css",
                "/com/fr/bi/web/cross/css/bi.quicklist.css",
                "/com/fr/bi/web/cross/css/bi.rename.dialog.css",

                "/com/fr/bi/web/cross/css/bi.template.list.css",
                "/com/fr/bi/web/cross/css/bi.template.createdlist.css",

                "/com/fr/bi/web/cross/css/theme/bi.theme.css",

                "/com/fr/bi/web/cross/css/bi.text.css",
        });
    }

    /**
     * 启动时BI加载的js文件
     *
     * @return 路径地址
     */
    @Override
    public String[] getPlateJavaScriptFiles4WebClient() {
        return (String[]) ArrayUtils.addAll(ResourceHelper.getFoundationJs(), new String[]{
                "/com/fr/bi/web/cross/js/bi.user.manager.js",
                "/com/fr/bi/web/cross/js/effect/create.by.me.js",
                "/com/fr/bi/web/cross/js/effect/share.to.me.js",
                "/com/fr/bi/web/cross/js/effect/allreports.js",
                "/com/fr/bi/web/cross/js/bi.share.js",
                "/com/fr/bi/web/cross/js/theme/bi.theme.js",
                "/com/fr/bi/web/cross/js/theme/bi.widget.newanalysis.js",
                "/com/fr/bi/web/cross/js/bi.toolbar.add.js",
                "/com/fr/bi/web/cross/js/bi.extra.dialog.js",
                "/com/fr/bi/web/cross/js/bi.segment.js",
                "/com/fr/bi/web/cross/js/bi.combo.js",
                "/com/fr/bi/web/cross/js/bi.button.js",

                "/com/fr/bi/web/cross/js/bi.label.js",
                "/com/fr/bi/web/cross/js/bi.title.js"
        });
    }


    @Override
    public PlatformManageModule[] supportPlatformManageModules() {
        return new PlatformManageModule[]{
                new PlatformManageModule("BI-Data_Setting", Inter.getLocText("BI_Data_Settings"), 15, 1, true)
        };
    }

    static {
        VT4FBI.dealWithLic();
    }

    @Override
    public String[] getLocaleFile() {
        return new String[]{"com/fr/bi/stable/locale/fbi"};
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
        ObjectTableMapper[] mappers = new ObjectTableMapper[]{
                BITableMapper.BI_REPORT_NODE.TABLE_MAPPER,
                BITableMapper.BI_SHARED_REPORT_NODE.TABLE_MAPPER,
                BITableMapper.BI_CREATED_TEMPLATE_FOLDER.TABLE_MAPPER,
                BITableMapper.BI_REPORT_NODE_LOCK.TABLE_MAPPER
        };
        return mappers;
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
        return null;
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

    }

    /**
     * 目前主要做兼容的一些东西
     */
    @Override
    public void refreshManager() {

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

}
