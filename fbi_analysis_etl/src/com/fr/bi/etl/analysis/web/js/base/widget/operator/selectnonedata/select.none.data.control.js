
BI.AnalysisETLOperatorSelectNoneDataController = BI.inherit(BI.AnalysisETLOperatorAbstractController, {

    _init : function() {
        BI.AnalysisETLOperatorSelectNoneDataController.superclass._init.apply(this, arguments);
        this._editing = false;
    },


    refreshCenterState : function(widget) {
        widget.center.setEnable(!this._editing )
    },
    
    populate : function (widget, model) {
        BI.AnalysisETLOperatorSelectNoneDataController.superclass.populate.apply(this, arguments);
        this.refreshCenterState(widget);
    }
})