BI.AnalysisTopPointerSavePane = BI.inherit(BI.Widget, {

    _constant : {
        buttonWidth:90,
        buttonHeight:30
    },

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisTopPointerSavePane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "",
            contentItem :  {
                type:"bi.label",
                text:"help!!!"
            },
            pointerCls:"",
            height:300,
            pointerAreaHeight:10,
            pointerHeight:7,
            pointerWidth:30,
            editing:false,
            cancelHandler:BI.emptyFn,
            saveHandler:BI.emptyFn
        })
    },

    render: function(){
        var o = this.options;
        var self = this;
        this._editing = this.options.editing || false;
        this.cancel = BI.createWidget({
            type:"bi.button",
            text:BI.i18nText("BI-Basic_Cancel"),
            level:'ignore',
            width:this._constant.buttonWidth,
            height:this._constant.buttonHeight,
            handler : function(e){
                if(!o.cancelHandler()){
                    self.changeEditingState()
                    self.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CANCEL, arguments)
                }
            }
        })

        var label = BI.createWidget({
            type: "bi.label",
            cls: "delete-label",
            whiteSpace: "normal",
            width: 360
        })

        this.save = BI.createWidget({
            type:"bi.button",
            width:this._constant.buttonWidth,
            height:this._constant.buttonHeight,
            text:BI.i18nText("BI-Basic_Save"),
            handler : function(e){
                if(!o.saveHandler(self.isEditing())) {
                    if(self.isEditing()){
                        var res = o.checkBeforeSave();
                        if(res[0] === false) {
                            label.setText(res[1]);
                            self.confirmCombo.showView();
                        } else {
                            change();
                        }
                    } else {
                        change();
                    }
                }
            }
        });

        this.confirmCombo = BI.createWidget({
            type: "bi.bubble_combo",
            trigger: "",
            el: this.save,
            popup: {
                type: "bi.bubble_bar_popup_view",
                buttons: [{
                    value: BI.i18nText(BI.i18nText("BI-Basic_Sure")),
                    handler: function(){
                        self.confirmCombo.hideView();
                        change();
                    }
                }, {
                    value: BI.i18nText("BI-Basic_Cancel"),
                    level: "ignore",
                    handler: function () {
                        self.confirmCombo.hideView();
                    }
                }],
                el: {
                    type: "bi.vertical_adapt",
                    items: [label],
                    width: 400,
                    hgap: 20
                },
                minHeight: 140,
                maxHeight: 340,
                minWidth: 400
            }
        });


        this.contentItemWidget = BI.createWidget(o.contentItem);
        this.contentItemWidget.element.addClass("bi-analysis-etl-top-pointer-save-pane-item");

        self.contentItemWidget.on(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, function (status, title) {
            self.refreshSaveButtonStatus(status, BI.isNull(title) ? BI.i18nText('BI-Correct_The_Errors_Red') : title);
        });
        self.contentItemWidget.on(BI.AnalysisTopPointerSavePane.EVENT_FIELD_VALID, function () {
            self.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_FIELD_VALID, arguments);
        });
        self.contentItemWidget.on(BI.AnalysisTopPointerSavePane.EVENT_INVALID, function () {
            self.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_INVALID, arguments);
        });

        self.contentItemWidget.on(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, arguments)
        })

        self.contentItemWidget.on(BI.AnalysisETLOperatorMergeSheetPane.STOP_EDITING, function () {
            self.setEditing(false);
            self.fireSaveOrEditEvent();
        })

        self.contentItemWidget.on(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, arguments)
        })

        this.contentItemWidget.on(BI.AnalysisETLOperatorCenter.DATA_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorCenter.DATA_CHANGE, arguments);
        })

        this.contentItemWidget.on(BI.AnalysisETLOperatorAbstractController.VALID_CHANGE, function (v) {
            self.doValidCheck(v)
        })

        return {
            type:"bi.top_pointer_pane",
            pointerCls: o.pointerCls,
            height: o.height,
            pointerAreaHeight: o.pointerAreaHeight,
            pointerHeight: o.pointerHeight,
            pointerWidth: o.pointerWidth,
            contentItem :{
                type:"bi.vtape",
                items :[{
                    el:this.contentItemWidget
                }, {
                    el : {
                        type:"bi.right",
                        height:50,
                        items:[{
                            type:"bi.layout",
                            width:10,
                            height:1
                        }, {
                            type:"bi.center_adapt",
                            height:50,
                            items:[this.confirmCombo]
                        },{
                            type:"bi.layout",
                            width:10,
                            height:1
                        }, {
                            type:"bi.center_adapt",
                            height:50,
                            items:[this.cancel]
                        }]
                    },
                    height:50
                }]
            },
            ref: function(_ref){
                self.pointerPane = _ref;
            }
        };

        function change(){
            self.changeEditingState();
            self.fireSaveOrEditEvent();
        }
    },

    getContentWidget : function () {
        return this.contentItemWidget;
    },

    show : function(pointerPos) {
        this.pointerPane.show(pointerPos);
        this.refreshEditMask();
    },

    hide : function(){
        this.pointerPane.hide()
        this.refreshEditMask();
    },

    refreshButtonsStatus : function () {
        this.cancel.setEnable(this._editing);
        this.save.setText(this._editing ? BI.i18nText("BI-Basic_Save") : BI.i18nText("BI-Basic_Edit"));
        if(this._editing === false) {
            this.save.setEnable(true)
        }
        this.refreshEditMask();
    },

    refreshSaveButtonStatus : function (status, title) {
        if(this._editing === true) {
            this.save.setEnable(status);
        }
        this.save.setWarningTitle(status === true ? "" : title);
    },

    doValidCheck : function (v) {
        if(this._editing === false) {
            this.fireEvent(BI.AnalysisETLOperatorAbstractController.VALID_CHANGE, v);
        }
    },

    setEnable : function (v) {
        //正在编辑不允许修改
        if(this._editing === true){
            return;
        }
        this.save.setEnable(v)
        this.cancel.setEnable(v);
        this.refreshButtonsStatus();
    },

    changeEditingState : function () {
        this._editing = !this._editing;
        this.refreshButtonsStatus();
    },

    setEditing : function(editing) {
        this._editing = editing;
        this.refreshButtonsStatus();
        this.refreshEditMask()
    },

    isEditing : function () {
        return this._editing
    },

    refreshEditMask : function () {
        this._editing === true ? this._clearMask(): this._showMask();
    },

    _showMask:function () {
        var masker = BI.Layers.make(this.getName(),  this.contentItemWidget, {
            render : {
                cls:"disable"
            }
        });
        BI.Layers.show(this.getName());
    },

    _clearMask : function () {
        BI.Layers.hide(this.getName());
    },

    fireSaveOrEditEvent : function () {
        this.fireEvent(this._editing ? BI.AnalysisTopPointerSavePane.EVENT_EDIT : BI.AnalysisTopPointerSavePane.EVENT_SAVE, arguments)
        if(BI.isNotNull(this.contentItemWidget) && BI.isFunction(this.contentItemWidget.doCheck)) {
            this.contentItemWidget.doCheck();
        }
    }

})
BI.AnalysisTopPointerSavePane.EVENT_CANCEL="event_cancel";
BI.AnalysisTopPointerSavePane.EVENT_SAVE="event_save";
BI.AnalysisTopPointerSavePane.EVENT_EDIT="event_edit";
BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS="event_check_save_status";
BI.AnalysisTopPointerSavePane.EVENT_INVALID="AnalysisAnalysisTopPointerSavePane.EVENT_INVALID";
BI.AnalysisTopPointerSavePane.EVENT_FIELD_VALID="AnalysisAnalysisTopPointerSavePane.EVENT_FIELD_VALID";
BI.shortcut("bi.analysis_top_pointer_save_pane", BI.AnalysisTopPointerSavePane);
