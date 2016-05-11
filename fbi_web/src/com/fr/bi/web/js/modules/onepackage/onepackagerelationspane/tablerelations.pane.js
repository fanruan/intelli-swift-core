/**
 * @class BI.PackageTableRelationsPane
 * @extend BI.Widget
 * 单个业务包界面所有表关联
 */
BI.PackageTableRelationsPane = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.PackageTableRelationsPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-package-table-relations-pane"
        })
    },

    _init: function(){
        BI.PackageTableRelationsPane.superclass._init.apply(this, arguments);
        this.model = new BI.PackageTableRelationsPaneModel({
        });
        var self = this;
        this.relationView = BI.createWidget({
            type: "bi.relation_view"
        });
        this.relationView.on(BI.RelationView.EVENT_CHANGE, function(v){
            self.fireEvent(BI.PackageTableRelationsPane.EVENT_CLICK_TABLE, v);
        });
        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [this.relationView]
        })
    },

    _createItemsByTableIdsAndRelations: function () {
        var self = this;
        var items = [];
        var tableIds = this.model.getTableIds();
        var norelationTables = tableIds;
        var all_fields = this.model.getAllFields();
        var regionHandler = function(){
            self.fireEvent(BI.PackageTableRelationsPane.EVENT_CLICK_TABLE, this.options.value);
        };
        BI.each(this.model.getRelations().connectionSet, function(idx, relation){
            var primaryId = relation.primaryKey.field_id, foreignId = relation.foreignKey.field_id;
            if(BI.isNull(all_fields[primaryId]) || BI.isNull(all_fields[foreignId])){
                return;
            }
            if(BI.contains(tableIds, all_fields[primaryId].table_id) || BI.contains(tableIds, all_fields[foreignId].table_id)){
                norelationTables.remove(all_fields[primaryId].table_id);
                norelationTables.remove(all_fields[foreignId].table_id);
                items.push({
                    primary: {
                        region: all_fields[primaryId].table_id,
                        regionText: self.model.getTableTranName(all_fields[primaryId].table_id),
                        value: primaryId,
                        text: self.model.getFieldTranName(primaryId),
                        regionHandler: regionHandler
                    },
                    foreign: {
                        region: all_fields[foreignId].table_id,
                        regionText: self.model.getTableTranName(all_fields[foreignId].table_id),
                        value: foreignId,
                        text: self.model.getFieldTranName(foreignId),
                        regionHandler: regionHandler
                    }
                });
            }
        });
        BI.each(norelationTables, function(idx, tId){
            items.push({

            });
        });
        return items;
    },

    populate: function(items){
        var self = this, o = this.options;
        this.model.populate(items);
        this.model.getTableNamesOfAllPackages(function(){
            self.relationView.populate(self._createItemsByTableIdsAndRelations());
        });
    },

    getValue: function(){

    },

    setValue: function(){

    }
});
BI.PackageTableRelationsPane.EVENT_CLICK_TABLE = "EVENT_CLICK_TABLE";
$.shortcut("bi.package_table_relations_pane", BI.PackageTableRelationsPane);