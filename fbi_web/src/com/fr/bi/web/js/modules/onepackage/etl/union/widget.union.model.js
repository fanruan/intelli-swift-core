/**
 * Created by Young's on 2016/3/11.
 */
BI.UnionModel = BI.inherit(BI.Widget, {
    _init: function () {
        BI.UnionModel.superclass._init.apply(this, arguments);
        var o = this.options;
        var info = o.info;
        this.id = info.id;
        this.translations = o.translations;
        this.allETLTables = info.allETLTables;
        this.tableInfo = info.tableInfo;
        this.usedFields = info.usedFields;
        this.unionTables = info.unionTables;
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
        BI.Utils.getTablesDetailInfoByTables(this.isReopen() ? this.getUnionTables() : [this.getTableInfo()], function (data) {
            self.fields = data[0].fields;
            self._initMergeFields();
            if (self.reopen === true) {
                self.unionArray = self.getTableInfo().etl_value.union_array;
            } else {
                self._initUnionArray();
            }
            callback();
            mask.destroy();
        });
    },

    getId: function () {
        return this.id;
    },

    getFields: function () {
        return BI.deepClone(this.fields);
    },

    getAllETLTables: function () {
        return BI.deepClone(this.allETLTables);
    },

    getUnionTables: function () {
        return BI.deepClone(this.unionTables);
    },

    setUnionTables: function (tables) {
        this.unionTables = tables;
        this._initMergeFields();
        this._initUnionArray();
        this.isGenerated = false;
    },

    getUnionArray: function () {
        return BI.deepClone(this.unionArray);
    },

    setUnionArray: function (unionArray) {
        this.unionArray = unionArray;
        this.isGenerated = false;
    },

    getTableInfo: function () {
        return BI.deepClone(this.tableInfo);
    },

    getMergeFields: function () {
        return BI.deepClone(this.mergeFields);
    },

    getTranslations: function () {
        return this.translations;
    },

    setMergeFields: function (mergeFields) {
        this.mergeFields = mergeFields;
        this._initUnionArray();
        this.isGenerated = false;
    },

    isReopen: function () {
        return this.reopen;
    },

    isCubeGenerated: function () {
        return this.isGenerated;
    },

    getDefaultTableName: function () {
        if (BI.isNotNull(this.tableInfo.table_name)) {
            return this.tableInfo.table_name + "_union";
        }
        var tables = this.tableInfo.tables;
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
        if (this.reopen === true) {
            tableNameString += "_union";
        } else {
            tableNameString = tableNameString + "_" + this.tableInfo.etl_type + "_union";
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
        var tables = BI.deepClone(this.unionTables);
        if (this.reopen === true) {
            tables = BI.deepClone(this.tableInfo.tables);
        } else {
            tables.splice(0, 0, BI.extend(this.getTableInfo(), {fields: this.fields}));
        }
        return tables;
    },

    getAllUnionFields: function () {
        var self = this;
        var unionArray = [];
        //遍历所有表字段
        var allTableFields = this.getAllTableFields();
        BI.each(allTableFields, function (i, fields) {
            BI.each(fields, function (j, field) {
                var rowIndex = self.getFieldInUnionArray(unionArray, field, i);
                rowIndex !== -1 ?
                    unionArray[rowIndex][i] = j :
                    self.addOneUnionArray(unionArray, i, j);
            })
        });
        return unionArray;
    },

    getFieldInUnionArray: function (unionArray, field, col) {
        var out = false, res = -1;
        var allTableFields = this.getAllTableFields();

        function isEqualNameType(field1, field2) {
            if (BI.isNotNull(field1) && BI.isNotNull(field2)) {
                return field1.field_type === field2.field_type &&
                    field1.field_name === field2.field_name;
            }
        }

        BI.some(unionArray, function (row, oneRow) {
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

    addOneUnionArray: function (unionArray, col, index) {
        var arr = [];
        var tables = this.getAllTables();
        BI.each(tables, function (i, item) {
            i === col ?
                arr.push(index) :
                arr.push(BI.TableAddUnion.UNION_FIELD_NULL);
        });
        unionArray.push(arr);
    },

    getAllFields: function () {
        return BI.deepClone(this.tableInfo.fields);
    },


    getAllTableFields: function () {
        var self = this;
        var allTableFields = [], tables = this.getAllTables();
        BI.each(tables, function (i, table) {
            var fields = BI.deepClone(i === 0 ? self.getFields() : table.fields);
            var s = fields[0], n = fields[1], d = fields[2];
            fields = s.concat(n);
            fields = fields.concat(d);
            allTableFields.push(fields);
        });
        return allTableFields
    },

    checkMergeFields: function () {
        var self = this;
        var isValid = true;
        BI.each(this.mergeFields, function (i, mf) {
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

    _initMergeFields: function () {
        var self = this;
        //如果是点击了四边形
        if (this.reopen === true) {
            var unionArray = this.tableInfo.etl_value.union_array;
            this.mergeFields = [];
            var allTables = this.getAllTableFields();
            //只需要遍历合并的
            BI.some(unionArray, function (i, arr) {
                var single = [], count = 0;
                BI.each(arr, function (j, name) {
                    if (j > 0) {
                        name !== "" && count++;
                        var fields = allTables[j - 1];
                        BI.some(fields, function (k, field) {
                            if (name === field.field_name) {
                                single.push(k);
                                return true;
                            }
                        });
                    }
                });
                if (count <= 1) {
                    return true;
                }
                self.mergeFields.push(single);
            });
            return;
        }
        var allUnionFields = this.getAllUnionFields();
        this.mergeFields = [];
        BI.each(allUnionFields, function (i, ua) {
            var count = 0;
            BI.each(ua, function (i, u) {
                u > -1 && count++;
            });
            count > 1 && (self.mergeFields.push(ua));
        });
    },

    _initUnionArray: function () {
        var self = this;
        var allTables = this.getAllTables(), allTableFields = this.getAllTableFields();
        this.unionArray = [];
        //已合并的
        BI.each(this.mergeFields, function (i, row) {
            var fieldNames = [""];
            BI.each(row, function (j, col) {
                if (col > BI.TableAddUnion.UNION_FIELD_NULL) {
                    var fName = fieldNames[0];
                    var fieldName = allTableFields[j][col].field_name;
                    fName === "" ?
                        fName = fieldName :
                        fName += ("/" + fieldName);
                    fieldNames[0] = self._createDistinctName(fName);
                    fieldNames[j + 1] = fieldName;
                } else {
                    fieldNames[j + 1] = "";
                }
            });
            fieldNames[0] !== "" && self.unionArray.push(fieldNames);
        });
        //未合并的字段
        BI.each(allTableFields, function (i, fields) {
            var mergedFields = [];
            BI.each(self.mergeFields, function (j, row) {
                row[i] !== BI.TableAddUnion.UNION_FIELD_NULL && (mergedFields.push(row[i]));
            });
            BI.each(fields, function (k, field) {
                if (!mergedFields.contains(k)) {
                    var fs = [];
                    BI.each(allTables, function (l, t) {
                        i === l ? (fs[l] = field.field_name) : (fs[l] = "");
                    });
                    fs.splice(0, 0, self._createDistinctName(field.field_name));
                    self.unionArray.push(fs);
                }
            });
        });
    },

    _createDistinctName: function (name) {
        var self = this;
        var dName = name;
        var allUnionNames = [];
        BI.each(self.unionArray, function (i, array) {
            allUnionNames.push({name: array[0]});
        });
        BI.some(self.unionArray, function (i, array) {
            if (array[0] === name) {
                dName = BI.Func.createDistinctName(allUnionNames, name);
                return true;
            }
        });
        return dName;
    }
});

