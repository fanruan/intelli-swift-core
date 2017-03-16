/**
 * Created by roy on 16/3/14.
 */
BI.DetailTablePathSettingPopup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePathSettingPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "detail-table-path-setting-popup",
        });
    },

    _init: function () {
        BI.DetailTablePathSettingPopup.superclass._init.apply(this, arguments);
        var o = this.options, self = this;


        this.popup = BI.createWidget({
            type: "bi.detail_table_common_table_set",
            element: this.element,
            items: o.items
        });

        this.popup.on(BI.DetailTableCommonTableSet.EVENT_CHANGE, function (value) {
            self.fireEvent(BI.DetailTablePathSettingPopup.EVENT_CHANGE, value);
        })
    },

    populate: function (items) {
        this.popup.populate(items);
    },

    getValue: function () {
        return this.popup.getValue();
    },

    setValue: function (v) {
        this.popup.setValue(v);
    }

});
BI.DetailTablePathSettingPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_table_path_setting_popup", BI.DetailTablePathSettingPopup);