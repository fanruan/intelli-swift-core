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
            if (BI.isNull(model.get('id'))){
                this._showNamePop(widget, model);
            } else {
                this._doSave(widget, model);
            }
        });
        BI.Popovers.remove("etlTableWarning");
        BI.Popovers.create("etlTableWarning", warningPopover, {width : 400, height : 320, container:widget.element}).open("etlTableWarning");
    },

    _showNamePop : function (widget, model) {
        var self = this;
        var namePopover = BI.createWidget({
            type: "bi.etl_table_name_popover",
        });
        namePopover.on(BI.PopoverSection.EVENT_CLOSE, function () {
            BI.Layers.hide(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
        })
        namePopover.on(BI.ETLTableNamePopover.EVENT_CHANGE, function (v) {
            model.set('id', BI.UUID());
            model.set('name', v);
            self._doSave(widget, model);
        });
        BI.Popovers.remove("etlTableName");
        BI.Popovers.create("etlTableName", namePopover, {width : 400, height : 320, container: BI.Layers.create(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER)}).open("etlTableName");
        BI.Layers.show(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
        namePopover.populate(model.getTableDefaultName());
        namePopover.setTemplateNameFocus();
    },

    doCancel : function (widget, model) {
        var self = this;
        var r = false;
        BI.Msg.confirm(BI.i18nText("BI-Cancel"),"当前分析表还未保存，是否确定强制关闭？", function (v) {
            if(v === true) {
                r = true;
                self._hideView(widget)
            }
        })
        return r;
    },

    _hideView : function (widget) {
        widget.setVisible(false);
    },

    _showView : function (widget) {
        widget.setVisible(true);
    },

    _doSave : function (widget, model) {
        var self = this;
        BI.ETLReq.reqSaveTable(model.update(), function () {
            self._hideView(widget)
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
        this._showView(widget)
    }
})