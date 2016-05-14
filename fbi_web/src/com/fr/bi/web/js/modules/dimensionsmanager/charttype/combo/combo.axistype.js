/**
 * @class BI.AxisTypeCombo
 * @extend BI.AbstractTypeCombo
 *
 */
BI.AxisTypeCombo = BI.inherit(BI.AbstractTypeCombo, {
    _defaultConfig: function () {
        return BI.extend(BI.AxisTypeCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-axis-type-combo",
            invalid: false,
            items: []
        })
    },

    _init: function () {
        BI.AxisTypeCombo.superclass._init.apply(this, arguments);
    },

    _switchIcon: function(v){
        switch (v) {
            case BICst.WIDGET.AXIS:
                break;
            case BICst.WIDGET.ACCUMULATE_AXIS:
                break;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                break;
            case BICst.WIDGET.COMPARE_AXIS:
                break;
            case BICst.WIDGET.FALL_AXIS:
                break;
            default:
                this.trigger.setIcon("detail-chart-axis-font");
                break;
        }
    }
});
$.shortcut("bi.axis_type_combo", BI.AxisTypeCombo);