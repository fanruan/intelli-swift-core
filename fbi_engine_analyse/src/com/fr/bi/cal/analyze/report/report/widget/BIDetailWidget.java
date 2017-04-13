package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.relation.BITableRelationHelper;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.cal.analyze.cal.detail.PolyCubeDetailECBlock;
import com.fr.bi.cal.analyze.executor.detail.DetailExecutor;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.basic.DimAndTargetStyle;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.basic.IExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.DetailTableBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.manager.TableDirector;
import com.fr.bi.cal.analyze.report.report.widget.detail.BIDetailReportSetting;
import com.fr.bi.cal.analyze.report.report.widget.detail.BIDetailSetting;
import com.fr.bi.cal.analyze.report.report.widget.styles.BIStyleSetting;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.field.target.detailtarget.BIDetailTargetFactory;
import com.fr.bi.field.target.detailtarget.formula.BINumberFormulaDetailTarget;
import com.fr.bi.field.target.filter.TargetFilterFactory;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.report.poly.TemplateBlock;
import com.fr.web.core.SessionDealWith;

import java.util.*;

public class BIDetailWidget extends BIAbstractWidget {
    private static final long serialVersionUID = 3558768164064392671L;
    @BICoreField
    private BIDetailSetting data;
    @BICoreField
    private BIDetailTarget[] dimensions = new BIDetailTarget[0];
    @BIIgnoreField
    private /*transient*/ BIDetailTarget[] usedDimensions;
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
        if (usedDimensions != null) {
            return usedDimensions;
        }
        BIDetailTarget[] dims = getDimensions();
        if (data != null) {
            String[] array = data.getView();
            List<BIDetailTarget> usedDimensions = new ArrayList<BIDetailTarget>();
            for (int i = 0; i < array.length; i++) {
                BIDetailTarget dimension = BITravalUtils.getTargetByName(array[i], dimensions);
                usedDimensions.add(dimension);

            }
            dims = usedDimensions.toArray(new BIDetailTarget[usedDimensions.size()]);
        }
        usedDimensions = dims;
        return dims;
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
                if (null != dt.createColumnKey() && null != dt.createColumnKey().getTableBelongTo()) {
                    result.add(dt.createColumnKey().getTableBelongTo());
                }
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
        BITableID targetTableID = new BITableID();
        for (BIDetailTarget target : dimensions) {
            if (!(target instanceof BINumberFormulaDetailTarget)) {
                targetTableID = target.createTableKey().getID();
                break;
            }
        }
        target = BIModuleUtils.getBusinessTableById(new BITableID(targetTableID));
        for (int i = 0; i < dimensions.length; i++) {
            List<BITableRelation> relations = dimensions[i].getRelationList(null, userID);
            if (!relations.isEmpty()) {
                target = relations.get(relations.size() - 1).getForeignTable();
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
                BILoggerFactory.getLogger().info(e.getMessage(), e);
            }
        }
        if (jo.has("filterValue")) {
            JSONObject targetFilter = jo.getJSONObject("filterValue");
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
            JSONObject dimensionMap = dimObject.getJSONObject("dimensionMap");
            Iterator it = dimensionMap.keys();
            JSONArray relationJa = dimensionMap.optJSONObject(it.next().toString()).getJSONArray("targetRelation");
            List<BITableRelation> relationList = new ArrayList<BITableRelation>();
            for (int j = 0; j < relationJa.length(); j++) {
                relationList.add(BITableRelationHelper.getRelation(relationJa.getJSONObject(j)));
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
    public void refreshSources() {
        if (target == null) {
            return;
        }
        try {
            BusinessTable newTable = BIModuleUtils.getBusinessTableById(target.getID());
            if (null != newTable) {
                CubeTableSource newSource = newTable.getTableSource();
                if (isAnalysisSource(newSource)) {
                    newSource.refresh();
                }
                target.setSource(newSource);
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(BIStringUtils.append("error: the analysisTable " + target.getID().getIdentityValue() + " is absent", "\n", e.getMessage()), e);
        }
    }

    private boolean isAnalysisSource(CubeTableSource newSource) {
        List analysisTypes = new ArrayList();
        analysisTypes.add(BIBaseConstant.TABLE_TYPE.BASE);
        analysisTypes.add(BIBaseConstant.TABLE_TYPE.ETL);
        analysisTypes.add(BIBaseConstant.TABLE_TYPE.TEMP);
        analysisTypes.add(BIBaseConstant.TABLE_TYPE.USER_BASE);
        analysisTypes.add(BIBaseConstant.TABLE_TYPE.USER_ETL);
        return analysisTypes.contains(newSource.getType());
    }

    @Override
    public WidgetType getType() {
        return WidgetType.DETAIL;
    }

    @Override
    protected TemplateBlock createBIBlock(BISession session) {
        return new PolyCubeDetailECBlock(this, session, page);
    }


    @Override
    public void reSetDetailTarget() {
        for (BIDetailTarget ele : getDimensions()) {
            ele.reSetDetailGetter();
        }
        for (BIDetailTarget ele : getViewDimensions()) {
            ele.reSetDetailGetter();
        }
    }

    public JSONObject getPostOptions(String sessionId) throws Exception {
        JSONObject dataJSON = this.createDataJSON((BISession) SessionDealWith.getSessionIDInfor(sessionId)).getJSONObject("data");
        Map<Integer, List<JSONObject>> viewMap = createViewMap();
        List<DimAndTargetStyle> dimAndTargetStyles = createChartDimensions();
        IExcelDataBuilder builder = new DetailTableBuilder(viewMap, dimAndTargetStyles, dataJSON, getGlobalStyleSettings());
        TableDirector director = new TableDirector(builder);
        director.construct();
        return director.buildTableData().createJSON();
    }

    private List<DimAndTargetStyle> createChartDimensions() throws Exception {
        List<DimAndTargetStyle> dimAndTargetStyles = new ArrayList<DimAndTargetStyle>();
        for (BIDetailTarget detailTarget : this.getTargets()) {
            DimAndTargetStyle dimAndTargetStyle = new DimAndTargetStyle(detailTarget.getId(), detailTarget.getChartSetting());
            dimAndTargetStyle.setChartSetting(detailTarget.getChartSetting());
            dimAndTargetStyles.add(dimAndTargetStyle);
        }
        return dimAndTargetStyles;
    }

    private Map<Integer, List<JSONObject>> createViewMap() throws Exception {
        Map<Integer, List<JSONObject>> dimAndTar = new HashMap<Integer, List<JSONObject>>();
        List<JSONObject> dims = new ArrayList<JSONObject>();
        for (BIDetailTarget detailTarget : this.getDimensions()) {
            String dId = detailTarget.getId();
            int type = detailTarget.createColumnKey().getFieldType();
            String text = detailTarget.getText();
            JSONObject jo = new JSONObject().put("dId", dId).put("text", text).put("type", type).put("used", detailTarget.isUsed());
            dims.add(jo);
        }
        dimAndTar.put(Integer.valueOf(BIReportConstant.REGION.DIMENSION1), dims);
        return dimAndTar;
    }

    public BIStyleSetting getGlobalStyleSettings() {
        return data.getStyleSetting();
    }
}