/**
 * Created by GUY on 2016/3/17.
 * @class BI.CombineChartTargetScope
 * @extends BI.Widget
 */
BI.CombineChartTargetScope = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CombineChartTargetScope.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-combine-chart-target-scope",
        });
    },

    _init: function () {
        BI.CombineChartTargetScope.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var combo = BI.createWidget({
            type: "bi.icon_combo",
            width: 18,
            height: 18,
            iconWidth: 18,
            iconHeight: 18,
            items: [{
                value: "a",
                iconClass: "drag-axis-accu-icon",
                iconWidth: 24,
                iconHeight: 24,
            },{
                value: "b",
                iconClass: "area-chart-style-broken-icon",
                iconWidth: 24,
                iconHeight: 24,
            }, {
                value: "c",
                iconClass: "area-chart-style-curve-icon",
                iconWidth: 24,
                iconHeight: 24,

            }, {
                value: "d",
                iconClass: "area-chart-style-vertical-icon",
                iconWidth: 24,
                iconHeight: 24,
            }]
        });

        BI.createWidget({
            type: "bi.center_adapt",
            element: this.element,
            items: [combo]
        });
    },

    getValue: function () {
        return {};
    },

    populate: function () {

    }
});
$.shortcut("bi.combine_chart_target_scope", BI.CombineChartTargetScope);