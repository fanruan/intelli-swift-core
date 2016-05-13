/**
 * @class BI.BubbleTypeCombo
 * @extend BI.AbstractTypeCombo
 *
 */
BI.BubbleTypeCombo = BI.inherit(BI.AbstractTypeCombo, {
    _defaultConfig: function () {
        return BI.extend(BI.BubbleTypeCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-bubble-type-combo",
            invalid: false,
            items: []
        })
    },

    _init: function () {
        BI.BubbleTypeCombo.superclass._init.apply(this, arguments);
    },

    _switchIcon: function(v){
        switch (v) {
            case BICst.Widget.BUBBLE:
                break;
            case BICst.Widget.NIU_DUN_BUBBLE:
                break;
            default:
                this.trigger.setIcon("detail-chart-bubble-font");
                break;
        }
    }
});
$.shortcut("bi.bubble_type_combo", BI.BubbleTypeCombo);