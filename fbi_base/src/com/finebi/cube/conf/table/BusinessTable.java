package com.finebi.cube.conf.table;

import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

import java.io.Serializable;
import java.util.List;

/**
 * 业务包表，多用组合，谨慎继承
 * 通过DataSource的支持，
 * <p/>
 * This class created on 2016/5/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface BusinessTable extends JSONTransform, Cloneable, Serializable {

    /**
     * 表的ID对象
     * @return 描述业务包表ID的对象
     */
    BITableID getID();

    /**
     * 业务包表名
     * @return 业务包表的名字
     */
    String getTableName();

    /**
     * 获取表中的所有字段
     * @return 字段列表
     */
    List<BusinessField> getFields();

    /**
     * 选取该业务包表所需要包含的字段
     * @param fields 选取的字段列表
     */
    void setFields(List<BusinessField> fields);

    /**
     * 设置业务包表的数据
     * @param source 数据源
     */
    void setSource(CubeTableSource source);

    /**
     * 获取业务包表的数据源
     * @return 数据源
     */
    CubeTableSource getTableSource();

    /**
     * 将当前用户有权限查看的字段信息输出成JSON对象
     * @param userId 用户ID
     * @return JSON对象
     * @throws Exception 如果生成失败则抛出此异常
     */
    JSONObject toFieldJSONObject(long userId) throws Exception;

    /**
     * 克隆业务包表
     * @return 新的业务包表
     * @throws CloneNotSupportedException 克隆失败则抛出此异常
     */
    Object clone() throws CloneNotSupportedException;
}
