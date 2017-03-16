/**
 * Created by Young's on 2016/4/26.
 */
BI.GlobalUpdateSetting = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.GlobalUpdateSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-global-update-setting"
        })
    },

    _init: function () {
        BI.GlobalUpdateSetting.superclass._init.apply(this, arguments);
        var self = this;
        var addTime = BI.createWidget({
            type: "bi.button",
            height: 28,
            text: "+" + BI.i18nText("BI-Timing_Set")
        });
        addTime.on(BI.Button.EVENT_CHANGE, function () {
            self.timeSettingGroup.addItems([{
                type: "bi.time_setting_item",
                onRemoveSetting: function (id) {
                    self._removeSettingById(id);
                },
                onSettingChange: function () {
                    self.update();
                }
            }]);
            self.update();
        });

        this.timeSettingGroup = BI.createWidget({
            type: "bi.button_group",
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
        });

        BI.Utils.getUpdateSettingBySourceId({id: BICst.CUBE_UPDATE_TYPE.GLOBAL_UPDATE}, function (updateSetting) {
            var timeSettings = BI.isNotNull(updateSetting) ? updateSetting.time_list : [];
            self.timeSettingGroup.populate(BI.createItems(timeSettings, {
                type: "bi.time_setting_item",
                onRemoveSetting: function (id) {
                    self._removeSettingById(id);
                },
                onSettingChange: function () {
                    self.update();
                }
            }));
        });
    },

    update: function () {
        BI.Utils.modifyUpdateSetting({
            id: BICst.CUBE_UPDATE_TYPE.GLOBAL_UPDATE,
            updateSetting: this.getValue()
        }, BI.emptyFn, BI.emptyFn);
    },

    _removeSettingById: function (id) {
        var allButtons = this.timeSettingGroup.getAllButtons();
        var index = 0;
        BI.some(allButtons, function (i, button) {
            if (button.getValue().id === id) {
                index = i;
                return true;
            }
        });
        this.timeSettingGroup.removeItemAt(index);
        this.update();
    },

    getValue: function () {
        return {
            time_list: this.timeSettingGroup.getValue()
        }
    }
});
$.shortcut("bi.global_update_setting", BI.GlobalUpdateSetting);