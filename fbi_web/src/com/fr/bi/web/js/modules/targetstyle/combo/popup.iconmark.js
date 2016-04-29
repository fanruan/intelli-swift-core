/**
 * Created by Young's on 2016/3/23.
 */
BI.IconMarkPopup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.IconMarkPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-icon-mark-popup",
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_SINGLE
        });
    },

    _init: function () {
        BI.IconMarkPopup.superclass._init.apply(this, arguments);
        var o = this.options, self = this;
        this.popup = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            items: BI.createItems(o.items, {
                type: "bi.icon_mark_item",
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
                self.fireEvent(BI.IconMarkPopup.EVENT_CHANGE, val, obj);
            }
        })
    },

    getValue: function () {
        return this.popup.getValue();
    },

    setValue: function (v) {
        this.popup.setValue(v);
    }
});
BI.IconMarkPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.icon_mark_popup", BI.IconMarkPopup);