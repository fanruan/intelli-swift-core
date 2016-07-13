/**
 * @class BI.NumberIntervalCustomItemGroup
 * @extend BI.Widget
 */

BI.NumberIntervalCustomItemGroup = BI.inherit(BI.Widget, {

    constants: {
        vgap: 10,
        hgap: 10,
        itemHeight: 40,
        initialMax: 100
    },

    _defaultConfig: function () {
        return BI.extend(BI.NumberIntervalCustomItemGroup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-number-custom-item-group",
            items: []
        });
    },

    _init: function () {
        BI.NumberIntervalCustomItemGroup.superclass._init.apply(this, arguments);

        var self = this, o = this.options;

        this.buttongroup = BI.createWidget({
            type: "bi.list_pane",
            element: this.element,
            el: {
                type: "bi.button_group",
                chooseType: BI.ButtonGroup.CHOOSE_TYPE_NONE,
                layouts: [{
                    type: "bi.vertical"
                }]
            },
            items: o.items
        });

        this.buttons = this.buttongroup.getAllButtons();

        this._setEventForButton(this.buttons);
    },

    _setEventForButton: function (buttons) {
        var self = this;
        BI.each(buttons, function (idx, button) {
            button.on(BI.NumberIntervalCustomGroupItem.EVENT_VALID, function () {
                if (self.isValid()) {
                    self.fireEvent(BI.NumberIntervalCustomItemGroup.EVENT_VALID);
                }
            });

            button.on(BI.NumberIntervalCustomGroupItem.EVENT_CHANGE, function () {
                self._checkNextItemState(this.getValue());
            });

            button.on(BI.NumberIntervalCustomGroupItem.EVENT_ERROR, function () {
                self.fireEvent(BI.NumberIntervalCustomItemGroup.EVENT_ERROR);
            });

            button.on(BI.NumberIntervalCustomGroupItem.EVENT_DESTROY, function () {
                if (self.buttons.length === 0) {
                    self.buttongroup.setTipVisible(true);
                    self.fireEvent(BI.NumberIntervalCustomItemGroup.EVENT_EMPTY_GROUP);
                    return;
                }
                if (self.isValid()) {
                    self.fireEvent(BI.NumberIntervalCustomItemGroup.EVENT_VALID);
                }
            });
        });
    },

    isValid: function () {
        var isValid = true;
        BI.any(this.buttons, function (idx, button) {
            if (!button.isValid()) {
                isValid = false;
                return true;
            }
        });
        return isValid;
    },

    isEmptyGroup: function(){
        return this.buttons.length === 0;
    },

    _checkNextItemState: function (value) {

        var self = this;
        var nextButton = null;
        BI.any(this.buttons, function (idx, button) {
            if (BI.isEqual(button.getValue(), value)) {
                nextButton = self.buttons[idx + 1];
                return true;
            }
        });

        if (BI.isNotNull(nextButton)) {
            nextButton.setValue(BI.extend(nextButton.getValue(), {
                closemin: !value.closemax,
                min: value.max
            }));
        }
    },

    _checkButtonEnable: function () {
        BI.each(this.buttons, function (idx, button) {
            if (idx !== 0) {
                button.setSmallIntervalEnable(false);
            }
        });
    },

    populate: function (items) {
        this.options.items = items || [];
        this.buttongroup.populate(items);
        this.buttons = this.buttongroup.getAllButtons();
        this._setEventForButton(this.buttons);
        this._checkButtonEnable();
    },

    addItem: function () {
        var o = this.options;
        var item = {
            type: "bi.number_custom_group_item",
            group_name: BI.i18nText("BI-Grouping") + (this.buttons.length + 1),
            height: this.constants.itemHeight
        };
        if (this.buttons.length === 0) {
            BI.extend(item, {
                id: BI.UUID(),
                min: 0,
                max: this.constants.initialMax,
                closemin: true,
                closemax: true
            });
        } else {
            var beforeButton = this.buttons[this.buttons.length - 1];
            var beforeValue = beforeButton.getValue();
            beforeButton.setValue(BI.extend(beforeValue, {
                closemax: false
            }));
            BI.extend(item, {
                id: BI.UUID(),
                min: BI.parseInt(beforeValue.max),
                max: BI.parseInt(beforeValue.max) + this.constants.initialMax,
                closemin: !beforeValue.closemax,
                closemax: true
            });
        }
        this.buttongroup.addItems([item]);
        this.buttongroup.setTipVisible(false);
        this.buttons = this.buttongroup.getAllButtons();
        if (this.buttons.length !== 1) {
            this.buttons[this.buttons.length - 1].setSmallIntervalEnable(false);
        }
        this._setEventForButton([this.buttons[this.buttons.length - 1]]);
        this.fireEvent(BI.NumberIntervalCustomItemGroup.EVENT_VALID);
    },

    scrollToBottom: function () {
        var self = this;
        BI.delay(function () {
            self.buttongroup.element.scrollTop(BI.MAX);
        }, 30);
    },

    setValue: function (v) {

    },

    getValue: function () {
        var group_nodes = [];
        BI.each(this.buttons, function (idx, item) {
            group_nodes.push(item.getValue());
        });
        return group_nodes;
    }

});

BI.NumberIntervalCustomItemGroup.EVENT_ERROR = "EVENT_ERROR";
BI.NumberIntervalCustomItemGroup.EVENT_EMPTY_GROUP = "EVENT_EMPTY_GROUP";
BI.NumberIntervalCustomItemGroup.EVENT_VALID = "EVENT_VALID";

$.shortcut("bi.number_custom_item_group", BI.NumberIntervalCustomItemGroup);