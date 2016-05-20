package com.fr.bi.stable.data.source;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.base.TableData;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.common.BIMD5CoreWrapper;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.exception.BIAmountLimitUnmetException;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.BIBasicField;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.*;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import javax.activation.UnsupportedDataTypeException;
import java.util.*;

/**
 * Created by GUY on 2015/3/3.
 */
public abstract class AbstractTableSource implements ICubeTableSource {

    /**
     *
     */
    private static final long serialVersionUID = -8657998191260725924L;
    //表的唯一标识
    protected Map<String, BICubeFieldSource> fields = new LinkedHashMap<String, BICubeFieldSource>();
    protected PersistentTable dbTable;

    protected AbstractTableSource() {

    }

    @Override
    public String getSourceID() {
        return fetchObjectCore().getIDValue();
    }

    @Override
    public BICore fetchObjectCore() {
        return new BICoreGenerator(this).fetchObjectCore();
    }

    //重新获取数据 guy
    public IPersistentTable reGetBiTable() {
        dbTable = null;
        return getPersistentTable();
    }

    @Override
    public Set<BICubeFieldSource> getParentFields(Set<ICubeTableSource> sources) {
        return new HashSet<BICubeFieldSource>();
    }

    @Override
    public Set<BICubeFieldSource> getFacetFields(Set<ICubeTableSource> sources) {
        return getSelfFields(sources);
    }

    @Override
    public Set<BICubeFieldSource> getSelfFields(Set<ICubeTableSource> sources) {
        Set<BICubeFieldSource> result = new HashSet<BICubeFieldSource>();
        BICubeFieldSource[] fields = getFieldsArray(sources);
        for (BICubeFieldSource field : fields) {
            result.add(field);
        }
        return result;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public long read4Part(Traversal<BIDataValue> travel, BICubeFieldSource[] field, ICubeDataLoader loader, int start, int end) {
        return read(travel, field, loader);
    }

    @Override
    public JSONObject createPreviewJSONFromCube(ArrayList<String> fields, ICubeDataLoader loader) throws Exception {
        ICubeTableService tableIndex = loader.getTableIndex(fetchObjectCore());
        return createPreviewJSONFromTableIndex(fields, tableIndex);
    }

    public JSONObject createPreviewJSONFromTableIndex(ArrayList<String> fields, ICubeTableService tableIndex) throws Exception {
        JSONArray allFieldNamesJo = new JSONArray();
        JSONArray fieldValues = new JSONArray();
        JSONArray fieldTypes = new JSONArray();
        for (PersistentField column : getPersistentTable().getFieldList()) {
            if (!fields.isEmpty() && !fields.contains(column.getFieldName())) {
                continue;
            }
            allFieldNamesJo.put(column.getFieldName());
            fieldTypes.put(column.getBIType());
            JSONArray values = new JSONArray();
            fieldValues.put(values);
            int count = Math.min(tableIndex.getRowCount(), BIBaseConstant.PREVIEW_COUNT);
            for (int i = 0; i < count; i++) {
                values.put(tableIndex.getRowValue(new IndexKey(column.getFieldName()), i));
            }
        }
        return new JSONObject().put(BIJSONConstant.JSON_KEYS.FIELDS, allFieldNamesJo).put(BIJSONConstant.JSON_KEYS.VALUE, fieldValues).put(BIJSONConstant.JSON_KEYS.TYPE, fieldTypes);
    }


    @Override
    public Map<Integer, Set<ICubeTableSource>> createGenerateTablesMap() {
        Map<Integer, Set<ICubeTableSource>> generateTable = new HashMap<Integer, Set<ICubeTableSource>>();
        Set<ICubeTableSource> set = new HashSet<ICubeTableSource>();
        set.add(this);
        generateTable.put(0, set);
        return generateTable;
    }

    @Override
    public IPersistentTable getPersistentTable() {
        return null;
    }

    @Override
    public Set<Table> createTableKeys() {
        return null;
    }

    @Override
    public List<Set<ICubeTableSource>> createGenerateTablesList() {
        List<Set<ICubeTableSource>> generateTable = new ArrayList<Set<ICubeTableSource>>();
        Set<ICubeTableSource> set = new HashSet<ICubeTableSource>();
        set.add(this);
        generateTable.add(set);
        return generateTable;
    }

    @Override
    public boolean isIndependent() {
        return true;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public long read(Traversal<BIDataValue> travel, BICubeFieldSource[] field, ICubeDataLoader loader) {
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
        ICubeTableService tableIndex = loader.getTableIndex(fetchObjectCore());
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

    public Map<String, BICubeFieldSource> getFields() {
        try {
            this.fields = synchronousFieldsInforFromDB();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return this.fields;
    }

    /**
     * 根据sources获取fields, 用来生成cube,判断cube版本
     *
     * @param sources generatingobjects 的packs的sources
     * @return 字段
     */
    @Override
    public BICubeFieldSource[] getFieldsArray(Set<ICubeTableSource> sources) {
        return getFields().values().toArray(new BICubeFieldSource[getFields().values().size()]);
    }

    private Map<String, BICubeFieldSource> synchronousFieldsInforFromDB() {
        Map<String, BICubeFieldSource> fields = new LinkedHashMap<String, BICubeFieldSource>();
        IPersistentTable bt = getPersistentTable();
        if (bt == null) {
            throw new NullPointerException();
        }
        List<BICubeFieldSource> list = new ArrayList<BICubeFieldSource>();
        for (int i = 0, len = bt.getFieldSize(); i < len; i++) {
            PersistentField column = bt.getField(i);
            /**
             * Connery：原来传递的是MD5变量，把MD5当做ID传递了，这个是不对的。
             */

            BICubeFieldSource field = column.toDBField(new BITable(fetchObjectCore().getIDValue()));
            field.setCanSetUseable(column.canSetUseable());
            list.add(field);
        }
        for (BICubeFieldSource field : list) {
            String fieldName = field.getFieldName();
            BIBasicField old = this.fields.get(fieldName);
            boolean isUsable = old == null || old.isUsable();
            field.setUsable(isUsable);
            fields.put(fieldName, field);
        }

        return fields;
    }

    @Override
    public Set<String> getUsedFields(ICubeTableSource source) {
        if (ComparatorUtils.equals(source.fetchObjectCore(), fetchObjectCore())) {
            return getFields().keySet();
        }
        return new HashSet<String>();
    }

    @Override
    public void refresh() {
        PersistentTable temp = dbTable;
        try {
            if (reGetBiTable() == null) {
                dbTable = temp;
            }
        } catch (Exception e) {
            dbTable = temp;
        }

    }

    public void envChange() {
        fields.clear();
        dbTable = null;
    }

    public void setMd5(String md5) {
//        this.md5 = md5;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            BILogger.getLogger().info(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractTableSource && ComparatorUtils.equals(fetchObjectCore(), ((AbstractTableSource) (obj)).fetchObjectCore());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
//        result = prime * result
//                + ((fetchObjectCore() == null) ? 0 : md5.hashCode());
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
        Map<String, BICubeFieldSource> fields = getFields();
        for (Map.Entry<String, BICubeFieldSource> entry : fields.entrySet()) {
            BIBasicField field = entry.getValue();
//            switch (entry.getValue().getFieldType()) {
//                case DBConstant.COLUMN.STRING:
//                    stringList.add(field.createJSON());
//                    break;
//                case DBConstant.COLUMN.NUMBER:
//                    numberList.add(field.createJSON());
//                    break;
//                case DBConstant.COLUMN.DATE:
//                    dateList.add(field.createJSON());
//                    break;
//            }
            stringList.add(field.createJSON());
        }
        ja.put(stringList).put(numberList).put(dateList);
        jo.put("fields", ja);
        return jo;
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
    public Map<BICore, ICubeTableSource> createSourceMap() {
        Map<BICore, ICubeTableSource> map = new HashMap<BICore, ICubeTableSource>();
        map.put(fetchObjectCore(), this);
        return map;
    }

    public class BITableSourceCore extends BIMD5CoreWrapper {
        public BITableSourceCore(Object... attributes) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
            this("");
        }

        public BITableSourceCore(String md5) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
            super(md5);
        }
    }

    @Override
    public SourceFile getSourceFile() {
        return new SourceFile(fetchObjectCore().getID().getIdentityValue());
    }
}