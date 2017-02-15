package com.fr.bi.web.conf.utils;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.file.DatasourceManager;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/22.
 */
public class BIWebConfUtils {
    public static boolean checkCubeVersion(CubeTableSource source, long userId) {
        if (source == null) {
            return false;
        }
        ICubeDataLoader loader = BICubeManager.getInstance().fetchCubeLoader(userId);
        Iterator<CubeTableSource> it = source.createSourceMap().values().iterator();
        try {
            while (it.hasNext()) {
                CubeTableSource key = it.next();
                loader.getTableIndex(key);
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 数据连接
     * 业务包管理
     * 多路径设置
     * 权限配置管理
     * FineIndex更新
     *
     * @param userId
     * @return
     */
    public static JSONObject getAuthDataConfigNodes(long userId) throws Exception {
        JSONObject jo = new JSONObject();
        if (ComparatorUtils.equals(userId, UserControl.getInstance().getSuperManagerID())) {
            Iterator names = DatasourceManager.getInstance().getConnectionNameIterator();
            JSONArray nameJA = new JSONArray();
            while (names.hasNext()) {
                nameJA.put(names.next());
            }
            jo.put(DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION, nameJA);
            jo.put(DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER, nameJA);
            jo.put(DBConstant.DATA_CONFIG_AUTHORITY.MULTI_PATH_SETTING, true);
            jo.put(DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_AUTHORITY, true);
            jo.put(DBConstant.DATA_CONFIG_AUTHORITY.FINE_INDEX_UPDATE, true);
        } else {
            Set<BIDataConfigAuthority> authoritySet = BIConfigureManagerCenter.getDataConfigAuthorityManager().getDataConfigAuthoritiesByUserId(userId);
            JSONArray authConn = new JSONArray();
            JSONArray authPackageConn = new JSONArray();
            for (BIDataConfigAuthority authority : authoritySet) {
                String id = authority.getId();
                String pId = authority.getParentId();
                if (ComparatorUtils.equals(pId, DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION)) {
                    authConn.put(id.split(DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION)[1]);
                }
                if (ComparatorUtils.equals(pId, DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER)) {
                    authPackageConn.put(id.split(DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER)[1]);
                }
                if (ComparatorUtils.equals(id, DBConstant.DATA_CONFIG_AUTHORITY.MULTI_PATH_SETTING)) {
                    jo.put(DBConstant.DATA_CONFIG_AUTHORITY.MULTI_PATH_SETTING, true);
                }
                if (ComparatorUtils.equals(id, DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_AUTHORITY)) {
                    jo.put(DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_AUTHORITY, true);
                }
                if (ComparatorUtils.equals(id, DBConstant.DATA_CONFIG_AUTHORITY.FINE_INDEX_UPDATE)) {
                    jo.put(DBConstant.DATA_CONFIG_AUTHORITY.FINE_INDEX_UPDATE, true);
                }
            }
            if (authConn.length() > 0) {
                jo.put(DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION, authConn);
            }
            if (authPackageConn.length() > 0) {
                jo.put(DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER, authPackageConn);
            }
        }
        return jo;
    }

}