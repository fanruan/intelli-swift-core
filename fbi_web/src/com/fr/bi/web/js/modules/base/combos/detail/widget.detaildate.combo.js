/**
 * @class BI.DetailDateDimensionCombo
 * @extend BI.Widget
 * 明细表日期维度的combo
 */
BI.DetailDateDimensionCombo = BI.inherit(BI.Widget, {

    config : {
        DATE: BICst.DETAIL_DATE_COMBO.YMD,
        YEAR: BICst.DETAIL_DATE_COMBO.YEAR,
        QUARTER: BICst.DETAIL_DATE_COMBO.SEASON,
        MONTH: BICst.DETAIL_DATE_COMBO.MONTH,
        WEEK: BICst.DETAIL_DATE_COMBO.WEEK,
        YMD_HMS: BICst.DETAIL_DATE_COMBO.YMD_HMS
    },

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
                    value: BICst.DETAIL_DATE_COMBO.YMD,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Year_Fen"),
                    value: BICst.DETAIL_DATE_COMBO.YEAR,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Multi_Date_Quarter"),
                    value: BICst.DETAIL_DATE_COMBO.SEASON,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Multi_Date_Month"),
                    value: BICst.DETAIL_DATE_COMBO.MONTH,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Week_XingQi"),
                    value: BICst.DETAIL_DATE_COMBO.WEEK,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Time_ShiKe"),
                    value: BICst.DETAIL_DATE_COMBO.YMD_HMS,
                    cls: "dot-e-font"
                }],
                [{
                    text: BI.i18nText("BI-Show_Qualified_Result"),
                    value: BICst.DETAIL_DATE_COMBO.FILTER,
                    cls: "filter-h-font"
                }],
                [{
                    text: BI.i18nText("BI-Hyperlink"),
                    value: BICst.DETAIL_DATE_COMBO.HYPERLINK,
                    cls: "hyper-link-font"
                }],
                [{
                    text: BI.i18nText("BI-Remove"),
                    value: BICst.DETAIL_DATE_COMBO.DELETE,
                    cls: "delete-h-font"
                }],
                [{
                    text: BI.i18nText("BI-This_Target_From") + ":" + BI.Utils.getDimensionNameByID(o.dId),
                    title: BI.i18nText("BI-This_Target_From") + ":" + BI.Utils.getDimensionNameByID(o.dId),
                    tipType: "warning",
                    cls: "dimension-from-font",
                    value: BICst.DETAIL_DATE_COMBO.INFO,
                    disabled: true
                }]
            ]
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.DetailDateDimensionCombo.EVENT_CHANGE, v);
        });

        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW,function(){
            this.setValue(self._createValue());
        });
    },

    typeConfig: function(){
        return this.config;
    },

    _assertGroup: function(val){
        val || (val = {});
        if(BI.isNull(val.type)){
            val.type = BICst.GROUP.ID_GROUP;
        }
        return val;
    },

    _createValue: function () {
        var o = this.options;
        var group = BI.Utils.getDimensionGroupByID(o.dId);
        group = this._assertGroup(group);

        var result = {};

        var groupValue = {};

        switch(group.type){
            case BICst.GROUP.Y:
                groupValue.value = this.typeConfig().YEAR;
                break;
            case BICst.GROUP.M:
                groupValue.value = this.typeConfig().MONTH;
                break;
            case BICst.GROUP.W:
                groupValue.value = this.typeConfig().WEEK;
                break;
            case BICst.GROUP.YMD:
                groupValue.value = this.typeConfig().DATE;
                break;
            case BICst.GROUP.S:
                groupValue.value = this.typeConfig().QUARTER;
                break;
            case BICst.GROUP.YMDHMS:
                groupValue.value = this.typeConfig().YMD_HMS;
        }
        result.group = groupValue;
        return [result.group];
    },

    getValue: function () {
        return this.combo.getValue();
    }

});
BI.DetailDateDimensionCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_date_dimension_combo", BI.DetailDateDimensionCombo);