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
            iconClass: "detail-dimension-set-font",
            items: [{
                value: "第一项",
                iconClass: "delete-font"
            }, {
                value: "第二项",
                iconClass: "rename-font"
            }, {
                value: "第三项",
                iconClass: "move-font"
            }]
        });

        BI.createWidget({
            type: "bi.center_adapt",
            element: this.element,
            items: [combo]
        });
    },

    populate: function () {

    }
});
$.shortcut("bi.combine_chart_target_scope", BI.CombineChartTargetScope);