/**
 * Created by Young's on 2016/3/23.
 */
BI.TargetConditionStyleSetting = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TargetConditionStyleSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-condition-style-setting"
        })
    },

    _init: function () {
        BI.TargetConditionStyleSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var items = o.conditions;
        this.numLevel = o.numLevel;
        this.conditions = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(items, {
                type: "bi.target_style_condition_item",
                numLevel: this.numLevel,
                onRemoveCondition: function (id) {
                    self._removeConditionById(id);
                }
            }),
            layouts: [{
                type: "bi.vertical",
                hgap: 10,
                vgap: 5
            }]
        });
        this.conditions.on(BI.ButtonGroup.EVENT_CHANGE, function () {

        });
        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                type: "bi.left_right_vertical_adapt",
                cls: "add-condition-tool",
                items: {
                    left: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Text_Color"),
                        height: 30,
                        cls: "setting-title"
                    }],
                    right: [{
                        type: "bi.button",
                        text: "+" + BI.i18nText("BI-Conditions_Setting"),
                        height: 26,
                        handler: function () {
                            self._addNewCondition();
                        }
                    }]
                },
                lhgap: 10,
                rhgap: 10,
                height: 30
            }, this.conditions],
            height: 180
        })
    },

    _addNewCondition: function () {
        var self = this;
        var allConditions = this.conditions.getAllButtons();
        var lastMax = 0;
        if (allConditions.length > 0) {
            lastMax = BI.parseInt(allConditions[allConditions.length - 1].getValue().range.max) || 0;
        }
        this.conditions.addItems([{
            type: "bi.target_style_condition_item",
            numLevel: this.numLevel,
            range: {
                min: lastMax,
                max: lastMax + 100,
                closemin: false,
                closemax: false
            },
            color: "#09ABE9",
            cid: BI.UUID(),
            onRemoveCondition: function (id) {
                self._removeConditionById(id);
            }
        }])
    },

    _removeConditionById: function (id) {
        var allConditions = this.conditions.getAllButtons();
        var index = 0;
        BI.some(allConditions, function (i, co) {
            if (co.getValue().cid === id) {
                index = i;
                return true;
            }
        });
        this.conditions.removeItemAt(index);
    },

    setNumLevel: function (numLevel) {
        this.numLevel = numLevel;
        var buttons = this.conditions.getAllButtons();
        BI.each(buttons, function (i, button) {
            button.setNumLevel(numLevel);
        });
    },

    getValue: function () {
        var values = [];
        BI.each(this.conditions.getAllButtons(), function (i, bt) {
            values.push(bt.getValue());
        });
        return values;
    }
});
$.shortcut("bi.target_condition_style_setting", BI.TargetConditionStyleSetting);