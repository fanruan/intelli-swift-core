/**
 * @class BI.StatisticStringCombo
 * @extend BI.Widget
 */
BI.StatisticStringCombo = BI.inherit(BI.Widget, {

    _defaultItems:function(){
        return [
            [{
                text: BI.i18nText("BI-No_Repeat_Count"),
                value: BICst.STATISTICS_GROUP_STRING_COMBO.No_Repeat_Count,
                cls: "dot-ha-font"
            }, {
                text: BI.i18nText("BI-Record_Count"),
                value: BICst.STATISTICS_GROUP_STRING_COMBO.RECORD_COUNT,
                cls: "dot-ha-font"
            }, {
                text: BI.i18nText("BI-String_Summary_By_Connection"),
                value: BICst.STATISTICS_GROUP_STRING_COMBO.APPEND,
                cls: "dot-ha-font"
            }],
            [{
                text: BI.i18nText("BI-Rename"),
                value: BICst.STATISTICS_GROUP_STRING_COMBO.RENAME
            }, {
                text: BI.i18nText("BI-Remove"),
                value: BICst.STATISTICS_GROUP_STRING_COMBO.DELETE
            }]
        ];
    },

    _defaultConfig: function(){
        return BI.extend(BI.StatisticStringCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-statistic-string-combo"
        })
    },

    _init: function(){
        BI.StatisticStringCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            stopPropagation: true,
            height: 25,
            iconCls: "detail-dimension-set-font",
            items:this._defaultItems()
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function(v){
            self.fireEvent(BI.StatisticStringCombo.EVENT_CHANGE, v);
        });
        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW, function(){
            var selectedValue = self._createValueBySummary(o.dimension.group);
            this.setValue([selectedValue]);
        });
    },

    _assertSummary:function(val){
        val || (val = {});
        val.type || (val.type = BICst.SUMMARY_TYPE.COUNT);
        return val;
    },

    _createValueBySummary: function(summary){
        summary = this._assertSummary(summary);
        var summaryValue = {};
        switch (summary.type){
            case BICst.SUMMARY_TYPE.COUNT:
                summaryValue.value = BICst.STATISTICS_GROUP_STRING_COMBO.No_Repeat_Count;
                break;
            case BICst.SUMMARY_TYPE.APPEND:
                summaryValue.value = BICst.STATISTICS_GROUP_STRING_COMBO.APPEND;
                break;
            case BICst.SUMMARY_TYPE.RECORD_COUNT:
                summaryValue.value = BICst.STATISTICS_GROUP_STRING_COMBO.RECORD_COUNT;
                break;
        }
        return summaryValue;
    },

    getValue:function(){
        this.combo.getValue();
    }
});

BI.StatisticStringCombo.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.statistic_string_combo", BI.StatisticStringCombo);