/**
 * Created by 小灰灰 on 2016/4/18.
 */
BI.AnalysisETLOperatorUsePartPaneController = BI.inherit(BI.MVCController, {
    populate : function (widget, model) {
        var parent = model.get(ETLCst.PARENTS)[0];
        widget.fieldList.populate(parent[ETLCst.FIELDS]);
        var value = model.get('operator');
        if (!BI.isNull(value)){
            widget.fieldList.setValue(value);
        }
        widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, this.isValid(widget, model))
    },

    isValid : function (widget, model) {
        var parent = model.get(ETLCst.PARENTS)[0];
        return parent[ETLCst.FIELDS].length !== widget.fieldList.getValue().assist.length;
    },
    
    update : function (widget, model) {
        var newFields = [];
        var value = widget.fieldList.getValue();
        BI.each(model.get(ETLCst.PARENTS)[0].fields, function (i, item) {
            if (BI.indexOf(value.assist, item.field_name) > -1){
                newFields.push(item);
            }
        })
        var table = model.update();
        table.etlType = ETLCst.ETL_TYPE.USE_PART_FIELDS;
        table.fields = newFields;
        table.operator = value;
        return table;
    }
})