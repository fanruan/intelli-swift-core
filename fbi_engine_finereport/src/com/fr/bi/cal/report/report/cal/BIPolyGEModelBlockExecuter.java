package com.fr.bi.cal.report.report.cal;

import com.fr.bi.cal.report.report.poly.BIAnalyPolyBlock;
import com.fr.bi.cal.report.report.poly.BIPolyGEModelBlock;
import com.fr.bi.cal.report.report.poly.BIPolyGEModelResBlock;
import com.fr.main.impl.WorkBook;
import com.fr.main.workbook.AnalyWorkBook;
import com.fr.report.block.ResultBlock;
import com.fr.report.core.block.BlockSequenceExecutor;
import com.fr.report.core.sheet.SheetExecutor;
import com.fr.report.poly.AnalyPolySheet;
import com.fr.report.report.TemplateReport;

import java.util.Iterator;
import java.util.Map;

/**
 * 模型控件的计算
 *
 * @author Daniel
 */
public class BIPolyGEModelBlockExecuter extends SheetExecutor {

    /**
     *
     */
    private static final long serialVersionUID = 3142514542275903984L;

    private BIPolyGEModelBlock tplBlock;

    public BIPolyGEModelBlockExecuter(TemplateReport report, BIPolyGEModelBlock block,
                                      Map parameterMap, BlockSequenceExecutor bExecuter) {
        super(parameterMap);
        tplBlock = block;
    }

    @Override
    public ResultBlock execute4Poly(com.fr.report.stable.fun.Actor actor, long startTime) {
        BIPolyGEModelResBlock resBlock = new BIPolyGEModelResBlock();
        Iterator iter = tplBlock.getWBIterator();
        while (iter.hasNext()) {
            WorkBook wb = (WorkBook) iter.next();
            AnalyWorkBook rsWB = (AnalyWorkBook) wb.execute(parameterMap, actor);
            AnalyPolySheet rsReport = (AnalyPolySheet) rsWB.getReport(0);
            BIAnalyPolyBlock tmpBlock = (BIAnalyPolyBlock) rsReport.getBlock(0);
            resBlock.addResBlock(tmpBlock);
        }
        resBlock.setBlockAttr(tplBlock.getBlockAttr());
        resBlock.setBlockName(tplBlock.getBlockName());
        resBlock.setBounds(tplBlock.getBounds());
        return resBlock;
    }

}