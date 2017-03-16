/**
 * @class BI.PackageTableRelationsPaneModel
 * @extends FR.OB
 */
BI.PackageTableRelationsPaneModel = BI.inherit(FR.OB, {
    
    _init: function () {
        BI.PackageTableRelationsPaneModel.superclass._init.apply(this, arguments);
        this.tableIds = [];
        this.originalTableNames = {};
        this.tablesData = {};
        this.relations = {};
    },

    getRelations: function(){
        return Data.SharingPool.cat("relations");
    },

    getTableIds: function(){
        return this.tableIds;
    },

    getFieldsByTableId: function(id){
        return BI.pluck(this.tablesData[id].fields[0], "id");
    },

    getFieldsMap: function(){
        return this.table_map;
    },

    getTableTranName: function (tId) {
        var tableNameText = "";
        var transName = BI.Utils.getTransNameById4Conf(tId);
        if (BI.isNull(this.originalTableNames[tId]) || transName === this.originalTableNames[tId]) {
            tableNameText = transName;
        } else if (BI.isNotNull(transName)) {
            tableNameText = transName + "(" + this.originalTableNames[tId] + ")";
        }
        return tableNameText;
    },

    getFieldTranName: function (fieldId) {
        var fieldNameText = BI.Utils.getFieldNameById4Conf(fieldId);
        var transName = BI.Utils.getTransNameById4Conf(fieldId);
        if (BI.isNotNull(transName) && transName !== fieldNameText) {
            fieldNameText = transName + "(" + fieldNameText + ")";
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
        }, function() {
            mask.destroy();
        });
    },

    populate: function (items) {
        var self = this;
        this.tableIds = BI.keys(items.tablesData);
        this.tablesData = items.tablesData;
        this.table_map = {};
        BI.each(this.tableIds, function(idx, tId){
            self.table_map[tId] = BI.pluck(BI.Utils.getFieldsByTableId4Conf(tId), "id");
        })
    }

});