BI.AnalysisETLOperatorAddColumnFormulaPaneController = BI.inherit(BI.MVCController, {

    _checkCanSave : function (widget, model) {
        var value = model.get('formula');
        if (BI.isNull(value) || BI.isEmptyString(value)){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Formula_Valid'));
        } else {
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true);
        }
    },
    populate : function (widget, model) {
        widget.formula.populate(model.get(ETLCst.FIELDS))
        widget.formula.setValue(model.get("formula") || "")
        this._checkCanSave(widget, model);
    },

    setFormula : function (v, widget, model) {
        model.set("formula", v);
        this._checkCanSave(widget, model);
    }

})