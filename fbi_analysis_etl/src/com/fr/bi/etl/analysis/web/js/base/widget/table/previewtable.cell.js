/**
 * 预览表列
 *
 * Created by GUY on 2015/12/25.
 * @class BI.PreviewTableCell
 * @extends BI.Widget
 */
BI.PreviewTableCell = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.PreviewTableCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-analysis-etl-preview-table-cell",
            text: ""
        });
    },

    _init: function () {
        BI.PreviewTableCell.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        BI.createWidget({
            type: "bi.label",
            element: this.element,
            textAlign: "left",
            lgap:10,
            height: this.options.height,
            text: this.options.text,
            value: this.options.value
        })
    }
});
$.shortcut('bi.analysis_etl_preview_table_cell', BI.PreviewTableCell);