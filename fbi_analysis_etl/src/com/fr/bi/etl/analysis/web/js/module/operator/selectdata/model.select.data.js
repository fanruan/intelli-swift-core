/**
 * Created by windy on 2017/4/6.
 */
BI.AnalysisETLOperatorSelectDataModel = BI.inherit(BI.OB, {


    _init: function () {
        BI.AnalysisETLOperatorSelectDataModel.superclass._init.apply(this, arguments);
        //BI.AnalysisETLOperatorSelectDataModel.KEY后台已存这里暂时注释不需要
        // var operator = this.get("operator");
        // var fields = [];
        // if(BI.isNotNull(operator)) {
        //     var dimensions = operator["dimensions"];
        //     var self = this;
        //     BI.each(operator["view"][BICst.REGION.DIMENSION1], function (idx, item) {
        //         var dim = dimensions[item];
        //         var fieldType = dim["type"];
        //         var group = dim["group"]
        //         if(fieldType !== BICst.TARGET_TYPE.DATE) {
        //             group = null;
        //         }
        //         fieldType = self._getTypeFromTargetType(fieldType, group)
        //         fields.push({
        //             "fieldName":dim["name"],
        //             "fieldType" : fieldType,
        //             "id": dim["_src"]["fieldId"],
        //             "uid" : item,
        //             "group" : BI.isNull(group) ? null : group["type"]
        //         })
        //     })
        // }
        var fields = this.get(BI.AnalysisETLOperatorSelectDataModel.KEY) || [];
        this.set(BI.AnalysisETLOperatorSelectDataModel.KEY, fields);
        //this.set("operator", this._buildDetailTableModel(BI.AnalysisETLOperatorSelectDataModel.KEY));
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, fields);
        this.save();
    },

    //这get, set, getValue三个方法会删
    get: function(key){
        return this.options[key];
    },

    set: function(key, value){
        this.options[key] = value;
    },

    getValue: function(key){
        return BI.deepClone(this.options[key]);
    },


    save: function () {
        this.set(BI.AnalysisETLOperatorSelectDataModel.KEY, this.getTempFields());
    },

    addField: function (f) {
        var fieldId = f.fieldId || f;
        var name = BI.Utils.getFieldNameByID(fieldId);
        var fieldType = BI.Utils.getFieldTypeByID(fieldId)
        var group = null;
        if (fieldType === BICst.COLUMN.DATE) {
            group = f.group
            group = group.type;
            name += "(" + BICst.DATE_GROUP[group] + ")";
            fieldType = this._createNewFieldType(group);
        }
        var fieldName = this.createDistinctName(name);
        var field = {
            "fieldName": fieldName,
            "fieldType": fieldType,
            "id": fieldId,
            "uid": BI.UUID()
        }
        if (BI.isNotNull(group)) {
            field["group"] = group;
        }
        this._addField(field)
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
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        tempFields.push(field);
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, tempFields);
    },

    cancel: function () {
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, this.getValue(BI.AnalysisETLOperatorSelectDataModel.KEY))
    },

    getTempFields: function () {
        return this.getValue(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
    },

    createDistinctName: function (name) {
        var oldNames = [];
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        BI.each(tempFields, function (i, item) {
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
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        var field = tempFields[oldIndex];
        BI.removeAt(tempFields, oldIndex);
        tempFields.splice(newIndex, 0, field);
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, tempFields)
    },

    removeAt: function (v) {
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        BI.removeAt(tempFields, v);
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, tempFields)
    },

    rename: function (index, name) {
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        tempFields[index].fieldName = name;
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, tempFields)
    },

    needCancel: function () {
        return !BI.isEqual(this.get(BI.AnalysisETLOperatorSelectDataModel.KEY), this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY))
    },

    checkNameValid: function (index, name) {
        var self = this, valid = true;
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        BI.some(tempFields, function (i, item) {
            if (i != index && item.fieldName == name) {
                valid = false;
                return true;
            }
        });
        return valid;
    },

    _buildDetailTableModel: function (key) {
        var res = {
            filterValue: {},
            page: -1,
            type: BICst.WIDGET.DETAIL
        };
        var fields = this.get(key);
        var tableIds = this._getTablesFromFields(fields)
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
            var tableId = BI.Utils.getTableIdByFieldID(item.id)
            dm[fTable] = {
                target_relation: BI.Utils.getPathsFromTableAToTableB(tableId, fTable)[0] || []
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
        })
        res["view"] = view;
        res["dimensions"] = dimensions
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
        var fieldType = BI.Utils.getFieldTypeByID(fieldId)
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
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        return this._getTablesFromFields(tempFields);
    },


    _getTablesFromFields: function (fields) {
        var res = {};
        BI.each(fields, function (idx, item) {
            var tableId = BI.Utils.getTableIdByFieldID(item.id)
            res[tableId] = true;
        })
        return BI.map(res, function (idx, item) {
            return idx;
        })
    },

    update4Preview: function () {
        var v = BI.extend({}, this.options, {
            etlType: ETLCst.ETL_TYPE.SELECT_DATA
        })
        v[BI.AnalysisETLOperatorSelectDataModel.KEY] = v[BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY];
        v["operator"] = this._buildDetailTableModel(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        delete v[BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY]
        return v;
    },

    update: function () {
        var v = BI.extend({}, this.options, {
            etlType: ETLCst.ETL_TYPE.SELECT_DATA
        })
        v["operator"] = this._buildDetailTableModel(BI.AnalysisETLOperatorSelectDataModel.KEY);
        delete v[BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY]
        return v;
    },

    populate: function(model){
        this.options = BI.extend(this.options, model);
    }

})
BI.AnalysisETLOperatorSelectDataModel.KEY = ETLCst.FIELDS;
BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY = "temp_" + BI.AnalysisETLOperatorSelectDataModel.KEY;