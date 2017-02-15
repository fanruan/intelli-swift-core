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

        this.combo = BI.createWidget({
            type: "bi.combo",
            width: this.options.width,
            element: this.element,
            el: {
                type: "bi.detailed_setting_trigger"
            },
            popup: {
                el:  {
                    type: "bi.chart_label_detailed_setting_popup",
                    onChange: function() {
                        self.fireEvent(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE)
                    }
                },
                minWidth: 320,
                stopPropagation: false
            }
        })
    },

    setValue: function(v) {
        this.combo.setValue(v)
    },

    getValue: function() {
        return this.combo.getValue()
    }
});
BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE = 'EVENT_CHANGE';
$.shortcut("bi.chart_label_detailed_setting_combo", BI.ChartLabelDetailedSettingCombo);