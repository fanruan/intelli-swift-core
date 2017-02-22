/**
 * Created by 小灰灰 on 2016/4/7.
 */
BI.AnalysisETLMainController = BI.inherit(BI.MVCController, {
    
    _showWarningPop : function (element, widget, model) {
        var self = this;

        if(BI.isNull(this.warningConfirmCombo)){
            this.warningConfirmCombo = BI.createWidget({
                type: "bi.bubble_combo",
                el: {},
                element: element,
                popup: {
                    type: "bi.bubble_bar_popup_view",
                    buttons: [{
                        value: BI.i18nText(BI.i18nText("BI-Sure")),
                        handler: function () {
                            self.warningConfirmCombo.hideView();
                            if (BI.isNull(model.get('id'))){
                                self._showNamePop(element, widget, model);
                            } else {
                                self._doSave(widget, model);
                            }
                        }
                    }, {
                        value: BI.i18nText("BI-Cancel"),
                        level: "ignore",
                        handler: function () {
                            self.warningConfirmCombo.hideView();
                        }
                    }],
                    el: {
                        type: "bi.vertical_adapt",
                        items: [{
                            type: "bi.label",
                            whiteSpace: "normal",
                            text: BI.i18nText("BI-ETL_Saving_Warning_Text"),
                            cls: "delete-label",
                            textAlign: "left",
                            width: 300
                        }],
                        width: 300,
                        height: 100,
                        hgap: 20
                    },
                    maxHeight: 140,
                    minWidth: 340
                }
            })
        }
        this.warningConfirmCombo.showView();
    },

    _showNamePop : function (element, widget, model) {
        var self = this;

        if(BI.isNull(this.confirmCombo)){
            this.pane = BI.createWidget({
                type: "bi.etl_rename_pane",
                renameChecker : function (v) {
                    return !BI.Utils.getAllETLTableNames().contains(v);
                }
            });
            this.pane.on(BI.ETLNamePane.EVENT_VALID, function(){
                confirmButton.setEnable(true);
            });
            this.pane.on(BI.ETLNamePane.EVENT_ERROR, function(v){
                if (BI.isEmptyString(v)) {
                    confirmButton.setWarningTitle(BI.i18nText("BI-Report_Name_Not_Null"));
                } else {
                    confirmButton.setWarningTitle(BI.i18nText("BI-Template_Name_Already_Exist"));
                }
                confirmButton.setEnable(false);
            });
            var confirmButton = BI.createWidget({
                type: "bi.button",
                height: 30,
                value: BI.i18nText(BI.i18nText("BI-Sure")),
                handler: function () {
                    self.confirmCombo.hideView();
                    model.set('id', BI.UUID());
                    model.set('name', self.pane.getValue());
                    model.set('describe', self.pane.getDesc());
                    self._doSave(widget, model);
                }
            });
            this.confirmCombo = BI.createWidget({
                type: "bi.bubble_combo",
                el: {},
                element: element,
                popup: {
                    type: "bi.bubble_bar_popup_view",
                    buttons: [confirmButton, {
                        value: BI.i18nText("BI-Cancel"),
                        level: "ignore",
                        handler: function () {
                            self.confirmCombo.hideView();
                        }
                    }],
                    el: {
                        type: "bi.vertical_adapt",
                        items: [self.pane],
                        width: 400,
                        height: 300,
                        hgap: 20
                    },
                    maxHeight: 340,
                    minWidth: 400
                }
            });
        }
        this.pane.populate(model.getTableDefaultName());
        this.confirmCombo.showView();
        this.pane.setTemplateNameFocus();
    },

    doCancel : function (widget, model) {
        var self = this;
        BI.Msg.confirm(BI.i18nText("BI-Cancel"), BI.i18nText("BI-Etl_Cancel_Warning"), function (v) {
            if(v === true) {
                self._hideView(widget);
                self._resetPoolCurrentUsedTables();
            }
        });
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

    save : function (element, widget, model) {
        var self = this;
        if (model.getSheetLength() > 1){
            this._showWarningPop(element, widget, model);
        } else if (BI.isNull(model.get('id'))){
            this._showNamePop(element, widget, model);
        } else {
            BI.ETLReq.reqCheckTableInUse(model.update(), function(data){
                var items = data["usedTemplate"] || [];
                items = BI.map(items, function(idx, name){
                    return {
                        type: "bi.label",
                        text: name,
                        title: name,
                        cls: "delete-label",
                        textAlign: "center",
                        width: 300
                    }
                });
                if(BI.isNull(self.interfereConfirmPane)){
                    self.interfereConfirmPane = BI.createWidget({
                        type: "bi.bubble_combo",
                        el: {},
                        element: element,
                        popup: {
                            type: "bi.bubble_bar_popup_view",
                            buttons: [{
                                value: BI.i18nText(BI.i18nText("BI-Sure")),
                                handler: function () {
                                    self.interfereConfirmPane.hideView();
                                    self._doSave(widget, model);
                                }
                            }, {
                                value: BI.i18nText("BI-Cancel"),
                                level: "ignore",
                                handler: function () {
                                    self.interfereConfirmPane.hideView();
                                }
                            }],
                            el: {
                                type: "bi.vertical",
                                items: BI.concat([{
                                    type: "bi.label",
                                    whiteSpace: "normal",
                                    text: BI.i18nText("BI-Current_Edit_May_Interfere_Other_Template_Confirm_To_Continue"),
                                    cls: "delete-label",
                                    textAlign: "center",
                                    width: 300
                                }], items),
                                height: 140,
                                hgap: 20
                            },
                            maxHeight: 140,
                            minWidth: 340
                        }
                    })
                }
                self.interfereConfirmPane.showView();
            })
        }
        this._resetPoolCurrentUsedTables();
    },

    _resetPoolCurrentUsedTables: function() {
        Pool.current_edit_etl_used = [];
    },
    
    populate : function (widget, model) {
        this._showView(widget)
    }
})