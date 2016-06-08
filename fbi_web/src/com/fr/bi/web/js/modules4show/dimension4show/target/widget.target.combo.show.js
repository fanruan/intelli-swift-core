/**
 * @class BI.TargetCombo
 * @extend BI.Widget
 * 指标下拉
 */
BI.TargetComboShow = BI.inherit(BI.AbstractDimensionTargetComboShow, {

    constants: {
        CHART_TYPE_POSITION: 1
    },

    defaultItems: function(){
        var fieldId = BI.Utils.getFieldIDByDimensionID(this.options.dId);
        var fieldName = BI.Utils.getFieldNameByID(fieldId);
        var tableName = BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(fieldId));
        return [
            [{
                el: {
                    text: BI.i18nText("BI-Summary_Style"),
                    value: BICst.TARGET_COMBO.SUMMERY_TYPE,
                    iconCls1: ""
                },
                children: [{
                    text: BI.i18nText("BI-Qiu_Sum"),
                    value: BICst.SUMMARY_TYPE.SUM,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Qiu_Avg"),
                    value: BICst.SUMMARY_TYPE.AVG,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Qiu_Max"),
                    value: BICst.SUMMARY_TYPE.MAX,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Qiu_Min"),
                    value: BICst.SUMMARY_TYPE.MIN,
                    cls: "dot-e-font"
                }]
            }],
            [{
                text: BI.i18nText("BI-This_Target_From") + ": " + tableName + "." + fieldName,
                tipType: "warning",
                value: BICst.TARGET_COMBO.INFO,
                cls: "dimension-from-font",
                disabled: true
            }]
        ];
    },

    _defaultConfig: function(){
        return BI.extend(BI.TargetComboShow.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function(){
        BI.TargetComboShow.superclass._init.apply(this, arguments);
    },

    _assertGroup: function (val) {
        val || (val = {});
        val.type || (val.type = BICst.SUMMARY_TYPE.SUM);
        return val;
    },

    _assertChartType:function(val){
        val || (val = {});
        val.type || (val.type = BICst.WIDGET.AXIS);
        return val;
    },

    _rebuildItems: function(){
        return this.defaultItems();
    },

    _createValue: function () {
        var o = this.options;
        var group = BI.Utils.getDimensionGroupByID(o.dId);
        var chartType = BI.Utils.getDimensionStyleOfChartByID(o.dId);
        group = this._assertGroup(group);
        chartType = this._assertChartType(chartType);

        var result = {};

        result.chartType = {
            value: BICst.TARGET_COMBO.CHART_TYPE,
            childValue: chartType.type
        };
        result.group = {
            value: BICst.TARGET_COMBO.SUMMERY_TYPE,
            childValue: group.type
        };
        return [result.chartType, result.group];
    }
});
$.shortcut("bi.target_combo_show", BI.TargetComboShow);