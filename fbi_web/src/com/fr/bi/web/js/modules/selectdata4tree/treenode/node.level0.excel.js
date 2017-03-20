/**
 * Created by zcf on 2017/1/6.
 */
BI.TreeSelectDataLevel0ExcelNode = BI.inherit(BI.AbstractTreeSelectDataNode, {
    _defaultConfig: function () {
        return BI.extend(BI.TreeSelectDataLevel0ExcelNode.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-tree-detail-select-data-level0-node"
        })
    },

    _init: function () {
        BI.TreeSelectDataLevel0ExcelNode.superclass._init.apply(this, arguments);

        var self = this, o = this.options;
        var setExcelButton = BI.createWidget({
            type: "bi.update_excel_combo",
            tableId: o.id,
            height: o.height,
            width: 90
        });

        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: this.node,
                width: "fill"
            }, {
                el: setExcelButton,
                width: 90
            }]
        });
    },

    _createNode: function () {
        var o = this.options;
        return BI.createWidget({
            type: "bi.select_data_level_node",
            id: o.id,
            pId: o.pId,
            open: o.open,
            text: o.text,
            value: o.value,
            title: o.title,
            warningTitle: o.warningTitle
        });
    }
});
$.shortcut("bi.tree_select_data_level0_excel_node", BI.TreeSelectDataLevel0ExcelNode);