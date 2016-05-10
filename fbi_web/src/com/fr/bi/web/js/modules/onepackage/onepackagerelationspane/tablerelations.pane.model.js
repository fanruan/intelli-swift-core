/**
 * @class BI.PackageTableRelationsPaneModel
 * @extends FR.OB
 */
BI.PackageTableRelationsPaneModel = BI.inherit(FR.OB, {
    
    _init: function () {
        BI.PackageTableRelationsPaneModel.superclass._init.apply(this, arguments);
        this.tableIds = [];
        this.tableData = {};
        this.relations = {};
        this.translations = {};
        this.all_fields = {};
    },

    createItemsByTableIdsAndRelations: function () {
        var self = this;
        var items = [];
        BI.each(this.relations.connectionSet, function(idx, relation){
            var primaryId = relation.primaryKey.field_id, foreignId = relation.foreignKey.field_id;
            if(BI.contains(self.tableIds, BI.Utils.getTableIdByFieldId4Conf(primaryId)) || BI.contains(self.tableIds, BI.Utils.getTableIdByFieldId4Conf(foreignId))){
                items.push({
                    primary: {
                        region: self._getTableTranName(self.all_fields[primaryId].table_id),
                        value: primaryId,
                        text: self._getFieldTranName(primaryId)
                    },
                    foreign: {
                        region: self._getTableTranName(self.all_fields[foreignId].table_id),
                        value: foreignId,
                        text: self._getFieldTranName(foreignId)
                    }
                });
            }
        });
        return items;
    },

    _getTableTranName: function (tId) {
        var tableData = this.tableData[tId];
        var tableNameText = tableData.table_name;
        if (tableData.connection_name === BICst.CONNECTION.ETL_CONNECTION) {
            tableNameText = this.translations[tId];
        } else if (BI.isNotNull(this.translations[tId]) && this.translations[tId] !== tableNameText) {
            tableNameText = this.translations[tId] + "(" + tableNameText + ")";
        }
        return tableNameText;
    },

    _getFieldTranName: function (fieldId) {
        var fieldData = this.all_fields[fieldId];
        var fieldNameText = fieldData.field_name;
        var fId = fieldData.table_id + fieldNameText;
        if (BI.isNotNull(this.translations[fId]) && this.translations[fId] !== fieldNameText) {
            fieldNameText = this.translations[fId] + "(" + fieldNameText + ")";
        }
        return fieldNameText;
    },

    populate: function (items) {
        this.tableIds = BI.pluck(items.tableIds, "id");
        this.tableData = items.tableData;
        this.relations = items.relations;
        this.translations = items.translations;
        this.all_fields = items.all_fields;
    }

});