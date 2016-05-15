/**
 * Created by GUY on 2016/5/14.
 * @class BI.DetailTablePopupSelectDataLevel0Node
 * @extends BI.AbstractDetailTablePopupSelectDataNode
 */
BI.DetailTablePopupSelectDataLevel0Node = BI.inherit(BI.AbstractDetailTablePopupSelectDataNode, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePopupSelectDataLevel0Node.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-detail-table-popup-select-data-level0-node"
        })
    },
    _init: function () {
        BI.DetailTablePopupSelectDataLevel0Node.superclass._init.apply(this, arguments);
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
            value: o.value,
            title: o.title
        });
    }
});

$.shortcut("bi.detail_table_popup_select_data_level0_node", BI.DetailTablePopupSelectDataLevel0Node);