/**
 * Cube日志错误Item
 *
 * Created by GUY on 2016/4/1.
 * @class BI.CubeLogWrongInfoItem
 * @extends BI.Widget
 */
BI.CubeLogWrongInfoItem = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CubeLogWrongInfoItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-log-wrong-info-item",
            items: []
        });
    },

    _init: function () {
        BI.CubeLogWrongInfoItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.title = BI.createWidget({
            type: "bi.cube_log_wrong_info_item_title"
        });
        this.title.on(BI.CubeLogWrongInfoItemTitle.EVENT_CHANGE, function () {
            self.expander[this.isOpened() ? "showView" : "hideView"]();
        });
        this.expander = BI.createWidget({
            type: "bi.expander",
            element: this.element,
            trigger: "",
            el: this.title,
            popup: {
                type: "bi.cube_log_popup",
                items: this._formatItems(o.items)
            }
        });
        this.expander[this.title.isOpened() ? "showView" : "hideView"]();
    },

    _formatItems: function (items) {
        return BI.map(items, function (i, item) {
            return BI.extend({
                type: "bi.cube_log_item"
            }, item);
        });
    }
});
$.shortcut('bi.cube_log_wrong_info_item', BI.CubeLogWrongInfoItem);