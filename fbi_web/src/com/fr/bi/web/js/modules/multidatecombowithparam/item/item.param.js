/**
 * 日期控件参数面板的事件偏移选择group
 * @class BI.ParamItem
 * @extends BI.Widget
 */
BI.ParamItem = BI.inherit(BI.Widget, {

    constants: {
        lgap: 80,
        itemHeight: 35,
        defaultEditorValue: "1"
    },

    _defaultConfig: function () {
        return BI.extend(BI.ParamItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: 'bi-multidate-param-item'
        });
    },

    _init: function () {
        BI.ParamItem.superclass._init.apply(this, arguments);

        var self = this, opts = this.options;

        this.radioGroup = BI.createWidget({
            type: "bi.button_group",
            chooseType: 0,
            items: BI.createItems(this.dateConfig(), {
                type: 'bi.multidate_segment',
                height: this.constants.itemHeight
            }),
            layouts: [{
                type: "bi.vertical"
            }]
        });

        this.radioGroup.on(BI.Controller.EVENT_CHANGE, function (type) {
            if (type === BI.Events.CONFIRM) {
                self.fireEvent(BI.MultiDateCard.EVENT_CHANGE);
            }
        });
        this.radioGroup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.setValue(self.getValue());
            self.fireEvent(BI.MultiDateCard.EVENT_CHANGE);
        });
        BI.createWidget({
            element: this.element,
            type: 'bi.center_adapt',
            lgap: this.constants.lgap,
            items: [{
                type: 'bi.vertical',
                items: [this.radioGroup]
            }]
        });
    },

    getValue: function () {
        var button = this.radioGroup.getSelectedButtons()[0];
        var type = button.getValue(), value = button.getInputValue();
        return {
            type: type,
            value: value
        }
    },

    _isTypeAvaliable: function (type) {
        var res = false;
        BI.find(this.dateConfig(), function (i, item) {
            if (item.value === type) {
                res = true;
                return true;
            }
        });
        return res;
    },

    setValue: function (v) {
        var self = this;
        if (BI.isNotNull(v) && this._isTypeAvaliable(v.type)) {
            this.radioGroup.setValue(v.type);
            BI.each(this.radioGroup.getAllButtons(), function (i, button) {
                if (button.isEditorExist() === true && button.isSelected()) {
                    button.setInputValue(v.value);
                    button.setEnable(true);
                } else {
                    button.setInputValue(self.constants.defaultEditorValue);
                    button.setEnable(false);
                }
            });
        } else {
            this.radioGroup.setValue(this.defaultSelectedItem());
            BI.each(this.radioGroup.getAllButtons(), function (i, button) {
                button.setInputValue(self.constants.defaultEditorValue);
                if (button.isEditorExist() === true && button.isSelected()) {
                    button.setEnable(true);
                } else {
                    button.setEnable(false);
                }
            });
        }
    },

    getCalculationValue: function () {
        var valueObject = this.getValue();
        var type = valueObject.type, value = valueObject.value;
        switch (type) {
            case BICst.MULTI_DATE_YEAR_PREV:
                return new Date((new Date().getFullYear() - 1 * value), new Date().getMonth(), new Date().getDate());
            case BICst.MULTI_DATE_YEAR_BEGIN:
                return new Date(new Date().getFullYear(), 0, 1);
        }
    },

    dateConfig: function(){
        return [{
            selected: true,
            isEditorExist: true,
            text: BI.i18nText("BI-Multi_Date_Year_Prev"),
            value: BICst.MULTI_DATE_YEAR_PREV
        }, {
            isEditorExist: false,
            value: BICst.MULTI_DATE_YEAR_BEGIN,
            text: BI.i18nText("BI-Multi_Date_Year_Begin")
        }]
    },

    defaultSelectedItem: function(){
        return BICst.MULTI_DATE_YEAR_PREV;
    }
});
BI.ParamItem.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.param_item', BI.ParamItem);