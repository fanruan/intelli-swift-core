/**
 * Created by Young's on 2016/3/11.
 * one package的数据
 */
BI.OnePackageModel = BI.inherit(FR.OB, {

    constants: {
        addNewTableItems: [
            {
                text: BI.i18nText("BI-Database") + "/" + BI.i18nText("BI-Package"),
                value: BICst.ADD_NEW_TABLE.DATABASE_OR_PACKAGE
            }, {
                text: "ETL",
                value: BICst.ADD_NEW_TABLE.ETL
            }, {
                text: "SQL",
                value: BICst.ADD_NEW_TABLE.SQL
            }, {
                text: "EXCEL",
                value: BICst.ADD_NEW_TABLE.EXCEL
            }
        ],
        viewType: [{
            title: BI.i18nText("BI-Simple_View"),
            cls: "tables-tile-view-font",
            value: BICst.TABLES_VIEW.TILE
        }, {
            title: BI.i18nText("BI-Associate_View"),
            cls: "tables-relation-view-font",
            value: BICst.TABLES_VIEW.RELATION
        }]
    },

    _init: function () {
        BI.OnePackageModel.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.id = o.id;
        this.gid = o.gid;
    },

    initData: function (callback) {
        var self = this;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });

        BI.Utils.getSimpleTablesByPackId({
            id: this.id,
            groupName: BI.Utils.getGroupNameById4Conf(this.gid),
            packageName: BI.Utils.getPackageNameByID4Conf(this.id)
        }, function (tables) {
            if (BI.isNotNull(tables)) {
                self.tables = tables;
            }
            callback();
        }, function () {
            mask.destroy();
        });
    },

    getId: function () {
        return this.id;
    },

    getName: function () {
        return BI.Utils.getPackageNameByID4Conf(this.id);
    },

    setName: function (name) {
        this.name = name;
    },

    getGroupId: function () {
        return this.gid;
    },

    getGroupName: function () {
        return BI.Utils.getConfGroupNameByGroupId(this.gid);
    },

    getTables: function () {
        return BI.deepClone(this.tables);
    },

    getSortedTables: function () {
        var self = this;
        var tables = BI.Utils.getTablesIdByPackageId4Conf(this.id);
        var sortedTables = [];
        BI.each(tables, function (i, tId) {
            sortedTables.push(self.tables[tId]);
        });
        return sortedTables;
    },

    getTableByTableId: function (tableId) {
        return this.tables[tableId];
    },

    getNewTableItems: function () {
        return this.constants.addNewTableItems;
    },

    getViewType: function () {
        return this.constants.viewType;
    },

    getTableIdByFieldId: function (fieldId) {
        var field = this.allFields[fieldId];
        if (BI.isNotNull(field)) {
            return field.table_id;
        }
    },

    checkPackageName: function (name) {
        var self = this;
        var packages = Data.SharingPool.get("packages");
        var isValid = true;
        BI.some(packages, function (id, pack) {
            if (name === pack.name && id !== self.getId()) {
                isValid = false;
                return true;
            }
        });
        return isValid;
    },

    getTableTranName: function (table) {
        var tableName = table.table_name, tranName = BI.Utils.getTransNameById4Conf(table.id);
        //ETL 表
        if (table.connection_name === BICst.CONNECTION.ETL_CONNECTION) {
            return tranName;
        } else if (tableName !== tranName) {
            return tranName + "(" + tableName + ")";
        }
        return tableName;
    },

    changeTableInfo: function (id, data) {
        this.relations = data.relations;
        this.translations = data.translations;
        this.allFields = data.all_fields;
        this.excelViews[id] = data.excel_view;
        this.updateSettings = data.update_settings;
        //可能是新添加的
        if (BI.isNull(this.tablesData[id])) {
            this.tables.push({id: id});
        }
        this.tablesData[id] = data;
        this._syncSharedPackages();
    },

    removeTable: function (tableId, callback) {
        var self = this;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.removeTableById4Conf({
            id: tableId,
            packageId: self.id
        }, function () {
            delete self.tables[tableId];
            var tableIds = BI.Utils.getTablesIdByPackageId4Conf(self.id);
            BI.remove(tableIds, function (i, id) {
                return id === tableId;
            });
            BI.Utils.updateTableIdsInPackage4Conf(self.id, tableIds);
            BI.Utils.removeRelationByTableId4Conf(tableId);
            callback();
        }, function () {
            mask.destroy();
        });

    },

    /**
     * 2016.12.15 放弃了取消按钮，添加立刻保存到后台
     * @param tables
     * @param callback
     */
    addTablesToPackage: function (tables, callback) {
        var self = this;
        var newTables = [];
        //添加表的时候就应该把原始表的名称作为当前业务包表的转义，同理删除也删掉
        //对于业务包表逻辑：保存当前表的转义（当前业务包转义不可重名）、关联，但id是一个新的，
        //暂时根据 id 属性区分 source 表和 package 表
        var packTIds = [];
        var tableIds = BI.Utils.getTablesIdByPackageId4Conf(this.id);

        //继承过来的关联、转义
        var exTranslations = {}, exRelations = [];
        BI.each(tables, function (i, table) {
            var id = BI.UUID();
            var fieldIds = [], oFields = BI.deepClone(table.fields);
            BI.each(table.fields, function (j, fs) {
                BI.each(fs, function (k, field) {
                    var fId = BI.UUID();
                    fieldIds.push(fId);
                    //字段的转义
                    var exTran = BI.Utils.getTransNameById4Conf(field.id);
                    if (BI.isNotNull(field.id) && BI.isNotNull(exTran)) {
                        exTranslations[fId] = exTran;
                        BI.Utils.updateTranName4Conf(fId, exTran);
                    }

                    field.id = fId;
                    field.table_id = id;
                    //构造一个field
                    BI.Utils.updateFields4Conf(field.id, {
                        id: field.id,
                        table_id: id,
                        table_name: table.table_name,
                        field_name: field.field_name,
                        field_type: field.field_type,
                        is_usable: BI.isNotNull(field.is_usable) ? field.is_usable : true
                    });
                })
            });
            tableIds.push(id);

            if (BI.isNull(table.id)) {
                exTranslations[id] = self.createDistinctTableTranName(id, table.table_name);
                BI.Utils.updateTranName4Conf(id, self.createDistinctTableTranName(id, table.table_name));
            } else {
                //业务包表
                packTIds.push(id);
                var tableId = table.id;

                //转义、关联都是用sharing pool中的，相当于复制一份
                var tName = self.createDistinctTableTranName(tableId, BI.Utils.getTransNameById4Conf(tableId));
                exTranslations[id] = tName;
                BI.Utils.updateTranName4Conf(id, tName);

                //这里直接使用的sharing pool cat出relation 一般不这么使用
                var relations = Data.SharingPool.cat("relations");
                var connectionSet = relations.connectionSet, primaryKeyMap = relations.primKeyMap, foreignKeyMap = relations.foreignKeyMap;
                var addedConn = [], addedPriMap = {}, addedForMap = {};
                BI.each(connectionSet, function (k, keys) {
                    var copyRelation = self._getCopyOfRelation(keys, oFields, fieldIds, tableId, id);
                    if (BI.isNotEmptyObject(copyRelation)) {
                        addedConn.push(copyRelation);
                    }
                });
                relations.connectionSet = connectionSet.concat(addedConn);
                exRelations = exRelations.concat(addedConn);
                BI.each(primaryKeyMap, function (pfId, maps) {
                    var addedPris = [], nPKId = null;
                    BI.each(maps, function (k, keys) {
                        var copyRelation = self._getCopyOfRelation(keys, oFields, fieldIds, tableId, id);
                        if (BI.isNotEmptyObject(copyRelation)) {
                            nPKId = copyRelation.primaryKey.field_id;
                            addedPris.push(copyRelation);
                        }
                    });
                    if (addedPris.length > 0 && BI.isNotNull(nPKId)) {
                        addedPriMap[nPKId] = addedPris;
                    }
                });
                BI.each(addedPriMap, function (pkId, ms) {
                    var pkMaps = relations.primKeyMap[pkId];
                    if (BI.isNotNull(pkMaps)) {
                        primaryKeyMap[pkId] = pkMaps.concat(ms);
                    } else {
                        primaryKeyMap[pkId] = ms;
                    }
                });
                BI.each(foreignKeyMap, function (ffId, maps) {
                    var addedFors = [], nFKId = null;
                    BI.each(maps, function (k, keys) {
                        var copyRelation = self._getCopyOfRelation(keys, oFields, fieldIds, tableId, id);
                        if (BI.isNotEmptyObject(copyRelation)) {
                            nFKId = copyRelation.foreignKey.field_id;
                            addedFors.push(copyRelation);
                        }
                    });
                    if (addedFors.length > 0 && BI.isNotNull(nFKId)) {
                        addedForMap[nFKId] = addedFors;
                    }
                });
                BI.each(addedForMap, function (fkId, ms) {
                    var fkMaps = relations.foreignKeyMap[fkId];
                    if (BI.isNotNull(fkMaps)) {
                        foreignKeyMap[fkId] = fkMaps.concat(ms);
                    } else {
                        foreignKeyMap[fkId] = ms;
                    }
                });
            }
            table.id = id;
            newTables.push(table);
            self.tables[id] = table;
        });

        BI.Utils.updateTableIdsInPackage4Conf(this.id, tableIds);

        //添加完之后需要读关联转义信息
        //读关联的时候去除来自于服务器的
        // var oTables = {}, nTables = {};
        // BI.each(oldTables, function (id, t) {
        //     t.connection_name !== BICst.CONNECTION.SERVER_CONNECTION && (oTables[id] = t);
        // });
        // BI.each(newTables, function (id, t) {
        //     if (!packTIds.contains(t.id) &&
        //         t.connection_name !== BICst.CONNECTION.SERVER_CONNECTION) {
        //         nTables[id] = t;
        //     }
        // });
        // var data = {
        //     oldTables: oTables,
        //     newTables: nTables
        // };
        if (BI.size(newTables) > 0) {
            // var mask = BI.createWidget({
            //     type: "bi.loading_mask",
            //     masker: BICst.BODY_ELEMENT,
            //     text: BI.i18nText("BI-Loading")
            // });
            // BI.Utils.getRelationAndTransByTables(data, function (res) {
            //     var relations = res.relations, translations = res.translations;
            //     BI.Msg.toast(BI.i18nText("BI-Auto_Read_Relation_Translation_Toast", relations.length, BI.keys(translations.table).length, BI.keys(translations.field).length));
            //     self._setReadRelations(relations);
            //     self._setReadTranslations(translations);
            //     callback();
            // }, function() {
            //     mask.destroy();
            // });

            var mask = BI.createWidget({
                type: "bi.loading_mask",
                masker: BICst.BODY_ELEMENT,
                text: BI.i18nText("BI-Loading")
            });
            BI.Utils.addNewTables4Conf({
                tables: newTables,
                packageId: self.id,
                translations: exTranslations,
                relations: exRelations
            }, function (res) {
                var relations = res.relations, translations = res.translations;
                BI.Msg.toast(BI.i18nText("BI-Auto_Read_Relation_Translation_Toast", relations.length, BI.keys(translations.table).length, BI.keys(translations.field).length));
                self._setReadRelations(relations);
                self._setReadTranslations(translations);
                callback();
            }, function () {
                mask.destroy();
            });
        } else {
            callback();
        }
    },

    createDistinctTableTranName: function (id, v) {
        var currentPackTrans = [];
        BI.each(BI.Utils.getTablesIdByPackageId4Conf(this.id), function (i, tId) {
            id !== tId && currentPackTrans.push({
                name: BI.Utils.getTransNameById4Conf(tId)
            })
        });
        return BI.Func.createDistinctName(currentPackTrans, v);
    },

    _getCopyOfRelation: function (keys, oFields, fieldIds, oTableId, nTableId) {
        var primKey = keys.primaryKey, foreignKey = keys.foreignKey;
        var relation = {};
        BI.each(oFields, function (i, ofs) {
            BI.each(ofs, function (j, oField) {
                if (oField.id === primKey.field_id) {
                    var nPK = {}, nFK = BI.deepClone(foreignKey);
                    BI.each(fieldIds, function (k, fid) {
                        if (BI.Utils.getFieldNameById4Conf(fid) === BI.Utils.getFieldNameById4Conf(primKey.field_id)) {
                            nPK = {
                                field_id: fid,
                                table_id: nTableId
                            }
                        }
                    });
                    relation = {
                        primaryKey: nPK,
                        foreignKey: nFK
                    }
                }
                if (oField.id === foreignKey.field_id) {
                    var nPK = BI.deepClone(primKey), nFK = {};
                    BI.each(fieldIds, function (k, fid) {
                        if (BI.Utils.getFieldNameById4Conf(fid) === BI.Utils.getFieldNameById4Conf(foreignKey.field_id)) {
                            nFK = {
                                field_id: fid,
                                table_id: nTableId
                            }
                        }
                    });
                    relation = {
                        primaryKey: nPK,
                        foreignKey: nFK
                    }
                }
            });
        });
        return relation;
    },

    _setReadRelations: function (readRelations) {
        BI.Utils.saveReadRelation4Conf(readRelations);
    },

    _setReadTranslations: function (readTranslations) {
        var self = this;
        var tableTrans = readTranslations["table"], fieldTrans = readTranslations["field"];
        //重名
        BI.each(tableTrans, function (id, tranTName) {
            BI.Utils.updateTranName4Conf(id, self.createDistinctTableTranName(id, tranTName));
        });
        BI.each(fieldTrans, function (id, tranFName) {
            BI.Utils.updateTranName4Conf(id, tranFName);
        });
    }
});