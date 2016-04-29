package com.fr.bi.cal.report.report.cell;

import com.fr.base.FRContext;
import com.fr.base.Formula;
import com.fr.base.Style;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBBoxElementBox;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.page.ReportSettingsProvider;
import com.fr.report.cell.AnalyCellElement;
import com.fr.report.cell.CellElement;
import com.fr.report.cell.cellattr.PageExportCellElement;
import com.fr.report.core.box.BoxElementBox;
import com.fr.stable.html.Tag;
import com.fr.stable.unit.PT;
import com.fr.stable.web.Repository;
import com.fr.web.output.html.chwriter.HtmlWriteCellBox;
import com.fr.web.output.html.chwriter.PageCellWriter;

import java.util.Map;

/**
 * Created by Connery on 2016/2/27.
 */
public class BICellWriter extends PageCellWriter {  /**
 * 用于BI的报表写单元格
 *
 * @param repo            链接相关
 * @param reportIndex     报表的sheet的Index
 * @param reportSettrings
 */
public BICellWriter(Repository repo, int reportIndex, ReportSettingsProvider reportSettrings) {
    super(repo, reportIndex, reportSettrings, false);
}

    /**
     * BI相关的单元格属性
     */
    @Override
    protected void anyElse(HtmlWriteCellBox box) {
        try {
            BoxElementBox beb = getAnalyCell(box).getBoxElement().getBEB();
            dealWithDSColumnCell(beb, box);
//			dealWithSortCell(beb);
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
    }

    private AnalyCellElement getAnalyCell(HtmlWriteCellBox box) {
        if (box.getCell() instanceof PageExportCellElement) {
            return (AnalyCellElement) ((PageExportCellElement) box.getCell()).getOldCell();
        }
        return (AnalyCellElement) box.getCell();
    }

    /**
     * 列值
     *
     * @param beb
     * @param box
     */
    private void dealWithDSColumnCell(BoxElementBox beb, HtmlWriteCellBox box) {
        if (beb instanceof CBBoxElementBox) {
            CBBoxElementBox cb = (CBBoxElementBox) beb;
            CBBoxElement ce = cb.getBox();
            if (cb.getType() == CellConstant.CBCELL.SUMARYFIELD) {
                resTag.attr("self", ce.getDimensionJSON());
                resTag.attr("target", ce.getName());
            } else if (ce.getDimensionJSON() != null && ce.getName() != null) {
                resTag.attr("self", ce.getDimensionJSON());
                resTag.attr("dimension", ce.getName());
            }
            if (cb.getType() == CellConstant.CBCELL.TARGETTITLE_Y) {
                resTag.cls("fr_bi_cell_sort_title_y");
                if (ce.getSortTargetName() != null) {
                    resTag.attr("sort_target", ce.getSortTargetName());
                }
                if (ce.getSortTargetValue() != null) {
                    resTag.attr("sort_value", ce.getSortTargetValue());
                }
            } else if (cb.getType() == CellConstant.CBCELL.TARGETTITLE_X) {
                resTag.cls("fr_bi_cell_sort_title_x");
                if (ce.getSortTargetName() != null) {
                    resTag.attr("sort_target", ce.getSortTargetName());
                }
                if (ce.getSortTargetValue() != null) {
                    resTag.attr("sort_value", ce.getSortTargetValue());
                }
            } else if (cb.getType() == CellConstant.CBCELL.DIMENSIONTITLE_Y) {
//				resTag.cls("sort-asc-icon");
                if (ce.getName() != null) {
                    resTag.attr("dimension", ce.getName());
                }
            } else if (cb.getType() == CellConstant.CBCELL.DIMENSIONTITLE_X) {
//				resTag.cls("sort-asc-icon");
                if (ce.getName() != null) {
                    resTag.attr("dimension", ce.getName());
                }
            }
            if (ce.getIndexString_x() != null) {
                resTag.attr("index_v_x", ce.getIndexString_x());
                if (ce.isExpand()) {
                    resTag.cls("fr_bi_td_expand");
                } else {
                    resTag.cls("fr_bi_td_collapse");
                }
            }
            if (ce.getIndexString_y() != null) {
                resTag.attr("index_v_y", ce.getIndexString_y());
                if (ce.isExpand()) {
                    resTag.cls("fr_bi_td_expand");
                } else {
                    resTag.cls("fr_bi_td_collapse");
                }
            }
            if (ce.getDimensionRegionIndex() != null) {
                resTag.attr("region_index", ce.getDimensionRegionIndex());
            }
        }
    }

    protected void dealBeforeDrawContent(HtmlWriteCellBox box) {
        AnalyCellElement ce = getAnalyCell(box);
        if (ce instanceof CBCell) {
            return;
        }
    }

    /**
     * 排序标题
     *
     * @param beb
     */
    private void dealWithSortCell(BoxElementBox beb) {
        if (beb instanceof CBBoxElementBox) {
            CBBoxElementBox cb = (CBBoxElementBox) beb;
            if (cb.getSortType() != CellConstant.CBCELL.SORT.UNSORTFIELD) {
                CBBoxElement ce = cb.getBox();
                resTag.attr("deminsionName", ce.getName());
                resTag.attr("sortField", "true");
                if (ce.getSortType() == CellConstant.CBCELL.SORT.INCREASEFIELD) {
                    resTag.cls("fr_bi_cell_sort_increase");
                } else if (ce.getSortType() == CellConstant.CBCELL.SORT.DECREASESORTFIELD) {
                    resTag.cls("fr_bi_cell_sort_decrease");
                } else if (ce.getSortType() == CellConstant.CBCELL.SORT.CUSTOMSORTFIELD) {
                    resTag.cls("fr_bi_cell_sort_custom");
                } else {
//			        resTag.cls("fr_bi_cell_sort_none");
                }
            }
        }
    }

    @Override
    protected void drawContent(CellElement ce, Object rawValue, Tag contentTag) {
        Style style = ce.getStyle();

        Object displayValue = unwrapLobs(ce, getDisplayValue(ce, rawValue));
        int valueWrapperHeight = Math.max(getHeightOfValueWrapper(ce), 0) - 1;
        Tag valueWrapper = getValueWrapper(contentTag, style, ce, valueWrapperHeight);

        //daniel:displayvalue如果是公式就再解析下例如 toImage函数
        if (displayValue instanceof Formula) {
            processFormula(ce, (Formula) displayValue);
            displayValue = dealWithValue4Formula((Formula) displayValue);
        }

        // carl:假如是竖排文字变图
        if (shouldBeDisplayAsVerticalText(style, displayValue)) {
            displayVerticalText(style, displayValue, valueWrapper, valueWrapperHeight);
        } else if (displayValue != null) {
            if (style.getRotation() != 0) {
                displayVerticalText(style, displayValue, valueWrapper, valueWrapperHeight);
            } else {
                displayNormalText(ce, displayValue, valueWrapper, valueWrapperHeight);
            }
        }

    }

    protected Tag getValueWrapper(Tag contentTag, Style style, CellElement ce, int valueWrapperHeight) {
        Tag valueWrapper = contentTag;
        boolean useHeavy = showUseHeavy(ce);
        if (useHeavy) {
            Tag fixHeightDiv = new Tag("div").cls("fx").css("height", (valueWrapperHeight) + "px");
            // 填报冻结带按钮控件单元格会高出这一块
            // 带Button之类的控件会在td下生成两个div导致高度加倍
            if (!skipValueWrapper(ce)) {
                contentTag.sub(fixHeightDiv);
            }
            //默认paddingRight = 2的时候, 输出html时不显示.
            double delay = PT.pt2pix((style.getPaddingLeft() +
                    style.getPaddingRight() == 2 ? 0 : style.getPaddingRight()), repo.getResolution());

            //neil:上面逻辑错的, 这边不是什么浮点转换的问题, 是边框线情况下需要额外的px, 这边不知道要不要/2,各浏览器计算方式不一致
            delay += style.getBorderLeftWidth() + style.getBorderRightWidth();
            valueWrapper = new Tag("td").attr("hv", "true").css("height", valueWrapperHeight + "px").css("width", (cellWidth - delay) + "px");
            //重方式输出, td上加上原本单元格td的class属性, 如超链的tdu等.
            valueWrapper.attr("class", style.getContentClsCss(ce.getValue()));
            Map cssMap = style.getContentStyleCssMap();
            cssMap.remove("padding-left");
            cssMap.remove("padding-right");
            valueWrapper.css(cssMap);

            fixHeightDiv.sub(new Tag("table").css("width", "100%").sub(new Tag("tbody").sub(new Tag("tr").sub(valueWrapper))));
        }

        return valueWrapper;
    }
}
