package com.fr.bi.cal.report.report.poly;

import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.report.cell.CellElement;
import com.fr.report.core.PaintUtils;
import com.fr.report.poly.AnalyECBlock;
import com.fr.stable.StringUtils;
import com.fr.stable.html.Tag;
import com.fr.stable.unit.OLDPIX;
import com.fr.stable.unit.PT;
import com.fr.stable.unit.UnitRectangle;
import com.fr.stable.web.Repository;
import com.fr.web.core.HTMLWriter;
import com.fr.web.core.reportcase.WebElementReportCase;
import com.fr.web.output.html.chwriter.CellHtmlWriter;

import java.awt.*;

/**
 * BI表格控件
 *
 * @author Daniel
 */
public class BIPolyAnalyECBlock extends AnalyECBlock implements BIAnalyPolyBlock {

    /**
     *
     */
    private static final long serialVersionUID = -7450187792434782281L;
    private Rectangle southEastRectangle;

    /**
     * 写HTML
     *
     * @param backgroundTag fr参数
     * @param htmlWriter    fr参数
     * @param cellWriter    fr参数
     * @param reportIndex   fr参数
     * @param repo          fr参数
     */
    @Override
    public void writeHtml(Tag backgroundTag, HTMLWriter htmlWriter, CellHtmlWriter cellWriter, int reportIndex, Repository repo) {
        UnitRectangle bounds = getBounds();
        int offsetX = bounds.x.toPixI(repo.getResolution());
        int offsetY = bounds.y.toPixI(repo.getResolution());
        int width = bounds.width.toPixI(repo.getResolution());
        int height = bounds.height.toPixI(repo.getResolution());
        backgroundTag.sub(this.getTag(htmlWriter, cellWriter, reportIndex, repo));
        backgroundTag.css("width", offsetX + width + 5 + "px").css("height", offsetY + height + 5 + "px");
    }

    /**
     * 生成相应的TAG
     *
     * @see com.fr.report.block.AnalyPolyBlock#getTag(com.fr.web.core.HTMLWriter,
     * com.fr.web.core.chwriter.CellHtmlWriter, int, com.fr.stable.web.Repository)
     */
    @Override
    public Tag getTag(HTMLWriter htmlWriter, CellHtmlWriter cellWriter, int reportIndex, Repository repo) {
        return getContentTag(htmlWriter, cellWriter, reportIndex, repo);
    }

    /* (non-Javadoc)
     * @see com.fr.bi.report.poly.BIAnalyPolyBlock#getContentTag(com.fr.web.core.HTMLWriter, com.fr.web.core.chwriter.CellHtmlWriter, int, com.fr.stable.web.Repository)
     */
    @Override
    public Tag getContentTag(HTMLWriter htmlWriter, CellHtmlWriter cellWriter,
                             int reportIndex, Repository repo) {
        Tag reportTableTag = htmlWriter.writeReportToHtml(new WebElementReportCase(this, repo), reportIndex,
                cellWriter, repo, StringUtils.EMPTY);
        return reportTableTag;
    }

    /**
     * fr方法
     *
     * @param cellElement fr参数
     * @param fixwidth    宽度
     */
    public void shrinkTOFitColumnWidth(CellElement cellElement, int fixwidth) {
        if (cellElement == null) {
            return;
        }

        int row = cellElement.getRow();
        int rowSpan = cellElement.getRowSpan();
        long rowHeight = getRowHeightList_DEC().getRangeValue(row, row + rowSpan).toFU();

        long colWidth = getColumnWidthList_DEC().getRangeValue(cellElement.getColumn(), cellElement.getColumn() + cellElement.getColumnSpan()).toFU();
        if (colWidth == 0) {
            return;
        }

        long preferredWidth = (PaintUtils.getPreferredWidth(cellElement, PT.valueOfFU(rowHeight))).toFU();
        if (((CBBoxElement) ((CBCell) cellElement).getBoxElement()).hasIcon()
                || isShowAsHTML((CBCell) cellElement)) {
            preferredWidth += new OLDPIX(fixwidth).toFU();
        }
        ;
        // carl：照着调整行高来弄
        if (cellElement.getColumnSpan() == 1) {
            getColumnWidthList_DEC().setToFU(cellElement.getColumn(), Math.max(preferredWidth, getColumnWidthList_DEC().get(cellElement.getColumn()).toFU()));
        } else {
            int lastColumnIndex = cellElement.getColumn() + cellElement.getColumnSpan() - 1;
            long extraWidth = preferredWidth - getColumnWidthList_DEC().getRangeValue(cellElement.getColumn(), lastColumnIndex + 1).toFU();
            int displayColumn = cellElement.getColumnSpan();
            for (int i = cellElement.getColumn(); i <= lastColumnIndex; i++) {
                if (getColumnWidthList_DEC().get(i).equal_zero()) {
                    displayColumn -= 1;
                }
            }
            if (extraWidth > 0) {
                for (int j = cellElement.getColumn(); j <= lastColumnIndex; j++) {
                    if (getColumnWidthList_DEC().get(j).more_than_zero()) {
                        getColumnWidthList_DEC().setToFU(j, (getColumnWidthList_DEC().get(j).toFU() + extraWidth / displayColumn));
                    }
                }
            }

            // carl:把缺失的补上
            for (int c = 0, j = cellElement.getColumn(); j <= lastColumnIndex && c < (extraWidth % displayColumn); j++) {
                if (getColumnWidthList_DEC().get(j).more_than_zero()) {
                    getColumnWidthList_DEC().setToFU(j, getColumnWidthList_DEC().get(j).toFU() + 1);
                    c++;
                }
            }
        }
    }

    private boolean isShowAsHTML(CBCell cellElement) {
        return (cellElement.getCellGUIAttr() != null
                && cellElement.getCellGUIAttr().isShowAsHTML());
    }

    /**
     * fr方法
     *
     * @param cellElement fr参数
     */
    @Override
    public void shrinkTOFitColumnWidthForCellElement(CellElement cellElement) {
        shrinkTOFitColumnWidth(cellElement, 74);
    }

    /**
     * fr方法
     *
     * @param cellElement fr参数
     */
    public void shrinkTOFitColumnWidthForHeavyCellElement(CellElement cellElement) {
        shrinkTOFitColumnWidth(cellElement, 82);
    }

    public Rectangle getSouthEastRectangle() {
        return southEastRectangle;
    }

    public void setSouthEastRectangle(Rectangle southEastRectangle) {
        this.southEastRectangle = southEastRectangle;
    }
}