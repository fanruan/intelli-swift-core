/**
 * 显示标题的详细设置
 * Created by AstronautOO7 on 2016/9/28.
 */
BI.ShowTitleDetailedSettingCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.ShowTitleDetailedSettingCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-show-title-detailed-setting",
            width: 100
        })
    },

    _init: function() {
        BI.ShowTitleDetailedSettingCombo.superclass._init.apply(this, arguments);
        var self = this;

        this.popup = BI.createWidget({
            type: "bi.show_title_detailed_setting_popup"
        });
        this.popup.on(BI.ShowTitleDetailedSettingPopup.EVENT_CHANGE, function() {
            self.fireEvent(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE)
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
BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE = 'EVENT_CHANGE';
$.shortcut("bi.show_title_detailed_setting_combo", BI.ShowTitleDetailedSettingCombo);