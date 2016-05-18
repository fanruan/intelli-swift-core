BI.AnalysisETLOperatorSelectDataModel = BI.inherit(BI.MVCModel, {



    _init : function () {
        BI.AnalysisETLOperatorSelectDataModel.superclass._init.apply(this, arguments);
        //BI.AnalysisETLOperatorSelectDataModel.KEY后台已存这里暂时注释不需要
        // var operator = this.get("operator");
        // var fields = [];
        // if(BI.isNotNull(operator)) {
        //     var dimensions = operator["dimensions"];
        //     var self = this;
        //     BI.each(operator["view"][BICst.REGION.DIMENSION1], function (idx, item) {
        //         var dim = dimensions[item];
        //         var field_type = dim["type"];
        //         var group = dim["group"]
        //         if(field_type !== BICst.TARGET_TYPE.DATE) {
        //             group = null;
        //         }
        //         field_type = self._getTypeFromTargetType(field_type, group)
        //         fields.push({
        //             "field_name":dim["name"],
        //             "field_type" : field_type,
        //             "id": dim["_src"]["field_id"],
        //             "uid" : item,
        //             "group" : BI.isNull(group) ? null : group["type"]
        //         })
        //     })
        // }
        var fields = this.get(BI.AnalysisETLOperatorSelectDataModel.KEY) || []
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, fields);
        this.save();
    },


    save : function () {
        this.set(BI.AnalysisETLOperatorSelectDataModel.KEY, this.getTempFields())
    },

    addField : function (fieldId) {
        var name = BI.Utils.getFieldNameByID(fieldId);
        var fieldType = BI.Utils.getFieldTypeByID(fieldId)
        var group = null;
        if(fieldType === BICst.COLUMN.DATE) {
            group = fieldId.group
            name += "(" + this._createDateString(group)+ ")";
            fieldType = this._createNewFieldType(group);
            fieldId = fieldId.field_id
        }
        var fieldName =  this.createDistinctName(name);
        var field = {
            "field_name":fieldName,
            "field_type" : fieldType,
            "id":fieldId,
            "uid":BI.UUID()
        }
        if(BI.isNotNull(group)) {
            field["group"] = group;
        }
        this._addField(field)
    },


    
    _createNewFieldType : function (group) {
        switch (group) {
            case BICst.GROUP.Y : return BICst.COLUMN.NUMBER;
            case BICst.GROUP.S : return BICst.COLUMN.NUMBER;
            case BICst.GROUP.M : return BICst.COLUMN.NUMBER;
            case BICst.GROUP.W : return BICst.COLUMN.NUMBER;
            case BICst.GROUP.YMD : return BICst.COLUMN.DATE;
        }
    },


    _createDateString : function (group) {
        switch (group) {
            case BICst.GROUP.Y : return BI.i18nText("BI-Year_Fen");
            case BICst.GROUP.S : return BI.i18nText("BI-Quarter");
            case BICst.GROUP.M : return BI.i18nText("BI-Multi_Date_Month");
            case BICst.GROUP.W : return BI.i18nText("BI-Week_XingQi");
            case BICst.GROUP.YMD : return BI.i18nText("BI-Date");
        }
    },
    
    _addField : function (field) {
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        tempFields.push(field);
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, tempFields)
    },
    
    cancel : function () {
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, this.getValue(BI.AnalysisETLOperatorSelectDataModel.KEY))
    },

    getTempFields: function () {
        return this.getValue(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
    },

    createDistinctName : function (name) {
        var oldNames  = [];
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        BI.each(tempFields, function(i, item){
            if(!BI.isNull(item.field_name)){
                oldNames.push(item.field_name);
            }
        });
        return BI.Utils.createDistinctName(oldNames, name);
    },

    sort : function (oldIndex, newIndex) {
        if(newIndex > oldIndex) {
            newIndex --;
        }
        if(oldIndex === newIndex) {
            //还是原来位置 do nothing
            return
        }
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        var field = tempFields[oldIndex];
        BI.removeAt(tempFields, oldIndex);
        tempFields.splice(newIndex, 0, field);
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, tempFields)
    },

    removeAt : function (v) {
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        BI.removeAt(tempFields, v);
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, tempFields)
    },

    rename : function (index, name) {
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        tempFields[index].field_name = name;
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, tempFields)
    },

    needCancel : function () {
        return !BI.isEqual(this.get(BI.AnalysisETLOperatorSelectDataModel.KEY), this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY))
    },

    checkNameValid : function (index, name) {
        var self = this, valid = true;
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        BI.some(tempFields, function (i, item) {
            if (i != index && item.field_name == name){
                valid =  false;
                return true;
            }
        });
        return valid;
    },

    _buildDetailTableModel : function (key) {
        var res = {
            filter_value:{},
            page : -1,
            type : BICst.WIDGET.DETAIL
        };
        var fields = this.get(key);
        var tableIds = this._getTablesFromFields(fields)
        var fTable = tableIds[0];
        BI.each(tableIds, function (idx, item) {
            var relation = BI.Utils.getPathsFromTableAToTableB(item, fTable);
            if(relation.length === 0) {
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
                target_relation:BI.Utils.getPathsFromTableAToTableB(tableId, fTable)[0] || []
            };
            dimensions[item["uid"]] = {
                _src : {
                    id:item["id"] + (BI.isNull(item["group"]) ? "":item["group"]),
                    field_id:item["id"]
                },
                group :   {
                    type: BI.isNull(item["group"]) ? BICst.GROUP.NO_GROUP : item["group"]
                },
                dimension_map:dm,
                name:item["field_name"],
                type: self._getDimensionType(item["id"]),
                used:true
            }
            view[BICst.REGION.DIMENSION1].push(item["uid"])
        })
        res["view"] = view;
        res["dimensions"] = dimensions
        return res;
    },

    _getTypeFromTargetType : function (type, group) {
        switch (type)  {
            case BICst.TARGET_TYPE.NUMBER : return BICst.COLUMN.NUMBER;
            case BICst.TARGET_TYPE.DATE : return this._createNewFieldType(group["type"]);
            default : return  BICst.COLUMN.STRING;
        }
    },

    _getDimensionType : function (fieldId) {
        var fieldType = BI.Utils.getFieldTypeByID(fieldId)
        switch (fieldType)  {
            case BICst.COLUMN.NUMBER : return BICst.TARGET_TYPE.NUMBER;
            case BICst.COLUMN.DATE : return BICst.TARGET_TYPE.DATE;
            default : return  BICst.TARGET_TYPE.STRING;
        }
    },


    getTempFieldsTables : function () {
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        return this._getTablesFromFields(tempFields);
    },


    _getTablesFromFields : function (fields) {
        var res = {};
        BI.each(fields, function (idx, item) {
            var tableId = BI.Utils.getTableIdByFieldID(item.id)
            res[tableId] = true;
        })
        return BI.map(res, function (idx, item) {
            return idx;
        })
    },

    update4Preview : function () {
        var v  = BI.extend(BI.AnalysisETLOperatorSelectDataModel.superclass.update.apply(this, arguments), {
            etlType:ETLCst.ETL_TYPE.SELECT_DATA
        })
        v[BI.AnalysisETLOperatorSelectDataModel.KEY] = v[BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY];
        v["operator"] = this._buildDetailTableModel(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        delete v[BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY]
        return v;
    },

    update : function () {
        var v  = BI.extend(BI.AnalysisETLOperatorSelectDataModel.superclass.update.apply(this, arguments), {
            etlType:ETLCst.ETL_TYPE.SELECT_DATA
        })
        v["operator"] = this._buildDetailTableModel(BI.AnalysisETLOperatorSelectDataModel.KEY);
        delete v[BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY]
        return v;
    }

})
BI.AnalysisETLOperatorSelectDataModel.KEY = ETLCst.FIELDS;
BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY = "temp_" + BI.AnalysisETLOperatorSelectDataModel.KEY;