

BI.TopPointerSavePane = BI.inherit(BI.MVCWidget, {

    _constant : {
        buttonWidth:90,
        buttonHeight:30
    },

    _defaultConfig: function () {
        return BI.extend(BI.TopPointerSavePane.superclass._defaultConfig.apply(this, arguments), {
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

    _initController : function() {
        return BI.TopPointerSavePaneController;
    },


    _initView: function () {
        var o = this.options;
        var self = this;
        this.cancel = BI.createWidget({
            type:"bi.button",
            text:BI.i18nText("BI-Cancel"),
            level:'ignore',
            width:this._constant.buttonWidth,
            height:this._constant.buttonHeight,
            handler : function(e){
                if(!o.cancelHandler()){
                    self.controller.changeEditingState()
                    self.fireEvent(BI.TopPointerSavePane.EVENT_CANCEL, arguments)
                }
            }
        })

        this.save = BI.createWidget({
            type:"bi.button",
            width:this._constant.buttonWidth,
            height:this._constant.buttonHeight,
            text:BI.i18nText("BI-Save"),
            handler : function(e){
                if(!o.saveHandler(self.controller.isEditing())) {
                    var change  = function () {
                        self.controller.changeEditingState()
                        self.controller.fireSaveOrEditEvent();
                    }
                    if(self.controller.isEditing()){
                         var res = o.checkBeforeSave();
                        if(res[0] === true) {
                            BI.Msg.confirm(res[2], res[1], function (v) {
                                if(v === true) {
                                    change();
                                }
                            })
                        } else {
                            change();
                        }
                    } else {
                        change();
                    }
                }
            }
        });


        this.contentItemWidget = BI.createWidget(o.contentItem);
        this.contentItemWidget.element.addClass("bi-analysis-etl-top-pointer-save-pane-item");

        self.contentItemWidget.on(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, function (status, title) {
            self.controller.refreshSaveButtonStatus(status, BI.isNull(title) ? BI.i18nText('BI-Correct_The_Errors_Red') : title);
        });
        self.contentItemWidget.on(BI.TopPointerSavePane.EVENT_FIELD_VALID, function () {
            self.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, arguments);
        });
        self.contentItemWidget.on(BI.TopPointerSavePane.EVENT_INVALID, function () {
            self.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, arguments);
        });

        self.contentItemWidget.on(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, arguments)
        })

        self.contentItemWidget.on(BI.AnalysisETLOperatorMergeSheetPane.STOP_EDITING, function () {
            self.setEditing(false);
            self.controller.fireSaveOrEditEvent();
        })

        self.contentItemWidget.on(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, arguments)
        })

        this.contentItemWidget.on(BI.AnalysisETLOperatorCenter.DATA_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorCenter.DATA_CHANGE, arguments);
        })

        this.contentItemWidget.on(BI.AnalysisETLOperatorAbstractController.VALID_CHANGE, function (v) {
            self.controller.doValidCheck(v)
        })

        this.pointerPane = BI.createWidget({
            element:this.element,
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
                            items:[this.save]
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
            }
        })
    },

    getContentWidget : function () {
        return this.contentItemWidget;
    },

    setEditing : function(editing) {
        this.controller.setEditing(editing);
    },

    setEnable : function(v) {
        this.controller.setEnable(v)
    },

    show : function(pointerPos) {
        this.pointerPane.show(pointerPos)
        this.controller.refreshEditMask();
    },

    hide : function(){
        this.pointerPane.hide()
        this.controller.refreshEditMask();
    }

})
BI.TopPointerSavePane.EVENT_CANCEL="event_cancel";
BI.TopPointerSavePane.EVENT_SAVE="event_save";
BI.TopPointerSavePane.EVENT_EDIT="event_edit";
BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS="event_check_save_status";
BI.TopPointerSavePane.EVENT_INVALID="TopPointerSavePane.EVENT_INVALID";
BI.TopPointerSavePane.EVENT_FIELD_VALID="TopPointerSavePane.EVENT_FIELD_VALID";
$.shortcut("bi.top_pointer_save_pane", BI.TopPointerSavePane);