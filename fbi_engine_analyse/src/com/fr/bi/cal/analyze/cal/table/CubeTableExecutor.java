package com.fr.bi.cal.analyze.cal.table;

import com.fr.base.FRContext;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.exception.NoneRegisterationException;
import com.fr.bi.cal.analyze.exception.TooManySummaryException;
import com.fr.bi.cal.analyze.executor.BIEngineExecutor;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.cal.report.engine.CBCellCase;
import com.fr.bi.cal.report.report.poly.BIPolyAnalyECBlock;
import com.fr.general.DateUtils;
import com.fr.report.block.ResultBlock;
import com.fr.report.core.reserve.ExecuteParameterMapNameSpace;
import com.fr.report.core.sheet.SheetExecutor;
import com.fr.report.elementcase.TemplateElementCase;
import com.fr.report.poly.AbstractPolyECBlock;
import com.fr.report.report.Report;
import com.fr.report.report.ResultECReport;
import com.fr.report.report.TemplateReport;
import com.fr.report.worksheet.AbstractResECWorkSheet;
import com.fr.report.worksheet.PageRWorkSheet;
import com.fr.report.worksheet.WorkSheet;
import com.fr.script.Calculator;

import java.awt.*;
import java.util.Map;
import java.util.logging.Level;

public class CubeTableExecutor extends SheetExecutor {

    /**
     *
     */
    private static final long serialVersionUID = 6803318153614021764L;
    private TemplateElementCase elementCase;
    private TableWidget widget;
    private Calculator cal;
    private CBCell[][] cbcells;
    private Report report;
    private BISession session;
    // page from 1 ~ max
    private int page = 1;
    private Rectangle southEastRectangle;

    public CubeTableExecutor(TemplateReport report, TableWidget widget,
                             BISession session,
                             AbstractPolyECBlock tplBlock, Map parameterMap, int page) {
        super(parameterMap);
        this.report = report;
        this.widget = widget;
        this.session = session;
        this.elementCase = tplBlock;
        initCalculator(parameterMap);
        this.page = page;
    }

    private void initCalculator(Map parameterMap) {
        this.cal = Calculator.createCalculator();
        this.cal.pushNameSpace(ExecuteParameterMapNameSpace.create(parameterMap));
    }

    /**
     * 注释
     *
     * @param actor 注
     * @return long 注
     */
    @Override
    public long execute4Expand(com.fr.report.stable.fun.Actor actor) {
        long startTime = System.currentTimeMillis();
        try {
            execute();
        } catch (TooManySummaryException e) {
            throw e;
        } catch (NoneAccessablePrivilegeException e) {
            throw e;
        } catch (NoneRegisterationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            FRContext.getLogger().log(Level.WARNING, e.getMessage(), e);
        }
        return startTime;
    }

    private void execute() throws NoneAccessablePrivilegeException {
        if (session == null) {
            cbcells = new CBCell[][]{new CBCell[0]};
        }
        BIEngineExecutor exe = widget.getExecutor(session);
        cbcells = exe.createCellElement();
        southEastRectangle = exe.getSouthEastRectangle();
    }

    /**
     * 注释
     *
     * @param actor     注释
     * @param startTime 注释
     * @return long 注释
     */
    // b:TODO 考虑sheet的行高列宽，高亮，页眉页脚
    @Override
    public ResultBlock execute4Poly(com.fr.report.stable.fun.Actor actor, long startTime) {
        BIPolyAnalyECBlock block = new BIPolyAnalyECBlock();
        try {
            ((AbstractPolyECBlock) this.elementCase).cloneWithoutCellCase(block);
        } catch (CloneNotSupportedException e) {
            // 直接抛Runtime的
            throw new RuntimeException(e);
        }
        block.setCellCase(new CBCellCase(cbcells));
        block.setSouthEastRectangle(southEastRectangle);
        for (int i = 0, len = cbcells.length; i < len; i++) {
            for (int j = 0, jlen = cbcells[i].length; j < jlen; j++) {
                block.shrinkTOFitColumnWidthForCellElement(cbcells[i][j]);
            }
        }
        FRContext.getLogger().log(Level.WARNING, DateUtils.timeCostFrom(startTime) + " beb time");
        release();
        return block;
    }

    protected ResultECReport execute4ECReport(int execute_type, long startTime) {
        long s = System.currentTimeMillis();
        ResultECReport result = getResultReport((WorkSheet) this.report);
        FRContext.getLogger().log(Level.WARNING, DateUtils.timeCostFrom(s) + " p2");

        release();
        return result;
    }

    private void release() {
        elementCase = null;
        widget = null;
        cal = null;
        cbcells = null;
        report = null;
    }

    // b:TODO 考虑sheet的行高列宽，高亮，页眉页脚...gecells需要另外生成，遍历一遍的性能还是要考虑的
    private ResultECReport getResultReport(WorkSheet ws) {

        // b: TODO 先直接返回Page,内部逻辑是按page处理的，公式之类的，实际是需要编辑的类似analysisreport
        AbstractResECWorkSheet sheet = new PageRWorkSheet();
        try {
            ws.cloneWithoutCellCase(sheet);
        } catch (CloneNotSupportedException e) {
            // 直接抛Runtime的
            throw new RuntimeException(e);
        }
        sheet.setCellCase(new CBCellCase(cbcells));

        return sheet;
    }

}