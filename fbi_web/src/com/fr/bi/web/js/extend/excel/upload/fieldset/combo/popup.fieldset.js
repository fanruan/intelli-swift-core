/**
 * Created by Young's on 2016/3/16.
 */
BI.FieldSetPopup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.FieldSetPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-field-set-popup",
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_SINGLE
        });
    },

    _init: function () {
        BI.FieldSetPopup.superclass._init.apply(this, arguments);
        var o = this.options, self = this;
        this.popup = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            items: BI.createItems(o.items, {
                type: "bi.excel_field_type_item",
                height: 30
            }),
            chooseType: o.chooseType,
            layouts: [{
                type: "bi.vertical"
            }]
        });

        this.popup.on(BI.Controller.EVENT_CHANGE, function (type, val, obj) {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
            if (type === BI.Events.CLICK) {
                self.fireEvent(BI.FieldSetPopup.EVENT_CHANGE, val, obj);
            }
        })
    },

    populate: function(items){
        items = BI.createItems(items, {
            type: "bi.single_select_icon_text_item",
            iconClass: "item-check-font ",
            height: 30
        });
        this.popup.populate(items);
    },

    getValue: function () {
        return this.popup.getValue();
    },

    setValue: function (v) {
        this.popup.setValue(v);
    }
});
BI.FieldSetPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.field_set_popup", BI.FieldSetPopup);
