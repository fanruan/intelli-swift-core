/**
 * @class BI.StatisticNumberCombo
 * @extend BI.Widget
 * @abstract
 */
BI.StatisticNumberCombo = BI.inherit(BI.Widget, {

    _defaultItems:function(){
        return [
            [{
                text: BI.i18nText("BI-Qiu_Sum"),
                value: BICst.STATISTICS_GROUP_NUMBER_COMBO.SUM,
                cls: "dot-ha-font"
            },{
                text: BI.i18nText("BI-Qiu_Avg"),
                value: BICst.STATISTICS_GROUP_NUMBER_COMBO.AVG,
                cls: "dot-ha-font"
            },{
                text: BI.i18nText("BI-Qiu_Max"),
                value: BICst.STATISTICS_GROUP_NUMBER_COMBO.MAX,
                cls: "dot-ha-font"
            },{
                text: BI.i18nText("BI-Qiu_Min"),
                value: BICst.STATISTICS_GROUP_NUMBER_COMBO.MIN,
                cls: "dot-ha-font"
            },{
                text: BI.i18nText("BI-No_Repeat_Count"),
                value: BICst.STATISTICS_GROUP_NUMBER_COMBO.No_Repeat_Count,
                cls: "dot-ha-font"
            }, {
                text: BI.i18nText("BI-Record_Count"),
                value: BICst.STATISTICS_GROUP_NUMBER_COMBO.RECORD_COUNT,
                cls: "dot-ha-font"
            }],
            [{
                text: BI.i18nText("BI-Display"),
                value: BICst.STATISTICS_GROUP_NUMBER_COMBO.DISPLAY,
                cls: "dot-ha-font"
            }, {
                text: BI.i18nText("BI-Hidden"),
                value: BICst.STATISTICS_GROUP_NUMBER_COMBO.HIDDEN,
                cls: "dot-ha-font"
            }],
            [{
                text: BI.i18nText("BI-Rename"),
                value: BICst.STATISTICS_GROUP_NUMBER_COMBO.RENAME
            }, {
                text: BI.i18nText("BI-Remove"),
                value: BICst.STATISTICS_GROUP_NUMBER_COMBO.DELETE
            }]
        ];
    },

    _defaultConfig: function(){
        return BI.extend(BI.StatisticNumberCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-statistic-number-combo"
        })
    },

    _init: function(){
        BI.StatisticNumberCombo.superclass._init.apply(this, arguments);
        var self = this,o = this.options;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            stopPropagation: true,
            height: 25,
            iconCls: "detail-dimension-set-font",
            items:this._defaultItems()
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function(v){
            self.fireEvent(BI.StatisticNumberCombo.EVENT_CHANGE, v);
        });

        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW,function(){
            var selectedValue = self._createValueBySummary(o.dimension.group);
            this.setValue([selectedValue, self._createValueByUsed(o.dimension.used)]);
        });
    },

    _assertSummary:function(val){
        val || (val = {});
        val.type || (val.type = BICst.SUMMARY_TYPE.SUM);
        return val;
    },

    _createValueBySummary: function(summary){
        summary = this._assertSummary(summary);
        var summaryValue = {};
        switch (summary.type){
            case BICst.SUMMARY_TYPE.SUM:
                summaryValue.value = BICst.STATISTICS_GROUP_NUMBER_COMBO.SUM;
                break;
            case BICst.SUMMARY_TYPE.AVG:
                summaryValue.value = BICst.STATISTICS_GROUP_NUMBER_COMBO.AVG;
                break;
            case BICst.SUMMARY_TYPE.MAX:
                summaryValue.value = BICst.STATISTICS_GROUP_NUMBER_COMBO.MAX;
                break;
            case BICst.SUMMARY_TYPE.MIN:
                summaryValue.value = BICst.STATISTICS_GROUP_NUMBER_COMBO.MIN;
                break;
            case BICst.SUMMARY_TYPE.COUNT:
                summaryValue.value = BICst.STATISTICS_GROUP_NUMBER_COMBO.No_Repeat_Count;
                break;
            case BICst.SUMMARY_TYPE.RECORD_COUNT:
                summaryValue.value = BICst.STATISTICS_GROUP_NUMBER_COMBO.RECORD_COUNT;
                break;
        }
        return summaryValue;
    },

    _createValueByUsed: function(used){
        if(used === true){
            return {value: BICst.STATISTICS_GROUP_NUMBER_COMBO.DISPLAY};
        }else{
            return {value: BICst.STATISTICS_GROUP_NUMBER_COMBO.HIDDEN};
        }
    },

    getValue:function(){
        return this.combo.getValue();
    }
});

BI.StatisticNumberCombo.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.statistic_number_combo", BI.StatisticNumberCombo);