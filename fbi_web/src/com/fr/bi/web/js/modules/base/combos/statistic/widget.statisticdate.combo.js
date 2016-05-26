/**
 * @class BI.StatisticDateCombo
 * @extend BI.Widget
 */
BI.StatisticDateCombo = BI.inherit(BI.Widget, {

    _defaultItems:function(){
        return [
            [{
                text: BI.i18nText("BI-No_Repeat_Count"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.No_Repeat_Count,
                cls: "dot-ha-font"
            }, {
                text: BI.i18nText("BI-Record_Count"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.RECORD_COUNT,
                cls: "dot-ha-font"
            }],
            [{
                text: BI.i18nText("BI-Rename"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.RENAME
            }, {
                text: BI.i18nText("BI-Remove"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.DELETE
            }]
        ];
    },

    _defaultConfig: function(){
        return BI.extend(BI.StatisticDateCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-statistic-date-combo"
        })
    },

    _init: function(){
        BI.StatisticDateCombo.superclass._init.apply(this, arguments);
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
            self.fireEvent(BI.StatisticDateCombo.EVENT_CHANGE, v);
        });

        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW,function(){
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
                summaryValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.No_Repeat_Count;
                break;
            case BICst.SUMMARY_TYPE.RECORD_COUNT:
                summaryValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.RECORD_COUNT;
                break;
        }
        return summaryValue;
    },

    getValue:function(){
        this.combo.getValue();
    }
});

BI.StatisticDateCombo.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.statistic_date_combo", BI.StatisticDateCombo);