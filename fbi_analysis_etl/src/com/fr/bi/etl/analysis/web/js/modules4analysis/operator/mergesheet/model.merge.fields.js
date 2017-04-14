/**
 * Created by windy on 2017/4/12.
 */
BI.AnalysisETLMergeSheetFieldsModel = BI.inherit(BI.OB, {

    _init : function () {
        BI.AnalysisETLMergeSheetFieldsModel.superclass._init.apply(this, arguments)
        this.populate(this.options)
    },

    get: function(key){
        return this.options[key];
    },

    set: function(key, value){
        this.options[key] = value;
    },

    getCopyValue: function(key){
        return BI.deepClone(this.options[key]);
    },

    reset : function () {
        var fields = this._initFields();
        this.set(ETLCst.FIELDS, fields);
    },

    isValid : function () {
        var joinFields = this.getCopyValue(ETLCst.FIELDS);
        if(joinFields.length === 0) {
            return false;
        }
        var valid = true;
        var tables = this.get("tables")
        var table1 = tables[0];
        var table2 = tables[1];
        var table1Fields = BI.Utils.getFieldArrayFromTable(table1);
        var table2Fields = BI.Utils.getFieldArrayFromTable(table2);
        BI.some(joinFields, function (idx, item) {
            if(item[0] === BI.TableAddUnion.UNION_FIELD_NULL || item[1] === BI.TableAddUnion.UNION_FIELD_NULL
                || table1Fields[item[0]].fieldType !== table2Fields[item[1]].fieldType) {
                valid = false;
                return true;
            }
        })
        return valid;
    },

    getAllTables : function () {
        var tables = this.getCopyValue("tables")
        return BI.map(tables, function (idx, item) {
            return BI.extend(item, {
                fields:[item[ETLCst.FIELDS] || [],[],[]]
            })
        })
    },

    getAllFields : function () {
        var mergeFields = this.getCopyValue(ETLCst.FIELDS);
        var tables = this.get("tables")
        var table1 = tables[0];
        var table2 = tables[1];
        var table1Fields = BI.Utils.getFieldArrayFromTable(table1);
        var table2Fields = BI.Utils.getFieldArrayFromTable(table2);
        var left = [];
        BI.each(table1Fields, function (idx, item) {
            if(BI.isNull(BI.find(mergeFields, function (i, field) {
                    return field[0] === idx;
                }))){
                left.push([idx, -1])
            }
        })
        BI.each(table2Fields, function (idx, item) {
            if(BI.isNull(BI.find(mergeFields, function (i, field) {
                    return field[1] === idx;
                }))){
                left.push([-1, idx])
            }
        })
        return BI.concat(mergeFields, left);
    },

    getFieldsLength : function() {
        var joinFields = this.getCopyValue(ETLCst.FIELDS);
        if(BI.isNotNull(joinFields)) {
            return joinFields.length;
        }
        else{
            return 0;
        }
    },

    /**
     * fields为空时候的默认生成一个
     * @returns {Array}
     * @private
     */
    _initFields: function(){
        var self = this;
        var tables = this.get("tables")
        var table1 = tables[0];
        var table2 = tables[1];
        var joinFields = [];
        var table1Fields = BI.Utils.getFieldArrayFromTable(table1);
        var table2Fields = BI.Utils.getFieldArrayFromTable(table2);
        BI.each(table1Fields, function(i, item){
            var index = self._getIndexByFieldNameAndType(item, table2Fields);
            if(index > -1) {
                joinFields.push([i, index])
            }
        });
        return joinFields;
    },


    _getIndexByFieldNameAndType : function (field, table2Fields) {
        var self = this;
        return BI.findIndex(table2Fields, function (idx, item) {
            return self._isEqualNameType(field, item)
        })
    },

    _isEqualNameType : function(field1, field2){
        if(BI.isNotNull(field1) && BI.isNotNull(field2)){
            return field1.fieldType === field2.fieldType &&
                field1.fieldName === field2.fieldName;
        }
    },

    populate: function(model){
        this.options = model || {};
        var fields = this.get(ETLCst.FIELDS);
        if(BI.isNull(fields) && BI.isNotNull(this.get("tables"))){
            this.reset();
        }
    },

    update: function(){
        return BI.deepClone(this.options);
    }
})