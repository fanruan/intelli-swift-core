/**
 *
 */
package com.fr.bi.field.target.filter.tree;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.field.target.filter.AbstractTargetFilter;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.util.BIConfUtils;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;


public class TreeColumnFieldsFilter extends AbstractTargetFilter {
    private static String XML_TAG = "TreeColumnFieldsFilter";
    private BusinessField[] keys;
    private TreeFilterValue[] values;
    private JSONObject valueJo;
    private BusinessTable foreignTable;
    private BITableRelation[][] relations;

    /**
     * 重写code
     *
     * @return 数值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(keys);
        result = prime * result + Arrays.hashCode(values);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        TreeColumnFieldsFilter other = (TreeColumnFieldsFilter) obj;
        if (!ComparatorUtils.equals(keys, other.keys)) {
            return false;
        }
        if (!ComparatorUtils.equals(keys, other.keys)) {
            return false;
        }

        return true;
    }

    /**
     * 解析json
     *
     * @param jo     json对象
     * @param userId 用户id
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        valueJo = jo;
        if (jo.has("value")) {
            JSONArray ja = jo.getJSONArray("value");
            int len = ja.length();
            this.values = new TreeFilterValue[len];
            for (int i = 0; i < len; i++) {
                this.values[i] = new TreeFilterValue();
                this.values[i].parseJSON(ja.getJSONObject(i));
            }
        }
        if (jo.has("statistics_elements")) {
            JSONArray ja = jo.getJSONArray("statistics_elements");

            int len = ja.length();
            this.keys = new BusinessField[len];
            for (int i = 0; i < len; i++) {
                BusinessField column = new BIBusinessField();
                column.parseJSON(ja.getJSONObject(i));
                this.keys[i] = column;
            }
        }
        if (jo.has("foreignTable")) {
            JSONObject fTable = jo.getJSONObject("foreignTable");
            BusinessField c = new BIBusinessField();
            c.parseJSON(fTable);
            this.foreignTable = c.getTableBelongTo();
            if (fTable.has("relationmap")) {
                JSONArray rmap = fTable.getJSONArray("relationmap");
                relations = new BITableRelation[rmap.length()][];
                for (int i = 0; i < relations.length; i++) {
                    relations[i] = new BITableRelation[0];
                }
                for (int i = 0; i < rmap.length(); i++) {
                    JSONArray r = rmap.getJSONArray(i);
                    BITableRelation[] relation = new BITableRelation[r.length()];
                    for (int j = 0; j < r.length(); j++) {
                        BITableRelation rela = new BITableRelation();
                        rela.parseJSON(r.getJSONObject(j));
                        relation[j] = rela;
                    }
                    relations[i] = relation;
                }
            }
        }
    }

    /**
     * 创建json
     *
     * @return json对象
     * @throws Exception 报错
     */
    @Override
    public JSONObject createJSON() throws Exception {
        return valueJo;
    }

    /**
     * @param reader XML读取对象
     * @see com.fr.stable.xml.XMLableReader
     */
    @Override
    public void readXML(XMLableReader reader) {
        if (reader.getTagName().equals("value")) {

            try {
                JSONObject jo = new JSONObject(reader.getAttrAsString("value", StringUtils.EMPTY));
                this.parseJSON(jo, UserControl.getInstance().getSuperManagerID());
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }

    /**
     * @param writer XML写入对象
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);

        try {
            writer.attr("value", createJSON().toString());
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        writer.end();
    }

    /**
     * 创建过滤条件的index，用于指标过滤
     *
     * @param target
     * @param loader loader对象
     * @return 分组索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        GroupValueIndex resgvi = null;
        if (values == null || values.length == 0) {
            return resgvi;
        }
//        List<List<BITableRelation>> relationLists = BIRelationManager.getInstance(userId).getRelations(foreignTable, target);
        Set<BITableRelationPath> allPath = null;
        try {
            allPath = BICubeConfigureCenter.getTableRelationManager().getAllPath(userId, target, foreignTable);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
//        allPath.addAll(BIConfigureManagerCenter.getConnectionManager().getAllPath(userId, target, foreignTable));
        if (allPath != null || ComparatorUtils.equals(target, foreignTable)) {
            for (int i = 0; i < values.length; i++) {
                if (values[i] != null) {
                    GroupValueIndex tgvi = values[i].createFilterIndex(dimension, this.keys, 0, foreignTable, this.relations, loader, userId);
                    if (resgvi == null) {
                        resgvi = tgvi;
                    } else {
                        resgvi = resgvi.OR(tgvi);
                    }
                }
            }
            GroupValueIndex gvi = null;
            Iterator<BITableRelationPath> it = allPath.iterator();
            while (it.hasNext()) {
                ICubeTableService ti = loader.getTableIndex(foreignTable.getTableSource());
                BITableRelationPath onePath = it.next();
                GroupValueIndex groupValueIndex = GVIUtils.getTableLinkedOrGVI(resgvi, ti.ensureBasicIndex(BIConfUtils.convert2TableSourceRelation(onePath.getAllRelations())));
                if (gvi == null) {
                    gvi = groupValueIndex;
                } else {
                    gvi = gvi.OR(groupValueIndex);
                }
            }
            return gvi;
        }
        return resgvi;
    }

    /**
     * 指标过滤用不到树控件
     *
     * @param target
     * @param loader
     * @param userID
     * @return
     */
    @Override
    public GroupValueIndex createFilterIndex(BusinessTable target, ICubeDataLoader loader, long userID) {
        return null;
    }

}