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

    /**
     * 以当前业务包的tableId为key构造两个map
     * 一个map存table中对应作为关联主键的field_id
     * 一个map存table中对应作为关联外键的field_id
     * @private
     */
    _createFieldsMap: function(){
        var self = this;
        this.foreignFieldsMap = {};
        this.primaryFieldsMap = {};
        this.degrees = {};
        BI.each(this.relations.connectionSet, function(idx, obj){
            var pId = obj.primaryKey.table_id, fId = obj.foreignKey.table_id;
            if(BI.contains(self.tableIds, pId)){
                if(!BI.has(self.primaryFieldsMap, pId)){
                    self.primaryFieldsMap[pId] = [];
                }
                self.primaryFieldsMap[pId].push(obj.primaryKey.field_id);
            }
            if(BI.contains(self.tableIds, fId)){
                if(!BI.has(self.foreignFieldsMap, fId)){
                    self.foreignFieldsMap[fId] = [];
                }
                self.foreignFieldsMap[fId].push(obj.foreignKey.field_id);
            }
            //顺便计算一下入度
            if (!BI.has(self.degrees, fId)) {
                self.degrees[fId] = 0;
            }
            self.degrees[fId]++;
        });
        //去重
        BI.each(self.primaryFieldsMap, function(tId, arr){
            self.primaryFieldsMap[tId] = BI.uniq(arr);
        });
        BI.each(self.foreignFieldsMap, function(tId, arr){
            self.foreignFieldsMap[tId] = BI.uniq(arr);
        });
    },

    _getAllRelationTablesByTables: function (tableIds, resultTables) {
        var self = this;
        var relations = this.getRelations();
        var primKeyMap = relations.primKeyMap;
        var foreignKeyMap = relations.foreignKeyMap;
        BI.each(tableIds, function (idx, tableId) {
            if (BI.contains(resultTables, tableId)) {
                return;
            }
            resultTables.push(tableId);
            var primFields = self.primaryFieldsMap[tableId];
            var foreFields = self.foreignFieldsMap[tableId];
            BI.each(BI.concat(primFields, foreFields), function (id, fieldId) {
                var rels = [];
                if (BI.has(primKeyMap, fieldId)) {
                    rels = primKeyMap[fieldId];
                    BI.each(rels, function (i, rel) {
                        var tId = rel.foreignKey.table_id;
                        if (!BI.contains(resultTables, tId)) {
                            self._getAllRelationTablesByTables([tId], resultTables);
                        }
                    })
                }
                if (BI.has(foreignKeyMap, fieldId)) {
                    rels = foreignKeyMap[fieldId];
                    BI.each(rels, function (i, rel) {
                        var tId = rel.primaryKey.table_id;
                        if (!BI.contains(resultTables, tId)) {
                            self._getAllRelationTablesByTables([tId], resultTables);
                        }
                    })
                }
            });
        });
    },

    getRelationsByPrimaryId: function (tId) {
        var rel = [];
        var relations = this.getRelations();
        var primKeyMap = relations.primKeyMap;
        var primFields = this.primaryFieldsMap[tId];
        BI.each(primFields, function (idx, fieldId) {
            rel = BI.concat(rel, primKeyMap[fieldId]);
        });
        return rel;
    },

    createItems: function (regionHandler) {
        var self = this;
        this.cacheItems = [];
        var tableIds = this.getTableIds();
        this._createFieldsMap();
        var relations = this.relations;
        var connectSet = relations.connectionSet;
        var calcDegree = {};
        var distinctTableIds = [];
        var relationTableSet = [];
        this._getAllRelationTablesByTables(tableIds, relationTableSet);
        BI.each(relationTableSet, function (idx, tId) {
            calcDegree[tId] = 0;
        });
        BI.each(relationTableSet, function (idx, tId) {
            if (BI.contains(distinctTableIds, tId)) {
                return;
            }
            var primFields = self.primaryFieldsMap[tId] || [];
            var foreFields = self.foreignFieldsMap[tId] || [];
            //其他业务包的表
            if (!BI.contains(self.tableIds, tId)) {
                BI.each(connectSet, function (idx, obj) {
                    var primaryId = obj.primaryKey.field_id;
                    var foreignId = obj.foreignKey.field_id;
                    var primTableId = obj.primaryKey.table_id;
                    var foreignTableId = obj.foreignKey.table_id;
                    if (tId === primTableId && BI.contains(tableIds, foreignTableId)) {
                        var pTTranName = self.getKeyTableTranName(obj.primaryKey);
                        var pFTranName = self.getKeyFieldTranName(obj.primaryKey);
                        var fTTranName = self.getKeyTableTranName(obj.foreignKey);
                        var fFTranName = self.getKeyFieldTranName(obj.foreignKey);
                        self.cacheItems.push({
                            primary: {
                                region: tId,
                                regionText: pTTranName,
                                regionTitle: pTTranName,
                                value: primaryId,
                                text: pFTranName,
                                title: pFTranName,
                                belongPackage: self.isCurrentPackageTable(tId),
                                isPrimary: true
                            },
                            foreign: {
                                region: foreignTableId,
                                regionText: fTTranName,
                                regionTitle: fTTranName,
                                value: foreignId,
                                text: fFTranName,
                                title: fFTranName,
                                regionHandler: regionHandler,
                                belongPackage: self.isCurrentPackageTable(foreignTableId),
                                isPrimary: false
                            }
                        });
                    }
                });
                return;
            }
            if (BI.isEmptyArray(primFields) && BI.isEmptyArray(foreFields)) {
                self.cacheItems.push({
                    primary: {
                        region: tId,
                        regionText: self.getTableTranName(tId),
                        regionTitle: self.getTableTranName(tId),
                        regionHandler: regionHandler,
                        belongPackage: self.isCurrentPackageTable(tId)
                    }
                });
            } else {
                self.cacheItems = BI.concat(self.cacheItems, getViewItemsByTableId(tId, []));
            }
            distinctTableIds.push(tId);
        });
        return self.cacheItems;

        function getViewItemsByTableId(tId, visitSet) {
            var rels = self.getRelationsByPrimaryId(tId);
            var items = [];
            BI.each(rels, function (idx, rel) {
                var primaryId = rel.primaryKey.field_id, foreignId = rel.foreignKey.field_id;
                var foreignTableId = rel.foreignKey.table_id;
                var primaryTableId = rel.primaryKey.table_id;
                //是未访问过的节点且入度未满
                if (!BI.contains(visitSet, foreignTableId) && !BI.contains(distinctTableIds, tId) && calcDegree[foreignTableId] !== self.degrees[foreignTableId]) {
                    var pTTranName = self.getKeyTableTranName(rel.primaryKey);
                    var pFTranName = self.getKeyFieldTranName(rel.primaryKey);
                    var fTTranName = self.getKeyTableTranName(rel.foreignKey);
                    var fFTranName = self.getKeyFieldTranName(rel.foreignKey);
                    //自循环
                    if (primaryTableId === foreignTableId) {
                        items.push({
                            primary: {
                                region: primaryTableId,
                                regionText: pTTranName,
                                regionTitle: pTTranName,
                                value: primaryId,
                                text: pFTranName,
                                title: pFTranName,
                                regionHandler: regionHandler,
                                belongPackage: self.isCurrentPackageTable(primaryTableId),
                                isPrimary: true
                            },
                            foreign: {
                                region: BI.UUID(),
                                regionText: fTTranName,
                                regionTitle: fTTranName,
                                value: foreignId,
                                text: fFTranName,
                                title: fFTranName,
                                isPrimary: false
                            }
                        });
                    } else {
                        var primaryItem = {
                            region: primaryTableId,
                            regionText: pTTranName,
                            regionTitle: pTTranName,
                            value: primaryId,
                            text: pFTranName,
                            title: pFTranName,
                            regionHandler: regionHandler,
                            belongPackage: self.isCurrentPackageTable(primaryTableId),
                            isPrimary: true
                        };
                        var foreignItem = {
                            region: foreignTableId,
                            regionText: fTTranName,
                            regionTitle: fTTranName,
                            value: foreignId,
                            text: fFTranName,
                            title: fFTranName,
                            regionHandler: regionHandler,
                            belongPackage: self.isCurrentPackageTable(foreignTableId),
                            isPrimary: false
                        };
                        if (!BI.contains(self.tableIds, foreignTableId)) {
                            delete foreignItem.regionHandler;
                        }
                        items.push({
                            primary: primaryItem,
                            foreign: foreignItem
                        });
                        var visittable = BI.concat(visitSet, [tId]);
                        if (!BI.contains(visittable, foreignTableId) && calcDegree[foreignTableId] !== self.degrees[foreignTableId]) {
                            calcDegree[foreignTableId]++;
                            items = BI.concat(items, getViewItemsByTableId(foreignTableId, visittable));
                            distinctTableIds.pushDistinct(foreignTableId);
                        }
                    }
                }
            });
            return items;
        }
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