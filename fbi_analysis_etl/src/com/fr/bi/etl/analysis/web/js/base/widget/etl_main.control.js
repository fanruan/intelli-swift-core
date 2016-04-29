/**
 * Created by 小灰灰 on 2016/4/7.
 */
BI.AnalysisETLMainController = BI.inherit(BI.MVCController, {
    
    _showWarningPop : function (widget, model) {
        var self = this;
        var warningPopover = BI.createWidget({
            type: "bi.etl_table_name_warning_popover",
        });
        warningPopover.on(BI.ETLTableNamePopover.EVENT_CHANGE, function () {
            self._showNamePop(widget, model);
        });
        BI.Popovers.remove("etlTableWarning");
        BI.Popovers.create("etlTableWarning", warningPopover, {width : 400, height : 320}).open("etlTableWarning");
    },

    _showNamePop : function (widget, model) {
        var self = this;
        var namePopover = BI.createWidget({
            type: "bi.etl_table_name_popover",
        });
        namePopover.on(BI.ETLTableNamePopover.EVENT_CHANGE, function (v) {
            model.set('id', BI.UUID());
            model.set('table_name', v);
            self._doSave(widget, model);
        });
        BI.Popovers.remove("etlTableName");
        BI.Popovers.create("etlTableName", namePopover, {width : 400, height : 320}).open("etlTableName");
        namePopover.populate(model.getTableDefaultName());
        namePopover.setTemplateNameFocus();
    },

    _doSave : function (widget, model) {
        BI.ETLReq.reqSaveTable(model.update(), function () {
            widget.setVisible(false);
        });
    },

    save : function (widget, model) {
        if (model.getSheetLength() > 1){
            this._showWarningPop(widget, model);
        } else if (BI.isNull(model.get('id'))){
            this._showNamePop(widget, model);
        } else {
            this._doSave(widget, model);
        }
    },
    
    populate : function (widget, model) {
        widget.setVisible(true);
    }
})