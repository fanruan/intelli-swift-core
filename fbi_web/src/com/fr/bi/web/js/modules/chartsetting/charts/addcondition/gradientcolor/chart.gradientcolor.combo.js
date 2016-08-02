/**
 * 渐变色combo
 * Created by GameJian on 2016/7/15.
 */
BI.ChartGradientColorCombo = BI.inherit(BI.Widget, {
   _defaultConfig: function () {
       return BI.extend(BI.ChartGradientColorCombo.superclass._defaultConfig.apply(this, arguments), {
           baseCls: "bi-chart-gradient-color-combo"
       })
   },

    _init: function () {
        BI.ChartGradientColorCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.comboFrom = BI.createWidget({
            type: "bi.color_chooser",
            width: 25,
            height: 25
        });

        this.comboFrom.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.setValue(self.getValue());
            self.fireEvent(BI.ChartGradientColorCombo.EVENT_CHANGE)
        });

        this.gradientColor = BI.createWidget({
            type: "bi.layout"
        });

        var gradient = BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: this.gradientColor,
                left: 0,
                right: 0,
                top: 3,
                bottom: 3
            }],
            height: 25,
            width: 30
        });

        this.comboTo = BI.createWidget({
            type: "bi.color_chooser",
            width: 25,
            height: 25
        });

        this.comboTo.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.setValue(self.getValue());
            self.fireEvent(BI.ChartGradientColorCombo.EVENT_CHANGE)
        });

        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [this.comboFrom, gradient, this.comboTo],
            hgap: 3
        });
    },

    getValue: function () {
        return {
            from_color: this.comboFrom.getValue(),
            to_color: this.comboTo.getValue()
        }
    },

    setValue: function (v) {
        this.comboFrom.setValue(v.from_color);
        this.comboTo.setValue(v.to_color);
        var color = "linear-gradient(to right, " + v.from_color + " , " + v.to_color + ")";
        this.gradientColor.element.css("background", color)
    }
});
BI.ChartGradientColorCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_gradient_color_combo" , BI.ChartGradientColorCombo);