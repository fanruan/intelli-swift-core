package com.finebi.cube.conf;


import com.finebi.cube.conf.relation.path.BITableContainer;
import com.finebi.cube.conf.relation.relation.IRelationContainer;
import com.finebi.cube.conf.table.IBusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.stable.exception.*;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * 系统的关联接口，其中包括对关联的计算，比如间的路径，公共主子表等计算
 */
public interface BITableRelationConfigurationProvider {

    String XML_TAG = "BIConnectionManager";

    /**
     * 持久化数据
     * TODO 应该按照规则自动调用
     *
     * @param userId 用户ID
     */
    @Deprecated
    void persistData(long userId);

    /**
     * 获得全部表的关联
     *
     * @param userId 用户ID
     * @return 全部表关联
     */
    Set<BITableRelation> getAllTableRelation(long userId);

    /**
     * 获得一个表与关联集合的MAP，并且该表是集合中关联的主键
     *
     * @param userId 用户ID
     * @return 表与关联集合的MAP
     */
    Map<IBusinessTable, IRelationContainer> getAllTable2PrimaryRelation(long userId);

    /**
     * 获得一个表与关联集合的MAP，并且该表是集合中关联的外键
     *
     * @param userId 用户ID
     * @return 表与关联集合的MAP
     */
    Map<IBusinessTable, IRelationContainer> getAllTable2ForeignRelation(long userId);

    /**
     * 利用当前可用的关联，计算子表到长辈表的全部路径
     * 当前采用的算法，由于防止死环的出现，所以同一个表在路径中只会出现一次
     *
     * @param userId       用户ID
     * @param juniorTable  子表
     * @param primaryTable 长辈表
     * @return 全部路径
     * @throws BITableUnreachableException       子表和长辈表不可达
     * @throws BITableAbsentException            子表和长辈表缺失，未注册
     * @throws BITableRelationConfusionException 注册的关联有误
     * @throws BITablePathConfusionException     路径有误
     */
    Set<BITableRelationPath> getAllPath(long userId, IBusinessTable juniorTable, IBusinessTable primaryTable) throws BITableUnreachableException,
            BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException;

    /**
     * 获得全部可用的路径
     *
     * @param userId       用户ID
     * @param juniorTable  子表
     * @param primaryTable 长辈表
     * @return 全部可用路径
     * @throws BITableUnreachableException       子表和长辈表不可达
     * @throws BITableAbsentException            子表和长辈表缺失，未注册
     * @throws BITableRelationConfusionException 注册的关联有误
     * @throws BITablePathConfusionException     路径有误
     */

    Set<BITableRelationPath> getAllAvailablePath(long userId, IBusinessTable juniorTable, IBusinessTable primaryTable) throws BITableUnreachableException,
            BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException;

    Set<BITableRelationPath> getAllTablePath(long userId) throws BITableRelationConfusionException, BITablePathConfusionException;

    /**
     * 获得第一条路径
     *
     * @param userId       用户ID
     * @param juniorTable  子表
     * @param primaryTable 长辈表
     * @return 获得第一条路径
     * @throws BITableUnreachableException 表不可达
     */
    BITableRelationPath getFirstPath(long userId, IBusinessTable juniorTable, IBusinessTable primaryTable) throws BITableUnreachableException;

    /**
     * 获取第一条可用路径
     *
     * @param userId       用户ID
     * @param juniorTable  子表
     * @param primaryTable 长辈表
     * @return 获得第一条路径
     * @throws BITableUnreachableException 表不可达
     */
    BITableRelationPath getFirstAvailablePath(long userId, IBusinessTable primaryTable, IBusinessTable juniorTable) throws BITableUnreachableException;

    /**
     * 获得表集合中的公共子类表
     *
     * @param userId 用户ID
     * @param tables 表集合
     * @return 公共子类表
     */
    BITableContainer getCommonSonTables(long userId, Set<IBusinessTable> tables);

    /**
     * 环境改变
     */
    void envChanged();

    /**
     * 判断路径是否可用
     *
     * @param userId      用户ID
     * @param disablePath 目标路径
     * @return 是否可用
     */
    boolean isPathDisable(long userId, BITableRelationPath disablePath);

    /**
     * 添加不可用路径
     *
     * @param userId      用户ID
     * @param disablePath 不可用路径
     * @throws BITablePathDuplicationException 重复添加抛错
     */
    void addDisableRelations(long userId, BITableRelationPath disablePath) throws BITablePathDuplicationException;

    /**
     * 取消路径的不可用
     *
     * @param userId      用户ID
     * @param disablePath 不可用路径
     * @throws BITablePathAbsentException 当前路径是可用状态抛错
     */
    void removeDisableRelations(long userId, BITableRelationPath disablePath) throws BITablePathAbsentException;


    /**
     * @param userId
     * @param tableRelation
     * @return
     */
    boolean containTableRelation(long userId, BITableRelation tableRelation);


    boolean containTableRelationship(long userId, BITableRelation tableRelation);


    boolean containTablePrimaryRelation(long userId, IBusinessTable table);

    boolean containTableForeignRelation(long userId, IBusinessTable table);

    /**
     * 注册表关联
     *
     * @param userId        用户ID
     * @param tableRelation 表关联
     * @throws BIRelationDuplicateException 重复注册抛错
     */
    void registerTableRelation(long userId, BITableRelation tableRelation) throws BIRelationDuplicateException;

    /**
     * 注册表关联集合
     *
     * @param userId         用户ID
     * @param tableRelations 表关联集合
     */
    void registerTableRelationSet(long userId, Set<BITableRelation> tableRelations);

    /**
     * 取消表关联
     *
     * @param userId        用户ID
     * @param tableRelation 已注册的表关联
     * @throws BIRelationAbsentException 当前表关联并未注册抛错
     * @throws BITableAbsentException    当前的表不存在
     */
    void removeTableRelation(long userId, BITableRelation tableRelation) throws BIRelationAbsentException, BITableAbsentException;

    /**
     * 环境十分改变
     *
     * @param userId 用户ID
     * @return 十分改变
     */
    boolean isChanged(long userId);

    /**
     * 结束生成CUBE
     *
     * @param userId      用户ID
     * @param relationSet 关联集合
     */
    void finishGenerateCubes(long userId, Set<BITableRelation> relationSet);

    /**
     * 清除用户的全部数据
     *
     * @param user 用户ID
     */
    void clear(long user);

    /**
     * 创建JSON 数据
     *
     * @param userId 用户ID
     * @return JSON数据
     */
    JSONObject createBasicRelationsJSON(long userId);

    /**
     * 创建JSON 数据
     *
     * @param userId 用户ID
     * @return JSON 数据
     * @throws JSONException 异常
     */
    JSONObject createRelationsPathJSON(long userId) throws JSONException;

    /**
     * 两表是否可达
     *
     * @param userId       用户ID
     * @param juniorTable  子表
     * @param primaryTable 父类表
     * @return 是否可达
     */
    boolean isReachable(long userId, IBusinessTable juniorTable, IBusinessTable primaryTable);

    IRelationContainer getPrimaryRelation(long userId, IBusinessTable table) throws BITableAbsentException;

    IRelationContainer getForeignRelation(long userId, IBusinessTable table) throws BITableAbsentException;


}