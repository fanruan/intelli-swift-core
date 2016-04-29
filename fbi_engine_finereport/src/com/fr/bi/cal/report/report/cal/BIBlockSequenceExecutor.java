package com.fr.bi.cal.report.report.cal;

import com.fr.base.FRContext;
import com.fr.base.Formula;
import com.fr.bi.cal.report.report.BIAnalyReport;
import com.fr.bi.cal.report.report.cal.blockcalculator.BIAnalyReportExecutor;
import com.fr.bi.cal.report.report.poly.BIPolyWorkSheet;
import com.fr.cache.list.IntList;
import com.fr.parser.BlockIntervalLiteral;
import com.fr.report.block.ResultBlock;
import com.fr.report.cell.TemplateCellElement;
import com.fr.report.core.sheet.SequenceExecutor;
import com.fr.report.core.sheet.SheetExecutor;
import com.fr.report.elementcase.ResultElementCase;
import com.fr.report.elementcase.TemplateElementCase;
import com.fr.report.poly.TemplateBlock;
import com.fr.report.report.ResultReport;
import com.fr.report.stable.fun.Actor;
import com.fr.script.Calculator;
import com.fr.stable.ListMap;
import com.fr.stable.script.Expression;
import com.fr.third.antlr.ANTLRException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author:ben Administrator
 * @time: 2012 2012-7-3
 * @description:此类用于
 */
public class BIBlockSequenceExecutor extends SequenceExecutor {

    private BIPolyWorkSheet tplReport;
    private BIAnalyReportExecutor current_ee;

    public BIBlockSequenceExecutor(BIPolyWorkSheet tplReport, Map parameterMap) {
        if (tplReport != null) {
            int count = tplReport.getBlockCount();
            nameListMap = new ListMap(count);
            executed_mark = new IntList(count);
            executor_list = new ArrayList(count);
            exe_info_list = new ArrayList(count);
            exe_start_time_list = new ArrayList(count);
            TemplateBlock block = null;
            for (int i = 0; i < count; i++) {
                block = (TemplateBlock) tplReport.getBlock(i);
                // 这边nameListMap如果放(block, block)的话,下面用的地方一起改下
                nameListMap.put(block.getBlockName(), block);
                executed_mark.add(NO_EXE_MARK);
                executor_list.add(null);
                exe_info_list.add(new int[count]);
                exe_start_time_list.add(null);
            }
        }
        this.tplReport = tplReport;
        this.parameterMap = parameterMap;
    }

    public ResultReport execute(Actor actor) {
        if (tplReport == null) {
            return null;
        }
        current_ee = new BIAnalyReportExecutor(tplReport, parameterMap);
        _execute();
        try {
            return current_ee.result();
        } finally {
            current_ee = null;
        }
    }

    protected void _execute() {
        TemplateBlock pb;
        for (int i = 0, len = this.nameListMap.size(); i < len; i++) {
            pb = (TemplateBlock) this.nameListMap.getByIndex(i);
            this.current_index = i;
            SheetExecutor exe = null;
//			if(checkIncludingBlockExecuteSequence() || pb instanceof PolyChartBlock){// kunsnat: 聚合图表, 全部先处理下表间公式.
//				exe = current_ee.createExecutor(pb, this);
//			}else{
            exe = current_ee.createExecutor(pb, null);
//			}

            executor_list.set(BIBlockSequenceExecutor.this.current_index, exe);
        }
        // richer：从最后一个块开始算起
        SheetExecutor exe;
        while (next()) {
            exe = this.getExecutor();
            execute(exe);
        }
    }

    @Override
    protected long execute4Expand(SheetExecutor se) {
        return se.execute4Expand(current_ee.getExeType());
    }

    @Override
    protected void addExecutor4Result(SheetExecutor se, long startTime) {
        current_ee.addResult(this.current_index, se.execute4Poly(current_ee.getExeType(), startTime));
    }

    @Override
    protected void addExecutor4Result(int index) {
        current_ee.addResult(index, ((SheetExecutor) this.executor_list.get(index)).execute4Poly(current_ee
                .getExeType(), ((Long) this.exe_start_time_list.get(index)).longValue()));
    }

    @Override
    protected String getLiteralName(List list, int i) {
        BlockIntervalLiteral literal = (BlockIntervalLiteral) list.get(i);
        return literal.getBlockName();
    }

    @Override
    protected SheetExecutor getExecutor() {
        return (SheetExecutor) this.executor_list.get(current_index);
    }

    /**
     * 根据序号返回报表模板.
     */
    @Override
    public TemplateElementCase getTemplateByIndex(int index) {
        TemplateBlock tb = (TemplateBlock) this.nameListMap.getByIndex(index);
        return tb.isCells() ? (TemplateElementCase) tb : null;
    }

    /**
     * 检查模块之间是否有表间计算.
     */
    public boolean checkIncludingBlockExecuteSequence() {

        int len = this.nameListMap.size();
        if (len <= 1) {
            return false;
        }

        boolean result = false;
        List bilList = new ArrayList();
        TemplateElementCase workSheet;

        for (int i = 0; i < len; i++) {
            workSheet = this.getTemplateByIndex(i);
            if (workSheet == null) {
                continue;
            }
            Iterator ceIt = workSheet.cellIterator();
            TemplateCellElement ce;
            Object value;

            while (ceIt.hasNext()) {
                ce = (TemplateCellElement) ceIt.next();
                if (ce != null) {
                    value = ce.getValue();
                    if (value instanceof Formula) {
                        try {
                            Expression ex = Calculator.createCalculator().parse(((Formula) value).getContent());

                            ex.trav4HuntBIL(bilList);
                            if (bilList.size() > 0) {
                                result = true;
                                break;
                            }
                        } catch (ANTLRException e) {
                            FRContext.getLogger().error(e.getMessage(), e);
                        }
                    }
                }

            }
        }

        return result;
    }

    /**
     * 根据序号 返回计算结果.
     */
    @Override
    public ResultElementCase getResultByIndex(int index) {
        ResultBlock rb = (ResultBlock) this.current_ee.reslist.get(index);
        return rb.isCells() ? (ResultElementCase) rb : null;
    }

    public BIAnalyReport execute4BI() {
        current_ee = new BIAnalyReportExecutor(tplReport, parameterMap);
        _execute();
        try {
            return current_ee.result();
        } finally {
            current_ee = null;
        }
    }
}