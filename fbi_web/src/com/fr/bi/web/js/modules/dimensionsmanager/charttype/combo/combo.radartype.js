/**
 * @class BI.RadarTypeCombo
 * @extend BI.AbstractTypeCombo
 *
 */
BI.RadarTypeCombo = BI.inherit(BI.AbstractTypeCombo, {
    _defaultConfig: function () {
        return BI.extend(BI.RadarTypeCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-radar-type-combo",
            invalid: false,
            items: []
        })
    },

    _init: function () {
        BI.RadarTypeCombo.superclass._init.apply(this, arguments);
    },

    _switchIcon: function(v){
        switch (v) {
            case BICst.WIDGET.RADAR:
                break;
            case BICst.WIDGET.ACCUMULATE_RADAR:
                break;
            default:
                this.trigger.setIcon("detail-chart-radar-font");
                break;
        }
    }
});
$.shortcut("bi.radar_type_combo", BI.RadarTypeCombo);