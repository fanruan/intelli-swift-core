/**
 * Created by GUY on 2016/2/2.
 *
 * @class BI.IconComboTrigger
 * @extend BI.Widget
 */
BI.IconComboTrigger = BI.inherit(BI.Trigger, {
    _defaultConfig: function () {
        return BI.extend(BI.IconComboTrigger.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-icon-combo-trigger",
            items: [],
            width: 25,
            height: 25
        });
    },

    _init: function () {
        BI.IconComboTrigger.superclass._init.apply(this, arguments);
        var o = this.options, self = this;
        this.button = BI.createWidget({
            type: "bi.icon_change_button",
            forceNotSelected: true,
            element: this.element,
            height: 25
        });
        if (BI.isKey(o.value)) {
            this.setValue(o.value);
        }
    },

    populate: function (items) {
        this.options.items = items || [];
    },

    setValue: function (v) {
        BI.IconComboTrigger.superclass.setValue.apply(this, arguments);
        var iconClass = "";
        v = BI.isArray(v) ? v[0] : v;
        if (BI.any(this.options.items, function (i, item) {
                if (v === item.value) {
                    iconClass = item.iconClass;
                    return true;
                }
            })) {
            this.button.setIcon(iconClass);
        }
    }

});
BI.IconComboTrigger.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.icon_combo_trigger", BI.IconComboTrigger);