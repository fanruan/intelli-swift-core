/**
 * Created by GUY on 2016/4/18.
 * @class BI.DetailDetailSelectDataLevel1Node
 * @extends BI.AbstractDetailDetailSelectDataNode
 */
BI.DetailDetailSelectDataLevel1Node = BI.inherit(BI.AbstractDetailDetailSelectDataNode, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailDetailSelectDataLevel1Node.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-detail-detail-select-data-level1-node"
        })
    },
    _init: function () {
        BI.DetailDetailSelectDataLevel1Node.superclass._init.apply(this, arguments);
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

$.shortcut("bi.detail_detail_select_data_level1_node", BI.DetailDetailSelectDataLevel1Node);