/**
 * 图例详细设置
 * Created by AstronautOO7 on 2016/10/12.
 */
BI.LegendDetailedSettingCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.LegendDetailedSettingCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-legend-detailed-setting",
            width: 50
        })
    },

    _init: function() {
        BI.LegendDetailedSettingCombo.superclass._init.apply(this, arguments);
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
                    type: "bi.legend_detailed_setting_popup",
                    onChange: function() {
                        self.fireEvent(BI.LegendDetailedSettingCombo.EVENT_CHANGE)
                    }
                },
                minWidth: 350,
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
BI.LegendDetailedSettingCombo.EVENT_CHANGE = 'EVENT_CHANGE';
$.shortcut("bi.legend_detailed_setting_combo", BI.LegendDetailedSettingCombo);