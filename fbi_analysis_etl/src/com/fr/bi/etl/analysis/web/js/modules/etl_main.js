BI.AnalysisETLMain = FR.extend(BI.MVCWidget, {

    _constant : {
        buttonHeight:30,
        buttonWidth:90,
        titleHeight:40,
        WARNING: 1,
        RENAME: 2,
        MULTI_SHEET: 3
    },

    _defaultConfig: function() {
        var conf = BI.AnalysisETLMain.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls : "bi-analysis-etl-main bi-analysis-etl-main-animate",
            data : null
        })
    },

    _initView : function() {
        var self = this, c = this._constant;
        this.saveButton = BI.createWidget({
            type:"bi.button",
            height: this._constant.buttonHeight,
            width: this._constant.buttonWidth,
            text:BI.i18nText("BI-Base_Save")
        });

        this.saveButton.on(BI.Button.EVENT_CHANGE, function(){
            if (self.controller.getSheetLength() > 1){
                self.confirmCombo.showView();
                popupTab.setSelect(c.MULTI_SHEET);
            } else if (BI.isNull(self.controller.getId())){
                self.confirmCombo.showView();
                popupTab.setSelect(c.RENAME);
                self.pane.populate(self.controller.getTableDefaultName());
                self.pane.setTemplateNameFocus();
            } else {
                popupTab.setSelect(c.WARNING);
                BI.ETLReq.reqCheckTableInUse(self.controller.getModelJSON(), function(data){
                    var items = data["usedTemplate"] || [];
                    if(BI.isEmptyArray(items)){
                        self.controller.doSave();
                    }else{
                        self.confirmCombo.showView();
                        items = BI.map(items, function(idx, name){
                            return {
                                type: "bi.label",
                                text: name,
                                title: name,
                                cls: "delete-label",
                                textAlign: "center",
                                width: 360
                            }
                        });
                        popupTab.getSelectedTab().populate(BI.concat([{
                            type: "bi.label",
                            whiteSpace: "normal",
                            text: BI.i18nText("BI-Current_Edit_May_Interfere_Other_Template_Confirm_To_Continue"),
                            cls: "delete-label",
                            textAlign: "center",
                            width: 360
                        }], items));
                    }
                })
            }
            self.controller.resetPoolCurrentUsedTables();
        });

        this.confirmButton = BI.createWidget({
            type: "bi.button",
            height: 30,
            value: BI.i18nText(BI.i18nText("BI-Base_Sure")),
            handler: function () {
                self.confirmCombo.hideView();
                switch (popupTab.getSelect()){
                    case c.MULTI_SHEET:
                        if (BI.isNull(model.get('id'))){
                            popupTab.setSelect(c.RENAME);
                            self.pane.populate(self.controller.getTableDefaultName());
                            self.confirmCombo.showView();
                            self.pane.setTemplateNameFocus();
                        } else {
                            self.controller.doSave();
                        }
                        break;
                    case c.WARNING:
                        self.controller.doSave();
                        break;
                    case c.RENAME:
                        self.controller.setSaveInfo(self.pane.getValue(), self.pane.getDesc());
                        self.controller.doSave();
                        break;
                }
            }
        });


        var popupTab = BI.createWidget({
            direction: "custom",
            type: "bi.tab",
            logic: {
                dynamic: true
            },
            cardCreator: BI.bind(this._createTabs, this)
        });

        this.confirmCombo = BI.createWidget({
            type: "bi.bubble_combo",
            trigger: "",
            el: this.saveButton,
            popup: {
                type: "bi.bubble_bar_popup_view",
                buttons: [this.confirmButton, {
                    value: BI.i18nText("BI-Basic_Cancel"),
                    level: "ignore",
                    handler: function () {
                        self.confirmCombo.hideView();
                    }
                }],
                el: {
                    type: "bi.vertical",
                    items: [popupTab],
                    width: 400,
                    hgap: 20
                },
                minHeight: 140,
                maxHeight: 340,
                minWidth: 400
            }
        });

        var cancelButton = BI.createWidget({
            type:"bi.button",
            height: this._constant.buttonHeight,
            width: this._constant.buttonWidth,
            level:"ignore",
            text:BI.i18nText("BI-Basic_Cancel"),
            handler : function(e){
                self.controller.doCancel()
            }
        })

        var title = BI.createWidget({
            type:"bi.right",
            height:this._constant.titleHeight,
            rgap: 20,
            items : [{
                type:"bi.center_adapt",
                cls:"bi-analysis-etl-main-save-button",
                items:[this.confirmCombo],
                height:this._constant.titleHeight
            }, {
                type:"bi.center_adapt",
                cls:"bi-analysis-etl-main-save-button",
                items:[cancelButton],
                height:this._constant.titleHeight
            }]
        })
        self.tab = BI.createWidget({
            type: "bi.dynamic_tab",
            items: []
        })
        this.registerChildWidget(BI.AnalysisETLMainModel.TAB, self.tab)
        BI.createWidget({
            type:"bi.vtape",
            element: this.element,
            items: [{
                el: title,
                height:this._constant.titleHeight
            }, {
                el:self.tab,
            }]
        })
    },

    _createTabs: function(v){
        var self = this;
        var c = this._constant;
        switch (v) {
            case c.MULTI_SHEET:
                return BI.createWidget({
                    type: "bi.vertical_adapt",
                    items:[{
                        type: "bi.label",
                        whiteSpace: "normal",
                        text: BI.i18nText("BI-ETL_Saving_Warning_Text"),
                        cls: "delete-label",
                        textAlign: "left",
                        width: 300,
                        height: 100
                    }],
                    width: 300,
                    height: 100,
                    hgap: 20
                });
            case c.RENAME:
                this.pane = BI.createWidget({
                    type: "bi.etl_rename_pane",
                    renameChecker : function (v) {
                        return !BI.Utils.getAllETLTableNames().contains(v);
                    }
                });
                this.pane.on(BI.ETLNamePane.EVENT_VALID, function(){
                    self.confirmButton.setEnable(true);
                });
                this.pane.on(BI.ETLNamePane.EVENT_ERROR, function(v){
                    if (BI.isEmptyString(v)) {
                        self.confirmButton.setWarningTitle(BI.i18nText("BI-Report_Name_Not_Null"));
                    } else {
                        self.confirmButton.setWarningTitle(BI.i18nText("BI-Template_Name_Already_Exist"));
                    }
                    self.confirmButton.setEnable(false);
                });
                return this.pane;
            case c.WARNING:
                return BI.createWidget({
                    type: "bi.button_group",
                    items: [],
                    layouts: [{
                        type: "bi.vertical"
                    }]
                })
        }
    },

    _initController : function () {
        return BI.AnalysisETLMainController;
    },

    _initModel : function () {
        return BI.AnalysisETLMainModel;
    },

    setVisible : function(v){
        BI.AnalysisETLMain.superclass.setVisible.apply(this, arguments);
        if(v === true){
            BI.Layers.show(ETLCst.ANALYSIS_LAYER);
        } else {
            BI.Layers.remove(ETLCst.ANALYSIS_LAYER);
        }
    }

})
$.shortcut("bi.analysis_etl_main", BI.AnalysisETLMain);