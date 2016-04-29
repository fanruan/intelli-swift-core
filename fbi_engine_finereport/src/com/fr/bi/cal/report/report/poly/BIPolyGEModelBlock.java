package com.fr.bi.cal.report.report.poly;

import com.fr.bi.cal.report.report.cal.BIPolyGEModelBlockExecuter;
import com.fr.main.impl.WorkBook;
import com.fr.report.core.block.BlockSequenceExecutor;
import com.fr.report.core.sheet.SheetExecutor;
import com.fr.report.poly.PolyWorkSheet;
import com.fr.report.poly.TemplateBlock;
import com.fr.report.report.TemplateReport;
import com.fr.stable.unit.UnitRectangle;

import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * 模型模块，block包含多个内置block
 *
 * @author Daniel
 */
public class BIPolyGEModelBlock extends BIPolyGEAbstractBlock implements
        TemplateBlock {

    /**
     *
     */
    private static final long serialVersionUID = -3140206775944453042L;
    private List workBookList = new ArrayList();

    /* (non-Javadoc)
     * @see com.fr.form.OB#addPropertyListener(com.fr.base.core.PropertyChangeListener)
     */
    public void addPropertyListener(PropertyChangeListener l) {
    }

    /* (non-Javadoc)
     * @see com.fr.report.poly.TemplateBlock#createExecutor(com.fr.report.TemplateReport, java.util.Map, com.fr.report.core.cal.blockCalculator.BlockSequenceExecuter)
     */
    @Override
    public SheetExecutor createExecutor(TemplateReport report,
                                        Map parameterMap, BlockSequenceExecutor bExecuter) {
        BIPolyGEModelBlock block;
        try {
            block = (BIPolyGEModelBlock) this.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        if (parameterMap == null) {
            parameterMap = new HashMap();
        }

        return new BIPolyGEModelBlockExecuter(report, block, parameterMap, bExecuter);
    }

    @Override
    public int[] getVerticalLine() {
        return new int[0];
    }

    @Override
    public int[] getHorizontalLine() {
        return new int[0];
    }

    @Override
    public void setBounds(UnitRectangle unitRectangle, PolyWorkSheet polyWorkSheet) {

    }

    /**
     * 添加内部workbook
     *
     * @param wb
     */
    public void addWorkBook(WorkBook wb) {
        workBookList.add(wb);
    }

    /**
     * 获取内容的迭代器
     *
     * @return
     */
    public Iterator getWBIterator() {
        return workBookList.iterator();
    }

    /* (non-Javadoc)
     * @see com.fr.form.event.OB#addPropertyListener(com.fr.stable.core.PropertyChangeListener)
     */
    @Override
    public void addPropertyListener(
            com.fr.stable.core.PropertyChangeListener arg0) {

    }

}