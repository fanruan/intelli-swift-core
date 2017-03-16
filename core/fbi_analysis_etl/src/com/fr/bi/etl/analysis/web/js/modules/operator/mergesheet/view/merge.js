BI.AnalysisETLMergeSheet = BI.inherit(BI.MVCWidget, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLMergeSheet.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-merge-sheet",
            model : {
                sheets:[1,2]
            },
            controller : {
                sheets:[{text:1,value:1},{text:3,value:2},{text:3,value:3},{text:4,value:4}]
            }
        })
    },

    _initController : function () {
        return BI.AnalysisETLMergeSheetController
    },

    _initModel : function () {
        return BI.AnalysisETLMergeSheetModel
    },

    _initView: function () {
        var self = this, o = this.options;
        this.saveButton = BI.createWidget({
            type:'bi.button',
            height:30,
            text:BI.i18nText("BI-Finish_Merge"),
            title:BI.i18nText("BI-Finish_Merge"),
            handler : function () {
                BI.Layers.hide(ETLCst.ANALYSIS_MERGE_LAYER)
                self.controller.doSave();
            }
        });
        this.nameInput = BI.createWidget({
            type:"bi.text_editor",
            cls:"editor",
            height:30,
            width:250,
            watermark: BI.i18nText("BI-Table_Name"),
            errorText: BI.i18nText("BI-Table_Name_Not_Null"),
            validationChecker : function (v) {
                return self.controller.validationChecker(v)
            }
        });
        this.mergeType = BI.createWidget({
            type:"bi.analysis_etl_merge_type",
            cls:"background-border"
        })
        this.mergeType.on(BI.AnalysisETLMergeSheetType.EVENT_CHANGE, function (v) {
            self.controller.mergeTypeChange();
        });
        this.registerChildWidget(BI.AnalysisETLMergeSheetModel.MERGE_TYPE, self.mergeType)
        this.mergeFields = BI.createWidget({
            type:"bi.analysis_etl_merge_fields",
            cls:"background-border"
        });
        this.mergeFields.on(BI.AnalysisETLMergeSheetFields.MERGE_CHANGE, function (isValid) {
            self.controller.mergeChange(isValid);
        })
        this.registerChildWidget(BI.AnalysisETLMergeSheetModel.MERGE_FIELDS, self.mergeFields)
        this.preview = BI.createWidget({
            type:"bi.analysis_etl_merge_preview",
            cls:"background-border",
            nameValidationController: function (j, v) {
                return self.controller.checkNameValid(j, v)
            }
        })
        this.preview.on(BI.AnalysisETLMergePreviewTable.EVENT_RENAME, function (idx, name) {
            self.controller.rename(idx, name)
        })
        BI.createWidget({
            type:"bi.htape",
            element:this.element,
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
                                           }, this.nameInput, {
                                               type:"bi.button",
                                               text: BI.i18nText("BI-Modify_Used_Tables"),
                                               height:30,
                                               handler : function () {
                                                   var popover = BI.createWidget({
                                                       type: "bi.analysis_etl_choose_sheet_popover",
                                                       items: self.controller.getSheets(),
                                                       value: self.controller.getCurrentSheets()
                                                   });
                                                   popover.on(BI.PopoverSection.EVENT_CLOSE, function () {
                                                       BI.Layers.hide(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
                                                   })
                                                   popover.on(BI.AnalysisETLMergeSheetPopover.EVENT_CHANGE, function (v) {
                                                       self.controller.setCurrent2Sheet(v);
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
                                                el: this.mergeType,
                                                height:175
                                            }, {
                                                type:"bi.layout",
                                                height:10
                                            }, this.mergeFields]
                                        },
                                        width:418
                                    }, {
                                        type:"bi.layout",
                                        width:20
                                    }, this.preview]
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
                                    text:BI.i18nText("BI-Cancel"),
                                    title:BI.i18nText("BI-Cancel"),
                                    handler : function () {
                                        BI.Layers.hide(ETLCst.ANALYSIS_MERGE_LAYER)
                                       self.controller.doCancel()
                                    }
                                }],
                                right:[this.saveButton]
                            }
                        },
                        height:50
                    }]
                }
            },{
                type:"bi.layout",
                width:20
            }]
        })
    }
})

$.shortcut("bi.analysis_etl_merge_sheet", BI.AnalysisETLMergeSheet);
