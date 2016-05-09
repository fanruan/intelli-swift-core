package com.fr.bi.conf.provider;

import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessPackageGetterService;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.base.pack.data.BIPackageName;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.json.JSONObject;

import java.util.Set;

/**
 * Created by wuk on 16/4/25.
 */
public interface BISystemPackAndAuthConfigurationProvider {
    String XML_TAG = "BIPackAndAuthManager";


    /**
     * 获取当前最新版本的业务包用于数据更新
     *
     * @return
     */
    Set<BIBusinessPackage> getAllPackages(long userId);



    /**
     * 更新
     */
    void envChanged();

    /**
     * 根据ID获得相应的业务包
     *
     * @param userId    用户ID
     * @param packageID 业务包ID
     * @return 业务包
     * @throws BIPackageAbsentException 业务包不存在
     */
    BIBusinessPackageGetterService getPackage(long userId, BIPackageID packageID) throws BIPackageAbsentException;





    /**
     * 添加一个业务包
     *
     * @param userId            用户ID
     * @param biBusinessPackage 业务包
     * @throws BIPackageDuplicateException
     */
    void addPackage(long userId, BIBusinessPackage biBusinessPackage) throws BIPackageDuplicateException;

    /**
     * 移除相应业务包
     *
     * @param userId    用户ID
     * @param packageID 业务包ID
     * @throws BIPackageAbsentException 业务包缺少抛错
     */
    void removePackage(long userId, BIPackageID packageID) throws BIPackageAbsentException;


    /**
     * 持久化数据
     * TODO 应该按照规则自动调用
     *
     * @param userId 用户ID
     */
    @Deprecated
    void persistData(long userId);

    /**
     * 根据业务包名字获得业务包
     *
     * @param userId   用户ID
     * @param packName 业务包名字
     * @return 业务包
     */
    Set<BIBusinessPackage> getPackageByName(long userId, BIPackageName packName);

    /**
     * 创建业务包的Json数据
     *
     * @param userId 用户ID
     * @return json数据
     * @throws Exception
     */
    JSONObject createPackageJSON(long userId) throws Exception;


}
