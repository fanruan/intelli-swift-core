/**
 * 预览表
 *
 * Created by GUY on 2015/12/25.
 * @class BI.PreviewTableHeaderCell
 * @extends BI.Widget
 */
BI.AnalysisETLPreviewTableHeaderNormalCell = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLPreviewTableHeaderNormalCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-analysis-etl-p-t-h-cell",
            text: "",
            height:30
        });
    },

    _init: function () {
        BI.AnalysisETLPreviewTableHeaderNormalCell.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var label = BI.createWidget({
            type: "bi.label",
            textAlign: "center",
            height: o.height,
            text: o.text,
            value: o.value
        })
        BI.createWidget({
            type:"bi.htape",
            element: this.element,
            items:[{
                el : {
                    type:"bi.icon_button",
                    cls:BI.Utils.getFieldClass(o.field_type),
                    forceNotSelected :true,
                    height: o.height,
                    width: o.height
                },
                width: o.height
            }, {
                el: label
            }]
        })
    }
});
$.shortcut(ETLCst.ANALYSIS_TABLE_OPERATOR_PREVIEW_HEADER + BI.ANALYSIS_ETL_HEADER.NORMAL, BI.AnalysisETLPreviewTableHeaderNormalCell);