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

    getFieldsByTableId: function(id){
        return BI.pluck(this.tableData[id].fields[0], "id");
    },

    getFieldsMap: function(){
        return this.table_map;
    },

    getTableTranName: function (tId) {
        var tableNameText = "";
        if (BI.isNull(this.originalTableNames[tId]) || this.translations[tId] === this.originalTableNames[tId]) {
            tableNameText = this.translations[tId];
        } else if (BI.isNotNull(this.translations[tId])) {
            tableNameText = this.translations[tId] + "(" + this.originalTableNames[tId] + ")";
        }
        return tableNameText;
    },

    getFieldTranName: function (fieldId) {
        var fieldData = this.all_fields[fieldId];
        var fieldNameText = fieldData.field_name;
        if (BI.isNotNull(this.translations[fieldId]) && this.translations[fieldId] !== fieldNameText) {
            fieldNameText = this.translations[fieldId] + "(" + fieldNameText + ")";
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
        var self = this;
        this.tableIds = BI.pluck(items.tableIds, "id");
        this.tableData = items.tableData;
        this.relations = items.relations;
        this.translations = items.translations;
        this.all_fields = items.all_fields;
        this.table_map = {};
        BI.each(this.tableIds, function(idx, tId){
            self.table_map[tId] = BI.pluck(self.tableData[tId].fields[0], "id");
        })
    }

});