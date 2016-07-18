/**
 * 组合选色
 * @class BI.ChartSettingSelectColorCombo
 * @extends BI.Widget
 */
BI.ChartSettingSelectColorCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ChartSettingSelectColorCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-setting-select-color-combo",
            width: 130
        })
    },

    _init: function () {
        BI.ChartSettingSelectColorCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.popup = BI.createWidget({
            type: "bi.chart_setting_select_color_popup"
        });

        this.combo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            adjustLength: 1,
            el: {
                type: "bi.chart_setting_select_color_trigger"
            },
            popup: {
                el: this.popup,
                stopPropagation: false,
                minWidth: 145
            }
        });

        this.combo.on(BI.Combo.EVENT_CHANGE, function () {
            self.setValue(this.getValue()[0]);
            self.combo.hideView();
            self.fireEvent(BI.ChartSettingSelectColorCombo.EVENT_CHANGE);
        });
    },

    populate: function () {
        var defaultChartConfig = BI.Utils.getDefaultChartConfig();
        var items = [];
        if (defaultChartConfig.styleList.length > 0) {
            BI.each(defaultChartConfig.styleList, function (i, config) {
                items.push({
                    header: config.value,
                    text: config.colors.slice(0, 5),
                    value: config.colors
                })
            });
        } else {
            items = BICst.CHART_COLORS;
        }
        this.combo.populate(items);
    },

    setValue: function (color) {
        color = BI.isArray(color) ? color : [color];
        this.combo.setValue(color);
    },

    getValue: function () {
        return this.combo.getValue();
    }
});
BI.ChartSettingSelectColorCombo.EVENT_CHANGE = "ChartSettingSelectColorCombo.EVENT_CHANGE";
$.shortcut("bi.chart_setting_select_color_combo", BI.ChartSettingSelectColorCombo);