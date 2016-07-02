/**
 * 图标条件选择
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
            items: o.items
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
                closemin: !value.closemin,
                min: value.max
            }));
        }
    }

});

BI.ChartAddCondition.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.chart_add_condition" , BI.ChartAddCondition);