package com.fr.bi.cal.report.report.cal.blockcalculator;

import com.fr.bi.cal.report.report.BIAnalyReport;
import com.fr.bi.cal.report.report.poly.BIPolyWorkSheet;
import com.fr.report.block.AnalyPolyBlock;
import com.fr.report.block.ResultBlock;
import com.fr.report.core.block.BlockSequenceExecutor;
import com.fr.report.core.sheet.SheetExecutor;
import com.fr.report.poly.PolyCoreUtils;
import com.fr.report.poly.TemplateBlock;
import com.fr.report.stable.fun.Actor;
import com.fr.stable.ActorConstants;
import com.fr.stable.ActorFactory;
import com.fr.stable.unit.UNITConstants;
import com.fr.stable.unit.UnitRectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author:ben Administrator
 * @time: 2012 2012-7-3
 * @description:此类用于
 */
public class BIAnalyReportExecutor {
    public List reslist;
    protected Map parameterMap;
    private BIAnalyReport sheet = null;
    private BIPolyWorkSheet ws = null;

    public BIAnalyReportExecutor(BIPolyWorkSheet ws, Map parameterMap) {
        this.ws = ws;
        this.parameterMap = parameterMap;
        init(ws);
    }

    void init(BIPolyWorkSheet tplReport) {
        int count = tplReport.getBlockCount();
        reslist = new ArrayList(tplReport.getBlockCount());
        for (int i = 0, len = count; i < len; i++) {
            reslist.add(null);
        }
    }

    // 生成Executer并且放入到excuter_list中去让后面使用
    public SheetExecutor createExecutor(TemplateBlock block, BlockSequenceExecutor blockSequenceExecuter) {
        SheetExecutor exe = block.createExecutor(ws, parameterMap, blockSequenceExecuter);
        return exe;
    }

    public void addResult(int index, ResultBlock rpb) {
        UnitRectangle bounds = rpb.getBounds();
        // 重置结果块的宽度
        if (PolyCoreUtils.isFreezeWidth(rpb)) {
            bounds.width = bounds.width.subtract(UNITConstants.DELTA);
        } else {
            bounds.width = rpb.getEffectiveWidth();
        }
        // 重置结果块的高度
        if (PolyCoreUtils.isFreezeHeight(rpb)) {
            bounds.height = bounds.height.subtract(UNITConstants.DELTA);
        } else {
            bounds.height = rpb.getEffectiveHeight();
        }
        reslist.set(index, rpb);
    }


    public BIAnalyReport result() {
        if (sheet == null) {
            sheet = new BIAnalyReport();
            ws.copyReportAttr(sheet);
            for (int i = 0, len = reslist.size(); i < len; i++) {
                //b:TODO ge executor结果现在还是analypolyblock
                sheet.addBlock((AnalyPolyBlock) reslist.get(i));
            }
        }
        return sheet;
    }

    public Actor getExeType() {
        return ActorFactory.getActor(ActorConstants.TYPE_BI);
    }
}