/**
 * @class BI.CombineChartTypeCombo
 * @extend BI.AbstractTypeCombo
 *
 */
BI.CombineChartTypeCombo = BI.inherit(BI.AbstractTypeCombo, {
    _defaultConfig: function () {
        return BI.extend(BI.CombineChartTypeCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-combine-chart-type-combo",
            invalid: false,
            items: []
        })
    },

    _init: function () {
        BI.CombineChartTypeCombo.superclass._init.apply(this, arguments);
    },

    _switchIcon: function(v){
        switch (v) {
            case BICst.WIDGET.COMBINE_CHART:
                break;
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                break;
            default:
                this.trigger.setIcon("detail-chart-axis-font");
                break;
        }
    }
});
$.shortcut("bi.combine_chart_type_combo", BI.CombineChartTypeCombo);