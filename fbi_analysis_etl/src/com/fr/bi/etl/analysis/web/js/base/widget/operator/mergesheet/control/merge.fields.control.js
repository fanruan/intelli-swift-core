BI.AnalysisETLMergeSheetFieldsController = BI.inherit(BI.MVCController, {

    populate : function (widget, model) {
        widget.joinTableFields.populate(model.getAllTables(), model.getValue(ETLCst.FIELDS));
    },


    addEmptyMerge: function (widget, model) {
        var joinFields = model.getValue(ETLCst.FIELDS);
        joinFields.push([BI.TableAddUnion.UNION_FIELD_NULL, BI.TableAddUnion.UNION_FIELD_NULL]);
        model.setValue(ETLCst.FIELDS, joinFields)
        this.populate(widget, model)
        return joinFields.length;
    },

    isValid : function (widget, model) {
        return model.isValid();
    },

    removeMergeField : function (index, widget, model) {
        var joinFields = model.getValue(ETLCst.FIELDS);
        BI.removeAt(joinFields, index);
        model.setValue(ETLCst.FIELDS, joinFields)
        this.populate(widget, model)
        return joinFields.length;
    },

    changeMergeField : function (row, col, nValue, widget, model) {
        var joinFields = model.getValue(ETLCst.FIELDS);
        BI.each(joinFields, function(i, fields){
            fields[col] === nValue && (joinFields[i][col] = -1);
        });
        joinFields[row][col] = nValue;
        model.setValue(ETLCst.FIELDS, joinFields);
        this.populate(widget, model)
        return joinFields.length;
    }

})
