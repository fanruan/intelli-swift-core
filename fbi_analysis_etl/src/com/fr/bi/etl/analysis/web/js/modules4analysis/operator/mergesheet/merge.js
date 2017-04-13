/**
 * Created by windy on 2017/4/12.
 */
BI.AnalysisETLMergeSheet = BI.inherit(BI.Widget, {

    props: {
        extraCls: "bi-analysis-etl-merge-sheet"
    },

    render: function(){
        var self = this;
        this.model = new BI.AnalysisETLMergeSheetModel({});
        return {
            type:"bi.htape",
            items:[{
                type:"bi.layout",
                width:20
            }, {
                el:{
                    type:"bi.vtape",
                    items:[{
                        el :{
                            type:"bi.border",
                            items:{
                                north:{
                                    el :{
                                        type:"bi.left_right_vertical_adapt",
                                        cls:"title",
                                        lrgap:20,
                                        items:{
                                            left:[{
                                                type:"bi.label",
                                                cls:"label",
                                                text: BI.i18nText("BI-Merge_Table") + ":"
                                            }, {
                                                type:"bi.text_editor",
                                                cls:"editor",
                                                height:30,
                                                width:250,
                                                watermark: BI.i18nText("BI-Table_Name"),
                                                errorText: BI.i18nText("BI-Table_Name_Not_Null"),
                                                validationChecker : function (v) {
                                                    return self.validationChecker(v)
                                                },
                                                ref: function(_ref){
                                                    self.nameInput = _ref;
                                                }
                                            }, {
                                                type:"bi.button",
                                                text: BI.i18nText("BI-Modify_Used_Tables"),
                                                height:30,
                                                handler : function () {
                                                    var popover = BI.createWidget({
                                                        type: "bi.analysis_etl_choose_sheet_popover",
                                                        items: self.getSheets(),
                                                        value: self.getCurrentSheets()
                                                    });
                                                    popover.on(BI.PopoverSection.EVENT_CLOSE, function () {
                                                        BI.Layers.hide(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
                                                    })
                                                    popover.on(BI.AnalysisETLMergeSheetPopover.EVENT_CHANGE, function (v) {
                                                        self.setCurrent2Sheet(v);
                                                    });

                                                    BI.Popovers.remove("etlChooseSheetForMerge");
                                                    BI.Popovers.create("etlChooseSheetForMerge", popover, {width : 500, height : 400, container:  BI.Layers.create(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER)}).open("etlChooseSheetForMerge");
                                                    BI.Layers.show(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
                                                }
                                            }],
                                            right:[]
                                        }
                                    },
                                    height:50
                                },
                                center:{
                                    type:"bi.htape",
                                    items :[{
                                        el:{
                                            type:"bi.vtape",
                                            items:[{
                                                el: {
                                                    type:"bi.analysis_etl_merge_type",
                                                    cls:"background-border",
                                                    ref: function(_ref){
                                                        self.mergeType = _ref;
                                                    },
                                                    listeners: [{
                                                        eventName: BI.AnalysisETLMergeSheetType.EVENT_CHANGE,
                                                        action: function(){
                                                            self.mergeTypeChange();
                                                        }
                                                    }]
                                                },
                                                height:175
                                            }, {
                                                type:"bi.layout",
                                                height:10
                                            }, {
                                                type:"bi.analysis_etl_merge_fields",
                                                cls:"background-border",
                                                ref: function(_ref){
                                                    self.mergeFields = _ref;
                                                },
                                                listeners: [{
                                                    eventName: BI.AnalysisETLMergeSheetFields.MERGE_CHANGE,
                                                    action: function(isValid){
                                                        self.mergeChange(isValid);
                                                    }
                                                }]
                                            }]
                                        },
                                        width:418
                                    }, {
                                        type:"bi.layout",
                                        width:20
                                    }, {
                                        type:"bi.analysis_etl_merge_preview",
                                        cls:"background-border",
                                        nameValidationController: function (j, v) {
                                            return self.checkNameValid(j, v)
                                        },
                                        ref: function (_ref) {
                                            self.preview = _ref;
                                        },
                                        listeners: [{
                                            eventName: BI.AnalysisETLMergePreviewTable.EVENT_RENAME,
                                            action: function(idx, name){
                                                self.rename(idx, name)
                                            }
                                        }]
                                    }]
                                },
                                south:{
                                    type:"bi.layout",
                                    height:10
                                }
                            }
                        }
                    }, {
                        el:{
                            type:"bi.left_right_vertical_adapt",
                            items:{
                                left:[{
                                    type:"bi.button",
                                    height:30,
                                    text:BI.i18nText("BI-Basic_Cancel"),
                                    title:BI.i18nText("BI-Basic_Cancel"),
                                    handler : function () {
                                        BI.Layers.hide(ETLCst.ANALYSIS_MERGE_LAYER)
                                        self.doCancel()
                                    }
                                }],
                                right:[{
                                    type:'bi.button',
                                    height:30,
                                    text:BI.i18nText("BI-Finish_Merge"),
                                    title:BI.i18nText("BI-Finish_Merge"),
                                    handler : function () {
                                        BI.Layers.hide(ETLCst.ANALYSIS_MERGE_LAYER)
                                        self.doSave();
                                    },
                                    ref: function(_ref){
                                        self.saveButton = _ref;
                                    }
                                }]
                            }
                        },
                        height:50
                    }]
                }
            },{
                type:"bi.layout",
                width:20
            }]
        }
    },

    mounted: function(){
        this.populate(this.options.model);
    },

    _populate: function () {
        this.nameInput.setValue(this.model.get("name"));
        this.model.refreshColumnName();
        this.preview.populate(this.model.createPreviewData(),{});
        this.saveButton.setWarningTitle(BI.i18nText('BI-ETL_Join_Merge_Field_Not_Set'));
        this.saveButton.setEnable(this.mergeFields.isValid())
    },

    populate: function(m, options){
        this.model.populate(m);
        BI.extend(this.options, options);
        this.mergeType.populate(this.model.getChildValue(BI.AnalysisETLMergeSheetModel.MERGE_TYPE));
        this.mergeFields.populate(this.model.getChildValue(BI.AnalysisETLMergeSheetModel.MERGE_FIELDS))
        this._populate();
    },

    //原controller
    validationChecker : function (v) {
        var allSheets = this.getSheets();

        return BI.isNull(BI.find(allSheets, function (idx, item) {
            return item.text === v;
        }));
    },

    getSheets : function () {

        return this.model.getAllSheets();
    },

    doSave : function () {
        this.model.set("name", this.nameInput.getValue())
        if(BI.isFunction(this.options.saveHandler)) {
            this.options.saveHandler(this.model.update())
        }
        //为了临时适配外层节点，等外层节点也整好后可删
        if(BI.isFunction(this.options.controller.saveHandler)){
            this.options.controller.saveHandler(this.model.update())
        }
        BI.Layers.hide(BICst.ANALYSIS_MERGE_LAYER)
    },

    doCancel : function () {
        if(BI.isFunction(this.options.cancelHandler)) {
            this.options.cancelHandler()
        }
        BI.Layers.hide(BICst.ANALYSIS_MERGE_LAYER)
    },

    getCurrentSheets : function () {
        return this.model.getSheets();
    },

    rename : function (idx, name) {
        this.model.rename(idx, name)
    },


    setCurrent2Sheet : function (v) {
        if(this.model.setCurrent2Sheet(v) === true) {
            this.preview.resetLeftRight();
            this.refreshView();
        }
    },

    checkNameValid : function (j, v) {
        var column = this.model.get("columns")
        var valid = true;
        BI.some(column, function (idx, item) {
            if(idx !== j && item.fieldName === v) {
                valid = false;
                return true;
            }
        })
        return valid;
    },

    mergeTypeChange : function () {
        this.model.get(BI.AnalysisETLMergeSheetModel.MERGE_TYPE).populate(this.mergeType.update());
        this.preview.populate(this.model.createPreviewData(),{})
    },

    mergeChange : function (isValid) {
        if(isValid === true) {
            this.model.get(BI.AnalysisETLMergeSheetModel.MERGE_FIELDS).populate(this.mergeFields.update());
            this.model.refreshColumnName()
            this.preview.populate(this.model.createPreviewData(),{})
        }
        else{
            var data = this.model.createPreviewData();
            delete data["merge"]
            this.preview.populate(data,{})
            if(this.model.getJoinFieldsLength() === 0){
                this.saveButton.setWarningTitle(BI.i18nText('BI-ETL_Join_Merge_Field_Not_Set'));
            }
            else{
                this.saveButton.setWarningTitle(BI.i18nText('BI-ETL_Join_Wrong_Merge_Field'));
            }
        }
        this.saveButton.setEnable(isValid)
    }
})

BI.shortcut("bi.analysis_etl_merge_sheet", BI.AnalysisETLMergeSheet);