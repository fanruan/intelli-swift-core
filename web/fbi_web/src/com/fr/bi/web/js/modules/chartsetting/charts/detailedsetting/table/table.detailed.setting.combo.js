/**
 * 表格样式详细设置
 * Created by AstronautOO7 on 2016/10/10.
 */
BI.TableDetailedSettingCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.TableDetailedSettingCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-table-detailed-setting",
            width: 50
        })
    },

    _init: function() {
        BI.TableDetailedSettingCombo.superclass._init.apply(this, arguments);
        var self = this;

        this.combo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            width: this.options.width,
            el: {
                type: "bi.detailed_setting_trigger"
            },
            popup: {
                el:  {
                    type: "bi.table_detailed_setting_popup",
                    onChange: function() {
                        self.fireEvent(BI.TableDetailedSettingCombo.EVENT_CHANGE);
                    }
                },
                minWidth: 390,
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
BI.TableDetailedSettingCombo.EVENT_CHANGE = 'EVENT_CHANGE';
$.shortcut("bi.table_detailed_setting_combo", BI.TableDetailedSettingCombo);