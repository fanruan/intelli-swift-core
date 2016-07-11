/**
 * @class BI.DetailNumberDimensionComboShow
 * @extend BI.Widget
 * 明细表数值维度的combo
 */
BI.DetailNumberDimensionComboShow = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailNumberDimensionComboShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-number-dimension-combo"
        })
    },

    _init: function () {
        BI.DetailNumberDimensionComboShow.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            height: 25,
            iconCls: "detail-dimension-set-font",
            items: [
                [{
                    text: BI.i18nText("BI-This_Target_From") + ":" + BI.Utils.getDimensionNameByID(o.dId),
                    title: BI.i18nText("BI-This_Target_From") + ":" + BI.Utils.getDimensionNameByID(o.dId),
                    tipType: "warning",
                    value: BICst.DETAIL_NUMBER_COMBO.INFO,
                    disabled: true
                }]
            ]
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.DetailNumberDimensionComboShow.EVENT_CHANGE, v);
        })
    },

    getValue: function () {
        return this.combo.getValue();
    }

});
BI.DetailNumberDimensionComboShow.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_number_dimension_combo_show", BI.DetailNumberDimensionComboShow);