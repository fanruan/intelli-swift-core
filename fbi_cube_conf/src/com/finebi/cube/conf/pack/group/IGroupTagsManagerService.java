package com.finebi.cube.conf.pack.group;

import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.pack.data.BIGroupTagName;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.bi.common.inter.Release;
import com.fr.bi.conf.data.pack.exception.BIGroupAbsentException;
import com.fr.bi.conf.data.pack.exception.BIGroupDuplicateException;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;

/**
 * 业务包分组提供的服务
 * Created by Connery on 2015/12/22.
 */

public interface IGroupTagsManagerService extends Release, IGroupTagsManagerGetterService {

    void createEmptyGroup(BIGroupTagName groupName, long position) throws BIGroupDuplicateException;

    void addPackage(BIGroupTagName groupName, BIBusinessPackage pack) throws BIGroupAbsentException, BIPackageDuplicateException;

    void removeGroup(BIGroupTagName groupTagName) throws BIGroupAbsentException;

    /**
     * 从指定分组中移除指定业务包
     *
     * @param groupTagName
     * @param pack
     * @throws BIGroupAbsentException
     * @throws BIPackageAbsentException
     */
    void removeSpecificGroupPackageTag(BIGroupTagName groupTagName, BIBusinessPackage pack) throws BIGroupAbsentException, BIPackageAbsentException;

    /**
     * 在分组中删除全部关于指定业务包的引用
     *
     * @param packageID
     * @throws BIPackageAbsentException
     */
    void removePackage(BIPackageID packageID) throws BIPackageAbsentException;

    void rename(BIGroupTagName oldName, BIGroupTagName newName) throws BIGroupDuplicateException, BIGroupAbsentException;

    void addGroup(BIGroupTagName groupName, BIBusinessGroup group) throws BIGroupDuplicateException;

}