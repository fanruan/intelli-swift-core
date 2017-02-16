/**
 * 显示标题的详细设置
 * Created by AstronautOO7 on 2016/9/28.
 */
BI.ShowTitleDetailedSettingCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.ShowTitleDetailedSettingCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-show-title-detailed-setting",
            width: 50
        })
    },

    _init: function() {
        BI.ShowTitleDetailedSettingCombo.superclass._init.apply(this, arguments);
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
                    type: "bi.show_title_detailed_setting_popup",
                    onChange: function () {
                        self.fireEvent(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE)
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
BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE = 'EVENT_CHANGE';
$.shortcut("bi.show_title_detailed_setting_combo", BI.ShowTitleDetailedSettingCombo);