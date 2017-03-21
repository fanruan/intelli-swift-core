/**
 * Created by Young's on 2016/3/11.
 * one package的数据
 */
BI.OnePackageModel = BI.inherit(FR.OB, {

    constants: {
        addNewTableItems: [
            {
                text: BI.i18nText("BI-Basic_Database") + "/" + BI.i18nText("BI-Basic_Package"),
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
            text: BI.i18nText("BI-Basic_Loading")
        });

        var data = {};
        data[BICst.JSON_KEYS.ID] = this.id;
        data[BICst.JSON_KEYS.GROUP_NAME] = BI.Utils.getGroupNameById4Conf(this.gid);
        data[BICst.JSON_KEYS.PACKAGE_NAME] = BI.Utils.getPackageNameByID4Conf(this.id);
        BI.Utils.getSimpleTablesByPackId(data, function (res) {
            if (BI.isNotNull(res)) {
                if (BI.isNotNull(res.tables)) {
                    self.tables = res.tables;
                }
                if (BI.isNotNull(res.connNames)) {
                    self.connNames = res.connNames;
                }
                if (BI.isNotNull(res.tableIds)) {
                    self.tableIds = res.tableIds;
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

    getOtherTableNames: function (id) {
        var names = [];
        BI.each(this.tables, function (i, table) {
            table.id !== id && names.push(table[BICst.JSON_KEYS.TRAN_NAME]);
        });
        return names;
    },

    getSortedTables: function () {
        var self = this;
        var sortedTables = [];
        BI.each(this.tableIds, function (i, tId) {
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
        var tableName = table[BICst.JSON_KEYS.TABLE_NAME], tranName = table[BICst.JSON_KEYS.TRAN_NAME];
        //ETL 表
        if (table[BICst.JSON_KEYS.CONNECTION_NAME] === BICst.CONNECTION.ETL_CONNECTION) {
            return tranName;
        } else if (tableName !== tranName) {
            return tranName + "(" + tableName + ")";
        }
        return tableName;
    },

    changeTableInfo: function (id, data) {
        var table = data.table;
        !this.tableIds.contains(table.id) && this.tableIds.push(table.id);
        this.tables[table.id] = table;
    },

    removeTable: function (tableId, callback) {
        var self = this;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Basic_Loading")
        });
        BI.Utils.removeTableById4Conf({
            id: tableId,
            packageId: self.id
        }, function () {
            delete self.tables[tableId];
            BI.remove(self.tableIds, function (i, tId) {
                return tId === tableId;
            });
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

        //继承过来的关联、转义
        var exTranslations = {}, exRelations = {};
        BI.each(tables, function (i, table) {
            var id = BI.UUID();
            var fieldIds = [];
            BI.each(table.fields, function (j, fs) {
                BI.each(fs, function (k, field) {
                    var fId = BI.UUID();
                    fieldIds.push(fId);
                    //字段的转义
                    var exTran = field[BICst.JSON_KEYS.TRAN_NAME];
                    if (BI.isNotNull(field.id) && BI.isNotNull(exTran)) {
                        exTranslations[fId] = exTran;
                    }
                    field.id = fId;
                    field.table_id = id;
                })
            });
            self.tableIds.push(id);

            var tranName;
            if (BI.isNull(table.id)) {
                tranName = self.createDistinctTableTranName(id, table.table_name);
            } else {
                //业务包表
                packTIds.push(id);
                var tableId = table.id;
                tranName = self.createDistinctTableTranName(tableId, table[BICst.JSON_KEYS.TRAN_NAME]);
                exRelations[tableId] = table;   //{oldTableId: newTable}
            }
            table[BICst.JSON_KEYS.TRAN_NAME] = tranName;
            table.id = id;
            newTables.push(table);
            self.tables[id] = table;
        });

        if (BI.size(newTables) > 0) {
            var mask = BI.createWidget({
                type: "bi.loading_mask",
                masker: BICst.BODY_ELEMENT,
                text: BI.i18nText("BI-Basic_Loading")
            });
            BI.Utils.addNewTables4Conf({
                tables: newTables,
                packageId: self.id,
                translations: exTranslations,
                relations: exRelations
            }, function (res) {
                var relations = res.relations, translations = res.translations;
                BI.Msg.toast(BI.i18nText("BI-Auto_Read_Relation_Translation_Toast", relations.length, BI.keys(translations.table).length, BI.keys(translations.field).length));
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
        BI.each(this.tables, function (i, table) {
            id !== table[BICst.JSON_KEYS.ID] && currentPackTrans.push({
                name: table[BICst.JSON_KEYS.TRAN_NAME]
            })
        });
        return BI.Func.createDistinctName(currentPackTrans, v);
    },

    _setReadTranslations: function (readTranslations) {
        var self = this;
        var tableTrans = readTranslations["table"];
        //重名
        BI.each(tableTrans, function (id, tranTName) {
            self.tables[id][BICst.JSON_KEYS.TRAN_NAME] = self.createDistinctTableTranName(id, tranTName);
        });

        var trans = {};
        BI.each(this.tables, function (i, table) {
            trans[table.id] = table[BICst.JSON_KEYS.TRAN_NAME];
        });
        //同步到后台
        BI.Utils.updateTablesTranOfPackage({translations: trans}, BI.emptyFn, BI.emptyFn);
    }
});