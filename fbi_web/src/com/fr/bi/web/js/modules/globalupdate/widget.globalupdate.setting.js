/**
 * Created by Young's on 2016/4/26.
 */
BI.GlobalUpdateSetting = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.GlobalUpdateSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-global-update-setting"
        })
    },

    _init: function(){
        BI.GlobalUpdateSetting.superclass._init.apply(this, arguments);
        var self = this;
        var globalUpdate = BI.Utils.getUpdateSettingByID(BICst.GLOBAL_UPDATE);
        var timeSettings = BI.isNotNull(globalUpdate) ? globalUpdate.time_list : [];
        var addTime = BI.createWidget({
            type: "bi.button",
            height: 28,
            text: "+" + BI.i18nText("BI-Timing_Set")
        });
        addTime.on(BI.Button.EVENT_CHANGE, function(){
            self.timeSettingGroup.addItems([{
                type: "bi.time_setting_item",
                onRemoveSetting: function(id){
                    self._removeSettingById(id);
                },
                onSettingChange: function(){
                    self.update();
                }
            }]);
            self.update();
        });

        this.timeSettingGroup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(timeSettings, {
                type: "bi.time_setting_item",
                onRemoveSetting: function(id){
                    self._removeSettingById(id);
                },
                onSettingChange: function(){
                    self.update();
                }
            }),
            layouts: [{
                type: "bi.vertical"
            }]
        });
        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                type: "bi.left_right_vertical_adapt",
                cls: "add-time-toolbar",
                items: {
                    left: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Timing_Set"),
                        height: 30,
                        hgap: 10,
                        cls: "add-time-comment"
                    }],
                    right: [addTime]
                },
                height: 30
            }, this.timeSettingGroup]
        })
    },
    
    update: function(){
        var settings = Data.SharingPool.get("update_settings");
        settings[BICst.GLOBAL_UPDATE] = this.getValue();
        Data.SharingPool.put("update_settings", settings);
        BI.Utils.modifyGlobalUpdateSetting({setting: this.getValue()}, function(){});
    },

    _removeSettingById: function(id){
        var allButtons = this.timeSettingGroup.getAllButtons();
        var index = 0;
        BI.some(allButtons, function(i, button) {
            if(button.getValue().id === id) {
                index = i;
                return true;
            }
        });
        this.timeSettingGroup.removeItemAt(index);
        this.update();
    },

    getValue: function(){
        return {
            time_list: this.timeSettingGroup.getValue()
        }
    }
});
$.shortcut("bi.global_update_setting", BI.GlobalUpdateSetting);