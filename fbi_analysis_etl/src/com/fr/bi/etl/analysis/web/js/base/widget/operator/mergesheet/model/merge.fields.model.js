BI.AnalysisETLMergeSheetFieldsModel = BI.inherit(BI.MVCModel, {

    _init : function () {
        BI.AnalysisETLMergeSheetFieldsModel.superclass._init.apply(this, arguments)
        var fields = this.get(ETLCst.FIELDS);
        if(BI.isNull(fields)){
            this.reset();
        }
    },

    reset : function () {
        var fields = this._initFields();
        this.set(ETLCst.FIELDS, fields);
    },

    isValid : function () {
        var joinFields = this.getValue(ETLCst.FIELDS);
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
                    || table1Fields[item[0]].field_type !== table2Fields[item[1]].field_type) {
                valid = false;
                return true;
            }
        })
        return valid;
    },

    getAllTables : function () {
        var tables = this.getValue("tables")
        return BI.map(tables, function (idx, item) {
            return BI.extend(item, {
                fields:[item[ETLCst.FIELDS] || [],[],[]]
            })
        })
    },

    getAllFields : function () {
        var mergeFields = this.getValue(ETLCst.FIELDS);
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
            return field1.field_type === field2.field_type &&
                field1.field_name === field2.field_name;
        }
    }
})
