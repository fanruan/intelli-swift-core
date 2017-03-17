/**
 * @class BI.RelationTablesButton
 * @extend BI.Widget
 * 多个关联表按钮（设置表关联）
 */
BI.RelationTablesButton = BI.inherit(BI.BasicButton, {
    _defaultConfig: function(){
        var conf = BI.RelationTablesButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-relation-tables-button"
        })
    },

    _init: function(){
        BI.RelationTablesButton.superclass._init.apply(this, arguments);
        var o = this.options;
        var fieldId = o.fieldId, items = [];
        BI.each(BI.Utils.getRelationFieldsByFieldId4Conf(fieldId), function(i, id) {
            var tableId = BI.Utils.getTableIdByFieldId4Conf(id);
            items.push({
                text: BI.Utils.getTransNameById4Conf(tableId)
            });
        });
        if(BI.isEmptyArray(items)) {
            items.push({text: " "});
        }

        BI.createWidget({
            type: "bi.center_adapt",
            element: this.element,
            items: [{
                type: "bi.vertical",
                items: BI.createItems( items, {
                    type: "bi.label",
                    height: 25,
                    whiteSpace: "normal",
                    textAlign: "left",
                    hgap: 2
                })
            }]
        });
    },

    doClick: function(){
        BI.RelationTablesButton.superclass.doClick.apply(this, arguments);
        if(this.isValid()) {
            this.fireEvent(BI.RelationTablesButton.EVENT_CHANGE);
        }
    }
});
BI.RelationTablesButton.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.relation_tables_button", BI.RelationTablesButton);