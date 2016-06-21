/**
 * Cube日志Item
 *
 * Created by GUY on 2016/3/31.
 * @class BI.CubeLogItem
 * @extends BI.Widget
 */
BI.CubeLogItem = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CubeLogItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-log-item",
            text: "",
            level: 0
        });
    },

    _init: function () {
        BI.CubeLogItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.text = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            cls: "cube-log-item-text",
            hgap: 10,
            vgap: 5,
            text: o.text,
            value: o.value
        });
        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: BI.createWidget(),
                width: o.level * 10
            }, {
                type: "bi.vertical",
                vgap: 5,
                items: [this.text]
            }],
            height: 40
        });
    }
});
$.shortcut('bi.cube_log_item', BI.CubeLogItem);