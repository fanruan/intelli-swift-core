/**
 * Created by 小灰灰 on 2016/4/7.
 */
BI.AnalysisETLMainController = BI.inherit(BI.MVCController, {

    doCancel : function (widget, model) {
        var self = this;
        BI.Msg.confirm(BI.i18nText("BI-Cancel"), BI.i18nText("BI-Etl_Cancel_Warning"), function (v) {
            if(v === true) {
                self._hideView(widget);
                self.resetPoolCurrentUsedTables();
            }
        });
    },

    _hideView : function (widget) {
        widget.setVisible(false);
    },

    _showView : function (widget) {
        widget.setVisible(true);
    },
    
    doSave : function (widget, model) {
        var self = this;
        BI.ETLReq.reqSaveTable(model.update(), function () {
            self._hideView(widget)
        });
    },

    getSheetLength: function(widget, model){
        return model.getSheetLength()
    },

    setSaveInfo: function(name, desc, widget, model){
        model.set('id', BI.UUID());
        model.set('name', name);
        model.set('describe', desc);
    },

    getId: function(widget, model){
        return model.get('id');
    },

    getModelJSON: function(widget, model){
        return model.update();
    },

    getTableDefaultName: function(widget, model){
        return model.getTableDefaultName();
    },

    resetPoolCurrentUsedTables: function() {
        Pool.current_edit_etl_used = [];
    },
    
    populate : function (widget, model) {
        this._showView(widget)
    }
})