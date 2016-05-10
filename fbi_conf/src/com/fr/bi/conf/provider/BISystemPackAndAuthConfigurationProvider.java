package com.fr.bi.conf.provider;

import com.fr.bi.conf.base.pack.data.BIPackAndAuthority;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;

import java.util.Set;

/**
 * Created by wuk on 16/4/25.
 */
public interface BISystemPackAndAuthConfigurationProvider {
    String XML_TAG = "BIPackAndAuthManager";


    /**
     *
     * @return
     */
    Set<BIPackAndAuthority> getAllPackages(long userId);


    /**
     * 根据ID获得相应的业务包
     *
     * @param userId    用户ID
     * @param packageID 业务包ID
     * @return 业务包
     * @throws BIPackageAbsentException 业务包不存在
     */
    BIPackAndAuthority getPackageByID(long userId, String packageID) throws BIPackageAbsentException;

    /**
     * 添加一个业务包
     *
     * @param userId            用户ID
     * @param biPackAndAuthority 业务包
     * @throws BIPackageDuplicateException
     */
    void addPackage(long userId, BIPackAndAuthority biPackAndAuthority) throws BIPackageDuplicateException, Exception;

    /**
     * 持久化数据
     * TODO 应该按照规则自动调用
     *
     * @param userId 用户ID
     */
    @Deprecated
    void persistData(long userId);

    void updateAuthority(long userId, BIPackAndAuthority biPackAndAuthority) throws Exception;

    boolean containPackage(long userId,BIPackAndAuthority biPackAndAuthority);

}
