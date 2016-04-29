BI.AnalysisETLMergeSheetTypeController = BI.inherit(BI.MVCController,  {


    
    populate : function (widget, model) {
        var value = model.get("merge") || BICst.ETL_JOIN_STYLE.LEFT_JOIN;
        model.set("merge", value)
        widget.union.setSelected(value === ETLCst.ETL_UNION_STYLE)
        widget.join_type.setValue(value)
    },
    
    setValue : function (v, widget, model) {
        model.set("merge", v)
        this.populate(widget, model)
    }
    
});