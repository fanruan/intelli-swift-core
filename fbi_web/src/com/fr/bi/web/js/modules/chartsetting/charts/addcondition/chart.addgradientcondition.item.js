/**
 * 渐变色条件添加
 * Created by GameJian on 2016/7/14.
 */
BI.ChartAddGradientConditionItem = BI.inherit(BI.Widget, {
    _defaultConfig: function() {
        return BI.extend(BI.ChartAddGradientConditionItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-add-gradient-condition-item"
        })
    },

    _init: function () {
        BI.ChartAddGradientConditionItem.superclass._init.apply(this, arguments);
        var self = this,o = this.options;
        var range = o.range, color_range = o.color_range;

        this.numberRange = BI.createWidget({
           type: "bi.numerical_interval",
            width: 350,
            min: range.min,
            max: range.max,
            closemin: range.closemin,
            closemax: range.closemax
        });

        this.numberRange.on(BI.NumericalInterval.EVENT_CHANGE, function () {
            self.fireEvent(BI.ChartAddGradientConditionItem.EVENT_CHANGE)
        });

        this.gradientCombo = BI.createWidget({
            type: "bi.chart_gradient_color_combo"
        });

        this.gradientCombo.setValue(color_range);

        this.gradientCombo.on(BI.ChartGradientColorCombo.EVENT_CHANGE, function() {
           self.fireEvent(BI.ChartAddGradientConditionItem.EVENT_CHANGE)
        });

        this.deleteIcon = BI.createWidget({
            type: "bi.icon_button",
            cls: "data-link-remove-font",
            width: 25,
            height: 25,
            handler: function () {
                o.removeCondition(o.cid)
            }
        });

        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [this.numberRange, this.gradientCombo, this.deleteIcon],
            hgap: 3,
            vgap: 2
        });

    },

    getValue: function () {
        return {
            range: this.numberRange.getValue(),
            color_range: this.gradientCombo.getValue(),
            cid: this.options.cid
        }
    },

    setValue: function (v) {
        this.numberRange.setValue(v.range);
        this.gradientCombo.setValue(v.color_range)
    },

    setSmallIntervalEnable: function (v) {
        this.numberRange.setMinEnable(v);
        this.numberRange.setCloseMinEnable(v)
    }
});
BI.ChartAddGradientConditionItem.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_add_gradient_condition_item", BI.ChartAddGradientConditionItem);
