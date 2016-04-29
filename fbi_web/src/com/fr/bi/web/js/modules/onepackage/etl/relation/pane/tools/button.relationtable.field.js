/**
 * @class BI.RelationTableFieldButton
 * @extend BI.Widget
 * 表字段设置弹出面板中的表和字段的组合按钮
 */
BI.RelationTableFieldButton = BI.inherit(BI.BasicButton, {
    _defaultConfig: function(){
        var conf = BI.RelationTableFieldButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-relation-table-field-button"
        })
    },

    _init: function(){
        BI.RelationTableFieldButton.superclass._init.apply(this, arguments);
        var o = this.options;
        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                type: "bi.label",
                text: o.table_name,
                cls: "relation-table-name",
                textHeight: 30,
                whiteSpace: "normal"
            }, {
                type: "bi.label",
                text: o.field_name,
                textHeight: 30,
                whiteSpace: "normal"
            }],
            width: 140
        })
    }
});
$.shortcut("bi.relation_table_field_button", BI.RelationTableFieldButton);