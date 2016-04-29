/**
 * cube日志popup
 *
 * Created by GUY on 2016/3/31.
 * @class BI.CubeLogPopup
 * @extends BI.Widget
 */
BI.CubeLogPopup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CubeLogPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-log-popup",
            items: []
        });
    },

    _init: function () {
        BI.CubeLogPopup.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.popup = BI.createWidget({
            type: "bi.button_map",
            element: this.element,
            items: this._formatItems(o.items),
            layouts: [{
                type: "bi.vertical"
            }]
        })
    },

    _formatItems: function (items) {
        return BI.map(items, function (i, item) {
            return BI.extend({
                type: "bi.cube_log_item"
            }, item);
        });
    },

    doBehavior: function () {
        this.popup.doBehavior.apply(this.popup, arguments);
    },

    populate: function (items) {
        this.popup.populate(this._formatItems(items));
    },

    setValue: function (v) {
        this.popup.setValue(v);
    },

    getValue: function () {
        return this.popup.getValue();
    },

    empty: function () {
        this.popup.empty();
    }
});
BI.CubeLogPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.cube_log_popup", BI.CubeLogPopup);