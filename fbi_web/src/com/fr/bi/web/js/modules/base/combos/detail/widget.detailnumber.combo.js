/**
 * @class BI.DetailNumberDimensionCombo
 * @extend BI.Widget
 * 明细表数值维度的combo
 */
BI.DetailNumberDimensionCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.DetailNumberDimensionCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-number-dimension-combo"
        })
    },

    _init: function(){
        BI.DetailNumberDimensionCombo.superclass._init.apply(this, arguments);
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            height: 25,
            iconCls: "detail-dimension-set-font",
            items: [
                [{
                    text: BI.i18nText("BI-Format_Setting"),
                    value: BICst.DETAIL_NUMBER_COMBO.FORM_SETTING
                }],
                [{
                    text: BI.i18nText("BI-Filter_Setting"),
                    value: BICst.DETAIL_NUMBER_COMBO.FILTER
                }],
                [{
                    text: BI.i18nText("BI-Hyperlink"),
                    value: BICst.DETAIL_NUMBER_COMBO.HYPERLINK
                }],
                [{
                    text: BI.i18nText("BI-Remove"),
                    value: BICst.DETAIL_NUMBER_COMBO.DELETE
                }],
                [{
                    text: BI.i18nText("BI-This_Target_From"),
                    value: BICst.DETAIL_NUMBER_COMBO.INFO,
                    disabled: true
                }]
            ]
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function(v){
            self.fireEvent(BI.DetailNumberDimensionCombo.EVENT_CHANGE, v);
        })
    },

    getValue: function(){
        return this.combo.getValue();
    }

});
BI.DetailNumberDimensionCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_number_dimension_combo", BI.DetailNumberDimensionCombo);