package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.cal.analyze.report.report.widget.util.BIWidgetFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.main.impl.BIWorkBook;
import com.fr.bi.cal.report.report.poly.BIPolyWorkSheet;
import com.fr.bi.conf.base.auth.data.BIPackageAuthority;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.conf.BIWidgetConf;
import com.fr.bi.conf.report.conf.BIWidgetSettings;
import com.fr.bi.conf.report.widget.BIWidgetStyle;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.field.dimension.calculator.NoneDimensionCalculator;
import com.fr.bi.field.target.filter.TargetFilterFactory;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.report.result.DimensionCalculator;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.main.impl.WorkBook;
import com.fr.report.poly.TemplateBlock;
import com.fr.stable.CodeUtils;
import com.fr.stable.Constants;
import com.fr.stable.unit.UnitRectangle;
import com.fr.web.core.SessionDealWith;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 处理BI控件的公共属性，如数据源，数据列
 *
 * @author Daniel-pc
 */
public abstract class AbstractBIWidget implements BIWidget {

    private static final long serialVersionUID = 1959074307747827366L;

    private String blockName;

    private String widgetId;

    private Rectangle rect = new Rectangle();

    @BICoreField
    private TargetFilter filter;

    private long initTime;

    private long userId;

    private boolean realData = true;

    private String sessionId;

    @BICoreField
    protected BIWidgetConf widgetConf = new BIWidgetConf();

    /**
     * 跳转组件
     */
    protected BIWidget globalFilterWidget;

    /**
     * 跳转点击的值
     */
    protected JSONObject globalFilterClick;

    /**
     * 跳转的源字段和目标字段
     */
    protected JSONArray globalFilterSourceAndTargetField;

    public long getUserId() {

        return userId;
    }

    public boolean isRealData() {

        return realData;
    }

    public void setRealData(boolean realData) {

        this.realData = realData;
    }

    private UnitRectangle getBlockBounds() {

        return new UnitRectangle(rect, Constants.DEFAULT_WEBWRITE_AND_SCREEN_RESOLUTION);
    }

    /**
     */
    @Override
    public String getWidgetName() {

        return blockName;
    }

    public String getWidgetId() {

        return widgetId;
    }

    @Override
    public void setWidgetName(String name) {

        this.blockName = name;
    }

    public TargetFilter getFilter() {

        return filter;
    }

    public void setFilter(TargetFilter filter) {

        this.filter = filter;
    }

    /**
     * 创建workbook
     *
     * @return 工作簿
     */
    @Override
    public WorkBook createWorkBook(BISessionProvider session) {

        BIWorkBook wb = new BIWorkBook();
        wb.addReport(createWorkSheet(session));
        return wb;
    }

    @Override
    public BIPolyWorkSheet createWorkSheet(BISessionProvider session) {

        BIPolyWorkSheet ws = new BIPolyWorkSheet();
        ws.addBlock(this.createTemplateBlock((BISession) session));
        return ws;
    }

    @Override
    public boolean showRowToTal() {

        return true;
    }

    @Override
    public boolean showColumnTotal() {

        return true;
    }

    @Override
    public void refreshColumns() {

        for (BITargetAndDimension td : getTargets()) {
            td.refreshColumn();
        }
        for (BITargetAndDimension td : getDimensions()) {
            td.refreshColumn();
        }
    }

    /**
     * 根据widget创建TemplateBlock
     */
    protected TemplateBlock createTemplateBlock(BISession session) {

        TemplateBlock block = createBIBlock(session);
        block.setBlockName(CodeUtils.passwordEncode(blockName));
        block.getBlockAttr().setFreezeHeight(true);
        block.getBlockAttr().setFreezeWidth(true);
        block.setBounds(getBlockBounds());
        return block;
    }

    /**
     * 根据计算好的属性创建block
     *
     * @return
     */
    protected abstract TemplateBlock createBIBlock(BISession session);


    /**
     * 将传过来的JSONObject 对象转换成java对象
     *
     * @param jo     json对象
     * @param userId 用户id
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {

        widgetConf.parseJSON(jo);

        blockName = widgetConf.getWidgetName();
        widgetId = widgetConf.getWId();

        if (jo.has("filter")) {
            JSONObject filterJo = jo.getJSONObject("filter");
            filter = TargetFilterFactory.parseFilter(filterJo, userId);
        }
        if (jo.has("initTime")) {
            initTime = jo.getLong("initTime");
        }
        if (jo.has("realData")) {
            realData = jo.optBoolean("realData", true);
        }
        if (jo.has("sessionID")) {
            sessionId = jo.getString("sessionID");
        }
        this.userId = userId;

        /**
         * 跳转相关解析
         */
        if (jo.has("globalFilter")) {
            JSONObject glf = jo.getJSONObject("globalFilter");
            if (glf.has("linkedWidget")) {
                globalFilterWidget = BIWidgetFactory.parseWidget(glf.getJSONObject("linkedWidget"), userId);
                // 跳转过来的组件有clieck属性
                if (glf.has("clicked")) {
                    globalFilterClick = glf.getJSONObject("clicked");
                }
            }
        }
        // 跳转过来的组件上面有jump属性
        if (jo.has("jump")) {
            globalFilterSourceAndTargetField = jo.getJSONArray("jump");
        }
    }

    @Override
    public Object clone() {

        try {
            return super.clone();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    public GroupValueIndex createFilterGVI(DimensionCalculator[] row, BusinessTable targetKey, ICubeDataLoader loader, long userId) {

        // 自身的过滤
        if (row.length == 0) {
            row = new DimensionCalculator[]{new NoneDimensionCalculator(targetKey.getFields().get(0), new ArrayList<BITableSourceRelation>())};
        }
        GroupValueIndex gvi = loader.getTableIndex(targetKey.getTableSource()).getAllShowIndex();
        GroupValueIndex authGVI = null;
        List<TargetFilter> filters = getAuthFilter(userId);
        for (int i = 0; i < filters.size(); i++) {
            for (int j = 0; j < row.length; j++) {
                if (authGVI == null) {
                    authGVI = filters.get(i).createFilterIndex(row[j], targetKey, loader, userId);
                } else {
                    authGVI = GVIUtils.OR(authGVI, filters.get(i).createFilterIndex(row[j], targetKey, loader, userId));
                }
            }
        }
        gvi = GVIUtils.AND(gvi, authGVI);
        if (filter != null) {
            for (int i = 0; i < row.length; i++) {
                gvi = GVIUtils.AND(gvi, filter.createFilterIndex(row[i], targetKey, loader, userId));
            }
        }

        return gvi;
    }

    public List<TargetFilter> getAuthFilter(long userId) {

        List<TargetFilter> filters = new ArrayList<TargetFilter>();
        //管理员用户没有权限的过滤条件
        if (userId == UserControl.getInstance().getSuperManagerID()) {
            return filters;
        }
        List<BIPackageID> authPacks;
        BISessionProvider session = null;
        if (sessionId != null && SessionDealWith.hasSessionID(sessionId)) {
            session = (BISessionProvider) SessionDealWith.getSessionIDInfor(sessionId);
            authPacks = BIConfigureManagerCenter.getAuthorityManager().getAuthPackagesByRoles(session.getCompanyRoles(), session.getCustomRoles());
        } else {
            authPacks = BIConfigureManagerCenter.getAuthorityManager().getAuthPackagesByUser(userId);
        }
        for (int i = 0; i < authPacks.size(); i++) {
            List<BIPackageAuthority> packAuths;
            if (sessionId != null && SessionDealWith.hasSessionID(sessionId)) {
                packAuths = BIConfigureManagerCenter.getAuthorityManager().getPackageAuthByRoles(authPacks.get(i), session.getCompanyRoles(), session.getCustomRoles());
            } else {
                packAuths = BIConfigureManagerCenter.getAuthorityManager().getPackageAuthByID(authPacks.get(i), userId);
            }
            for (int j = 0; j < packAuths.size(); j++) {
                BIPackageAuthority auth = packAuths.get(j);
                if (auth.getFilter() != null) {
                    try {
                        TargetFilter filter = TargetFilterFactory.parseFilter(auth.getFilter(), userId);
                        filters.add(filter);
                    } catch (Exception e) {
                        BILoggerFactory.getLogger().error(e.getMessage(), e);
                    }
                }
            }
        }
        return filters;
    }

    @Override
    public BICore fetchObjectCore() {

        return new BICoreGenerator(this).fetchObjectCore();
    }

    @Override
    public void refreshSources() {

    }

    @Override
    public Rectangle getRect() {
        return widgetConf.getRect();
    }

    public JSONObject getPostOptions(BISessionProvider session, HttpServletRequest req) throws Exception {

        return new JSONObject();
    }

    public JSONObject createChartConfigWidthData(BISessionProvider session, HttpServletRequest req, JSONObject data) throws Exception {

        return data;
    }

    /**
     * 获取跳转源字段以及目标字段设置
     *
     * @return
     */
    public List<Map> getGlobalSourceAndTargetFieldList() {

        List<Map> r = new ArrayList<Map>();
        if (globalFilterSourceAndTargetField != null && globalFilterSourceAndTargetField.length() > 0) {
            try {
                JSONArray sf = globalFilterSourceAndTargetField.getJSONObject(0).getJSONArray("sourceTargetFields");
                for (int i = 0; i < sf.length(); i++) {
                    JSONObject j = sf.getJSONObject(i);
                    Map<String, String> s = new HashMap<String, String>();
                    s.put("sourceFieldId", j.optString("sourceFieldId"));
                    s.put("targetFieldId", j.optString("targetFieldId"));
                    r.add(s);
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger(this.getClass()).info("error in get global filter source and target field", e);
            }
        }
        return r;
    }

    public BIWidget getGlobalFilterWidget() {

        return globalFilterWidget;
    }

    public Map<String, JSONArray> getGlobalFilterClick() {

        Map<String, JSONArray> r = new HashMap<String, JSONArray>();
        try {
            // 明细表的click值和分组表的不相同
            if (globalFilterClick != null) {
                //
                Iterator<String> iterator = (Iterator<String>) globalFilterClick.keys();
                while (iterator.hasNext()) {
                    String k = iterator.next();
                    // 明细表专用
                    if (k.equals("pageCount") || k.equals("rowIndex") || k.equals("value") || k.equals("dId")) {
                        JSONArray v = JSONArray.create();
                        v.put(globalFilterClick.optString(k, ""));
                        r.put(k, v);
                    } else {
                        r.put(k, globalFilterClick.getJSONArray(k));
                    }
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).info("error in get jump link filter click value");
        }
        return r;
    }

    public JSONArray getGlobalFilterSourceAndTargetField() {

        return globalFilterSourceAndTargetField;
    }

    /**
     * 跳转过滤
     *
     * @return
     */
    public GroupValueIndex getJumpLinkFilter(BusinessTable targetKey, long userId, BISession session) {

        return null;
    }

    public BusinessTable getBaseTable() {

        return null;
    }

    public BIWidgetConf getWidgetConf() {
        return widgetConf;
    }


    public BIWidgetSettings getWidgetSettings(BIWidgetConf widgetConf) {
        if (null != widgetConf) {
            return widgetConf.getWidgetSettings();
        } else {
            return getWidgetConf().getWidgetSettings();
        }
    }

    public BIWidgetSettings getWidgetSettings() {
        return widgetConf.getWidgetSettings();
    }
    
    @Override
    public JSONObject generateResult(BIWidgetConf widgetConf, JSONObject data) throws Exception {
        return null;
    }

    public BIWidgetStyle getStyle() {
        return null;
    }
}