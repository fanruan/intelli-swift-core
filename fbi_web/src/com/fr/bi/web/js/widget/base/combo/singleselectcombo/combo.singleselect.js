/**
 * 单选combo
 *
 * @class BI.SingleSelectCombo
 * @extend BI.Widget
 */
BI.SingleSelectCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SingleSelectCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-single-select-combo",
            height: 30,
            text: "",
            el:{},
            items: [],
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_SINGLE
        })
    },

    _init: function () {
        BI.SingleSelectCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.trigger = BI.createWidget(o.el, {
            type: "bi.text_icon_item",
            cls: "bi-select-text-trigger pull-down-font",
            text: o.text,
            readonly: true,
            textLgap: 5,
            height: o.height
        });
        this.popup = BI.createWidget({
            type: "bi.text_icon_combo_popup",
            chooseType: o.chooseType,
            items: o.items
        });
        this.popup.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        this.popup.on(BI.TextIconComboPopup.EVENT_CHANGE, function () {
            self.combo.hideView();
            self.fireEvent(BI.SingleSelectCombo.EVENT_CHANGE, arguments);
        });
        this.combo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            adjustLength: 2,
            el: this.trigger,
            popup: {
                el: this.popup
            }
        });
    },

    populate: function(items){
        this.combo.populate(items);
    },

    setValue: function (v) {
        this.combo.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue();
    }
});
BI.SingleSelectCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.single_select_combo", BI.SingleSelectCombo);