/**
 * Cube日志错误信息Node
 *
 * Created by GUY on 2016/3/31.
 * @class BI.CubeLogNode
 * @extends BI.Widget
 */
BI.CubeLogNode = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CubeLogNode.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-log-wrong-info-node",
            text: "",
            second: 0,
            id: "",
            pId: "",
            open: false,
            level: 0
        });
    },

    _init: function () {
        BI.CubeLogNode.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.node = BI.createWidget({
            type: "bi.triangle_group_node",
            height: 40,
            // element: this.element,
            id: o.id,
            pId: o.pId,
            open: o.open,
            text: this._formatText(o.text, o.second),
            value: o.value
        });
        this.node.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: BI.createWidget(),
                width: o.level * 10
            }, {
                el: this.node
            }],
            height: 40
        });
    },

    _formatText: function (text, second) {
        return text + ": " + ((second >= 1000 ? Math.floor(second / 1000) : second) + (second >= 1000 ? "秒" : "毫秒"));
    },

    populate: function (items, keyword, context) {
        this.node.setText(this._formatText(context.el.text, context.el.second));
    },

    isSelected: function () {
        return this.node.isSelected();
    },

    setSelected: function (b) {
        return this.node.setSelected(b);
    },

    isOpened: function () {
        return this.node.isOpened();
    },

    setOpened: function (v) {
        this.node.setOpened(v);
    }
});
$.shortcut('bi.cube_log_node', BI.CubeLogNode);