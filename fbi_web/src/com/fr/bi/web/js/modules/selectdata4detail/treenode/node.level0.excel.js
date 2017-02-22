/**
 * Created by zcf on 2017/1/18.
 */
BI.DetailDetailSelectDataLevel0ExcelNode = BI.inherit(BI.AbstractDetailDetailSelectDataNode, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailDetailSelectDataLevel0ExcelNode.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-detail-detail-select-data-level0-node"
        })
    },
    _init: function () {
        BI.DetailDetailSelectDataLevel0ExcelNode.superclass._init.apply(this, arguments);
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
            type: "bi.select_data_level0_node",
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

$.shortcut("bi.detail_detail_select_data_level0_excel_node", BI.DetailDetailSelectDataLevel0ExcelNode);