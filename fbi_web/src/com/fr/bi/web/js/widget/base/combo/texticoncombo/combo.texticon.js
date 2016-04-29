/**
 * @class BI.TextIconCombo
 * @extend BI.Widget
 * combo : text + icon, popup : text
 * 参见场景dashboard布局方式选择
 */
BI.TextIconCombo = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        return BI.extend(BI.TextIconCombo.superclass._defaultConfig.apply(this, arguments), {
            width: 100,
            height: 22,
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_SINGLE
        })
    },

    _init: function () {
        BI.TextIconCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.trigger = BI.createWidget({
            type: "bi.select_text_trigger",
            items: o.items,
            height: o.height
        });
        this.popup = BI.createWidget({
            type: "bi.text_icon_combo_popup",
            chooseType: o.chooseType,
            items: o.items
        });
        this.popup.on(BI.TextIconComboPopup.EVENT_CHANGE, function () {
            self.setValue(self.popup.getValue());
            self.textIconCombo.hideView();
            self.fireEvent(BI.TextIconCombo.EVENT_CHANGE, arguments);
        });
        this.popup.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        this.textIconCombo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            adjustLength: 2,
            el: this.trigger,
            popup: {
                el: this.popup,
                maxWidth: "",
                maxHeight: 300
            }
        });
    },

    setValue: function (v) {
        this.textIconCombo.setValue(v);
    },

    setEnable: function (v) {
        BI.TextIconCombo.superclass.setEnable.apply(this, arguments);
        this.textIconCombo.setEnable(v);
    },

    getValue: function () {
        return this.textIconCombo.getValue();
    },

    populate: function (items) {
        this.options.items = items;
        this.textIconCombo.populate(items);
    }
});
BI.TextIconCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.text_icon_combo", BI.TextIconCombo);