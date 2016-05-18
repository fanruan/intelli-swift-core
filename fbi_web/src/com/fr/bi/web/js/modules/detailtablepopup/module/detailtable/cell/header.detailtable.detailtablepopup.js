/**
 * 指标弹出明细表表格单元格
 *
 * Created by GUY on 2016/5/18.
 * @class BI.DetailTablePopupDetailTableHeader
 * @extends BI.Widget
 */
BI.DetailTablePopupDetailTableHeader = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePopupDetailTableHeader.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-table-popup-detail-table-cell"
        })
    },

    _init: function () {
        BI.DetailTablePopupDetailTableHeader.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        BI.createWidget({
            type: "bi.label",
            element: this.element,
            text: o.text,
            value: o.value
        })
    }
});
$.shortcut("bi.detail_table_popup_detail_table_header", BI.DetailTablePopupDetailTableHeader);