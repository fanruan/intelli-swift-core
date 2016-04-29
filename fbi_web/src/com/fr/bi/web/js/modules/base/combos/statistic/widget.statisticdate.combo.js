/**
 * @class BI.StatisticDateCombo
 * @extend BI.Widget
 */
BI.StatisticDateCombo = BI.inherit(BI.Widget, {

    _defaultItems:function(){
        return [
            [{
                text: BI.i18nText("BI-No_Repeat_Count"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.No_Repeat_Count
            }],
            [{
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
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            height: 25,
            iconCls: "detail-dimension-set-font",
            items:this._defaultItems()
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function(v){
            self.fireEvent(BI.StatisticDateCombo.EVENT_CHANGE, v);
        });
    },

    getValue:function(){
        this.combo.getValue();
    }
});

BI.StatisticDateCombo.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.statistic_date_combo", BI.StatisticDateCombo);