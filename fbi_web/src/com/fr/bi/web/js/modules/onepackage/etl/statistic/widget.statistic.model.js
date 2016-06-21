/**
 * @class BI.GroupStatisticModel
 * @extends BI.Widget
 * @author windy
 */
BI.GroupStatisticModel = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.GroupStatisticModel.superclass._defaultConfig.apply(this, arguments), {
            info: {}
        });
    },

    _init: function () {
        BI.GroupStatisticModel.superclass._init.apply(this, arguments);
        var o = this.options;
        this.populate(o.info);
    },

    isCubeGenerated: function () {
        return this.isGenerated;
    },

    getDefaultTableName: function () {
        if (BI.isNotNull(this.old_tables.table_name)) {
            return this.old_tables.table_name + "_group";
        }
        var tables = this.old_tables.tables;
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
            tableNameString += "_group";
        } else {
            tableNameString = tableNameString + "_" + this.old_tables.etl_type + "_group";
        }
        return tableNameString;
    },

    getAllFields: function () {
        return BI.deepClone(this.old_tables.fields);
    },

    getPreTableStructure: function () {
        return this.old_tables;
    },

    getTableStructure: function () {
        var tables = {};
        if (this.reopen === true) {
            tables = this.tables[0];
        } else {
            tables = this.old_tables;
        }
        return tables;
    },

    getView: function () {
        return this.view;
    },

    getDimension: function (id) {
        if (BI.isNull(id)) {
            return this.dimensions;
        }
        return this.dimensions[id];
    },

    getTablesDetailInfoByTables: function (callback) {
        BI.Utils.getTablesDetailInfoByTables([this.getTableStructure()], function (data) {
            callback(data);
        });
    },

    /**
     * 删除dimension
     * @param dId
     */
    deleteDimension: function (dId) {
        var views = this.getView(), dimensions = this.getDimension();
        delete dimensions[dId];
        BI.each(views, function (region, ids) {
            BI.any(ids, function (idx, id) {
                if (dId == id) {
                    ids.remove(id);
                    return true;
                }
            });
        });
        this.dimensions = dimensions;
        this.view = views;
    },

    /**
     * 添加dimension
     * @param field
     */
    addDimensionByField: function (field) {
        var self = this;
        var view = this.getView(), dimensions = this.getDimension();
        var f = field;
        var fields = f["fieldInfo"], regionType = f["regionType"];
        fields = BI.isArray(fields) ? fields : [fields];

        var getDimensionTypeByFieldType = function (fieldType) {
            switch (fieldType) {
                case BICst.COLUMN.STRING:
                    return BICst.TARGET_TYPE.STRING;
                case BICst.COLUMN.NUMBER:
                    return BICst.TARGET_TYPE.NUMBER;
                case BICst.COLUMN.DATE:
                    return BICst.TARGET_TYPE.DATE;
            }
        };

        var currentID = [];
        BI.each(fields, function (idx, field) {
            var id = BI.UUID();
            var type = field["field_type"];

            dimensions[id] = {
                name: BI.Func.createDistinctName(dimensions, field["field_name"]),
                _src: {
                    field_name: field["field_name"]
                },
                type: getDimensionTypeByFieldType(type),
                used: true
            };

            var assertTargetSummary = function (type) {
                switch (type) {
                    case BICst.COLUMN.NUMBER:
                        return {
                            type: BICst.SUMMARY_TYPE.SUM
                        };
                    case BICst.COLUMN.STRING:
                    case BICst.COLUMN.DATE:
                        return {
                            type: BICst.SUMMARY_TYPE.COUNT
                        }
                }
            };

            var assertDimensionSummary = function (type) {
                switch (type) {
                    case BICst.COLUMN.NUMBER:
                        return {
                            type: BICst.GROUP.AUTO_GROUP
                        };
                    case BICst.COLUMN.STRING:
                        return {
                            type: BICst.GROUP.ID_GROUP
                        };
                    case BICst.COLUMN.DATE:
                        return {
                            type: BICst.GROUP.M
                        };
                }
            };

            regionType == BICst.REGION.DIMENSION1 ? dimensions[id]["group"] = assertDimensionSummary(type) : dimensions[id]["group"] = assertTargetSummary(type);
            view[regionType] || (view[regionType] = []);
            view[regionType].push(id);
            currentID.push(id);
        });
        this.dimensions = dimensions;
        this.view = view;
        this.editing = currentID;
    },

    /**
     * 修改dimension
     * @param sorted
     */
    setSortBySortInfo: function (sorted) {
        var view = this.getView();
        var dims = sorted.dimensions;
        var type = sorted.regionType;
        view[type] = dims;
        this.view = view;
    },

    /**
     * 修改dimension
     * @param id
     * @param info
     */
    setDimensionByDimensionInfo: function (id, info) {
        this.dimensions[id] = info;
    },

    getEditing: function () {
        return this.editing;
    },

    getValue: function () {
        return {
            etl_type: "group",
            etl_value: {
                dimensions: this.dimensions,
                view: this.view
            },
            connection_name: BICst.CONNECTION.ETL_CONNECTION,
            tables: [this.getTableStructure()]
        }
    },

    populate: function (info) {
        var etlValue = info.tableInfo.etl_value;
        this.reopen = info.reopen;
        this.dimensions = info.reopen === true ? etlValue.dimensions : {};
        this.view = info.reopen === true ? etlValue.view : {};
        this.tables = info.tableInfo.tables;
        this.old_tables = BI.extend(info.tableInfo, {id: BI.UUID()});
        this.isGenerated = info.isGenerated;
    },

    getDimensionNameById: function (id) {
        return this.dimensions[id].name;
    },

    setDimensionNameById: function (id, name) {
        this.dimensions[id].name = name;
    },

    getDimensionGroupById: function (id) {
        return this.dimensions[id].group;
    },

    setDimensionGroupById: function (id, group) {
        this.dimensions[id].group = group;
    },

    getDimensionUsedById: function (id) {
        return this.dimensions[id].used;
    },

    setDimensionUsedById: function (id, used) {
        this.dimensions[id].used = used;
    },

    getTextByType: function (id, groupOrSummary, fieldtype) {
        var list = [];
        var obj = this.dimensions[id].group;
        if (groupOrSummary === 1) {
            switch (fieldtype) {
                case BICst.COLUMN.STRING:
                    list = BICst.CONF_STATISTIC_STRING;
                    break;
                case BICst.COLUMN.NUMBER:
                    list = BICst.CONF_STATISTIC_NUMBER;
                    break;
                case BICst.COLUMN.DATE:
                    list = BICst.CONF_STATISTIC_DATE;
            }
        }
        if (groupOrSummary === 0) {
            switch (fieldtype) {
                case BICst.COLUMN.STRING:
                    list = BICst.CONF_GROUP_STRING;
                    break;
                case BICst.COLUMN.NUMBER:
                    list = BICst.CONF_GROUP_NUMBER;
                    break;
                case BICst.COLUMN.DATE:
                    list = BICst.CONF_GROUP_DATE;
                    break;
            }
        }
        var result = BI.find(list, function (idx, item) {
            return item.value === obj.type || (item.value === BICst.GROUP.CUSTOM_NUMBER_GROUP && obj.type === BICst.GROUP.AUTO_GROUP);
        });
        result = result || {};
        return result.text;
    },

    getValuesForCustomGroup: function (id, callback) {
        BI.Utils.getConfDataByField(this.getTableStructure(), this.getDimension(id)._src.field_name, {}, function (unGroupedFields) {
            callback(unGroupedFields)
        });
    },

    getMinMaxValueForNumberCustomGroup: function (id, callback) {
        BI.Utils.getConfNumberFieldMaxMinValue(this.getTableStructure(), this.getDimension(id)._src.field_name, function (res) {
            callback(res);
        })
    }
});
