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
        this.name = o.name;
        this.tables = [];
        if (BI.isNull(this.name)) {
            this.name = BI.Utils.getConfPackageNameByID(this.id);
            this.tables = BI.Utils.getConfPackageTablesByID(this.id);
        }
        this.allFields = Data.SharingPool.get("fields");
        this.relations = Data.SharingPool.get("relations");
        this.translations = Data.SharingPool.get("translations");
        this.updateSettings = Data.SharingPool.get("update_settings");
    },

    initData: function (callback) {
        var self = this;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.getTablesOfOnePackage(this.id, function (res) {
            self.tablesData = res.table_data;
            self.excelViews = res.excel_views;
            //对于当前正在编辑的业务包，维护该sharing pool中的对象
            self._syncSharedPackages();
            callback();
            mask.destroy();
        });
    },

    getId: function () {
        return this.id;
    },

    getName: function () {
        return this.name;
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

    getTablesData: function () {
        return BI.deepClone(this.tablesData);
    },


    getExcelViews: function () {
        return BI.deepClone(this.excelViews);
    },

    getUpdateSettings: function () {
        return BI.deepClone(this.updateSettings);
    },

    getRelations: function () {
        return BI.deepClone(this.relations);
    },

    getTranslations: function () {
        return BI.deepClone(this.translations);
    },

    getNewTableItems: function () {
        return this.constants.addNewTableItems;
    },

    getViewType: function () {
        return this.constants.viewType;
    },

    getAllFields: function () {
        return BI.deepClone(this.allFields);
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

    getTableTranName: function (tId) {
        var tableData = this.getTablesData()[tId];
        var tableNameText = tableData.table_name;
        //ETL 表
        var translations = this.getTranslations();
        if (tableData.connection_name === BICst.CONNECTION.ETL_CONNECTION) {
            tableNameText = translations[tId];
        } else if (BI.isNotNull(translations[tId]) && translations[tId] !== tableNameText) {
            tableNameText = translations[tId] + "(" + tableNameText + ")";
        }
        return tableNameText;
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

    removeTable: function (tableId) {
        var self = this;

        //删除表的转义
        delete self.translations[tableId];
        //删除表中字段转义
        var allFieldsArray = self.tablesData[tableId].fields;
        BI.each(allFieldsArray, function (i, fieldsArray) {
            BI.each(fieldsArray, function (index, fieldObj) {
                delete self.translations[fieldObj.id];
            })
        });

        delete this.tablesData[tableId];
        BI.some(this.getTables(), function (i, table) {
            if (table.id === tableId) {
                self.tables.splice(i, 1);
                return true;
            }
        });


        //删除相关关联
        var connectionSet = this.relations.connectionSet, primaryKeyMap = this.relations.primKeyMap, foreignKeyMap = this.relations.foreignKeyMap;
        var resultConnectionSet = [];
        BI.each(connectionSet, function (i, keys) {
            var primKey = keys.primaryKey, foreignKey = keys.foreignKey;
            if (!(self.getTableIdByFieldId(primKey.field_id) === tableId || self.getTableIdByFieldId(foreignKey.field_id) === tableId)) {
                resultConnectionSet.push(connectionSet[i])
            }
        });
        this.relations.connectionSet = resultConnectionSet;
        BI.each(primaryKeyMap, function (kId, maps) {
            if (self.getTableIdByFieldId(kId) === tableId) {
                delete primaryKeyMap[kId];
            } else {
                BI.each(maps, function (i, keys) {
                    if (tableId === self.getTableIdByFieldId(keys.primaryKey.field_id) || tableId === self.getTableIdByFieldId(keys.foreignKey.field_id)) {
                        primaryKeyMap[kId].splice(i, 1);
                    }
                });
                if (primaryKeyMap[kId].length === 0) {
                    delete primaryKeyMap[kId];
                }
            }
        });
        BI.each(foreignKeyMap, function (kId, maps) {
            if (tableId === self.getTableIdByFieldId(kId)) {
                delete foreignKeyMap[kId];
            } else {
                BI.each(maps, function (i, keys) {
                    if (tableId === self.getTableIdByFieldId(keys.primaryKey.field_id) || tableId === self.getTableIdByFieldId(keys.foreignKey.field_id)) {
                        foreignKeyMap[kId].splice(i, 1);
                    }
                });
                if (foreignKeyMap[kId].length === 0) {
                    delete foreignKeyMap[kId];
                }
            }
        });

        BI.each(this.allFields, function (id, field) {
            if (field !== null && field.table_id === tableId) {
                delete self.allFields[id];
            }
        });
        this._syncSharedPackages();
    },

    addTablesToPackage: function (tables, callback) {
        var self = this;
        var oldTables = this.getTablesData();
        //添加表的时候就应该把原始表的名称作为当前业务包表的转义，同理删除也删掉
        //对于业务包表逻辑：保存当前表的转义（当前业务包转义不可重名）、关联，但id是一个新的，
        //暂时根据 id 属性区分 source 表和 package 表
        BI.each(tables, function (i, table) {
            var id = BI.UUID();
            BI.each(table.fields, function (j, fs) {
                BI.each(fs, function (k, field) {
                    // field.id = id + field.field_name;
                    field.id = BI.UUID();
                    field.table_id = id;
                    //这里简单维护一下field信息（包括table_id,table_name,field_name,field_type即可）
                    self.allFields[field.id] = {
                        id: field.id,
                        table_id: id,
                        table_name: table.table_name,
                        field_name: field.field_name,
                        field_type: field.field_type
                    };
                })
            });
            self.tables.push({id: id});

            if (BI.isNull(table.id)) {
                self.translations[id] = self.createDistinctTableTranName(table.table_name);
            } else {
                //业务包表
                var tableId = table.id;
                //转义、关联都是用sharing pool中的，相当于复制一份
                self.translations[id] = self.createDistinctTableTranName(self.translations[tableId]);
                var relations = self.getRelations();
                var connectionSet = relations.connectionSet, primaryKeyMap = relations.primKeyMap, foreignKeyMap = relations.foreignKeyMap;
                BI.each(connectionSet, function (i, keys) {
                    var primKey = keys.primaryKey, foreignKey = keys.foreignKey;
                    if (self.getTableIdByFieldId(primKey.field_id) || self.getTableIdByFieldId(foreignKey.field_id)) {
                        self.relations.connectionSet.push({
                            primaryKey: primKey.table_id === tableId ? BI.extend(primKey, {table_id: id}) : primKey,
                            foreignKey: foreignKey.table_id === tableId ? BI.extend(foreignKey, {table_id: id}) : foreignKey
                        })
                    }
                });
                BI.each(primaryKeyMap, function (kId, maps) {
                    if (kId === tableId) {
                        var tmpMaps = [];
                        BI.each(maps, function (i, keys) {
                            tmpMaps.push({
                                primaryKey: keys.primaryKey.table_id === tableId ? BI.extend(keys.primaryKey, {table_id: id}) : keys.primaryKey,
                                foreignKey: keys.foreignKey.table_id === tableId ? BI.extend(keys.foreignKey, {table_id: id}) : keys.foreignKey
                            })
                        });
                        self.relations.primKeyMap[id] = tmpMaps;
                    }
                });
                BI.each(foreignKeyMap, function (kId, maps) {
                    if (kId === tableId) {
                        var tmpMaps = [];
                        BI.each(maps, function (i, keys) {
                            tmpMaps.push({
                                primaryKey: keys.primaryKey.table_id === tableId ? BI.extend(keys.primaryKey, {table_id: id}) : keys.primaryKey,
                                foreignKey: keys.foreignKey.table_id === tableId ? BI.extend(keys.foreignKey, {table_id: id}) : keys.foreignKey
                            })
                        });
                        self.relations.foreignKeyMap[id] = tmpMaps;
                    }
                });
            }
            self.tablesData[id] = BI.extend(table, {id: id});
        });
        //添加完之后需要读关联转义信息
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });
        var data = {
            oldTables: oldTables,
            newTables: this.getTablesData()
        };
        BI.Utils.getRelationAndTransByTables(data, function (res) {
            var relations = res.relations, translations = res.translations;
            BI.Msg.toast(BI.i18nText("BI-Auto_Read_Relation_Translation_Toast", relations.length, BI.keys(translations.table).length, BI.keys(translations.field).length));
            self._setReadRelations(relations);
            self._setReadTranslations(translations);
            callback();
            mask.destroy();
        });
        this._syncSharedPackages();
    },

    createDistinctTableTranName: function (v) {
        var self = this;
        var tableIds = this.getTables();
        var currentPackTrans = [];
        BI.each(tableIds, function (i, table) {
            currentPackTrans.push({
                name: self.getTranslations()[table.id]
            })
        });
        return BI.Func.createDistinctName(currentPackTrans, v);
    },

    _setReadRelations: function (readRelations) {
        var connectionSet = this.relations.connectionSet;
        var primKeyMap = this.relations.primKeyMap;
        var foreignKeyMap = this.relations.foreignKeyMap;
        BI.each(readRelations, function (i, read) {
            var pk = read.primaryKey, fk = read.foreignKey;
            var isExist = false;
            BI.each(connectionSet, function (j, conn) {
                var p = conn.primaryKey, f = conn.foreignKey;
                if (p.field_id === pk.field_id && f.field_id === fk.field_id) {
                    isExist = true;
                }
            });
            //读取到的关联都是1:N的
            if (isExist === false) {
                connectionSet.push(read);
                primKeyMap[pk.field_id] || (primKeyMap[pk.field_id] = []);
                foreignKeyMap[fk.field_id] || (foreignKeyMap[fk.field_id] = []);
                primKeyMap[pk.field_id].push(read);
                foreignKeyMap[fk.field_id].push(read);
            }
        });
    },

    _setReadTranslations: function (readTranslations) {
        var self = this;
        var tableTrans = readTranslations["table"], fieldTrans = readTranslations["field"];
        //重名
        BI.each(tableTrans, function (id, tranTName) {
            self.translations[id] = BI.Func.createDistinctName(self._getCurrentPackTrans(id), tranTName);
        });
        BI.each(fieldTrans, function (id, tranFName) {
            self.translations[id] = tranFName;
        });
    },

    _getCurrentPackTrans: function (id) {
        var self = this;
        var currentTrans = [];
        BI.each(this.getTables(), function (i, table) {
            //去掉本身
            table.id !== id && currentTrans.push({name: self.getTranslations()[table.id]});
        });
        return currentTrans;
    },

    _syncSharedPackages: function () {
        var shared = {};
        shared[this.getId()] = {
            id: this.getId(),
            name: this.getName(),
            groupId: this.getGroupId(),
            groupName: this.getGroupName(),
            tables: this.getTablesData()
        };
        Data.SharingPool.put(BICst.CURRENT_EDITING_PACKAGE, shared);
    },

    getValue: function () {
        return {
            id: this.getId(),
            name: this.getName(),
            groupName: this.getGroupName(),
            tables: this.getTables(),
            table_data: this.getTablesData(),
            relations: this.getRelations(),
            translations: this.getTranslations(),
            excel_views: this.getExcelViews(),
            update_settings: this.getUpdateSettings()
        }
    }
});