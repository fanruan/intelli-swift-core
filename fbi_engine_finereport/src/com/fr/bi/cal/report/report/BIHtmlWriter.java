/**
 *
 */
package com.fr.bi.cal.report.report;

import com.fr.base.Utils;
import com.fr.stable.html.Tag;
import com.fr.stable.web.Repository;
import com.fr.web.core.HTMLWriter;
import com.fr.web.core.reportcase.WebReportCase;
import com.fr.web.output.html.chwriter.CellHtmlWriter;
import com.fr.web.output.html.chwriter.PageCellWriter;



public class BIHtmlWriter extends HTMLWriter {
    private static HTMLWriter SC = new BIHtmlWriter();

    public static HTMLWriter getInstance() {
        return SC;
    }

    @Override
    protected Tag createTableTag(CellHtmlWriter cellWriter, WebReportCase reportCase, java.awt.Rectangle clip, Repository repo, String ids) {
        Tag table = new Tag("table").cls("x-table").attr("id", Utils.doubleToString(cellWriter.getTableID()))
                .attr("cellSpacing", "0").attr("cellPadding", "0");

        // TODO: ie的center设置会影响单元格的位置，cell to tag的时候暂时这么处理
        if (cellWriter instanceof PageCellWriter) {
//			table
//			.css("position", "absolute")
//			.css("left", "0px");
        }
//		table.css("table-layout", "fixed");
        Tag colgroup = new Tag("colgroup");
        table.sub(colgroup);
        int width = 0;
        for (int i = clip.x, len = clip.width + i; i < len; i++) {
            int columnWidth = reportCase.getColumnPixWidth(i);
            /*
			 * alex:这里的width,必须写在css里面,如果写在attr里面,在ie中,如果width:0px的话,其实还是会画一点宽度的
			 * carl:TODO IE的盒模型同其它浏览器不同，width是包括border和padding的宽度的，
			 * 因此假如width为0那么border和padding都不会有
			 * 所以对于IE，列宽需要计算上border的宽度(在table-layout：fix的情况下)
			 * （虽然css3有了box-sizing：content-box属性，但暂时只有IE8支持,当然页面用XHTML也可以）
			 * 假如需要和其它浏览器一致，对于IE，width和height都是需要另算的
			 * 还有一点，border-collapse：collapse，虽然方便，但width和height的处理会很复杂
			 */
            if (columnWidth == 0) { // width为0不出现边框的问题不处理，都隐藏列了当然没边框啦
                continue;
            }
            // carl:因为列宽为0的不输出了，所以外部需要知道这是第几列的宽度
            Tag colTag = new Tag("col").attr("col", i + "").css("width", columnWidth + "px").attr("min_width", String.valueOf(columnWidth));
            colgroup.sub(colTag);

            width += columnWidth;
        }
        //jim:  bug31489 不知道为什么之前此处的TableWidth是width+1，导致第一列列宽加1从而悬浮元素遮挡住边框
//        table.css("width", width+"px");
        colgroup.attr("min_width", String.valueOf(width));
        return table;
    }
}