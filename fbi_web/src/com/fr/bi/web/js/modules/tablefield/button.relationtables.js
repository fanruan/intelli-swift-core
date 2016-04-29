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
        var relationTables = o.relation_tables, items = [];
        BI.each(relationTables, function(i, name) {
            items.push({
                text: name
            })
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
                    height: 30,
                    whiteSpace: "normal",
                    textAlign: "left"
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