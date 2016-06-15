/**
 * Created by GUY on 2016/6/15.
 * @class BI.TreeSelectDataLevel1Node
 * @extends BI.AbstractTreeSelectDataNode
 */
BI.TreeSelectDataLevel1Node = BI.inherit(BI.AbstractTreeSelectDataNode, {
    _defaultConfig: function () {
        return BI.extend(BI.TreeSelectDataLevel1Node.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-tree-detail-select-data-level1-node"
        })
    },
    _init: function () {
        BI.TreeSelectDataLevel1Node.superclass._init.apply(this, arguments);
    },

    _createNode: function () {
        var o = this.options;
        return BI.createWidget({
            type: "bi.select_data_level1_node",
            element: this.element,
            id: o.id,
            pId: o.pId,
            open: o.open,
            text: o.text,
            value: o.value,
            title: o.title
        });
    }
});

$.shortcut("bi.tree_select_data_level1_node", BI.TreeSelectDataLevel1Node);