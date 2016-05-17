/**
 * Created by GUY on 2016/4/18.
 * @class BI.DetailTablePopupSelectDataLevel1Node
 * @extends BI.AbstractDetailTablePopupSelectDataNode
 */
BI.DetailTablePopupSelectDataLevel1Node = BI.inherit(BI.AbstractDetailTablePopupSelectDataNode, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePopupSelectDataLevel1Node.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-detail-table-popup-select-data-level1-node"
        })
    },
    _init: function () {
        BI.DetailTablePopupSelectDataLevel1Node.superclass._init.apply(this, arguments);
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

$.shortcut("bi.detail_table_popup_select_data_level1_node", BI.DetailTablePopupSelectDataLevel1Node);