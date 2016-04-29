BI.AnalysisETLMergeSheetModel = BI.inherit(BI.MVCModel, {

    _defaultConfig: function () {
        var v =  {
            mergeType:BICst.ETL_JOIN_STYLE.LEFT_JOIN,
            fields:null,
            tables:null,
            sheets:[]
        }
        return v;
    },

    _init : function () {
        BI.AnalysisETLMergeSheetModel.superclass._init.apply(this, arguments);
        //处理格式转换
        this.set(BI.AnalysisETLMergeSheetModel.MERGE_TYPE, new BI.MVCModel({
            merge:this.options[BI.AnalysisETLMergeSheetModel.MERGE_TYPE]
        }));
        var baseTable = this.get("tables") || this.get(ETLCst.PARENTS);
        this.set("tables", baseTable)

        var tables = this.getTablesBySheetId()
        this.set(BI.AnalysisETLMergeSheetModel.MERGE_FIELDS, new BI.AnalysisETLMergeSheetFieldsModel({
            fields:this.options[BI.AnalysisETLMergeSheetModel.MERGE_FIELDS],
            tables:tables
        }))
    },

    createPreviewData : function () {
        var tables = this.getTablesBySheetId();
        return {
            left :tables[0],
            right:tables[1],
            merge : {
                fields:[this.getValue("columns")],
                mergeColumns:this.getValue("mergeColumns"),
                leftColumns : this.getValue("leftColumns")
            }
        }
    },

    rename : function (idx , name) {
        var columns = this.getValue("columns");
        columns[idx].field_name = name;
        return this.setValue("columns", columns)
    },

    getTablesBySheetId : function () {
        var sheets = this.getSheets();
        var tables = [];
        var self = this;
        BI.each(sheets, function (idx, item) {
            tables.push(self._getTableById(item))
        })
        return tables;
    },

    getMergeFieldsName : function () {
        var fields = this.get(BI.AnalysisETLMergeSheetModel.MERGE_FIELDS).getValue(ETLCst.FIELDS);
        return  this._createNameArray(fields)
    },

    _createNameArray : function (fields) {
        var tables = this.getTablesBySheetId();
        var allFields = BI.map(tables, function (idx, item) {
            return BI.Utils.getFieldArrayFromTable(item);
        })
        return BI.map(fields, function (j, item) {
            return BI.map(item, function (idx, item) {
                return allFields[idx][item].field_name
            })
        })
    },

    _getTableById : function (id) {
        return BI.find(this.get("tables"), function (idx, item) {
            return item.value === id;
        })
    },

    getAllSheets : function () {
        return BI.map(this.get("tables"), function (idx, item) {
            return {
                value: item.value,
                text: item.table_name
            }
        })
    },

    getSheets : function () {
        return this.getValue("sheets");
    },

    setCurrent2Sheet : function (v) {
        var changed =  this.setValue("sheets", v)
        if(changed === true) {
            var tables = this.getTablesBySheetId();
            var mergeModel =  this.get(BI.AnalysisETLMergeSheetModel.MERGE_FIELDS);
            mergeModel.set("tables", tables)
            mergeModel.reset()
        }
        return changed;
    },

    refreshColumnName : function () {
        var allFields = this.get(BI.AnalysisETLMergeSheetModel.MERGE_FIELDS).getAllFields();
        var columns = [];
        var mergeColumns = [];
        var leftColumns = [];
        var tables = this.getTablesBySheetId();
        var table1Fields = BI.Utils.getFieldArrayFromTable(tables[0]);
        var table2Fields = BI.Utils.getFieldArrayFromTable(tables[1]);
        var getNameArray = function (columns) {
            var array = [];
            BI.each(columns, function (idx, item) {
                array.push(item.field_name)
            })
            return array;
        }
        BI.each(allFields, function (idx, item) {
            if(item[0] > -1 && item[1] > -1) {
                mergeColumns.push({
                    index:idx,
                    mergeBy:[table1Fields[item[0]].field_name, table2Fields[item[1]].field_name]
                })
                var newFields = BI.deepClone(table1Fields[item[0]]);
                newFields.field_name = BI.Utils.createDistinctName(getNameArray(columns), newFields.field_name)
                columns.push(newFields)
            } else if(item[0] > -1) {
                leftColumns.push(idx)
                var newFields = BI.deepClone(table1Fields[item[0]]);
                newFields.field_name = BI.Utils.createDistinctName(getNameArray(columns), newFields.field_name)
                columns.push(newFields)
            } else {
                var newFields = BI.deepClone(table2Fields[item[1]]);
                newFields.field_name = BI.Utils.createDistinctName(getNameArray(columns), newFields.field_name)
                columns.push(newFields)
            }
        })
        this.set("columns", columns);
        this.set("mergeColumns", mergeColumns)
        this.set("leftColumns", leftColumns)
    },

    update : function () {
        //处理格式转换
        var json = BI.AnalysisETLMergeSheetModel.superclass.update.apply(this, arguments);
        json[BI.AnalysisETLMergeSheetModel.MERGE_TYPE] = json[BI.AnalysisETLMergeSheetModel.MERGE_TYPE]["merge"];
        json[BI.AnalysisETLMergeSheetModel.MERGE_FIELDS] =json[BI.AnalysisETLMergeSheetModel.MERGE_FIELDS][ETLCst.FIELDS]
        json[ETLCst.PARENTS] = this.getTablesBySheetId();
        json[ETLCst.FIELDS] = json["columns"]
        delete json["tables"];
        return json;
    }
})
BI.AnalysisETLMergeSheetModel.MERGE_TYPE = "mergeType";
BI.AnalysisETLMergeSheetModel.MERGE_FIELDS ="mergeFields";