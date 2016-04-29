BI.AnalysisETLOperatorAddColumnFormulaPaneController = BI.inherit(BI.MVCController, {


    populate : function (widget, model) {
        widget.formula.populate(model.get(ETLCst.FIELDS))
        widget.formula.setValue(model.get("formula") || "")
        widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, widget.formula.checkValidation());
    },

    setFormula : function (v, widget, model) {
       model.set("formula", v)
    }

})