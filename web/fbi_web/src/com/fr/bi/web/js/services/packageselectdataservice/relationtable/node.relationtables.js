/**
 * create by young
 * 选择字段相关表node
 */
BI.SelectDataRelationTablesNode = BI.inherit(BI.NodeButton, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectDataRelationTablesNode.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-select-data-relation-table-node bi-list-item",
            id: "",
            pId: "",
            open: false,
            height: 25
        })
    },

    _init: function () {
        BI.SelectDataRelationTablesNode.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.text = BI.createWidget({
            type: "bi.label",
            cls: "relation-table-node",
            textAlign: "left",
            whiteSpace: "nowrap",
            textHeight: o.height,
            height: o.height,
            hgap: o.hgap,
            text: o.text,
            value: o.value,
            py: o.py
        });
        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                width: 23,
                el: BI.createWidget()
            }, {
                el: this.text
            }]
        })
    },

    doRedMark: function () {
        this.text.doRedMark.apply(this.text, arguments);
    },

    unRedMark: function () {
        this.text.unRedMark.apply(this.text, arguments);
    },

    doClick: function () {
        BI.SelectDataRelationTablesNode.superclass.doClick.apply(this, arguments);
    },

    setOpened: function (v) {
        BI.SelectDataRelationTablesNode.superclass.setOpened.apply(this, arguments);
    },

    setValue: function (items) {
        BI.SelectDataRelationTablesNode.superclass.setValue.apply(this, arguments);
    }
});
$.shortcut("bi.select_data_relation_tables_node", BI.SelectDataRelationTablesNode);