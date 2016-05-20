package com.fr.bi.stable.data.source;


import com.finebi.cube.api.ICubeDataLoader;
import com.fr.base.TableData;
import com.fr.bi.base.BICore;
import com.fr.bi.common.BICoreService;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by GUY on 2015/2/28.
 */
public interface ITableSource extends XMLable, JSONCreator, BICoreService {

    IPersistentTable getDbTable();

    String getSourceID();

    /**
     * 根据sources获取fields, 用来生成cube,判断cube版本
     *
     * @param sources generatingobjects 的packs的sources
     * @return 字段
     */
    DBField[] getFieldsArray(Set<ITableSource> sources);
    /**
     * 当前TableSource父类的全部可用字段。
     *
     * @param sources
     * @return
     */
    Set<DBField> getParentFields(Set<ITableSource> sources);

    /**
     * 当前TableSource最终全部可用字段。
     * 例如A含有字段a，父类有c，d字段，那么该函数返回
     * a,c,d
     * 如果A是使用部分字段，父类有c，d字段，只使用c，那么该
     * 函数返回c。
     *
     * @param sources
     * @return
     */
    Set<DBField> getFacetFields(Set<ITableSource> sources);

    /**
     * 当前TableSource自身的字段。
     *
     * @param sources
     * @return
     */
    Set<DBField> getSelfFields(Set<ITableSource> sources);


    /**
     * 获取所有相关基础表
     *
     * @return
     */
    Set<Table> createTableKeys();

    /**
     * key为层次
     *
     * @return
     */
    Map<Integer, Set<ITableSource>> createGenerateTablesMap();

    List<Set<ITableSource>> createGenerateTablesList();

    /**
     * 层级
     *
     * @return
     */
    int getLevel();

    int getType();

    /**
     * 写简单索引
     *
     * @return
     */
    long read(Traversal<BIDataValue> travel, DBField[] field, ICubeDataLoader loader);

    long read4Part(Traversal<BIDataValue> travel, DBField[] field, ICubeDataLoader loader, int start, int end);

    /**
     * 获取某个字段的distinct值
     */
    Set getFieldDistinctNewestValues(String fieldName, ICubeDataLoader loader, long userId);

    Set getFieldDistinctValuesFromCube(String fieldName, ICubeDataLoader loader, long userId);

    JSONObject createPreviewJSON(ArrayList<String> fields, ICubeDataLoader loader, long userId) throws Exception;

    TableData createTableData(List<String> fields, ICubeDataLoader loader, long userId) throws Exception;

    JSONObject createPreviewJSONFromCube(ArrayList<String> fields, ICubeDataLoader loader) throws Exception;

    boolean needGenerateIndex();

    Map<BICore, ITableSource> createSourceMap();

    SourceFile getSourceFile();

    Set<String> getUsedFields(ITableSource source);

    void refresh();

    boolean isIndependent();
}