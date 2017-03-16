/**
 * Created by roy on 15/10/20.
 */
BI.CustomGroupCombo = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        return BI.extend(BI.CustomGroupCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: 'bi-custom-group-combo',
            items: []
        })
    },
    _init: function () {
        var self = this, o = this.options;
        BI.CustomGroupCombo.superclass._init.apply(this, arguments);

        this.triggerButton = BI.createWidget({
            type: 'bi.button',
            value: BI.i18nText("BI-Have_Selected") + 0 + BI.i18nText("BI-Item"),
            level: 'ignore',
            readonly: false,
            height: 30
        });


        this.combo = BI.createWidget({
            type: "bi.combo",
            isNeedAdjustHeight: false,
            isNeedAdjustWidth: false,
            element: this.element,
            el: self.triggerButton,
            popup: {
                width: 580,
                maxHeight: 350,
                el: o.popup
            }

        });

        self.combo.on(BI.Combo.EVENT_BEFORE_POPUPVIEW, function () {
            self.fireEvent(BI.CustomGroupCombo.EVENT_BEFORE_POPUPVIEW);
        });

        self.combo.on(BI.Combo.EVENT_AFTER_HIDEVIEW, function () {
            self.fireEvent(BI.CustomGroupCombo.EVENT_HIDEVIEW);
        })


    },
    populate: function (items) {
        this.popup.empty();
        this.combo.populate(items);

    },

    doRedMark: function () {
        this.triggerButton.doRedMark.apply(this.triggerButton, arguments);
    },

    setTriggerValue: function (v) {
        this.triggerButton.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue();
    }

});
BI.CustomGroupCombo.EVENT_BEFORE_POPUPVIEW = "EVENT_BEFORE_POPUPVIEW";
BI.CustomGroupCombo.EVENT_CHANGE = "EVENT_CHANGE";
BI.CustomGroupCombo.EVENT_HIDEVIEW = "EVENT_HIDEVIEW";
$.shortcut("bi.etl_add_group_field_custom_group_combo", BI.CustomGroupCombo);