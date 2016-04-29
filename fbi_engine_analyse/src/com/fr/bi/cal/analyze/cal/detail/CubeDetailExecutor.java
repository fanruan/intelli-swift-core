package com.fr.bi.cal.analyze.cal.detail;

import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.executor.detail.DetailExecutor;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.cal.report.engine.CBCellCase;
import com.fr.bi.cal.report.report.poly.BIPolyAnalyECBlock;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.general.DateUtils;
import com.fr.report.block.ResultBlock;
import com.fr.report.cell.cellattr.CellGUIAttr;
import com.fr.report.core.reserve.ExecuteParameterMapNameSpace;
import com.fr.report.core.sheet.SheetExecutor;
import com.fr.report.elementcase.TemplateElementCase;
import com.fr.report.poly.AbstractPolyECBlock;
import com.fr.report.poly.ResultECBlock;
import com.fr.report.report.Report;
import com.fr.report.report.ResultECReport;
import com.fr.report.report.TemplateReport;
import com.fr.report.worksheet.AbstractResECWorkSheet;
import com.fr.report.worksheet.PageRWorkSheet;
import com.fr.report.worksheet.WorkSheet;
import com.fr.script.Calculator;

import java.util.Map;

public class CubeDetailExecutor extends SheetExecutor {

    /**
     *
     */
    private static final long serialVersionUID = 7869582707254192595L;


    private TemplateElementCase elementCase;
    private BIDetailWidget widget;
    private Calculator cal;
    private CBCell[][] cbcells;
    private Report report;
    private BISession session;
    // page from 1 ~ max
    private int page = 1;

    public CubeDetailExecutor(TemplateReport report, BIDetailWidget widget,
                              BISession session, AbstractPolyECBlock tplBlock, Map<String, Object> parameterMap, int page) {
        super(parameterMap);
        this.report = report;
        this.widget = widget;
        this.session = session;
        this.elementCase = tplBlock;
        this.page = page;
        initCalculator(parameterMap);
    }

    private void initCalculator(Map parameterMap) {
        this.cal = Calculator.createCalculator();
        this.cal.pushNameSpace(ExecuteParameterMapNameSpace.create(parameterMap));
    }


    public BISessionProvider getSession() {
        return session;
    }

    /**
     * 展开执行
     *
     * @param actor 锚
     * @return long值
     */
    @Override
    public long execute4Expand(com.fr.report.stable.fun.Actor actor) {
        long startTime = System.currentTimeMillis();
        try {
            execute();
        } catch (NoneAccessablePrivilegeException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return startTime;
    }

    private void execute() throws NoneAccessablePrivilegeException {
        if (session == null) {
            return;
        }
        Paging paging = PagingFactory.createPaging(BIExcutorConstant.PAGINGTYPE.GROUP100);
        paging.setCurrentPage(page);

        DetailExecutor exe = new DetailExecutor(widget, paging, session);
        cbcells = exe.createCellElement();
    }

    /**
     * 执行
     *
     * @param actor     锚
     * @param startTime 开始时间
     * @return 结果
     */
    @Override
    public ResultBlock execute4Poly(com.fr.report.stable.fun.Actor actor, long startTime) {
        ResultECBlock block = new BIPolyAnalyECBlock();
        try {
            ((AbstractPolyECBlock) this.elementCase).cloneWithoutCellCase(block);
        } catch (CloneNotSupportedException e) {
            // 直接抛Runtime的
            throw new RuntimeException(e);
        }
        block.setCellCase(new CBCellCase(cbcells));
        for (CBCell[] cells : cbcells) {
            for (int j = 0, length = cells.length; j < length; j++) {
                if (cells[j] != null) {
                    CellGUIAttr attr = cells[j].getCellGUIAttr();
                    if (attr != null && attr.isShowAsHTML()) {
                        continue;
                    }
                }
                block.shrinkTOFitColumnWidthForCellElement(cells[j]);
            }
        }
        BILogger.getLogger().info(DateUtils.timeCostFrom(startTime) + " beb time");
        release();
        return block;
    }

    @Override
    protected ResultECReport execute4ECReport(com.fr.report.stable.fun.Actor actor, long startTime) {
        long s = System.currentTimeMillis();
        ResultECReport result = getResultReport((WorkSheet) this.report);
        BILogger.getLogger().info(DateUtils.timeCostFrom(s) + " p2");

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