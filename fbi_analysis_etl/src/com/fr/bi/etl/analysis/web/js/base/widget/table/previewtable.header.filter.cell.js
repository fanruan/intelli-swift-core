/**
 * 预览表
 *
 * Created by GUY on 2015/12/25.
 * @class BI.PreviewTableHeaderCell
 * @extends BI.Widget
 */
BI.AnalysisETLPreviewTableHeaderFilterCell = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLPreviewTableHeaderFilterCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-analysis-etl-p-t-h-cell",
            text: "",
            height:30
        });
    },

    _init: function () {
        BI.AnalysisETLPreviewTableHeaderFilterCell.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var label = BI.createWidget({
            type: "bi.label",
            textAlign: "center",
            height: o.height,
            text: o.text,
            value: o.value
        })
        var op = {
            type : 'bi.filter_combo_etl',
            field_type : o.field_type,
            field_name : o.text,
            fieldValuesCreator : function(callback){
                return o.fieldValuesCreator(o.field_id, callback);
            }
        }
        op[ETLCst.FIELDS] = o[ETLCst.FIELDS];
        var filter =  BI.createWidget(op);
        var filterValue = o.filterValueGetter(o.text);
        if (BI.isNotNull(filterValue)){
            filter.setValue(filterValue);
        }
        filter.on(BI.ETLFilterCombo.EVENT_VALUE_CHANGED, function () {
            self.fireEvent(BI.AnalysisETLPreviewTable.EVENT_FILTER,  filter.getValue())
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
            }, {
                el :filter,
                width:o.height
            }]
        })
    }
});

$.shortcut(ETLCst.ANALYSIS_TABLE_OPERATOR_PREVIEW_HEADER + BI.ANALYSIS_ETL_HEADER.FILTER, BI.AnalysisETLPreviewTableHeaderFilterCell);