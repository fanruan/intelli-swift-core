/**
 * condition group of gradient color
 * Created by GameJian on 2016/7/14.
 */
BI.ChartAddGradientConditionGroup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ChartAddGradientConditionGroup.superclass._defaultConfig.apply(this,arguments) , {
            baseCls: "bi-chart-add-gradient-condition-group",
            items: []
        })
    },

    _init: function () {
        BI.ChartAddGradientConditionGroup.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.buttonGroup = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            items: o.items,
            layouts: [{
                type: "bi.vertical"
            }]
        });

        this.buttons = this.buttonGroup.getAllButtons();

        self._sendEventToButtons(this.buttons)
    },

    addItem: function() {
        var self = this;
        var item = {
            type: "bi.chart_add_gradient_condition_item",
            range: {
                min: 0,
                max: 100,
                closemin: true,
                closemax: true
            },
            color_range: {
                from_color: "#e85050",
                to_color: "#0c6d23"
            },
            cid: BI.UUID(),
            removeCondition: function (cid) {
                self._removeCondition(cid)
            }
        };

        if(this.buttons.length != 0) {
            var lastButton = this.buttons[this.buttons.length - 1];
            var lastValue = lastButton.getValue();
            lastButton.setValue(BI.extend(lastValue , {
                range: BI.extend(lastValue.range , {
                    closemax: false
                })
            }));
            BI.extend(item , {
                range: {
                    min: BI.parseInt(lastValue.range.max),
                    max: BI.parseInt(lastValue.range.max) + 100,
                    closemin: !lastValue.range.closemax,
                    closemax: true
                }
            });
        }

        this.buttonGroup.addItems([item]);
        this.buttons = this.buttonGroup.getAllButtons();
        if(this.buttons.length != 1){
            this.buttons[this.buttons.length - 1].setSmallIntervalEnable(false);
        }
        this._sendEventToButtons([this.buttons[this.buttons.length - 1]])
    },

    _sendEventToButtons: function (buttons) {
        var self = this;

        BI.each(buttons , function (idx , button) {
            button.on(BI.ChartAddGradientConditionItem.EVENT_CHANGE , function () {
                self._checkItems(this.getValue());
                self.fireEvent(BI.ChartAddGradientConditionGroup.EVENT_CHANGE)
            })
        })
    },

    _removeCondition: function (id) {
        var allButtons = this.buttonGroup.getAllButtons();
        var index = -1;

        BI.some(allButtons , function (idx , button) {
            if(button.getValue().cid == id){
                index = idx;
                return true
            }
        });

        this.buttonGroup.removeItemAt(index);
        this._checkButtonEnable();
        if(index != 0) {
            this._checkItems(this.buttons[index - 1].getValue());
        }
        this.fireEvent(BI.ChartAddGradientConditionGroup.EVENT_CHANGE)
    },

    _checkItems: function (value) {
        var self = this;
        var nextButton = null;

        BI.some(this.buttons , function (idx , button) {
            if(BI.isEqual(button.getValue() , value)){
                nextButton = self.buttons[idx + 1];
                return true
            }
        });

        if(BI.isNotNull(nextButton)){
            nextButton.setValue(BI.extend(nextButton.getValue() , {
                range: BI.extend(nextButton.getValue().range , {
                    min: value.range.max,
                    closemin: !value.range.closemax
                })
            }))
        }
    },

    _checkButtonEnable: function () {
        BI.each(this.buttonGroup.getAllButtons() , function (idx , button) {
            if(idx !== 0) {
                button.setSmallIntervalEnable(false);
            } else {
                button.setSmallIntervalEnable(true);
            }
        } )
    },

    getValue: function () {
        var buttonValue = [];

        BI.each(this.buttons , function (idx , button) {
            buttonValue.push(button.getValue())
        });

        return buttonValue
    },

    setValue: function (v) {
        var self = this;
        this.options.items = v || [];

        BI.each(v , function (idx , button) {
            BI.extend(button , {
                type: "bi.chart_add_gradient_condition_item",
                removeCondition: function (cid) {
                    self._removeCondition(cid)
                }
            });
        });

        this.buttonGroup.addItems(v);
        this._checkButtonEnable();
        this.buttons = this.buttonGroup.getAllButtons();
        this._sendEventToButtons(this.buttons)
    }

});
BI.ChartAddGradientConditionGroup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_add_gradient_condition_group" , BI.ChartAddGradientConditionGroup);