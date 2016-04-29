/**
 * Created by roy on 16/3/14.
 */
BI.DetailTablePathSettingCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePathSettingCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "detail-table-setting-path-combo",
            items: []
        })
    },

    _init: function () {
        BI.DetailTablePathSettingCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.trigger = BI.createWidget({
            type: "bi.select_text_trigger",
            items: o.items,
            height: o.height
        });

        this.popup = BI.createWidget({
            type: "bi.detail_table_path_setting_popup",
            items: o.items
        });

        this.popup.on(BI.DetailTablePathSettingPopup.EVENT_CHANGE, function () {
            self.setValue(self.popup.getValue());
            self.combo.hideView();
            self.fireEvent(BI.DetailTablePathSettingCombo.EVENT_CHANGE, arguments);
        });

        this.combo = BI.createWidget({
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

    _createTriggerItems: function () {
        var o = this.options;
        var triggerItems = [];
        BI.each(o.items, function (i, item) {
            var triggerItem = {};
            triggerItem.value = item.value;
            triggerItem.text = item.text;
            triggerItems.push(triggerItem);
        });
        return triggerItems;

    },

    setValue: function (v) {
        this.combo.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue();
    },

    setEnable: function (v) {
        BI.DetailTablePathSettingCombo.setEnable.apply(this, arguments);
        this.combo.setEnable(v);
    },

    populate: function (items) {
        var self = this;
        this.options.items = items;
        this.trigger.populate(self._createTriggerItems());
        this.popup.populate(items);

    }
});
BI.DetailTablePathSettingCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_table_path_setting_combo", BI.DetailTablePathSettingCombo);