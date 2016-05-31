package com.fr.bi.web.conf.services;

import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.data.source.TableSourceFactory;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BIImportDBTableConnectionAction extends AbstractBIConfigureAction {


    @Override
    public String getCMD() {
        return "import_db_table_connection";
    }

    /**
     * 导入数据库关联
     *
     * @param req
     * @param res
     * @throws Exception
     */
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String newTableString = WebUtils.getHTTPRequestParameter(req, "newTables");
        String oldTableString = WebUtils.getHTTPRequestParameter(req, "oldTables");
        Map<String, DBTableSource> newTableSources = getDBSourceMap(newTableString, userId);
        Map<String, DBTableSource> oldTableSources = getDBSourceMap(oldTableString, userId);



        JSONArray relations = getJSONArrayRelationByTables(
                new BIImportDBTableConnectionExecutor().getRelationsByTables(newTableSources, oldTableSources, userId));
        JSONObject translations = getTranslationsByTables(newTableSources);
        JSONObject jo = new JSONObject();
        jo.put("relations", relations);
        jo.put("translations", translations);
        WebUtils.printAsJSON(res, jo);
    }


    private JSONArray getJSONArrayRelationByTables(Set<BITableRelation> relationsSet) throws Exception {
        JSONArray relations = new JSONArray();
        Iterator<BITableRelation> relationIterator = relationsSet.iterator();
        while (relationIterator.hasNext()) {
            relations.put(relationIterator.next().createJSON());//生成JSON并放入JSONARRAY中
        }
        return relations;
    }


    private Map<String, DBTableSource> getDBSourceMap(String tableJO, long userId) throws Exception {
        Map<String, DBTableSource> sources = new HashMap<String, DBTableSource>();
        JSONObject jo = new JSONObject(tableJO);
        Iterator<String> iterator = jo.keys();
        while (iterator.hasNext()) {
            String id = iterator.next();
            CubeTableSource source = TableSourceFactory.createTableSource(jo.getJSONObject(id), userId);
            if (source.getType() == BIBaseConstant.TABLETYPE.DB) {
                sources.put(id, (DBTableSource) source);
            }
        }
        return sources;
    }

    /**
     * 读取数据库转义
     *
     * @param sourceTables
     * @return
     * @throws Exception
     */
    private JSONObject getTranslationsByTables(Map<String, DBTableSource> sourceTables) throws Exception {
        JSONObject jo = new JSONObject();
        JSONObject tableTrans = new JSONObject();
        JSONObject fieldTrans = new JSONObject();
        jo.put(BIJSONConstant.JSON_KEYS.TABLE, tableTrans);
        jo.put(BIJSONConstant.JSON_KEYS.FIELD, fieldTrans);
        Iterator<Map.Entry<String, DBTableSource>> it = sourceTables.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, DBTableSource> entry = it.next();
            IPersistentTable table = entry.getValue().getPersistentTable();
            if (!StringUtils.isEmpty(table.getRemark())) {
                tableTrans.put(entry.getKey(), table.getRemark());
            }
            for (PersistentField column : table.getFieldList()) {
                if (!StringUtils.isEmpty(column.getRemark())) {
                    fieldTrans.put(entry.getKey() + column.getFieldName(), column.getRemark());
                }
            }
        }
        return jo;
    }


}