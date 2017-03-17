/**
 * Created by Young's on 2016/3/11.
 */
BI.ETLModel = BI.inherit(FR.OB, {
    _init: function () {
        BI.ETLModel.superclass._init.apply(this, arguments);
        var o = this.options;
        this.id = o.id;
        this.packageId = o.packageId;
        this.table = o.table;
        this.excelView = o.excelView || {};
        this.updateSettings = o.updateSettings || {};
        this.allTableIds = [];
        this.allTables = [];
        this.translations = {};
        this.tablesMap = {};
        this.isNew = BI.isNull(BI.Utils.getTransNameById4Conf(this.id));
        if (BI.isNotNull(this.table)) {
            this._prepareData();
        }
        this._initTranslations();
    },

    _prepareData: function () {
        var finalTable = [this.table];
        this.fields = this.table.fields;
        this._addId2Tables(finalTable, this.tablesMap);
        this.allTableIds = this._getTablesId(finalTable, []);
        this.allTables = [finalTable];
    },

    //当前表的所有的转义信息
    _initTranslations: function () {
        var self = this;
        if (!this.isNew) {
            var tableName = BI.Utils.getTransNameById4Conf(this.id);
            var connectionName = this.table.connection_name;
            if (BI.isNull(tableName)) {
                tableName = this._createDistinctNameByConnection(connectionName);
            }
            this.translations[this.id] = tableName;
            BI.each(this.fields, function (i, fs) {
                BI.each(fs, function (j, field) {
                    var fieldName = BI.Utils.getTransNameById4Conf(field.id);
                    if (BI.isNotNull(fieldName)) {
                        self.translations[field.id] = fieldName;
                    }
                });
            });
        } else {
            this.translations[this.id] = this._createDistinctNameByConnection(BI.isNotNull(this.table) ? this.table.connection_name : BICst.CONNECTION.ETL_CONNECTION);
        }
    },

    _createDistinctNameByConnection: function (connectionName) {
        switch (connectionName) {
            case BICst.CONNECTION.EXCEL_CONNECTION:
                return this.createDistinctTableTranName(this.id, BI.i18nText("BI-Excel_Dataset"));
            case BICst.CONNECTION.SQL_CONNECTION:
                return this.createDistinctTableTranName(this.id, BI.i18nText("BI-Sql_DataSet"));
            default:
                return this.createDistinctTableTranName(this.id, "ETL");
        }
    },

    getId: function () {
        return this.id;
    },

    isNewTable: function () {
        return this.isNew;
    },

    getFields: function () {
        return BI.deepClone(this.fields);
    },

    getTranslations: function () {
        return this.translations;
    },

    setFields: function (fields) {
        var self = this;
        BI.each(fields, function (i, fs) {
            BI.each(fs, function (j, field) {
                field.id = self._getCurrentFieldIdByFieldInfo(field);
                field.is_usable = BI.isNotNull(BI.Utils.getFieldUsableById4Conf(field.id)) ?
                    BI.Utils.getFieldUsableById4Conf(field.id) : true;
                field.table_id = self.id;
            });
        });
        var preFieldIds = BI.pluck(BI.flatten(this.fields), "id"),
            newFieldIds = BI.pluck(BI.flatten(fields), "id");
        if (newFieldIds.length < preFieldIds) {
            var diffFieldIds = BI.difference(preFieldIds, newFieldIds);
            BI.each(diffFieldIds, function (i, id) {
                BI.Utils.removeRelationByFieldId4Conf(id);
            });
        }
        this.fields = fields;
    },

    setTranslations: function (translations) {
        this.translations = translations;
    },

    setTableName: function (name) {
        this.translations[this.id] = name;
    },

    getTableName: function () {
        return this.translations[this.id];
    },

    setFieldsUsable: function (usedFields) {
        BI.each(this.fields, function (i, fs) {
            BI.each(fs, function (j, field) {
                field.is_usable = usedFields.contains(field.id);
            });
        });
    },

    /**
     * 行列转化用的，原始字段名和转义名的一一对应
     */
    constructFieldNameAndTranslationFieldNameRelation: function () {
        var fieldsIdName = [];
        BI.each(this.getFields(), function (idx, arr) {
            BI.each(arr, function (id, field) {
                fieldsIdName[field.field_name] = BI.Utils.getTransNameById4Conf(field.id);
            });
        });
        return fieldsIdName;
    },

    //为了复用字段id
    _getCurrentFieldIdByFieldInfo: function (field) {
        if (BI.Utils.isFieldExistById4Conf(field.id) ||
            BI.isNotNull(this.translations[field.id])) {
            return field.id;
        }
        return BI.Utils.getFieldIdByNameAndTableId4Conf(field.field_name, this.id) || BI.UUID();
    },

    getAllTables: function () {
        return BI.deepClone(this.allTables);
    },

    getAllTableIds: function () {
        return BI.deepClone(this.allTableIds);
    },

    getTablesMap: function () {
        return BI.deepClone(this.tablesMap);
    },

    getTableById: function (id) {
        return BI.deepClone(this.tablesMap[id]);
    },

    getFieldById: function (id) {
        var field;
        BI.each(this.fields, function (i, fs) {
            BI.each(fs, function (j, f) {
                if (f.id === id) {
                    field = f;
                }
            });
        });
        return field;
    },

    // relation不考虑取消的问题实时的保存
    setRelations: function (relations, fieldId) {
        BI.Utils.saveRelations4Conf(relations, fieldId);
        //同步到后台
        BI.Utils.updateRelation4Conf({
            id: fieldId,
            relations: relations
        }, BI.emptyFn, BI.emptyFn);
    },

    setTranslationsByETLValue: function (etl) {
        var self = this;
        if (BI.has(etl, "etl_type") && BI.isEqual(etl.etl_type, "convert")) {
            var etlValue = etl.etl_value;
            var translations = this.getTranslations();
            var transText = [], text = [];
            var assertArray = function (array) {
                if (BI.isEmpty(array[1])) {
                    array[1] = array[0];
                }
                return array;
            };

            BI.each(etlValue.lc_values, function (idx, lc) {
                lc = assertArray(lc);
                BI.each(etlValue.columns, function (id, co) {
                    co = assertArray(co);
                    transText.push(lc[1] + "-" + co[1]);
                    text.push(lc[0] + "-" + co[0]);
                });
            });

            BI.each(transText, function (idx, name) {
                if (!BI.contains(text, name)) {
                    translations[getFieldIdByFieldName(text[idx])] = name;
                }
            });
            this.setTranslations(translations);
        }

        function getFieldIdByFieldName(field_name) {
            var id = null;
            BI.find(self.fields, function (idx, fieldArray) {
                return BI.find(fieldArray, function (i, field) {
                    if (field.field_name === field_name) {
                        id = field.id;
                        return true;
                    }
                    return false;
                });
            });
            return id;
        }
    },

    getUpdateSettings: function () {
        return BI.deepClone(this.updateSettings);
    },

    setUpdateSettings: function (updateSettings) {
        this.updateSettings = updateSettings;
    },


    getAllFields: function () {
        return BI.deepClone(this.allFields);
    },

    getExcelView: function () {
        return BI.deepClone(this.excelView);
    },

    getFieldNamesByTableId: function (tId) {
        var table = this.getTableById(tId);
        var fieldNames = [];
        if (BI.isNotNull(table)) {
            BI.each(table.fields, function (i, fs) {
                BI.each(fs, function (j, field) {
                    fieldNames.push(field.field_name);
                });
            });
        }
        return fieldNames;
    },

    modifyExcelData: function (tId, fullFileName) {
        var table = this.getTableById(tId);
        table.full_file_name = fullFileName;
        this.saveTableById(tId, table);
    },

    isValidTableTranName: function (name) {
        var self = this;
        var packId = this.packageId || BI.Utils.getPackageIdByTableId4Conf(this.id);
        var tableIds = BI.Utils.getTablesIdByPackageId4Conf(packId);
        var isValid = true;
        BI.some(tableIds, function (i, tId) {
            if (tId !== self.id && BI.Utils.getTransNameById4Conf(tId) === name) {
                isValid = false;
                return true;
            }
        });
        return isValid;
    },

    createDistinctTableTranName: function (id, v) {
        var currentPackTrans = [];
        var packId = this.packageId || BI.Utils.getPackageIdByTableId4Conf(this.id);
        BI.each(BI.Utils.getTablesIdByPackageId4Conf(packId), function (i, tId) {
            id !== tId && currentPackTrans.push({
                name: BI.Utils.getTransNameById4Conf(tId)
            })
        });
        return BI.Func.createDistinctName(currentPackTrans, v);
    },

    /**
     * 添加新表 考虑业务包表 可以把相关的关联和转义全部放进来
     * @param tables
     */
    addNewTables: function (tables) {
        var self = this;
        BI.each(tables, function (i, table) {
            if (BI.isNotNull(table.id)) {
                self._addPackageTable(table);
            }
            var newIdsMap = {};
            self._addUUID2Tables([table], newIdsMap);
            BI.extend(self.tablesMap, newIdsMap);
            self.allTables.push([table]);
            self.allTableIds = self.getAllTableIds().concat(self._getTablesId([table], []));
        });
    },

    setExcelView: function (excelView) {
        this.excelView = excelView;
    },

    removeOneTable: function (tId) {
        var self = this;
        BI.some(this.allTables, function (i, tables) {
            //需要看是否含有子节点，1、没有直接删除 2、有的话还要把直接子节点拿出来放到allTables里面
            if (tables[0].id === tId) {
                self.allTables.splice(i, 1);
                var childTables = tables[0].tables;
                BI.each(childTables, function (j, cTable) {
                    self.allTables.push([cTable]);
                });
                BI.some(self.allTableIds, function (k, id) {
                    if (id === tId) {
                        self.allTableIds.splice(k, 1);
                        return true;
                    }
                });
                return true;
            }
        });
    },

    saveTableById: function (tId, data) {
        var self = this;
        if (BI.isNotNull(data.translations)) {
            this.setTranslations(data.translations);
        }
        if (BI.isNotNull(data.relations)) {
            this.setRelations(data.relations);
        }

        if (this.getAllTables().length > 1 &&
            (data.etl_type === BICst.ETL_OPERATOR.UNION || data.etl_type === BICst.ETL_OPERATOR.JOIN)) {
            var sAllTables = [];
            BI.each(this.getAllTables(), function (i, tables) {
                if (BI.isNotNull(tables) && tables[0].id === tId || !self._getTablesId(data.tables, []).contains(tables[0].id)) {
                    sAllTables.push(tables);
                }
            });
            this.allTables = sAllTables;
        }

        //changed的子节点需要全部替换为UUID，避免出现重复的
        self._addUUID2Tables(data.tables, {});

        //遍历一下这个etl树，找到修改的节点，替换掉
        self._replaceNodeInAllTables(BI.extend(data, {id: tId}));
        this.tablesMap = {};
        this.allTableIds = [];
        BI.each(this.getAllTables(), function (i, tables) {
            self._addId2Tables(tables, self.tablesMap);
            self.allTableIds = self.getAllTableIds().concat(self._getTablesId(tables, []));
        });
    },

    refresh4Fields: function (data) {
        var fields = data.fields, oFields = this.fields;

        BI.each(fields, function (i, fs) {
            BI.each(fs, function (j, field) {
                field.id = getFieldId(field.field_name, oFields);
            });
        });
        this.fields = fields;

        function getFieldId(name, fields) {
            var fieldId = BI.UUID();
            BI.some(fields, function (i, fs) {
                return BI.some(fs, function (j, field) {
                    if (field.field_name === name) {
                        fieldId = field.id;
                        return true;
                    }
                });
            });
            return fieldId;
        }
    },

    //添加业务包表
    _addPackageTable: function (table) {
        var self = this;
        var id = BI.UUID();
        table.id = id;
        var fieldIds = [], oFields = BI.deepClone(table.fields);
        BI.each(table.fields, function (j, fs) {
            BI.each(fs, function (k, field) {
                var fId = BI.UUID();
                fieldIds.push(fId);
                //字段的转义
                if (BI.isNotNull(field.id) && BI.isNotNull(BI.Utils.getTransNameById4Conf(field.id))) {
                    self.translations[fId] = BI.Utils.getTransNameById4Conf(field.id);
                }
                field.id = fId;
                field.table_id = id;
            });
        });

        var addedRelations = BI.Utils.copyRelation4Conf(oFields, fieldIds, id);
        this.setRelations(addedRelations);
    },

    //自己有id的table使用原来的
    _addId2Tables: function (tables, ids, isTemp) {
        var self = this;
        BI.each(tables, function (i, table) {
            var id = table.id || BI.UUID();
            if (BI.isNotNull(table.tables)) {
                self._addId2Tables(tables[i].tables, ids, true);
                tables[i] = BI.extend(table, {
                    id: id,
                    tables: tables[i].tables,
                    temp_name: table.temp_name || (table.tables[0].temp_name + "_" + table.etl_type)
                });
            } else {
                tables[i] = BI.extend(table, {
                    id: id,
                    temp_name: isTemp ?
                        (table.temp_name || BI.Utils.getTransNameById4Conf(id) || table.table_name) :
                        (BI.Utils.getTransNameById4Conf(id) || table.table_name)
                });
            }
            ids[id] = tables[i];
        });
    },

    //给tables添加新的uuid
    _addUUID2Tables: function (tables, ids) {
        var self = this;
        BI.each(tables, function (i, table) {
            var id = BI.UUID();
            if (BI.isNotNull(table.tables)) {
                self._addUUID2Tables(tables[i].tables, ids);
                tables[i] = BI.extend(table, {
                    id: id,
                    tables: tables[i].tables
                });
            } else {
                tables[i] = BI.extend(table, {
                    id: id
                });
            }
            ids[id] = tables[i];
        });
    },

    _replaceNodeInAllTables: function (newNode) {
        var allTables = BI.deepClone(this.getAllTables());
        BI.each(allTables, function (i, tables) {
            allTables[i] = replaceNode(tables, newNode);
        });
        this.allTables = allTables;

        function replaceNode(tables, newNode) {
            BI.some(tables, function (i, table) {
                if (table.id === newNode.id) {
                    tables[i] = newNode;
                    return true;
                }
                if (BI.isNotNull(table.tables)) {
                    tables[i].tables = replaceNode(table.tables, newNode);
                }

            });
            return tables;
        }
    },

    _getTablesId: function (tables, tableIds) {
        var self = this;
        BI.each(tables, function (i, table) {
            if (BI.isNotNull(table.tables)) {
                tableIds = self._getTablesId(table.tables, tableIds);
            }
            tableIds.push(table.id);
        });
        return tableIds;
    },

    getValue: function () {
        var finalTable = this.getAllTables()[0][0];
        var data = {
            id: this.getId(),
            translations: this.getTranslations(),
            fields: this.getFields(),
            excel_view: this.getExcelView(),
            update_settings: this.getUpdateSettings()
        };
        if (BI.isNotNull(finalTable.etl_type)) {
            data.etl_type = finalTable.etl_type;
            data.etl_value = finalTable.etl_value;
            data.tables = finalTable.tables;
            data.connection_name = finalTable.connection_name;
        } else {
            data = BI.extend(finalTable, data);
        }
        return data;
    },

    saveTable: function (callback) {
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Basic_Loading")
        });
        var table = this.getAllTables()[0][0];
        table.id = this.id;
        var data = {
            isNew: this.isNew,
            packageId: this.packageId,
            table: table,
            fields: this.fields,
            translations: this.translations,
            excelView: this.excelView,
            updateSettings: this.updateSettings
        };
        BI.Utils.updateOneTable4Conf(BI.deepClone(data), function () {
            callback(data);
        }, function () {
            mask.destroy();
        })
    }
});
