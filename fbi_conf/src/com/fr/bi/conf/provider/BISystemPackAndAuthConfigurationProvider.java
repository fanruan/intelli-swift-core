package com.fr.bi.conf.provider;

import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.json.JSONObject;

import java.util.Set;

/**
 * Created by wuk on 16/4/25.
 */
public interface BISystemPackAndAuthConfigurationProvider {
    String XML_TAG = "BIPackAndAuthManager";
//    String XML_TAG = "BIBusiPackManager";


    /**
     * 创建业务包的Json数据
     *
     * @param userId 用户ID
     * @return json数据
     * @throws Exception
     */
    JSONObject createPackageJSON(long userId) throws Exception;
    /**
     * 获取当前最新版本的业务包用于数据更新
     *
     * @return
     */
    Set<BIBusinessPackage> getAllPackages(long userId);

}
