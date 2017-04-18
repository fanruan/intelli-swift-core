/**
 * Created by windy on 2017/4/6.
 */
BI.AnalysisSelectDataOperatorModel = BI.inherit(BI.OB, {
    _init: function () {
        BI.AnalysisSelectDataOperatorModel.superclass._init.apply(this, arguments);
        this.etlFields = [];
        this.tempETLFields = [];
    },

    getFields: function () {
        return BI.deepClone(this.etlFields);
    },

    getTempFields: function () {
        return BI.deepClone(this.tempETLFields);
    },

    save: function () {
        this.etlFields = this.getTempFields();
    },

    addField: function (field) {
        var fieldId = field.fieldId || field;
        var name = BI.Utils.getFieldNameByID(fieldId);
        var fieldType = BI.Utils.getFieldTypeByID(fieldId);
        var group = null;
        if (fieldType === BICst.COLUMN.DATE) {
            group = field.group;
            group = group.type;
            name += "(" + BICst.DATE_GROUP[group] + ")";
            fieldType = this._createNewFieldType(group);
        }
        var fieldName = this.createDistinctName(name);
        var newField = {
            "fieldName": fieldName,
            "fieldType": fieldType,
            "id": fieldId,
            "uid": BI.UUID()
        };
        if (BI.isNotNull(group)) {
            newField["group"] = group;
        }
        this._addField(newField)
    },


    _createNewFieldType: function (group) {
        return BI.Utils.createDateFieldType(group);
    },


    _createDateString: function (group) {
        switch (group) {
            case BICst.GROUP.Y :
                return BI.i18nText("BI-Year_Fen");
            case BICst.GROUP.S :
                return BI.i18nText("BI-Basic_Quarter");
            case BICst.GROUP.M :
                return BI.i18nText("BI-Multi_Date_Month");
            case BICst.GROUP.W :
                return BI.i18nText("BI-Week_XingQi");
            case BICst.GROUP.YMD :
                return BI.i18nText("BI-Basic_Date");
        }
    },

    _addField: function (field) {
        this.tempETLFields.push(field);
    },

    cancel: function () {
        this.tempETLFields = BI.deepClone(this.etlFields);
    },

    createDistinctName: function (name) {
        var oldNames = [];
        BI.each(this.tempETLFields, function (i, item) {
            if (!BI.isNull(item.fieldName)) {
                oldNames.push(item.fieldName);
            }
        });
        return BI.Utils.createDistinctName(oldNames, name);
    },

    sort: function (oldIndex, newIndex) {
        if (newIndex > oldIndex) {
            newIndex--;
        }
        if (oldIndex === newIndex) {
            //还是原来位置 do nothing
            return
        }
        var field = this.tempETLFields[oldIndex];
        BI.removeAt(this.tempETLFields, oldIndex);
        this.tempETLFields.splice(newIndex, 0, field);
    },

    removeAt: function (v) {
        BI.removeAt(this.tempETLFields, v);
    },

    rename: function (index, name) {
        this.tempETLFields[index].fieldName = name;
    },

    needCancel: function () {
        return !BI.isEqual(this.etlFields, this.tempETLFields);
    },

    checkNameValid: function (index, name) {
        var valid = true;
        BI.some(this.tempETLFields, function (i, item) {
            if (i != index && item.fieldName == name) {
                valid = false;
                return true;
            }
        });
        return valid;
    },

    _buildDetailTableModel: function (fields) {
        var res = {
            filterValue: {},
            page: -1,
            type: BICst.WIDGET.DETAIL
        };
        var tableIds = this._getTablesFromFields(fields);
        var fTable = tableIds[0];
        BI.each(tableIds, function (idx, item) {
            var relation = BI.Utils.getPathsFromTableAToTableB(item, fTable);
            if (relation.length === 0) {
                fTable = item;
            }
        });
        var view = {};
        view[BICst.REGION.DIMENSION1] = [];
        var dimensions = {};
        var self = this;
        BI.each(fields, function (idx, item) {
            var dm = {};
            var tableId = BI.Utils.getTableIdByFieldID(item.id);
            dm[fTable] = {
                targetRelation: BI.Utils.getPathsFromTableAToTableB(tableId, fTable)[0] || []
            };
            dimensions[item["uid"]] = {
                _src: {
                    id: item["id"] + (BI.isNull(item["group"]) ? "" : item["group"]),
                    fieldId: item["id"]
                },
                group: {
                    type: BI.isNull(item["group"]) ? BICst.GROUP.NO_GROUP : item["group"]
                },
                dimensionMap: dm,
                name: item["fieldName"],
                type: self._getDimensionType(item["id"]),
                used: true
            }
            view[BICst.REGION.DIMENSION1].push(item["uid"])
        });
        res["view"] = view;
        res["dimensions"] = dimensions;
        return res;
    },

    _getTypeFromTargetType: function (type, group) {
        switch (type) {
            case BICst.TARGET_TYPE.NUMBER :
                return BICst.COLUMN.NUMBER;
            case BICst.TARGET_TYPE.DATE :
                return this._createNewFieldType(group["type"]);
            default :
                return BICst.COLUMN.STRING;
        }
    },

    _getDimensionType: function (fieldId) {
        var fieldType = BI.Utils.getFieldTypeByID(fieldId);
        switch (fieldType) {
            case BICst.COLUMN.NUMBER :
                return BICst.TARGET_TYPE.NUMBER;
            case BICst.COLUMN.DATE :
                return BICst.TARGET_TYPE.DATE;
            default :
                return BICst.TARGET_TYPE.STRING;
        }
    },


    getTempFieldsTables: function () {
        return this._getTablesFromFields(this.tempETLFields);
    },


    _getTablesFromFields: function (fields) {
        var res = {};
        BI.each(fields, function (idx, item) {
            var tableId = BI.Utils.getTableIdByFieldID(item.id);
            res[tableId] = true;
        });
        return BI.map(res, function (idx, item) {
            return idx;
        })
    },

    getValue4Preview: function () {
        var table = BI.extend({}, this.table, {
            etlType: ETLCst.ETL_TYPE.SELECT_DATA
        });
        table.etlFields = table.tempETLFields;
        table.operator = this._buildDetailTableModel(this.tempETLFields);
        delete table.tempETLFields;
        return table;
    },

    getValue: function () {
        var table = BI.extend({}, this.table, {
            etlType: ETLCst.ETL_TYPE.SELECT_DATA
        });
        table.operator = this._buildDetailTableModel(this.etlFields);
        delete table.tempETLFields;
        return table;
    },

    populate: function (table) {
        this.table = table;
    }

});