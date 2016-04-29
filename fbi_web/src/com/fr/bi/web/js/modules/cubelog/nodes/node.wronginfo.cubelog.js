/**
 * Cube日志错误信息Node
 *
 * Created by GUY on 2016/3/31.
 * @class BI.CubeLogWrongInfoNode
 * @extends BI.Widget
 */
BI.CubeLogWrongInfoNode = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CubeLogWrongInfoNode.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-log-wrong-info-node",
            text: "报错信息:",
            count: 2,
            id: "",
            pId: "",
            open: false
        });
    },

    _init: function () {
        BI.CubeLogWrongInfoNode.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.node = BI.createWidget({
            type: "bi.triangle_group_node",
            height: 40,
            element: this.element,
            id: o.id,
            pId: o.pId,
            open: o.open,
            text: o.text + "共" + o.count + "个",
            value: o.value,
            keyword: o.count + ""
        });
        this.node.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
    },

    populate: function () {

    },

    isOpened: function () {
        return this.node.isOpened();
    },

    setOpened: function (v) {
        this.node.setOpened(v);
    }
});
$.shortcut('bi.cube_log_wrong_info_node', BI.CubeLogWrongInfoNode);