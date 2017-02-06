BI.AnalysisETLOperatorAddColumnFormulaPaneController = BI.inherit(BI.MVCController, {

    _checkCanSave : function (widget, model) {
        var value = model.get('formula');
        if (BI.isNull(value) || BI.isEmptyString(value)){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Formula_Valid'));
        } else {
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true);
        }
    },

    _formatFormulaItems: function(){
        var fields = model.get(ETLCst.FIELDS);
        var fieldItems = [[], [], []];
        BI.each(fields, function (i, item) {
            var index = 0;
            switch (item.field_type){
                case BICst.COLUMN.STRING:
                    index = 1;
                    break;
                case BICst.COLUMN.NUMBER:
                case BICst.COLUMN.COUNTER:
                    index = 0;
                    break;
                case BICst.COLUMN.DATE:
                    index = 2;
                    break;
            }
            fieldItems[index].push({
                text : item.field_name,
                value : item.field_name,
                fieldType : item.field_type
            })
        });
        return fieldItems;
    },

    populate : function (widget, model) {
        widget.formula.populate(this._formatFormulaItems())
        widget.formula.setValue(model.get("formula") || "")
        this._checkCanSave(widget, model);
    },

    setFormula : function (v, widget, model) {
        model.set("formula", v);
        this._checkCanSave(widget, model);
    }

})