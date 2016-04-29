/**
 * Created by GUY on 2016/4/18.
 * @class BI.DetailDetailSelectDataLevel0Node
 * @extends BI.AbstractDetailDetailSelectDataNode
 */
BI.DetailDetailSelectDataLevel0Node = BI.inherit(BI.AbstractDetailDetailSelectDataNode, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailDetailSelectDataLevel0Node.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-detail-detail-select-data-level0-node"
        })
    },
    _init: function () {
        BI.DetailDetailSelectDataLevel0Node.superclass._init.apply(this, arguments);
    },

    _createNode: function () {
        var o = this.options;
        return BI.createWidget({
            type: "bi.select_data_level0_node",
            element: this.element,
            id: o.id,
            pId: o.pId,
            open: o.open,
            text: o.text,
            value: o.value
        });
    }
});

$.shortcut("bi.detail_detail_select_data_level0_node", BI.DetailDetailSelectDataLevel0Node);