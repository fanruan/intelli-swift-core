package com.fr.bi.conf.data.source.operator.create;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.FinalInt;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.SortTool;
import com.fr.bi.stable.engine.SortToolUtils;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.RoaringGroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.structure.object.CubeValueEntry;
import com.fr.cache.list.IntList;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.*;

/**
 * Created by GUY on 2015/3/5.
 */
public class TableJoinOperator extends AbstractCreateTableETLOperator {

    public static final String XML_TAG = "TableJoinOperator";

    private static final long serialVersionUID = -5395803667343259448L;

    @BICoreField
    private int type;

    @BICoreField
    private List<JoinColumn> columns = new ArrayList<JoinColumn>();
    @BICoreField
    private List<String> left = new ArrayList<String>();
    @BICoreField
    private List<String> right = new ArrayList<String>();

    public TableJoinOperator(long userId) {
        super(userId);
    }

    public TableJoinOperator() {
    }

    public TableJoinOperator(int type, List<JoinColumn> columns, List<String> left, List<String> right) {
        this.type = type;
        this.columns = columns;
        this.left = left;
        this.right = right;
    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    /**
     * 将Java对象转换成JSON对象
     *
     * @return JSON对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("join_style", type);
        JSONArray lr = new JSONArray();
        for (int i = 0; i < left.size(); i++) {
            JSONArray value = new JSONArray();
            value.put(left.get(i));
            value.put(right.get(i));
            lr.put(value);
        }
        jo.put("join_fields", lr);
        JSONArray fields = new JSONArray();
        for (int i = 0; i < columns.size(); i++) {
            fields.put(columns.get(i).createJSON());
        }
        jo.put("join_names", fields);
        return jo;
    }


    @Override
    public IPersistentTable getBITable(IPersistentTable[] tables) {
        IPersistentTable persistentTable = getBITable();
        IPersistentTable leftT = tables[0];
        IPersistentTable rightT = tables[1];
        for (int i = 0; i < columns.size(); i++) {
            PersistentField column = columns.get(i).isLeft() ? leftT.getField(columns.get(i).getColumnName()) : rightT.getField(columns.get(i).getColumnName());
            if (column != null) {
                persistentTable.addColumn(new PersistentField(columns.get(i).getName(), columns.get(i).getName(), column.getSqlType(), column.isPrimaryKey(), column.getColumnSize(), column.getScale()));
            }
        }
        return persistentTable;
    }

    @Override
    public int writeSimpleIndex(Traversal<BIDataValue> travel, List<? extends CubeTableSource> parents, ICubeDataLoader loader) {
        if (parents == null || parents.size() != 2) {
            throw new RuntimeException("invalid join parents");
        }
        ICubeTableService lti = loader.getTableIndex(parents.get(0));
        ICubeTableService rti = loader.getTableIndex(parents.get(1));
        return write(travel, lti, rti);
    }

    private int write(Traversal<BIDataValue> travel, ICubeTableService lti, ICubeTableService rti) {
        if (type == BIBaseConstant.JOINTYPE.OUTER) {
            return writeIndex(travel, lti, rti, false, true);
        } else if (type == BIBaseConstant.JOINTYPE.INNER) {
            return writeIndex(travel, lti, rti, true, false);
        } else if (type == BIBaseConstant.JOINTYPE.LEFT) {
            return writeIndex(travel, lti, rti, false, false);
        } else {
            return writeRIndex(travel, lti, rti);
        }
    }

    @Override
    public int writePartIndex(Traversal<BIDataValue> travel, List<? extends CubeTableSource> parents, ICubeDataLoader loader, int startCol, int start, int end) {
        if (parents == null || parents.size() != 2) {
            throw new RuntimeException("invalid join parents");
        }
        ICubeTableService lti = loader.getTableIndex(parents.get(0), start, end);
        ICubeTableService rti = loader.getTableIndex(parents.get(1), start, end);
        return write(travel, lti, rti);
    }


    private int writeRIndex(Traversal<BIDataValue> travel, ICubeTableService lti, ICubeTableService rti) {
        int rLen = getColumnSize(false);
        int lLeftCount = getColumnSize(true);
        int index = 0;
        ValueIterator lValueIterator = new ValueIterator(lti, left);
        ValueIterator rValueIterator = new ValueIterator(rti, right);
        ValuesAndGVI lValuesAndGVI = lValueIterator.next();
        Comparator[] comparators = new Comparator[left.size()];
        for (int i = 0; i < comparators.length; i ++){
            comparators[i] = lti.getColumns().get(new IndexKey(left.get(i))).getFieldType() == DBConstant.COLUMN.STRING ? BIBaseConstant.COMPARATOR.STRING.ASC_STRING_CC : BIBaseConstant.COMPARATOR.COMPARABLE.ASC;
        }
        while (rValueIterator.hasNext()){
            ValuesAndGVI rValuesAndGVI = rValueIterator.next();
            int result = rValuesAndGVI.compareTo(lValuesAndGVI, comparators);
            if (result < 0){
                index = writeROneGroup(travel, lti, rti, rLen, lLeftCount, index, null, rValuesAndGVI.gvi);
            } else if (result == 0){
                index = writeROneGroup(travel, lti, rti, rLen, lLeftCount, index, lValuesAndGVI.gvi, rValuesAndGVI.gvi);
            } else {
                while (rValuesAndGVI.compareTo(lValuesAndGVI, comparators) > 0){
                    lValuesAndGVI = lValueIterator.next();
                }
                if (rValuesAndGVI.compareTo(lValuesAndGVI, comparators) == 0){
                    index = writeROneGroup(travel, lti, rti, rLen, lLeftCount, index, lValuesAndGVI.gvi, rValuesAndGVI.gvi);
                } else {
                    index = writeROneGroup(travel, lti, rti, rLen, lLeftCount, index, null, rValuesAndGVI.gvi);
                }
            }
        }
        return index;
    }


    private int writeROneGroup(Traversal<BIDataValue> travel, ICubeTableService lti, ICubeTableService rti, int rLen, int lLeftCount, int index, GroupValueIndex lGvi, GroupValueIndex rGvi) {
        final IntList list = new IntList();
        rGvi.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                list.add(row);
            }
        });

        for (int i = 0;i < list.size(); i++){
            Object[] rvalues = new Object[rLen];
            for (int j = 0; j < rLen; j++) {
                rvalues[j] = rti.getColumnDetailReader(new IndexKey(columns.get(j < right.size() ? j : lLeftCount + j).getColumnName())).getValue(list.get(i));
            }
            index = rtravel(travel, lti, rLen, index, lGvi, rvalues, lLeftCount);
        }
        return index;
    }

    private int rtravel(Traversal<BIDataValue> travel, ICubeTableService lti, int rlen, int index, GroupValueIndex lGvi, Object[] rvalues, int lleftCount) {
        if (lGvi == null || lGvi.getRowsCountWithData() == 0) {
            for (int j = 0; j < rlen; j++) {
                travel.actionPerformed(new BIDataValue(index, j < right.size() ? j : lleftCount + j, rvalues[j]));
            }
            for (int j = 0; j < lleftCount; j++) {
                travel.actionPerformed(new BIDataValue(index, right.size() + j, null));
            }
            index++;
        } else {
            final IntList lRows = new IntList();
            lGvi.Traversal(new SingleRowTraversalAction() {
                @Override
                public void actionPerformed(int rowIndices) {
                    lRows.add(rowIndices);
                }
            });
            for (int k = 0; k < lRows.size(); k++) {
                for (int j = 0; j < rlen; j++) {
                    travel.actionPerformed(new BIDataValue(index, j < right.size() ? j : lleftCount + j, rvalues[j]));
                }
                for (int j = right.size(); j < lti.getColumns().size(); j++) {
                    travel.actionPerformed(new BIDataValue(index, j, lti.getColumnDetailReader(new IndexKey(columns.get(j).getColumnName())).getValue(lRows.get(k))));
                }
                index++;
            }
        }
        return index;
    }


    private int writeIndex(Traversal<BIDataValue> travel, ICubeTableService lti, ICubeTableService rti, boolean nullContinue, boolean writeLeft) {
        int lLen = getColumnSize(true);
        int index = 0;
        ValueIterator lValueIterator = new ValueIterator(lti, left);
        ValueIterator rValueIterator = new ValueIterator(rti, right);
        GroupValueIndex rTotalGvi = new RoaringGroupValueIndex();
        ValuesAndGVI rValuesAndGVI = rValueIterator.next();
        Comparator[] comparators = new Comparator[left.size()];
        for (int i = 0; i < comparators.length; i ++){
            comparators[i] = lti.getColumns().get(new IndexKey(left.get(i))).getFieldType() == DBConstant.COLUMN.STRING ? BIBaseConstant.COMPARATOR.STRING.ASC_STRING_CC : BIBaseConstant.COMPARATOR.COMPARABLE.ASC;
        }
        while (lValueIterator.hasNext()){
            ValuesAndGVI lValuesAndGVI = lValueIterator.next();
            int result = lValuesAndGVI.compareTo(rValuesAndGVI, comparators);
            if (result < 0 && !nullContinue){
                index = writeOneGroup(travel, lti, rti, lLen, index, lValuesAndGVI.gvi, null);
            } else if (result == 0){
                index = writeOneGroup(travel, lti, rti, lLen, index, lValuesAndGVI.gvi, rValuesAndGVI.gvi);
            } else {
                if (writeLeft){
                    rTotalGvi.or(rValuesAndGVI.gvi);
                }
                while (lValuesAndGVI.compareTo(rValuesAndGVI, comparators) > 0){
                    rValuesAndGVI = rValueIterator.next();
                    if (writeLeft){
                        rTotalGvi.or(rValuesAndGVI.gvi);
                    }
                }
                if (lValuesAndGVI.compareTo(rValuesAndGVI, comparators) == 0){
                    index = writeOneGroup(travel, lti, rti, lLen, index, lValuesAndGVI.gvi, rValuesAndGVI.gvi);
                } else {
                    index = writeOneGroup(travel, lti, rti, lLen, index, lValuesAndGVI.gvi, null);
                }
            }
        }
        return writeLeft ? writeLeftIndex(rTotalGvi, rti, lLen, index, travel) : index;
    }

    private int writeOneGroup(Traversal<BIDataValue> travel, ICubeTableService lti, ICubeTableService rti, int lLen, int index, GroupValueIndex lGvi, GroupValueIndex rGvi) {
        final IntList list = new IntList();
        lGvi.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                list.add(row);
            }
        });
        for (int i = 0;i < list.size(); i++){
            Object[] lvalues = new Object[lLen];
            for (int j = 0; j < lLen; j++) {
                lvalues[j] = lti.getColumnDetailReader(new IndexKey(columns.get(j).getColumnName())).getValue(list.get(i));
            }
            index = travel(travel, rti, lLen, index, rGvi, lvalues);
        }
        return index;
    }

    private class ValuesAndGVI{
        Object[] values;
        GroupValueIndex gvi;

        public ValuesAndGVI(Object[] values, GroupValueIndex gvi) {
            this.values = values;
            this.gvi = gvi;
        }

        public int compareTo(ValuesAndGVI o, Comparator[] comparators) {
            if (o == null){
                return -1;
            }
            for (int i = 0; i < values.length; i++){
                int result = comparators[i].compare(values[i], o.values[i]);
                if (result != 0){
                    return result;
                }
            }
            return 0;
        }
    }

    private class ValueIterator{
        private ICubeValueEntryGetter[] getters;
        private ValuesAndGVI next;
        private GroupValueIndex allShowIndex;
        private Iterator<Map.Entry<Object, GroupValueIndex>>[] iterators;
        private ValuesAndGVI[] valuesAndGVIs;
        public ValueIterator(ICubeTableService ti, List<String> fields) {
            allShowIndex = ti.getAllShowIndex();
            getters = new ICubeValueEntryGetter[fields.size()];
            iterators = new Iterator[fields.size()];
            valuesAndGVIs = new ValuesAndGVI[fields.size() + 1];
            valuesAndGVIs[0] = new ValuesAndGVI(new Object[0], allShowIndex);
            for (int i = 0; i < fields.size(); i++) {
                getters[i] = ti.getValueEntryGetter(new IndexKey(fields.get(i)), new ArrayList<BITableSourceRelation>());
            }
            iterators[0] = getIterByAllCal(getters[0], allShowIndex);
            move(0);
        }

        public boolean hasNext() {
            return next != null;
        }

        public ValuesAndGVI next() {
            ValuesAndGVI temp = next;
            moveNext();
            return temp;
        }

        private void moveNext() {
            for (int i = iterators.length - 1; i >= 0; i--){
                if (iterators[i].hasNext()){
                    move(i);
                    return;
                }
            }
            next = null;
        }
        private void move(int index){
            for (int i = index; i < iterators.length; i ++){
                if (i != index){
                    iterators[i] = getIterByAllCal(getters[i], valuesAndGVIs[i].gvi);
                }
                Map.Entry<Object, GroupValueIndex> entry = iterators[i].next();
                Object[] values = new Object[i + 1];
                System.arraycopy(valuesAndGVIs[i].values, 0, values, 0, values.length - 1);
                values[values.length - 1] = entry.getKey();
                valuesAndGVIs[i + 1] = new ValuesAndGVI(values, entry.getValue().AND(valuesAndGVIs[i].gvi));
            }
            next = valuesAndGVIs[valuesAndGVIs.length - 1];
        }

        private Iterator getIterByAllCal(ICubeValueEntryGetter getter, GroupValueIndex gvi) {
            if (GVIUtils.isAllShowRoaringGroupValueIndex(gvi)){
                return getAllShowIterator(getter);
            }
            SortTool tool = SortToolUtils.getSortTool(getter.getGroupSize(), gvi.getRowsCountWithData());
            switch (tool) {
                case INT_ARRAY:
                    return getArraySortIterator(getter, gvi);
                case DIRECT:
                    return getOneKeyIterator(getter, gvi);
                case TREE_MAP:
                    return getTreeMapSortIterator(getter, gvi);
                default:
                    return getArraySortIterator(getter, gvi);
            }
        }

        private Iterator getArraySortIterator(final ICubeValueEntryGetter getter, GroupValueIndex gvi) {
            final int[] groupIndex = new int[getter.getGroupSize()];
            Arrays.fill(groupIndex, NIOConstant.INTEGER.NULL_VALUE);
            gvi.Traversal(new SingleRowTraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    int groupRow = getter.getPositionOfGroupByRow(row);
                    if (groupRow != NIOConstant.INTEGER.NULL_VALUE) {
                        groupIndex[groupRow] = groupRow;
                    }
                }
            });
            return new Iterator() {

                private int index = 0;

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("remove");
                }
                @Override
                public boolean hasNext() {
                    while (index < groupIndex.length && groupIndex[index] == NIOConstant.INTEGER.NULL_VALUE) {
                        index++;
                    }
                    return index < groupIndex.length;
                }

                @Override
                public Object next() {
                    final CubeValueEntry gve = getter.getEntryByGroupRow(index);
                    Map.Entry entry = new Map.Entry() {
                        @Override
                        public Object getKey() {
                            return gve.getT();
                        }

                        @Override
                        public Object getValue() {
                            return gve.getGvi();
                        }

                        @Override
                        public Object setValue(Object value) {
                            return null;
                        }

                        @Override
                        public boolean equals(Object o) {
                            return false;
                        }

                        @Override
                        public int hashCode() {
                            return 0;
                        }
                    };
                    index++;
                    return entry;
                }
            };
        }

        private Iterator getAllShowIterator(final ICubeValueEntryGetter getter) {
            return new Iterator() {
                private int index = 0;
                private int groupSize = getter.getGroupSize();
                @Override
                public void remove() {
                    throw new UnsupportedOperationException("remove");
                }

                @Override
                public boolean hasNext() {
                    return index < groupSize;
                }

                @Override
                public Object next() {
                    final CubeValueEntry gve = getter.getEntryByGroupRow(index);
                    Map.Entry entry = new Map.Entry() {
                        @Override
                        public Object getKey() {
                            return gve.getT();
                        }

                        @Override
                        public Object getValue() {
                            return gve.getGvi();
                        }

                        @Override
                        public Object setValue(Object value) {
                            return null;
                        }

                        @Override
                        public boolean equals(Object o) {
                            return false;
                        }

                        @Override
                        public int hashCode() {
                            return 0;
                        }
                    };
                    index++;
                    return entry;
                }
            };
        }


        private Iterator getOneKeyIterator(final ICubeValueEntryGetter getter, GroupValueIndex gvi) {
            final FinalInt i = new FinalInt();
            i.value = NIOConstant.INTEGER.NULL_VALUE;
            gvi.Traversal(new SingleRowTraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    i.value = getter.getPositionOfGroupByRow(row);
                }
            });
            return new Iterator() {
                @Override
                public void remove() {
                    throw new UnsupportedOperationException("remove");
                }

                @Override
                public boolean hasNext() {
                    return i.value != NIOConstant.INTEGER.NULL_VALUE;
                }

                @Override
                public Object next() {
                    final CubeValueEntry gve = getter.getEntryByGroupRow(i.value);
                    Map.Entry entry = new Map.Entry() {
                        @Override
                        public Object getKey() {
                            return gve.getT();
                        }

                        @Override
                        public Object getValue() {
                            return gve.getGvi();
                        }

                        @Override
                        public Object setValue(Object value) {
                            return null;
                        }

                        @Override
                        public boolean equals(Object o) {
                            return false;
                        }

                        @Override
                        public int hashCode() {
                            return 0;
                        }
                    };
                    i.value = NIOConstant.INTEGER.NULL_VALUE;
                    return entry;
                }
            };
        }

        private Iterator getTreeMapSortIterator(final ICubeValueEntryGetter getter, GroupValueIndex gvi) {
            final TreeSet<Integer> set = new TreeSet<Integer>(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
            gvi.Traversal(new SingleRowTraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    int groupRow = getter.getPositionOfGroupByRow(row);
                    if (groupRow != NIOConstant.INTEGER.NULL_VALUE) {
                        set.add(groupRow);
                    }
                }
            });
            final Iterator<Integer> it = set.iterator();
            return new Iterator() {
                @Override
                public void remove() {
                    it.remove();
                }

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public Object next() {
                    final CubeValueEntry gve = getter.getEntryByGroupRow(it.next());
                    Map.Entry entry = new Map.Entry() {
                        @Override
                        public Object getKey() {
                            return gve.getT();
                        }

                        @Override
                        public Object getValue() {
                            return gve.getGvi();
                        }

                        @Override
                        public Object setValue(Object value) {
                            return null;
                        }

                        @Override
                        public boolean equals(Object o) {
                            return false;
                        }

                        @Override
                        public int hashCode() {
                            return 0;
                        }
                    };
                    return entry;
                }
            };
        }

    }

    private int travel(Traversal<BIDataValue> travel, ICubeTableService rti, int llen, int index, GroupValueIndex gvi, Object[] lvalues) {
        if (gvi == null || gvi.getRowsCountWithData() == 0) {
            for (int j = 0; j < llen; j++) {
                travel.actionPerformed(new BIDataValue(index, j, lvalues[j]));
            }
            for (int j = llen; j < columns.size(); j++) {
                travel.actionPerformed(new BIDataValue(index, j, null));
            }
            index++;
        } else {
            final IntList rRows = new IntList();
            gvi.Traversal(new SingleRowTraversalAction() {
                @Override
                public void actionPerformed(int rowIndices) {
                    rRows.add(rowIndices);
                }
            });
            for (int k = 0; k < rRows.size(); k++) {
                for (int j = 0; j < llen; j++) {
                    travel.actionPerformed(new BIDataValue(index, j, lvalues[j]));
                }
                for (int j = llen; j < columns.size(); j++) {
                    travel.actionPerformed(new BIDataValue(index, j, rti.getColumnDetailReader(new IndexKey(columns.get(j).getColumnName())).getValue(rRows.get(k))));
                }
                index++;
            }
        }
        return index;
    }

    private int writeLeftIndex(GroupValueIndex rTotalGvi, ICubeTableService rti, int llen, int index, Traversal<BIDataValue> travel) {
        GroupValueIndex rLeft = rTotalGvi == null ? rti.getAllShowIndex() : rTotalGvi.NOT(rti.getRowCount()).AND(rti.getAllShowIndex());
        final IntList rLeftRows = new IntList();
        rLeft.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int rowIndices) {
                rLeftRows.add(rowIndices);
            }
        });
        for (int k = 0; k < rLeftRows.size(); k++) {
            for (int j = 0; j < llen; j++) {
                travel.actionPerformed(new BIDataValue(index, j, null));
            }
            for (int j = llen; j < columns.size(); j++) {
                travel.actionPerformed(new BIDataValue(index, j, rti.getColumnDetailReader(new IndexKey(columns.get(j).getColumnName())).getValue(rLeftRows.get(k))));
            }
            index++;
        }
        return index;
    }


    /**
     * join_style: [], join类型
     * join_fields: [[ firstFieldName, secondFieldName]],
     * join_names: [  ],
     * table_name: 表名
     * 将JSON对象转换成java对象
     *
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        type = jo.getInt("join_style");
        JSONArray lr = jo.getJSONArray("join_fields");
        for (int i = 0; i < lr.length(); i++) {
            JSONArray value = lr.getJSONArray(i);
            left.add(value.getString(0));
            right.add(value.getString(1));
        }
        JSONArray fields = jo.getJSONArray("join_names");
        for (int i = 0; i < fields.length(); i++) {
            JoinColumn column = new JoinColumn();
            column.parseJSON(fields.getJSONObject(i));
            columns.add(column);
        }
    }

    private int getLeftIndex(String name) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).isLeft() && ComparatorUtils.equals(columns.get(i).getColumnName(), name)) {
                return i;
            }
        }
        String message = "can`t find column : " + name;
        BILoggerFactory.getLogger().info(message);
        throw new RuntimeException(message);
    }

    private int getRightIndex(String name) {
        for (int i = 0; i < columns.size(); i++) {
            if (!columns.get(i).isLeft() && ComparatorUtils.equals(columns.get(i).getColumnName(), name)) {
                return i;
            }
        }
        String message = "can`t find column : " + name;
        BILoggerFactory.getLogger().info(message);
        throw new RuntimeException(message);
    }

    /**
     * 读取子节点，应该会被XMLableReader.readXMLObject()调用多次
     *
     * @param reader XML读取对象
     * @see com.fr.stable.xml.XMLableReader
     */
    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        if (reader.isChildNode()) {
            String tag = reader.getTagName();
            if ("left".equals(tag)) {
                left.add(reader.getAttrAsString("value", StringUtils.EMPTY));
            } else if ("right".equals(tag)) {
                right.add(reader.getAttrAsString("value", StringUtils.EMPTY));
            } else if (JoinColumn.XML_TAG.equals(tag)) {
                JoinColumn column = new JoinColumn();
                column.readXML(reader);
                columns.add(column);
            }
        }
        if (reader.isAttr()) {
            type = reader.getAttrAsInt("type", 1);
        }
    }

    /**
     * Write XML.<br>
     * The method will be invoked when save data to XML file.<br>
     * May override the method to save your own data.
     * 从性能上面考虑，大家用writer.print(), 而不是writer.println()
     *
     * @param writer XML写入对象
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        writer.attr("type", type);
        for (int i = 0; i < left.size(); i++) {
            writer.startTAG("left");
            writer.attr("value", left.get(i));
            writer.end();
        }
        for (int i = 0; i < right.size(); i++) {
            writer.startTAG("right");
            writer.attr("value", right.get(i));
            writer.end();
        }
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).writeXML(writer);
        }
        writer.end();

    }

    public int getColumnSize(boolean isLeft) {
        int i = 0;
        for (JoinColumn c : columns) {
            if (c.isLeft() == isLeft) {
                i++;
            }
        }
        return i;
    }
}