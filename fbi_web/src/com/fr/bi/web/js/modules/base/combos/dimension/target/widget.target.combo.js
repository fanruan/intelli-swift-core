/**
 * @class BI.TargetCombo
 * @extend BI.Widget
 * 指标下拉
 */
BI.TargetCombo = BI.inherit(BI.AbstractDimensionTargetCombo, {

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
        ];
    },

    _defaultConfig: function(){
        return BI.extend(BI.TargetCombo.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function(){
        BI.TargetCombo.superclass._init.apply(this, arguments);
    },

    _assertGroup: function (val) {
        val || (val = {});
        val.type || (val.type = BICst.SUMMARY_TYPE.SUM);
        return val;
    },

    _rebuildItems: function(){
        return this.defaultItems();
    },

    _createValue: function () {
        var o = this.options;
        var group = BI.Utils.getDimensionGroupByID(o.dId);
        group = this._assertGroup(group);

        var result = {};

        result.group = {
            value: BICst.TARGET_COMBO.SUMMERY_TYPE,
            childValue: group.type
        };
        return [result.group];
    }
});
$.shortcut("bi.target_combo", BI.TargetCombo);