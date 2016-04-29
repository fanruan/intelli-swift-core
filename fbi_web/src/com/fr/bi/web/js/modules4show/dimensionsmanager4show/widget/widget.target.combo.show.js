/**
 * @class BI.TargetComboShow
 * @extend BI.Widget
 * 指标下拉
 */
BI.TargetComboShow = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.TargetComboShow.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BI.TargetComboShow.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var fieldId = BI.Utils.getFieldIDByDimensionID(o.dId);
        var fieldName = BI.Utils.getFieldNameByID(fieldId);
        var tableName = BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(fieldId));
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            height: 25,
            iconCls: "detail-dimension-set-font",
            items: [
                [{
                    el: {
                        text: BI.i18nText("BI-Summary_Style"),
                        value: BICst.TARGET_COMBO.SUMMERY_TYPE,
                        iconCls1: ""
                    },
                    children: [{
                        text: BI.i18nText("BI-Qiu_Sum"),
                        value: BICst.SUMMARY_TYPE.SUM
                    }, {
                        text: BI.i18nText("BI-Qiu_Avg"),
                        value: BICst.SUMMARY_TYPE.AVG
                    }, {
                        text: BI.i18nText("BI-Qiu_Max"),
                        value: BICst.SUMMARY_TYPE.MAX
                    }, {
                        text: BI.i18nText("BI-Qiu_Min"),
                        value: BICst.SUMMARY_TYPE.MIN
                    }]
                }, {
                    el: {
                        text: BI.i18nText("BI-Chart_Type"),
                        value: BICst.TARGET_COMBO.CHART_TYPE,
                        iconCls1: ""
                    },
                    children: [{
                        text: BI.i18nText("BI-Column_Chart"),
                        value: BICst.CHART_VIEW_STYLE_BAR
                    }, {
                        text: BI.i18nText("BI-Stacked_Chart"),
                        value: BICst.CHART_VIEW_STYLE_ACCUMULATED_BAR
                    }, {
                        text: BI.i18nText("BI-Line_Chart"),
                        value: BICst.CHART_VIEW_STYLE_LINE
                    }, {
                        text: BI.i18nText("BI-Area_Chart"),
                        value: BICst.CHART_VIEW_STYLE_SQUARE
                    }]
                }],
                [{
                    text: BI.i18nText("BI-Style_Setting"),
                    value: BICst.TARGET_COMBO.STYLE_SETTING,
                    cls: "style-set-h-font"
                }],
                [{
                    text: BI.i18nText("BI-Filter_Number_Summary"),
                    value: BICst.TARGET_COMBO.FILTER,
                    cls: "filter-h-font"
                }],
                [{
                    text: BI.i18nText("BI-Copy"),
                    value: BICst.TARGET_COMBO.COPY,
                    cls: "copy-h-font"
                }],
                [{
                    text: BI.i18nText("BI-Delete_Target"),
                    value: BICst.TARGET_COMBO.DELETE,
                    cls: "delete-h-font"
                }],
                [{
                    text: BI.i18nText("BI-This_Target_From") + ": " + tableName + "." + fieldName,
                    tipType: "warning",
                    value: BICst.TARGET_COMBO.INFO,
                    cls: "dimension-from-font",
                    disabled: true
                }]
            ]
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function(v){
            self.fireEvent(BI.TargetCombo.EVENT_CHANGE, v);
        });

        this.combo.on(BI.DownListCombo.EVENT_SON_VALUE_CHANGE,function(v, father){
            self.fireEvent(BI.TargetCombo.EVENT_CHANGE, father, v);
        });

        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            var selectedValue = self._createValueByGroupAndChartType(BI.Utils.getDimensionGroupByID(o.dId),BI.Utils.getDimensionStyleOfChart(o.dId));
            this.setValue([selectedValue.chartType,selectedValue.group]);
        })
    },

    _assertGroup: function (val) {
        val || (val = {});
        val.type || (val.type = BICst.SUMMARY_TYPE.SUM);
        return val;
    },

    _assertChartType:function(val){
        val || (val = {});
        val.type || (val.type = BICst.Widget.AXIS);
        return val;
    },

    _createValueByGroupAndChartType: function (group, chartType) {
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
        return result;
    },

    getValue: function () {
        return this.combo.getValue();
    }
});
BI.TargetComboShow.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.target_combo_show", BI.TargetComboShow);
