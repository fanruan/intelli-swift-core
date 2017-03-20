/**
 * @class BI.PackageTableRelationsPaneModel
 * @extends FR.OB
 */
BI.PackageTableRelationsPaneModel = BI.inherit(FR.OB, {

    _init: function () {
        BI.PackageTableRelationsPaneModel.superclass._init.apply(this, arguments);
        this.tableIds = [];
        this.tablesData = {};
        this.relations = {};
    },

    initData: function (callback) {
        var self = this;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Basic_Loading")
        });
        BI.Utils.getRelationsDetail4Conf(function (relations) {
            self.relations = relations;
            callback();
        }, function () {
            mask.destroy();
        });
    },

    getRelations: function () {
        return this.relations;
    },

    getTableIds: function () {
        return this.tableIds;
    },

    isCurrentPackageTable: function (tableId) {
        return this.tableIds.contains(tableId);
    },

    getFieldsByTableId: function (id) {
        return BI.pluck(this.tablesData[id].fields[0], "id");
    },

    getFieldsMap: function () {
        return this.table_map;
    },

    getTableTranName: function (tId) {
        var tableNameText = "";
        var table = this.tablesData[tId];
        if (BI.isNull(table)) {
            return tableNameText;
        }
        var transName = table[BICst.JSON_KEYS.TRAN_NAME];
        var tableName = table[BICst.JSON_KEYS.TABLE_NAME];
        if (BI.isNull(tableName) || transName === tableName) {
            tableNameText = transName;
        } else if (BI.isNotNull(transName)) {
            tableNameText = transName + "(" + tableName + ")";
        }
        return tableNameText;
    },

    getKeyTableTranName: function (key) {
        var tableName = key[BICst.JSON_KEYS.TABLE_NAME];
        var tranName = key[BICst.JSON_KEYS.TABLE_TRAN_NAME];
        if (BI.isNotNull(tranName) && tableName !== tranName) {
            tableName = tranName + "(" + tableName + ")";
        }
        return tableName;
    },

    getKeyFieldTranName: function (key) {
        var fieldName = key[BICst.JSON_KEYS.FIELD_NAME];
        var tranName = key[BICst.JSON_KEYS.FIELD_TRAN_NAME];
        if (BI.isNotNull(tranName) && tranName !== fieldName) {
            fieldName = tranName + "(" + fieldName + ")";
        }
        return fieldName;
    },

    getTableNamesOfAllPackages: function (callback) {
        var self = this;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Basic_Loading")
        });
        BI.Utils.getTableNamesOfAllPackages(function (res) {
            self.originalTableNames = res;
            callback();
        }, function () {
            mask.destroy();
        });
    },

    getRelationTablesByTableId: function (tableId) {
        var primKeyMap = this.relations.primKeyMap, foreignKeyMap = this.relations.foreignKeyMap;
        var relationTables = [];

        function dealWithPFKeys(keyMap) {
            BI.each(keyMap, function (fieldId, maps) {
                BI.each(maps, function (i, map) {
                    var pKey = map.primaryKey, fKey = map.foreignKey;
                    var pTId = pKey.table_id, fTId = fKey.table_id;
                    if (pTId === tableId) {
                        relationTables.push(fTId);
                    }
                    if (fTId === tableId) {
                        relationTables.push(pTId);
                    }
                });
            });
        }

        dealWithPFKeys(primKeyMap);
        dealWithPFKeys(foreignKeyMap);
        return BI.uniq(relationTables);
    },

    createItems: function (regionHandler) {
        var self = this;
        this.cacheItems = [];
        var connectSet = this.relations.connectionSet;

        var relationTables = [];
        var relatedTables = [];
        BI.each(this.tableIds, function (i, tId) {
            relationTables = relationTables.concat(self.getRelationTablesByTableId(tId));
        });
        relationTables = relationTables.concat(this.tableIds);
        relationTables = BI.uniq(relationTables);

        BI.each(connectSet, function (i, conn) {
            var primaryKey = conn.primaryKey, foreignKey = conn.foreignKey;
            var primaryId = primaryKey.field_id, foreignId = foreignKey.field_id;
            var primTableId = primaryKey.table_id, foreignTableId = foreignKey.table_id;

            BI.each(relationTables, function (j, tId) {
                if (tId === primTableId) {
                    relatedTables.push(tId);
                    self.cacheItems.push({
                        primary: {
                            region: tId,
                            regionText: self.getKeyTableTranName(primaryKey),
                            regionTitle: self.getKeyTableTranName(primaryKey),
                            value: primaryId,
                            text: self.getKeyFieldTranName(primaryKey),
                            title: self.getKeyFieldTranName(primaryKey),
                            belongPackage: self.isCurrentPackageTable(tId),
                            isPrimary: true
                        },
                        foreign: {
                            region: foreignTableId,
                            regionText: self.getKeyTableTranName(foreignKey),
                            regionTitle: self.getKeyTableTranName(foreignKey),
                            value: foreignId,
                            text: self.getKeyFieldTranName(foreignKey),
                            title: self.getKeyFieldTranName(foreignKey),
                            regionHandler: regionHandler,
                            belongPackage: self.isCurrentPackageTable(foreignTableId),
                            isPrimary: false
                        }
                    });
                }
            });
        });

        BI.each(relationTables, function (i, tId) {
            if (!relatedTables.contains(tId)) {
                self.cacheItems.push({
                    primary: {
                        region: tId,
                        regionText: self.getTableTranName(tId),
                        regionTitle: self.getTableTranName(tId),
                        regionHandler: regionHandler,
                        belongPackage: self.isCurrentPackageTable(tId)
                    }
                })
            }
        });
    },

    getCacheItems: function () {
        return this.cacheItems;
    },

    populate: function (items) {
        var self = this;
        this.tableIds = BI.keys(items.tablesData);
        this.tablesData = items.tablesData;
        this.table_map = {};
        BI.each(this.tableIds, function (idx, tId) {
            self.table_map[tId] = BI.pluck(BI.Utils.getFieldsByTableId4Conf(tId), "id");
        })
    }

});