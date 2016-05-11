/**
 * @class BI.PackageTableRelationsPaneModel
 * @extends FR.OB
 */
BI.PackageTableRelationsPaneModel = BI.inherit(FR.OB, {
    
    _init: function () {
        BI.PackageTableRelationsPaneModel.superclass._init.apply(this, arguments);
        this.tableIds = [];
        this.originalTableNames = {};
        this.tableData = {};
        this.relations = {};
        this.translations = {};
        this.all_fields = {};
    },

    getRelations: function(){
        return this.relations;
    },

    getAllFields: function(){
        return this.all_fields;
    },

    getTableIds: function(){
        return this.tableIds;
    },

    getTableTranName: function (tId) {
        var tableNameText = "";
        if (BI.isNull(this.originalTableNames[tId])) {
            tableNameText = this.translations[tId];
        } else if (BI.isNotNull(this.translations[tId]) && this.translations[tId] !== tableNameText) {
            tableNameText = this.translations[tId] + "(" + this.originalTableNames[tId] + ")";
        }
        return tableNameText;
    },

    getFieldTranName: function (fieldId) {
        var fieldData = this.all_fields[fieldId];
        var fieldNameText = fieldData.field_name;
        var fId = fieldData.table_id + fieldNameText;
        if (BI.isNotNull(this.translations[fId]) && this.translations[fId] !== fieldNameText) {
            fieldNameText = this.translations[fId] + "(" + fieldNameText + ")";
        }
        return fieldNameText;
    },

    getTableNamesOfAllPackages: function(callback){
        var self = this;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.getTableNamesOfAllPackages(function(res){
            self.originalTableNames = res;
            callback();
            mask.destroy();
        });
    },

    populate: function (items) {
        this.tableIds = BI.pluck(items.tableIds, "id");
        this.tableData = items.tableData;
        this.relations = items.relations;
        this.translations = items.translations;
        this.all_fields = items.all_fields;
    }

});