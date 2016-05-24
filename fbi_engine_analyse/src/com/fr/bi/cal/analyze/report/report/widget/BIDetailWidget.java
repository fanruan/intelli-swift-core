package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BISimpleRelation;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.cal.analyze.cal.detail.PolyCubeDetailECBlock;
import com.fr.bi.cal.analyze.executor.detail.DetailExecutor;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.cal.analyze.report.report.widget.detail.BIDetailReportSetting;
import com.fr.bi.cal.analyze.report.report.widget.detail.BIDetailSetting;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.field.target.detailtarget.BIDetailTargetFactory;
import com.fr.bi.field.target.filter.TargetFilterFactory;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.report.poly.TemplateBlock;

import java.util.*;

public class BIDetailWidget extends BIAbstractWidget {
    @BICoreField
    private BIDetailSetting data;
    @BICoreField
    private BIDetailTarget[] dimensions = new BIDetailTarget[0];
    @BICoreField
    private Map<String, TargetFilter> targetFilterMap = new LinkedHashMap<String, TargetFilter>();
    @BICoreField
    private BusinessTable target;//目标表
    private List<String> parent_widget = new ArrayList<String>();
    private String[] sortTargets = new String[0];

    //page from 1~ max
    private int page = 1;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public BIDetailTarget[] getDimensions() {
        return dimensions;
    }

    @Override
    public BIDetailTarget[] getViewDimensions() {
        if (data != null) {
            String[] array = data.getView();
            List<BIDetailTarget> usedDimensions = new ArrayList<BIDetailTarget>();
            for (int i = 0; i < array.length; i++) {
                BIDetailTarget dimension = BITravalUtils.getTargetByName(array[i], dimensions);
                if (dimension.isUsed()) {
                    usedDimensions.add(dimension);
                }

            }
            return usedDimensions.toArray(new BIDetailTarget[usedDimensions.size()]);
        }
        return dimensions;
    }

    @Override
    public BIDetailTarget[] getTargets() {
        return getDimensions();
    }

    @Override
    public BIDetailTarget[] getViewTargets() {
        return getViewDimensions();
    }

    @Override
    public List<BusinessTable> getUsedTableDefine() {
        List<BusinessTable> result = new ArrayList<BusinessTable>();
        BIDetailTarget[] dm = this.getDimensions();
        if (dm != null) {
            for (int i = 0; i < dm.length; i++) {
                BIDetailTarget dt = dm[i];
                result.add(dt.createColumnKey().getTableBelongTo());
            }
        }
        return result;
    }

    @Override
    public List<BusinessField> getUsedFieldDefine() {
        List<BusinessField> result = new ArrayList<BusinessField>();
        BIDetailTarget[] dm = this.getDimensions();
        if (dm != null) {
            for (int i = 0; i < dm.length; i++) {
                BIDetailTarget dt = dm[i];
                result.add(dt.createColumnKey());
            }
        }
        return result;
    }


    public Map<String, TargetFilter> getTargetFilterMap() {
        return targetFilterMap;
    }


    public void setTargetTable(long userID) {
        BITableID targetTableID = dimensions[0].createTableKey().getID();
        target = new BIBusinessTable(targetTableID);
        for (int i = 0; i < dimensions.length; i++) {
            List<BISimpleRelation> relations = dimensions[i].getRelationList(null, userID);
            if (!relations.isEmpty()) {
                target = relations.get(relations.size() - 1).getTableRelation().getForeignTable();
                break;
            }
        }
    }


    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("view")) {
            data = new BIDetailReportSetting();
            data.parseJSON(jo);
        }
        if (jo.has("sort_sequence")) {
            JSONArray ja = jo.getJSONArray("sort_sequence");
            int len = ja.length();
            sortTargets = new String[len];
            for (int i = 0; i < len; i++) {
                sortTargets[i] = ja.getString(i);
            }
        }
        if (jo.has("page")) {
            this.page = jo.getInt("page");
        }
        if (jo.has("dimensions")) {
            try {
                parseDimensions(jo, userId);
            } catch (Exception e) {
                BILogger.getLogger().info(e.getMessage());
            }
        }
        if (jo.has("filter_value")) {
            JSONObject targetFilter = jo.getJSONObject("filter_value");
            Iterator it = targetFilter.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                targetFilterMap.put(key, TargetFilterFactory.parseFilter(targetFilter.getJSONObject(key), userId));
            }
        }
    }

    private void parseDimensions(JSONObject jo, long userId) throws Exception {
        JSONObject dims = jo.getJSONObject("dimensions");
        JSONArray view = jo.getJSONObject("view").getJSONArray(BIReportConstant.REGION.DIMENSION1);
        this.dimensions = new BIDetailTarget[view.length()];
        for (int i = 0; i < view.length(); i++) {
            JSONObject dimObject = dims.getJSONObject(view.getString(i));
            dimObject.put("did", view.getString(i));
            this.dimensions[i] = BIDetailTargetFactory.parseTarget(dimObject, userId);
            JSONObject dimensionMap = dimObject.getJSONObject("dimension_map");
            Iterator it = dimensionMap.keys();
            JSONArray relationJa = dimensionMap.optJSONObject(it.next().toString()).getJSONArray("target_relation");
            List<BISimpleRelation> relationList = new ArrayList<BISimpleRelation>();
            for (int j = 0; j < relationJa.length(); j++) {
                BISimpleRelation viewRelation = new BISimpleRelation();
                viewRelation.parseJSON(relationJa.getJSONObject(j));
                relationList.add(viewRelation);
            }
            this.dimensions[i].setRelationList(relationList);
        }
        setTargetTable(userId);
    }


    public BusinessTable getTargetDimension() {
        return target;
    }

    public String[] getSortTargets() {
        return sortTargets;
    }

    @Override
    public int isOrder() {
        return data.isOrder();
    }

    @Override
    public JSONObject createDataJSON(BISessionProvider session) throws JSONException {
        JSONObject jo = new JSONObject();
        Paging paging = PagingFactory.createPaging(BIExcutorConstant.PAGINGTYPE.GROUP100);
        paging.setCurrentPage(page);
        DetailExecutor exe = new DetailExecutor(this, paging, (BISession) session);
        jo.put("data", exe.getCubeNode());
        jo.put("row", paging.getTotalSize());
        jo.put("size", paging.getPageSize());
        return jo;
    }

    @Override
    public int getType() {
        return BIReportConstant.WIDGET.DETAIL;
    }

    @Override
    protected TemplateBlock createBIBlock(BISession session) {
        return new PolyCubeDetailECBlock(this, session, page);
    }
}