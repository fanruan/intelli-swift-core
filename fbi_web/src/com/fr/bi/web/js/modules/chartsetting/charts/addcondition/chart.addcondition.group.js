/**
 * 图标条件选择组
 * Created by GameJian on 2016/7/1.
 */
BI.ChartAddConditionGroup = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ChartAddConditionGroup.superclass._defaultConfig.apply(this,arguments), {
            baseCls: "bi-chart-add-condition-group",
            items: []
        })
    },

    _init: function () {
        BI.ChartAddConditionGroup.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.buttongroup = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            items: o.items,
            layouts: [{
                type: "bi.vertical"
            }]
        });

        this.buttons = this.buttongroup.getAllButtons();

        self._sendEventForButton(this.buttons);
    },

    _sendEventForButton: function (buttons) {
        var self = this;

        BI.each(buttons , function (idx , button){
            button.on(BI.TargetStyleConditionItem.EVENT_CHANGE , function () {
                self._checkNextItemState(this.getValue());
            })
        })
    },

    _checkNextItemState: function (value) {
        var self = this;
        var nextButton = null;

        BI.any(this.buttons , function (index , button ) {
            if (BI.isEqual(button.getValue() , value)) {
                nextButton = self.buttons[index + 1];
                return true;
            }
        });

        if(BI.isNotNull(nextButton)) {
            nextButton.setValue(BI.extend(nextButton.getValue() , {
                closemin: !value.range.max,
                min: value.range.max
            }));
        }
    },

    _checkButtonEnable: function () {
        BI.each(this.buttons , function (idx , button) {
            if(idx !== 0) {
                button.setSmallIntervalEnable();
            }
        } )
    },

    populate: function (items) {
        this.options.items = items || [];
        this.buttongroup.populate(items);
        this.buttons = this.buttongroup.getAllButtons();
        this._sendEventForButton(this.buttons);
        this._checkButtonEnable();
    },

    addItem: function () {
        var self = this;
        var item = {
            type: "bi.target_style_condition_item",
            range: {
                min: 0,
                max: 100,
                closemin: false,
                clasemax: false
            },
            color: "#09ABE9",
            cid: BI.UUID(),
            onRemoveCondition: function () {
                self._removeCondition()
            }
        };

        if(this.buttons.length === 0) {
            item.range.closemin = true;
            item.range.closemax = true;
        } else {
            var beforeButton = this.buttons[this.buttons.length - 1];
            var beforeValue = beforeButton.getValue().range;
            beforeButton.setValue(BI.extend(beforeValue , {
                closemax: false
            }));
            BI.extend(item , {
                range: {
                    min: BI.parseInt(beforeValue.max),
                    max: BI.parseInt(beforeValue.max) + 100,
                    closemin: !beforeValue.closemax,
                    closemax: true
                }
            })
        }

        this.buttongroup.addItems([item]);
        this.buttons = this.buttongroup.getAllButtons();
        if(this.buttons.length !== 1) {
            this.buttons[this.buttons.length - 1].setSmallIntervalEnable(false);
        }

        this._sendEventForButton([this.buttons[this.buttons.length - 1 ]])
    },

    setValue: function () {

    },

    getValue: function () {

    },

    _removeCondition : function (id) {
        var allConditions = this.buttongroup.getAllButtons();
        var index = 0;

        BI.some(allConditions , function ( i , con ) {
            if(con.getValue().cid === id) {
                index = i;
                return true;
            }
        });

        this.buttongroup.removeItemAt(index)
    }

});

BI.ChartAddConditionGroup.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.chart_add_condition_group" , BI.ChartAddConditionGroup);