/**
 * 图的轴标签
 * Created by AstronautOO7 on 2016/10/11.
 */
BI.ChartLabelDetailedSettingCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.ChartLabelDetailedSettingCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-label-detailed-setting",
            width: 50
        })
    },

    _init: function() {
        BI.ChartLabelDetailedSettingCombo.superclass._init.apply(this, arguments);
        var self = this;

        this.popup = BI.createWidget({
            type: "bi.chart_label_detailed_setting_popup"
        });
        this.popup.on(BI.ChartLabelDetailedSettingPopup.EVENT_CHANGE, function() {
            self.fireEvent(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE)
        });

        BI.createWidget({
            type: "bi.combo",
            width: this.options.width,
            element: this.element,
            el: {
                type: "bi.detailed_setting_trigger"
            },
            popup: {
                el:  this.popup,
                minWidth: 350,
                stopPropagation: false
            }
        })
    },

    setValue: function(v) {
        this.popup.setValue(v)
    },

    getValue: function() {
        return this.popup.getValue()
    }
});
BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE = 'EVENT_CHANGE';
$.shortcut("bi.chart_label_detailed_setting_combo", BI.ChartLabelDetailedSettingCombo);