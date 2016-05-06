/**
 * @class BI.DetailDateDimensionCombo
 * @extend BI.Widget
 * 明细表日期维度的combo
 */
BI.DetailDateDimensionCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailDateDimensionCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-date-dimension-combo"
        })
    },

    _init: function () {
        BI.DetailDateDimensionCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            height: 25,
            iconCls: "detail-dimension-set-font",
            items: [
                [{
                    text: BI.i18nText("BI-Date"),
                    value: BICst.DETAIL_DATE_COMBO.YMD
                }, {
                    text: BI.i18nText("BI-Year_Fen"),
                    value: BICst.DETAIL_DATE_COMBO.YEAR
                }, {
                    text: BI.i18nText("BI-Multi_Date_Quarter"),
                    value: BICst.DETAIL_DATE_COMBO.SEASON
                }, {
                    text: BI.i18nText("BI-Multi_Date_Month"),
                    value: BICst.DETAIL_DATE_COMBO.MONTH
                }, {
                    text: BI.i18nText("BI-Week_XingQi"),
                    value: BICst.DETAIL_DATE_COMBO.WEEK
                }, {
                    text: BI.i18nText("BI-Time_ShiKe"),
                    value: BICst.DETAIL_DATE_COMBO.YMD_HMS
                }],
                [{
                    text: BI.i18nText("BI-Filter_Setting"),
                    value: BICst.DETAIL_DATE_COMBO.FILTER
                }],
                [{
                    text: BI.i18nText("BI-Hyperlink"),
                    value: BICst.DETAIL_DATE_COMBO.HYPERLINK
                }],
                [{
                    text: BI.i18nText("BI-Remove"),
                    value: BICst.DETAIL_DATE_COMBO.DELETE
                }],
                [{
                    text: BI.i18nText("BI-This_Target_From") + ":" + BI.Utils.getDimensionNameByID(o.dId),
                    title: BI.i18nText("BI-This_Target_From") + ":" + BI.Utils.getDimensionNameByID(o.dId),
                    tipType: "warning",
                    value: BICst.DETAIL_DATE_COMBO.INFO,
                    disabled: true
                }]
            ]
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.DetailDateDimensionCombo.EVENT_CHANGE, v);
        })
    },

    getValue: function () {
        return this.combo.getValue();
    }

});
BI.DetailDateDimensionCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_date_dimension_combo", BI.DetailDateDimensionCombo);