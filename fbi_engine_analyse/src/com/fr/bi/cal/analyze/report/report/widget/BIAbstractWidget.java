package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.main.impl.BIWorkBook;
import com.fr.bi.cal.report.report.poly.BIPolyWorkSheet;
import com.fr.bi.conf.base.auth.data.BIPackageAuthority;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.field.target.filter.TargetFilterFactory;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;
import com.fr.main.impl.WorkBook;
import com.fr.report.poly.TemplateBlock;
import com.fr.stable.CodeUtils;
import com.fr.stable.Constants;
import com.fr.stable.unit.UnitRectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 处理BI控件的公共属性，如数据源，数据列
 *
 * @author Daniel-pc
 */
public abstract class BIAbstractWidget implements BIWidget {

    private String blockName;
    private Rectangle rect = new Rectangle();
    @BICoreField
    private TargetFilter filter;
    private long initTime;
    private long userId;
    private boolean realData = true;

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

    @Override
    public void setWidgetName(String name) {
        this.blockName = name;
    }

    public TargetFilter getFilter() {
        return filter;
    }

    /**
     * 创建workbook
     *
     * @return 工作簿
     */
    @Override
    public WorkBook createWorkBook(BISessionProvider session) {
        BIWorkBook wb = new BIWorkBook();
        BIPolyWorkSheet ws = new BIPolyWorkSheet();
        ws.addBlock(this.createTemplateBlock((BISession) session));
        wb.addReport(ws);
        return wb;
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
        int x = 0, y = 0, width = 0, height = 0;
        if (jo.has("bounds")) {
            JSONObject bounds = jo.getJSONObject("bounds");
            x = bounds.optInt("left", 0);
            y = bounds.optInt("top", 0);
            width = bounds.optInt("width", 0);
            height = bounds.optInt("height", 0);
        }
        rect.setBounds(x, y, width, height);
        if (jo.has("name")) {
            this.blockName = jo.getString("name");
        }
        if (jo.has("filter")) {
            JSONObject filterJo = jo.getJSONObject("filter");
            filter = TargetFilterFactory.parseFilter(filterJo, userId);
        }
        if (jo.has("init_time")) {
            initTime = jo.getLong("init_time");
        }
        if(jo.has("real_data")) {
            realData = jo.optBoolean("real_data", true);
        }
        this.userId = userId;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    public GroupValueIndex createFilterGVI(DimensionCalculator[] row, BusinessTable targetKey, ICubeDataLoader loader, long userId) {
        GroupValueIndex gvi = loader.getTableIndex(targetKey.getTableSource()).getAllShowIndex();
        //非管理员用户需要考虑到对于权限的过滤条件
        if(userId != UserControl.getInstance().getSuperManagerID()) {
            List<TargetFilter> filters = getAuthFilter(userId);
            for(int i = 0; i < filters.size(); i++) {
                for (int j = 0; j < row.length; j++) {
                    gvi = GVIUtils.AND(gvi, filters.get(i).createFilterIndex(row[j], targetKey, loader, userId));
                }
            }
        }
        if (filter != null) {
            for (int i = 0; i < row.length; i++) {
                gvi = GVIUtils.AND(gvi, filter.createFilterIndex(row[i], targetKey, loader, userId));
            }
        }
        return gvi;
    }

    private List<TargetFilter> getAuthFilter(long userId){
        List<TargetFilter> filters = new ArrayList<TargetFilter>();
        List<BIPackageID> authPacks = BIConfigureManagerCenter.getAuthorityManager().getAuthPackagesByUser(userId);
        for(int i = 0 ; i < authPacks.size(); i ++) {
            List<BIPackageAuthority> packAuths = BIConfigureManagerCenter.getAuthorityManager().getPackageAuthByID(authPacks.get(i), userId);
            for(int j = 0; j < packAuths.size(); j++) {
                BIPackageAuthority auth = packAuths.get(j);
                if(auth.getFilter() != null) {
                    filters.add(auth.getFilter());
                }
            }
        }
        return filters;
    }

    @Override
    public BICore fetchObjectCore() {
        return new BICoreGenerator(this).fetchObjectCore();
    }
}