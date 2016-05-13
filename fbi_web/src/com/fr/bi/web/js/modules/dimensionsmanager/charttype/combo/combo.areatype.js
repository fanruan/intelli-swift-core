/**
 * @class BI.AreaTypeCombo
 * @extend BI.AbstractTypeCombo
 *
 */
BI.AreaTypeCombo = BI.inherit(BI.AbstractTypeCombo, {
    _defaultConfig: function () {
        return BI.extend(BI.AreaTypeCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-area-type-combo",
            invalid: false,
            items: []
        })
    },

    _init: function () {
        BI.AreaTypeCombo.superclass._init.apply(this, arguments);
    },

    _switchIcon: function(v){
        switch (v) {
            case BICst.Widget.AREA:
                break;
            case BICst.Widget.ACCUMULATE_AREA:
                break;
            case BICst.Widget.PERCENT_ACCUMULATE_AREA:
                break;
            case BICst.Widget.COMPARE_AREA:
                break;
            case BICst.Widget.RANGE_AREA:
                break;
            default:
                this.trigger.setIcon("detail-chart-pie-font");
                break;
        }
    }
});
$.shortcut("bi.table_type_combo", BI.AreaTypeCombo);