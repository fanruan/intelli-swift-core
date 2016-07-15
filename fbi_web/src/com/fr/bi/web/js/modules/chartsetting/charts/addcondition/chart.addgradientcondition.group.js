/**
 * condition group of gradient color
 * Created by GameJian on 2016/7/14.
 */
BI.ChartAddGradientConditionGroup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ChartAddGradientConditionGroup.superclass._defaultConfig.apply(this,arguments) , {
            baseCls: "bi-chart-add-gradient-condition-group",
            items: []
        })
    },

    _init: function () {
        BI.ChartAddGradientConditionGroup.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.buttons = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            items: o.items,
            layout: [{
                type: "bi.vertical"
            }]
        })
    },

    addItem: function() {
        var self = this;
        var item = {
            type: "bi.chart_add_gradient_condition_item",
            range: {
                min: 0,
                max: 100,
                colosemin: true,
                colosemax: true
            },

        }
    }
});
BI.ChartAddGradientConditionGroup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_add_gradient_condition_group" , BI.ChartAddGradientConditionGroup);