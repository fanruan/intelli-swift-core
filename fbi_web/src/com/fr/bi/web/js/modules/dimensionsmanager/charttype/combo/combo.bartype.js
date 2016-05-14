/**
 * @class BI.BarTypeCombo
 * @extend BI.AbstractTypeCombo
 *
 */
BI.BarTypeCombo = BI.inherit(BI.AbstractTypeCombo, {
    _defaultConfig: function () {
        return BI.extend(BI.BarTypeCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-bar-type-combo",
            invalid: false,
            items: []
        })
    },

    _init: function () {
        BI.BarTypeCombo.superclass._init.apply(this, arguments);
    },

    _switchIcon: function(v){
        switch (v) {
            case BICst.WIDGET.BAR:
                break;
            case BICst.WIDGET.ACCUMULATE_BAR:
                break;
            case BICst.WIDGET.COMPARE_BAR:
                break;
            default:
                this.trigger.setIcon("detail-chart-bar-font");
                break;
        }
    }
});
$.shortcut("bi.bar_type_combo", BI.BarTypeCombo);