package com.fr.bi.conf.data.source.operator.create;

import com.fr.bi.base.BICore;
import com.fr.bi.base.FinalInt;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.target.filter.TargetFilterFactory;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.BIColumn;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.bi.stable.data.source.ITableSource;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.engine.index.CubeTILoaderAdapter;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.List;

/**
 * Created by GUY on 2015/3/5.
 */
public class TableColumnFilterOperator extends AbstractCreateTableETLOperator {

    public static final String XML_TAG = "TableColumnFilterOperator";
    private static final long serialVersionUID = 1177276899686204275L;
    @BICoreField
    private TargetFilter filter;

    public TableColumnFilterOperator(long userId) {
        super(userId);
    }

    public TableColumnFilterOperator() {

    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    /**
     * 将Java对象转换成JSON对象
     *
     * @return json对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        if (filter != null) {
            jo.put("filter_value", filter.createJSON());
        }
        return jo;
    }



    @Override
    public DBTable getBITable(DBTable[] tables) {
        DBTable DBTable = getBITable();
        for (int i = 0; i < tables.length; i++) {
            for (int j = 0; j < tables[i].getBIColumnLength(); j++) {
                DBTable.addColumn(tables[i].getBIColumn(j));
            }
        }
        return DBTable;
    }

    private GroupValueIndex createFilterIndex(List<? extends ITableSource> parents, ICubeDataLoader loader){
        GroupValueIndex gvi = null;
        for (ITableSource parent : parents){
            GroupValueIndex temp = filter.createFilterIndex(new BITable(parent.fetchObjectCore().getID().getIdentityValue()), loader, loader.getUserId());
            if (gvi == null){
                gvi = temp;
            } else {
                gvi = gvi.AND(temp);
            }
        }
        return gvi;
    }

    @Override
    public int writeSimpleIndex(final Traversal<BIDataValue> travel, List<ITableSource> parents, ICubeDataLoader loader) {
        final ICubeTableService ti = loader.getTableIndex(getSingleParentMD5(parents));
        DBTable ptable = parents.get(0).getDbTable();
        final BIColumn[] columns = ptable.getColumnArray();
        GroupValueIndex fgvi = filter == null ? ti.getAllShowIndex() : createFilterIndex(parents ,loader);
        if (fgvi == null){
            return 0;
        }
        fgvi.Traversal(new SingleRowTraversalAction() {
        	int row = 0;

            @Override
            public void actionPerformed(int rowIndices) {
                for (int i = 0; i < columns.length; i++) {
                    travel.actionPerformed(new BIDataValue(row, i, ti.getRow(new IndexKey(columns[i].getFieldName()), rowIndices)));
                }
                row++;
            }
        });
        return fgvi.getRowsCountWithData();
    }
    private static final int STEP = 1000;
    @Override
    public int writePartIndex(final Traversal<BIDataValue> travel, List<? extends ITableSource> parents, ICubeDataLoader loader, int startCol, final int start, final int end) {
        ICubeTableService ti = loader.getTableIndex(getSingleParentMD5(parents), 0 , STEP);
        int index = 0;
        final FinalInt currentRow = new FinalInt();
        currentRow.i = -1;
        final FinalInt writeRow = new FinalInt();
        DBTable ptable = parents.get(0).getDbTable();
        final BIColumn[] columns = ptable.getColumnArray();
        while (ti.getRowCount() > 0){
            final ICubeTableService tableIndex = ti;
            GroupValueIndex fgvi = filter == null ? ti.getAllShowIndex() : createFilterIndex(parents, new CubeTILoaderAdapter() {
                @Override
                public ICubeTableService getTableIndex(Table td) {
                    return tableIndex;
                }
                @Override
                public ICubeTableService getTableIndex(BIField td) {
                    return tableIndex;
                }
                @Override
                public ICubeTableService getTableIndex(BICore md5Core) {
                    return tableIndex;
                }
                @Override
                public ICubeTableService getTableIndex(BITableID id) {
                    return tableIndex;
                }
            });
            fgvi.BrokenableTraversal(new BrokenTraversalAction() {
                @Override
                public boolean actionPerformed(int row) {
                    currentRow.i ++;
                    if (currentRow.i < start ){
                        return true;
                    }
                    if (currentRow.i > end ){
                        return false;
                    }
                    for (int i = 0; i < columns.length; i++) {
                        travel.actionPerformed(new BIDataValue(writeRow.i, i, tableIndex.getRowValue(new IndexKey(columns[i].getFieldName()), row)));
                    }
                    writeRow.i++;
                    return true;
                }
            });
            index++;
            ti = loader.getTableIndex(getSingleParentMD5(parents), (index - 1) * STEP , index * STEP);
        }
        return writeRow.i;
    }


    /**
     * 将JSON对象转换成java对象
     *
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has(BIJSONConstant.JSON_KEYS.FILTER_VALUE)) {
            filter = TargetFilterFactory.parseFilter(jo.getJSONObject(BIJSONConstant.JSON_KEYS.FILTER_VALUE), user.getUserId());
        }
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
        try {
            JSONObject jo = new JSONObject(reader.getAttrAsString("filter", StringUtils.EMPTY));
            this.parseJSON(jo);
        } catch (Exception e) {

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
        try {
            writer.attr("filter", this.createJSON().toString());
        } catch (Exception e) {
        }
        writer.end();
    }
}