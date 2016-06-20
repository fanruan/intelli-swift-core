/**
 * @class BI.DetailFormulaDimensionCombo
 * @extend BI.Widget
 * 明细表计算指标的combo
 */
BI.DetailFormulaDimensionComboShow = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailFormulaDimensionComboShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-formula-dimension-combo"
        })
    },

    _init: function () {
        BI.DetailFormulaDimensionComboShow.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            height: 25,
            iconCls: "detail-dimension-set-font",
            items: []
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.DetailFormulaDimensionComboShow.EVENT_CHANGE, v);
        })
    },

    getValue: function () {
        return this.combo.getValue();
    }

});
BI.DetailFormulaDimensionComboShow.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_formula_dimension_combo_show", BI.DetailFormulaDimensionComboShow);