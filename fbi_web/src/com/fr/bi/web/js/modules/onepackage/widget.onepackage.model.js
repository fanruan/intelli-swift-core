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
        var o = this.options;
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
        }, function (res) {
            if (BI.isNotNull(res)) {
                if (BI.isNotNull(res.tables)) {
                    self.tables = res.tables;
                }
                if (BI.isNotNull(res.connNames)) {
                    self.connNames = res.connNames;
                }
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
        var self = this;
        this.name = name;
        BI.Utils.updatePackageName4Conf({
            id: self.id,
            name: name
        }, BI.emptyFn);
    },

    getConnNames: function () {
        return this.connNames || [];
    },

    getGroupId: function () {
        return this.gid;
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
        var translations = data.translations,
            fields = data.fields,
            table = data.table;
        var tableIds = BI.Utils.getTablesIdByPackageId4Conf(this.id);
        if (!tableIds.contains(table.id)) {
            tableIds.push(table.id);
            BI.Utils.updateTableIdsInPackage4Conf(this.id, tableIds);
        }
        BI.each(fields, function (i, field) {
            BI.Utils.updateFields4Conf(field.id, field);
        });
        BI.Utils.updateTranslationsByTableId4Conf(id, translations);

        this.tables[table.id] = table;
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

            var tranName;
            if (BI.isNull(table.id)) {
                tranName = self.createDistinctTableTranName(id, table.table_name);
            } else {
                //业务包表
                packTIds.push(id);
                var tableId = table.id;

                //转义、关联都是用sharing pool中的，相当于复制一份
                tranName = self.createDistinctTableTranName(tableId, BI.Utils.getTransNameById4Conf(tableId));

                //copy relations
                var copyRelations = BI.Utils.copyRelation4Conf(oFields, fieldIds, id);
                exRelations = exRelations.concat(copyRelations.connectionSet);
            }
            BI.Utils.updateTranName4Conf(id, tranName);
            table.id = id;
            newTables.push(table);
            self.tables[id] = table;
        });

        BI.Utils.updateTableIdsInPackage4Conf(this.id, tableIds);

        if (BI.size(newTables) > 0) {
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

        var trans = {};
        BI.each(this.tables, function(i, table) {
            trans[table.id] = BI.Utils.getTransNameById4Conf(table.id);
        });
        //同步到后台
        BI.Utils.updateTablesTranOfPackage({translations: trans});
    }
});