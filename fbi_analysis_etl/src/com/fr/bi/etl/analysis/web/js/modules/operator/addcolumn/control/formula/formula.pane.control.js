BI.AnalysisETLOperatorAddColumnFormulaPaneController = BI.inherit(BI.MVCController, {

    _checkCanSave : function (widget, model) {
        var value = model.get('formula');
        if (BI.isNull(value) || BI.isEmptyString(value)){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Formula_Valid'));
        } else {
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true);
        }
    },

    _formatFormulaItems: function(fields){
        var fieldItems = [[], [], []];
        BI.each(fields, function (i, item) {
            var index = 0;
            switch (item.fieldType){
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
            fieldItems[index].push(item);
        });
        return fieldItems;
    },

    populate : function (widget, model) {
        widget.formula.populate(this._formatFormulaItems(model.get(ETLCst.FIELDS)))
        widget.formula.setValue(model.get("formula") || "")
        this._checkCanSave(widget, model);
    },

    setFormula : function (v, widget, model) {
        model.set("formula", v);
        this._checkCanSave(widget, model);
    }

})