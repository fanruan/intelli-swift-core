/**
 *
 * 表格
 *
 * Created by GUY on 2015/9/22.
 * @class BI.PageDetailTableCell
 * @extends BI.Single
 */
BI.PageDetailTableCell = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.PageDetailTableCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-page-detail-table-cell",
            text: "",
            title: ""
        })
    },

    _init: function () {
        BI.PageDetailTableCell.superclass._init.apply(this, arguments);
        var label = BI.createWidget({
            type: "bi.label",
            element: this.element,
            textAlign: "left",
            whiteSpace: "nowrap",
            height: this.options.height,
            text: this.options.text,
            title: this.options.title,
            value: this.options.value,
            lgap: 5,
            rgap: 5
        });

        if (BI.isNotNull(this.options.styles) && BI.isObject(this.options.styles)) {
            this.element.css(this.options.styles);
        }
    }
});

$.shortcut("bi.page_detail_table_cell", BI.PageDetailTableCell);