package com.fr.bi.stable.data.source;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.TableData;
import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.collections.array.IntArray;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by GUY on 2015/3/3.
 */
public abstract class AbstractTableSource implements CubeTableSource {
    private static BILogger LOGGER = BILoggerFactory.getLogger(AbstractTableSource.class);
    /**
     *
     */
    private static final long serialVersionUID = -8657998191260725924L;
    //表的唯一标识
    protected Map<String, ICubeFieldSource> fields = new LinkedHashMap<String, ICubeFieldSource>();
    @BIIgnoreField
    protected PersistentTable dbTable;
    @BIIgnoreField
    private transient BICore core;

    //用来存中间表产生的名字
    private String tempName;

    protected AbstractTableSource() {

    }

    @Override
    public String getTableName() {
        if (getPersistentTable() != null) {
            return getPersistentTable().getTableName();
        }
        return null;
    }

    @Override
    public String getSourceID() {
        return fetchObjectCore().getIDValue();
    }

    protected void clearCore() {
        this.core = null;
    }

    @Override
    public BICore fetchObjectCore() {
        if (core == null || core == BIBasicCore.EMPTY_CORE) {
            synchronized (this) {
                if (core == null) {
                    try {
                        core = new BICoreGenerator(this).fetchObjectCore();
                    } catch (Exception e) {
                        BILoggerFactory.getLogger().error(e.getMessage(), e);
                        core = BIBasicCore.EMPTY_CORE;
                    }
                }
            }
        }
        return core;


    }

    //重新获取数据 guy
    public void refreshDBTable() {
        PersistentTable tempTable = dbTable;
        try {
            dbTable = null;
            dbTable = (PersistentTable) getPersistentTable();
        } catch (Exception e) {
            BILoggerFactory.getLogger(AbstractTableSource.class).error(e.getMessage(), e);
            dbTable = tempTable;
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public void refreshFields() {
        Map<String, ICubeFieldSource> tempFields = new HashMap<String, ICubeFieldSource>(fields);
        try {
            fields.clear();
            getFields();
        } catch (Exception e) {
            BILoggerFactory.getLogger(AbstractTableSource.class).error(e.getMessage(), e);
            fields.clear();
            fields.putAll(tempFields);
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public Set<ICubeFieldSource> getParentFields(Set<CubeTableSource> sources) {
        return new HashSet<ICubeFieldSource>();
    }

    @Override
    public Set<ICubeFieldSource> getFacetFields(Set<CubeTableSource> sources) {
        return getSelfFields(sources);
    }

    @Override
    public Set<ICubeFieldSource> getSelfFields(Set<CubeTableSource> sources) {
        Set<ICubeFieldSource> result = new HashSet<ICubeFieldSource>();
        ICubeFieldSource[] fields = getFieldsArray(sources);
        for (ICubeFieldSource field : fields) {
            result.add(field);
        }
        return result;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public long read4Part(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader, int start, int end) {
        return read(travel, field, loader);
    }

    @Override
    public JSONObject createPreviewJSONFromCube(ArrayList<String> fields, ICubeDataLoader loader) throws Exception {
        try {
            ICubeTableService tableIndex = loader.getTableIndex(this);
            return createPreviewJSONFromCubeTableIndex(fields, tableIndex);
        } catch (Exception e) {
            throw e;
        } finally {
            loader.releaseCurrentThread();
        }

    }

    public JSONObject createPreviewJSONFromCubeTableIndex(ArrayList<String> fields, ICubeTableService tableIndex) throws Exception {
        JSONArray allFieldNamesJo = new JSONArray();
        JSONArray fieldValues = new JSONArray();
        JSONArray fieldTypes = new JSONArray();
        IntArray remove = tableIndex.getRemovedList();
        Map<BIKey, ICubeFieldSource> map = new ConcurrentHashMap<BIKey, ICubeFieldSource>();
        map.putAll(tableIndex.getColumns());
        ArrayList<ICubeFieldSource> fieldsCube = new ArrayList<ICubeFieldSource>();
        //将内存中的cube的fields按照前端的fields排序，需要考虑数据库字段增加删除的情况
        for (String fieldName : fields) {
            IndexKey key = new IndexKey(fieldName);
            if (map.containsKey(key)) {
                fieldsCube.add(map.get(key));
                map.remove(key);
            }
        }
        if (!map.isEmpty()) {
            for (ICubeFieldSource field : map.values()) {
                fieldsCube.add(field);
            }
        }
        for (ICubeFieldSource column : fieldsCube) {
            if (!fields.isEmpty() && !fields.contains(column.getFieldName())) {
                continue;
            }
            allFieldNamesJo.put(column.getFieldName());
            fieldTypes.put(column.getFieldType());
            JSONArray values = new JSONArray();
            fieldValues.put(values);
            int count = Math.min(tableIndex.getRowCount(), BIBaseConstant.PREVIEW_COUNT);
            for (int i = 0; i < count; i++) {
                if (remove.indexOf(i) < 0) {
                    // 从底层获取的数据传送给前端展示还需要做一下空值处理
                    Object v = tableIndex.getColumnDetailReader(new IndexKey(column.getFieldName())).getValue(i);
                    v = BICollectionUtils.cubeValueToWebDisplay(v);
                    values.put(v);
                }
            }
        }
        return new JSONObject().put(BIJSONConstant.JSON_KEYS.FIELDS, allFieldNamesJo).put(BIJSONConstant.JSON_KEYS.VALUE, fieldValues).put(BIJSONConstant.JSON_KEYS.TYPE, fieldTypes);
    }

    @Override
    public Map<Integer, Set<CubeTableSource>> createGenerateTablesMap() {
        Map<Integer, Set<CubeTableSource>> generateTable = new HashMap<Integer, Set<CubeTableSource>>();
        Set<CubeTableSource> set = new HashSet<CubeTableSource>();
        set.add(this);
        generateTable.put(0, set);
        return generateTable;
    }

    @Override
    public IPersistentTable getPersistentTable() {
        return null;
    }

    @Override
    public List<Set<CubeTableSource>> createGenerateTablesList() {
        List<Set<CubeTableSource>> generateTable = new ArrayList<Set<CubeTableSource>>();
        Set<CubeTableSource> set = new HashSet<CubeTableSource>();
        set.add(this);
        generateTable.add(set);
        return generateTable;
    }

    @Override
    public boolean isIndependent() {
        return true;
    }

    @Override
    public Set<CubeTableSource> getSourceUsedBaseSource(Set<CubeTableSource> set, Set<CubeTableSource> helper) {
        if (helper.contains(this)) {
            return set;
        }
        helper.add(this);
        set.add(this);
        return set;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public long read(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader) {
        return 0;
    }

    @Override
    public Set getFieldDistinctNewestValues(String fieldName, ICubeDataLoader loader, long userId) {
        return null;
    }

    @Override
    public JSONObject createPreviewJSON(ArrayList<String> fields, ICubeDataLoader loader, long userId) throws Exception {
        return null;
    }

    @Override
    public TableData createTableData(List<String> fields, ICubeDataLoader loader, long userId) throws Exception {
        return null;
    }

    @Override
    public Set getFieldDistinctValuesFromCube(String fieldName, ICubeDataLoader loader, long userId) {
        HashSet set = new HashSet();
        ICubeTableService tableIndex = loader.getTableIndex(this);
        ICubeColumnIndexReader getter = tableIndex.loadGroup(new IndexKey(fieldName));
        if (getter != null) {
            Iterator<Map.Entry> it = getter.iterator();
            while (it.hasNext()) {
                set.add(it.next().getKey());
            }
        }
        return set;
    }


    protected PersistentTable createBITable() {
        return new PersistentTable(null, fetchObjectCore().getID().getIdentityValue(), null);
    }

    public Map<String, ICubeFieldSource> getFields() {
        try {
            return getFieldsWithException();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error("Table " + this.getSourceID() + " has error ! Please check !", e);
            return null;
        }
    }

    public Map<String, ICubeFieldSource> getFieldsWithException() {
        try {
            if (fields == null || fields.isEmpty()) {
                this.fields = getFieldFromPersistentTable();
            }
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
        return this.fields;
    }

    public Map<String, ICubeFieldSource> getRecordedFields() {
        if (fields != null && !fields.isEmpty()) {
            return this.fields;
        } else {
            return this.getFields();
        }
    }

    /**
     * 根据sources获取fields, 用来生成cube,判断cube版本
     *
     * @param sources generatingobjects 的packs的sources
     * @return 字段
     */
    @Override
    public ICubeFieldSource[] getFieldsArray(Set<CubeTableSource> sources) {
        Collection<ICubeFieldSource> values = getFields().values();
        return values.toArray(new ICubeFieldSource[values.size()]);
    }

    protected Map<String, ICubeFieldSource> getFieldFromPersistentTable() {
        Map<String, ICubeFieldSource> fields = new LinkedHashMap<String, ICubeFieldSource>();
        IPersistentTable bt = getPersistentTable();
        if (bt == null) {
            throw new NullPointerException();
        }
        List<ICubeFieldSource> list = new ArrayList<ICubeFieldSource>();
        for (int i = 0, len = bt.getFieldSize(); i < len; i++) {
            PersistentField column = bt.getField(i);
            ICubeFieldSource field = column.toDBField(this);
            list.add(field);
        }
        for (ICubeFieldSource field : list) {
            String fieldName = field.getFieldName();
            ICubeFieldSource old = this.fields.get(fieldName);
            boolean isUsable = old == null || old.isUsable();
            if (fields.containsKey(fieldName)) {
                LOGGER.warn("The field name:" + fieldName + " is duplicated");
//                throw new FieldNameDuplicateException("The field name:" + fieldName + " is duplicated");
            } else {
                fields.put(fieldName, field);
            }
        }

        return fields;
    }

    @Override
    public Set<String> getUsedFields(CubeTableSource source) {
        if (ComparatorUtils.equals(source.fetchObjectCore(), fetchObjectCore())) {
            return getFields().keySet();
        }
        return new HashSet<String>();
    }

    @Override
    public void refresh() {
        refreshDBTable();
        refreshFields();
    }

    public void envChange() {
        fields.clear();
        dbTable = null;
    }


    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            BILoggerFactory.getLogger().info(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + fetchObjectCore().hashCode();
        return result;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        jo.put("md5", fetchObjectCore().getID().getIdentityValue());
        List<JSONObject> stringList = new ArrayList<JSONObject>();
        List<JSONObject> numberList = new ArrayList<JSONObject>();
        List<JSONObject> dateList = new ArrayList<JSONObject>();

        Map<String, ICubeFieldSource> fields = null;
        try {
            fields = getFieldsWithException();
        } catch (Exception e) {
            jo.put("error", e.getMessage());
            fields = new HashMap<String, ICubeFieldSource>();
        }

        for (Map.Entry<String, ICubeFieldSource> entry : fields.entrySet()) {
            ICubeFieldSource field = entry.getValue();
            stringList.add(field.createJSON());
        }
        ja.put(stringList).put(numberList).put(dateList);
        jo.put("fields", ja);
        jo.put("tempName", tempName);
        return jo;
    }

    public void parseJSON(JSONObject jo, long userId) throws Exception {
        if (jo.has("tempName")) {
            this.tempName = jo.getString("tempName");
        }

    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
//            md5 = reader.getAttrAsString("md5", StringUtils.EMPTY);
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
//        writer.attr("md5", md5);
    }

    @Override
    public boolean needGenerateIndex() {
        return true;
    }

    /**
     * 所有的source,包括parents
     *
     * @return
     */
    @Override
    public Map<BICore, CubeTableSource> createSourceMap() {
        Map<BICore, CubeTableSource> map = new HashMap<BICore, CubeTableSource>();
        map.put(fetchObjectCore(), this);
        return map;
    }

    @Override
    public long read4Part(Traversal<BIDataValue> traversal, ICubeFieldSource[] cubeFieldSources, String sql, long rowCount) {
        return 0;
    }

    @Override
    public SourceFile getSourceFile() {
        return new SourceFile(fetchObjectCore().getID().getIdentityValue());
    }

    @Override
    public boolean canExecute() throws Exception {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CubeTableSource that = (CubeTableSource) o;
        return ComparatorUtils.equals(that.getSourceID(), this.getSourceID());

    }

    @Override
    public boolean hasAbsentFields() {
        return false;
    }

    protected String getTempName() {
        return tempName;
    }
}
