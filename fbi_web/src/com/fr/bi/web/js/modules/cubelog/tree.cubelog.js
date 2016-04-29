/**
 * Cube日志
 *
 * Created by GUY on 2016/3/31.
 * @class BI.CubeLogTree
 * @extends BI.Widget
 */
BI.CubeLogTree = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CubeLogTree.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-log-tree",
            items: []
        });
    },

    _init: function () {
        BI.CubeLogTree.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.tree = BI.createWidget({
            type: "bi.custom_tree",
            isDefaultInit: true,
            expander: {
                type: "bi.cube_log_expander",
                el: {
                    type: "bi.cube_log_node"
                },
                popup: {
                    type: "bi.custom_tree"
                }
            },
            items: o.items,
            el: {
                type: "bi.cube_log_popup"
            }
        });
        this.tree.on(BI.CustomTree.EVENT_CHANGE, function () {

        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            hgap: 10,
            items: [this.tree]
        })
    },

    populate: function (items) {
        this.tree.populate(items);
    }
});
$.shortcut('bi.cube_log_tree', BI.CubeLogTree);