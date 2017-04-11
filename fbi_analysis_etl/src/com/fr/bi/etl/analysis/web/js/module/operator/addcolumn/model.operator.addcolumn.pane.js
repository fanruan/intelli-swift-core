/**
 * Created by windy on 2017/4/11.
 */
BI.AnalysisETLOperatorAddColumnPaneModel = BI.inherit(BI.OB, {


    _init : function () {
        BI.AnalysisETLOperatorAddColumnPaneModel.superclass._init.apply(this, arguments);
        this.populate();
    },

    get: function(key){
        return this.options[key];
    },

    set: function(key, value){
        this.options[key] = value;
    },

    unset: function(key){
        delete this.options[key];
    },

    getValue: function(key){
        return BI.deepClone(this.options[key]);
    },

    getAddColumns : function () {
        return this.get(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY)
    },

    addColumn : function (column) {
        var columns = this.get(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY);
        columns.push(column)
    },

    editColumn : function (column, oldName) {
        var columns = this.get(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY);
        var oldColumn = BI.find(columns, function (idx, item) {
            return item.fieldName === oldName;
        });
        if(BI.isNotNull(oldColumn)) {
            BI.extend(oldColumn, column)
        } else {
            console.log("can't find old column:" + oldName)
        }
    },


    deleteColumnByName : function (name) {
        var columns = this.get(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY);
        var res = [];
        BI.each(columns, function (idx, item) {
            if(item.fieldName !== name) {
                res.push(item)
            }
        })
        this.set(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY, res);
        return res;
    },


    getColumnByName : function (name) {
        var columns = this.get(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY);
        return BI.deepClone(BI.find(columns, function (idx, item) {
            return item.fieldName === name
        }))
    },

    createFields : function () {
        if(this.valid === true) {
            var parent = this.get(ETLCst.PARENTS)[0];
            return BI.concat(parent[ETLCst.FIELDS], BI.map(this.getAddColumns(), function (idx, item) {
                return {
                    fieldName : item.fieldName,
                    fieldType : item.fieldType
                }
            }))
        } else {
            return []
        }

    },

    isDefaultValue : function () {
        return this.getAddColumns().length === 0
    },

    setValid : function (valid) {
        this.valid = valid;
    },

    isValid : function (valid) {
        return this.valid;
    },

    check : function () {
        var parent = this.get(ETLCst.PARENTS)[0];
        var self = this;
        var columns = this.get(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY);
        var found = [false, ""];
        BI.some(columns, function(i ,column){
            switch (column.add_column_type){
                case BICst.ETL_ADD_COLUMN_TYPE.FORMULA :
                    found =  self._checkFormula(column, parent[ETLCst.FIELDS])
                    return found[0];
                case BICst.ETL_ADD_COLUMN_TYPE.DATE_DIFF :
                    var fields = [];
                    var f = column.item['firstField'];
                    if(f !== ETLCst.SYSTEM_TIME) {
                        fields.push(f);
                    }
                    f = column.item['secondField'];
                    if(f !== ETLCst.SYSTEM_TIME) {
                        fields.push(f);
                    }
                    found = self._checkField(fields, parent[ETLCst.FIELDS],column.fieldName, BICst.COLUMN.DATE);
                    return found[0];
                case BICst.ETL_ADD_COLUMN_TYPE.DATE_MONTH :
                case BICst.ETL_ADD_COLUMN_TYPE.DATE_SEASON :
                case BICst.ETL_ADD_COLUMN_TYPE.DATE_YEAR :
                    found = self._checkField([column.item['field']], parent[ETLCst.FIELDS],column.fieldName,BICst.COLUMN.DATE)
                    return found[0];
                case BICst.ETL_ADD_COLUMN_TYPE.EXPR_CPP:
                case BICst.ETL_ADD_COLUMN_TYPE.EXPR_CPP_PERCENT:
                    var fields = [];
                    fields.push(column.item['field'])
                    fields.push(column.item['monthSeason'])
                    fields.push(column.item['period'])
                    found =  self._checkField(fields, parent[ETLCst.FIELDS],column.fieldName,BICst.COLUMN.NUMBER);
                    return found[0];
                case BICst.ETL_ADD_COLUMN_TYPE.EXPR_LP:
                case BICst.ETL_ADD_COLUMN_TYPE.EXPR_LP_PERCENT:
                    var fields = [];
                    fields.push(column.item['field'])
                    fields.push(column.item['period'])
                    found = self._checkField(fields, parent[ETLCst.FIELDS],column.fieldName,BICst.COLUMN.NUMBER)
                    return found[0];
                case BICst.ETL_ADD_COLUMN_TYPE.EXPR_ACC:
                case BICst.ETL_ADD_COLUMN_TYPE.EXPR_RANK:
                case BICst.ETL_ADD_COLUMN_TYPE.EXPR_SUM:
                    found =  self._checkField([column.item['field']], parent[ETLCst.FIELDS],column.fieldName,BICst.COLUMN.NUMBER);
                    return found[0];
                case BICst.ETL_ADD_COLUMN_TYPE.GROUP:
                    return BI.some(column.item['items'], function (i, item) {
                        found =  self._checkField([item['field']["value"]], parent[ETLCst.FIELDS],column.fieldName, item['field']['fieldType'])
                        if(found[0] === true) {
                            return true;
                        }
                    })
            }
        })
        this.setValid(!found[0])
        return found;
    },

    _checkFormula : function (column, fields) {
        var fs = BI.Utils.getFieldsFromFormulaValue(column.item.formula);
        var msg = ""
        var found = false;
        var lostField = BI.find(fs, function (i, field) {
            if(BI.isNull(BI.find(fields, function (idx, f) {
                    return f.fieldName === field;
                }))){
                return field
            }
        })
        if (BI.isNotNull(lostField)){
            msg = BI.i18nText('BI-New_Column_Name') + column.fieldName + BI.i18nText('BI-Formula_Valid') + lostField + BI.i18nText('BI-Not_Fount');
            found =  true;
        }
        return [found, msg];
    },

    _checkField : function (dates, fields, columnName, type) {
        var msg = "";
        var found =  BI.some(dates, function (i, date) {
            var f = BI.find(fields, function (i, field) {
                return date === field.fieldName;
            });
            if (BI.isNull(f)){
                msg = BI.i18nText('BI-New_Column_Name') + columnName + '' + date + BI.i18nText('BI-Not_Fount');
                return true;
            } else  if (f.fieldType !== type){
                msg = BI.i18nText('BI-New_Column_Name') + columnName + '' + date + BI.i18nText('BI-Illegal_Field_Type')
                return true;
            }
        })
        return [found, msg]
    },

    update : function () {
        var v = BI.deepClone(this.options);
        v.etlType = ETLCst.ETL_TYPE.ADD_COLUMN;
        v[ETLCst.FIELDS] = this.createFields();
        v.operator = {};
        v.operator[BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY] = v[BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY];
        delete v[BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY];
        return v;
    },

    populate: function(model){
        this.options = model || {};
        var operator = this.get('operator') || {};
        this.set(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY, operator[BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY] || [])
    }
})
BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY = "columns";
ETLCst.OPERATOR_MODEL_CLASS[ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN] = BI.AnalysisETLOperatorAddColumnPaneModel;