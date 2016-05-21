package com.fr.bi.conf.base.cube.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.FinalInt;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.data.source.TableSourceFactory;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.connection.ConnectionRowGetter;
import com.fr.bi.stable.connection.DirectTableConnectionFactory;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.exception.BITableUnreachableException;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.bi.stable.relation.BITableRelationPath;
import com.fr.bi.util.BIConfUtils;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

import java.util.List;

/**
 * Created by Young's on 2016/5/19.
 */
public class BILoginInfoInTableField implements JSONTransform {
    private ITableSource table;
    private String fieldName;

    public ITableSource getTable() {
        return table;
    }

    public void setTable(ITableSource table) {
        this.table = table;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        if(fieldName != null) {
            jo.put("field_name", fieldName);
        }
        if(table != null) {
            jo.put("table", table.createJSON());
        }
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if(jo.has("field_name")){
            fieldName = jo.getString("field_name");
        }
        if(jo.has("table")) {
            table = TableSourceFactory.createTableSource(jo.getJSONObject("table"), UserControl.getInstance().getSuperManagerID());
        }
    }

    public Object getFieldValue(String userName, BIField ck, ICubeDataLoader loader) {
        try {
            BITableRelationPath firstPath = BIConfigureManagerCenter.getTableRelationManager().getFirstPath(loader.getUserId(), ck.getTableBelongTo(), table.getDbTable());
            List<BITableRelation> relations;
            relations = firstPath.getAllRelations();
            BIKey userNameIndex = loader.getTableIndex(table.getDbTable()).getColumnIndex(userName);
            if (userNameIndex != null) {
                final ConnectionRowGetter getter = DirectTableConnectionFactory.createConnectionRow(BIConfUtils.convert2TableSourceRelation(relations, new BIUser(loader.getUserId())), loader);
                ICubeTableService ti = loader.getTableIndex(table.getDbTable());
                GroupValueIndex gvi = ti.getIndexes(userNameIndex, new String[]{userName})[0];
                final FinalInt o = new FinalInt();
                if (gvi != null) {
                    //只取一个值
                    gvi.BrokenableTraversal(new BrokenTraversalAction() {
                        @Override
                        public boolean actionPerformed(int rowIndex) {
                            o.i = getter.getConnectedRow(rowIndex);
                            return true;
                        }
                    });
                    if (o.i != -1) {
                        ICubeTableService cti = loader.getTableIndex(ck);
                        return cti.getRow(cti.getColumnIndex(ck.getFieldName()), o.i);
                    }
                }
            }
        } catch (BITableUnreachableException e) {
            //TODO 这个异常不应该就这么捕获什么都不做。
        }
        return null;
    }
}
