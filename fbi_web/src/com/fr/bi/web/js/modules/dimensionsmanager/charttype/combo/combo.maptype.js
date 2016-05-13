/**
 * @class BI.MapTypeCombo
 * @extend BI.AbstractTypeCombo
 *
 */
BI.MapTypeCombo = BI.inherit(BI.AbstractTypeCombo, {
    _defaultConfig: function () {
        return BI.extend(BI.MapTypeCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-combine-chart-type-combo",
            invalid: false,
            items: []
        })
    },

    _init: function () {
        BI.MapTypeCombo.superclass._init.apply(this, arguments);
    },

    _switchIcon: function(v){
        switch (v) {
            case BICst.Widget.MAP:
                break;
            default:
                this.trigger.setIcon("detail-chart-map-font");
                break;
        }
    }
});
$.shortcut("bi.combine_chart_type_combo", BI.MapTypeCombo);