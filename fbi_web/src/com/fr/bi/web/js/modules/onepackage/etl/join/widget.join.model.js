/**
 * Created by Young's on 2016/3/11.
 */
BI.JoinModel = BI.inherit(FR.OB, {
    _init: function () {
        BI.JoinModel.superclass._init.apply(this, arguments);
        var o = this.options;
        var info = o.info;
        this.id = info.id;
        this.translations = o.translations;
        this.allETLTables = info.allETLTables;
        this.tableInfo = info.tableInfo;
        this.usedFields = info.usedFields;
        this.joinTables = info.joinTables;
        this.reopen = info.reopen;
        this.isGenerated = info.isGenerated;
    },

    initData: function (callback) {
        var self = this;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.getTablesDetailInfoByTables(this.isReopen() ? this.getJoinTables() : [this.getTableInfo()], function (data) {
            self.fields = data[0].fields;
            if (self.reopen === true) {
                self.joinStyle = self.getTableInfo().etl_value.join_style;
                self.joinFields = self._fieldName2Index(self.getTableInfo().etl_value.join_fields);
                self.joinNames = self.getTableInfo().etl_value.join_names;
            } else {
                self.joinStyle = BICst.ETL_JOIN_STYLE.LEFT_JOIN;
                self._initJoinFields();
                self._initJoinNames();
            }
            callback();
            mask.destroy();
        });
    },

    getId: function () {
        return this.id;
    },

    getJoinStyle: function () {
        return BI.deepClone(this.joinStyle);
    },

    setJoinStyle: function (joinStyle) {
        this.joinStyle = joinStyle;
        this._initJoinFields();
        this._initJoinNames();
        this.isGenerated = false;
    },

    getJoinFields: function () {
        return BI.deepClone(this.joinFields);
    },

    setJoinFields: function (joinFields) {
        this.joinFields = joinFields;
        this._initJoinNames();
        this.isGenerated = false;
    },

    getJoinNames: function () {
        return BI.deepClone(this.joinNames);
    },

    setJoinNames: function (joinNames) {
        this.joinNames = joinNames;
        this.isGenerated = false;
    },

    getAllETLTables: function () {
        return BI.deepClone(this.allETLTables);
    },

    getTableInfo: function () {
        return BI.deepClone(this.tableInfo);
    },

    getUsedFields: function () {
        return BI.deepClone(this.usedFields);
    },

    getJoinTables: function () {
        return BI.deepClone(this.joinTables);
    },

    getTranslations: function () {
        return this.translations;
    },

    setJoinTables: function (joinTables) {
        this.joinTables = joinTables;
        this._initJoinFields();
        this._initJoinNames();
        this.isGenerated = false;
    },

    isReopen: function () {
        return this.reopen;
    },

    isCubeGenerated: function () {
        return this.isGenerated;
    },

    getDefaultTableName: function () {
        var self = this;
        var tableInfo = this.getTableInfo();
        if (BI.isNotNull(tableInfo.table_name)) {
            return tableInfo.table_name + "_join";
        }
        var tables = tableInfo.tables;
        var tableName = [];

        function getDefaultName(tables) {
            //只取tables[0]
            if (BI.isNotNull(tables[0].etl_type)) {
                tableName.push("_" + tables[0].etl_type);
                getDefaultName(tables[0].tables);
            } else {
                tableName.push(tables[0].table_name);
            }
        }

        getDefaultName(tables);
        //反向遍历
        tableName.reverse();
        var tableNameString = "";
        BI.each(tableName, function (i, name) {
            tableNameString += name;
        });
        if (this.isReopen() === true) {
            tableNameString += "_join";
        } else {
            tableNameString = tableNameString + "_" + tableInfo.etl_type + "_join";
        }
        return tableNameString;
    },

    getETLTableNameByTable: function (table) {
        var tables = table.tables;
        var tableName = [];

        function getDefaultName(tables) {
            //只取tables[0]
            if (BI.isNotNull(tables[0].etl_type)) {
                tableName.push("_" + tables[0].etl_type);
                getDefaultName(tables[0].tables);
            } else {
                tableName.push(tables[0].table_name);
            }
        }

        BI.isNotNull(tables) && getDefaultName(tables);
        //反向遍历
        tableName.reverse();
        var tableNameString = "";
        BI.each(tableName, function (i, name) {
            tableNameString += name;
        });
        return tableNameString + "_" + table.etl_type;
    },

    getAllTables: function () {
        var tables = this.getJoinTables();
        var tableInfo = this.getTableInfo();
        if (this.isReopen() === true) {
            tables = tableInfo.tables;
        } else {
            tables.splice(0, 0, BI.extend(this.getTableInfo(), {fields: this.fields}));
        }
        return tables;
    },

    getAllJoinFields: function () {
        var self = this;
        var joinArray = [];
        //遍历所有表字段
        var allTableFields = this.getAllTableFields();
        BI.each(allTableFields, function (i, fields) {
            BI.each(fields, function (j, field) {
                var rowIndex = self.getFieldInJoinArray(joinArray, field, i);
                rowIndex !== -1 ?
                    joinArray[rowIndex][i] = j :
                    joinArray = self.addOneJoinArray(joinArray, i, j);
            })
        });
        return joinArray;
    },

    getFieldInJoinArray: function (joinArray, field, col) {
        var self = this;
        var out = false, res = -1;
        var allTableFields = self.getAllTableFields();

        function isEqualNameType(field1, field2) {
            if (BI.isNotNull(field1) && BI.isNotNull(field2)) {
                return field1.field_type === field2.field_type &&
                    field1.field_name === field2.field_name;
            }
        }

        BI.some(joinArray, function (row, oneRow) {
            BI.some(oneRow, function (column, item) {
                if (column !== col &&
                    isEqualNameType(field, allTableFields[column][oneRow[column]])) {
                    res = row;
                    return out = true;
                }
            });
            return out;
        });
        return res;
    },

    addOneJoinArray: function (joinArray, col, index) {
        var self = this, arr = [];
        var tables = self.getAllTables();
        BI.each(tables, function (i, item) {
            i === col ?
                arr.push(index) :
                arr.push(BI.TableAddUnion.UNION_FIELD_NULL);
        });
        joinArray.push(arr);
        return joinArray;
    },

    getAllTableFields: function () {
        var self = this;
        var allTableFields = [], tables = this.getAllTables();
        BI.each(tables, function (i, table) {
            var fields = i === 0 ? self.fields : table.fields;
            var s = fields[0], n = fields[1], d = fields[2];
            fields = s.concat(n);
            fields = fields.concat(d);
            allTableFields.push(fields);
        });
        return allTableFields
    },


    getAllFields: function () {
        return BI.deepClone(this.tableInfo.fields);
    },


    checkMergeFields: function () {
        var self = this;
        var isValid = true;
        BI.each(this.joinFields, function (i, mf) {
            var notNullCount = 0, fieldTypes = [], fieldTypeValid = true;
            BI.each(mf, function (j, fieldIndex) {
                if (fieldIndex > -1) {
                    notNullCount++;
                    var allFields = self.getAllTableFields();
                    var fieldType = allFields[j][fieldIndex].field_type;
                    if (fieldTypes.length > 0 && !fieldTypes.contains(fieldType)) {
                        fieldTypeValid = false;
                    }
                    fieldTypes.push(allFields[j][fieldIndex].field_type);
                }
            });
            if (notNullCount <= 1 || fieldTypeValid === false) {
                isValid = false;
            }
        });
        return isValid;
    },

    getJoinFieldsName: function () {
        var self = this;
        var joinFieldsName = [];
        var allTableFields = self.getAllTableFields();
        BI.each(this.joinFields, function (i, row) {
            joinFieldsName.push([allTableFields[0][row[0]].field_name, allTableFields[1][row[1]].field_name]);
        });
        return joinFieldsName;
    },

    _initJoinFields: function () {
        var self = this;
        var allJoinFields = self.getAllJoinFields();
        this.joinFields = [];
        BI.each(allJoinFields, function (i, ua) {
            var count = 0;
            BI.each(ua, function (i, u) {
                u > -1 && count++;
            });
            count > 1 && (self.joinFields.push(ua));
        });
    },

    _initJoinNames: function () {
        var self = this;
        var allTables = self.getAllTables(), allTableFields = self.getAllTableFields();
        this.joinNames = [];
        //合并依据
        BI.each(self.joinFields, function (i, row) {
            if (row[0] === -1 || row[1] === -1) {
                return;
            }
            if (self.joinStyle === BICst.ETL_JOIN_STYLE.RIGHT_JOIN) {
                self.joinNames.push({
                    isLeft: false,
                    name: self._createDistinctName(allTableFields[0][row[0]].field_name + "/" + allTableFields[1][row[1]].field_name),
                    column_name: allTableFields[1][row[1]].field_name
                })
            } else {
                self.joinNames.push({
                    isLeft: true,
                    name: self._createDistinctName(allTableFields[0][row[0]].field_name + "/" + allTableFields[1][row[1]].field_name),
                    column_name: allTableFields[0][row[0]].field_name
                })
            }
        });
        BI.each(allTableFields, function (i, fields) {
            BI.each(fields, function (j, field) {
                var isUsed = false;
                if (i === 0) {
                    BI.each(self.joinFields, function (k, f) {
                        f[0] === j && (isUsed = true);
                    });
                    if (isUsed === true) {
                        return;
                    }
                    self.joinNames.push({
                        isLeft: true,
                        name: self._createDistinctName(field.field_name),
                        column_name: field.field_name
                    });
                } else {
                    BI.each(self.joinFields, function (k, f) {
                        f[1] === j && (isUsed = true);
                    });
                    if (isUsed === true) {
                        return;
                    }
                    self.joinNames.push({
                        isLeft: false,
                        name: self._createDistinctName(field.field_name),
                        column_name: field.field_name
                    });
                }
            });
        });
    },

    _createDistinctName: function (name) {
        //先自己判断是否重名（大小写问题）
        var self = this;
        var dName = name;
        BI.some(this.joinNames, function (i, jName) {
            if (jName.name === name) {
                dName = BI.Func.createDistinctName(self.joinNames, name);
                return true;
            }
        });
        return dName;
    },

    _fieldName2Index: function (originalFields) {
        var self = this;
        var joinFields = [];
        var allTableFields = self.getAllTableFields();
        BI.each(originalFields, function (i, fieldNames) {
            var items = [];
            BI.each(fieldNames, function (j, name) {
                BI.each(allTableFields[j], function (k, field) {
                    name === field.field_name && (items.push(k));
                });
            });
            joinFields.push(items);
        });
        return joinFields;
    }
});
