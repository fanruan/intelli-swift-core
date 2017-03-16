BI.AnalysisETLMergeSheetPreview = BI.inherit(BI.MVCWidget, {

    _constants : {
        ERROR:"error",
        NORMAL:"normal"
    },
    
    
    _defaultConfig : function () {
        return BI.extend(BI.AnalysisETLMergeSheetPreview.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-merge-preview",
            model : {

            },
            controller : {

            }
        })
    },

    _initController : function () {
        return BI.AnalysisETLMergeSheetPreviewController;
    },

    resetLeftRight : function () {
        this.first = false;
    },
    
    _initView : function () {
        var self = this;
        var o = this.options;
        this.lefttable = BI.createWidget({
            type:"bi.label",
            text:""
        })
        this.left = BI.createWidget({
            type:"bi.analysis_etl_merge_preview_table",
            rename:false,
            items:[],
            header:[],
            leftColumns:[],
            mergeColumns:[]
        })
        this.righttable = BI.createWidget({
            type:"bi.label",
            text:""
        })
        this.right = BI.createWidget({
            type:"bi.analysis_etl_merge_preview_table",
            rename:false,
            items:[],
            header:[],
            leftColumns:[],
            mergeColumns:[]
        })

        this.merge = BI.createWidget({
            type:"bi.analysis_etl_merge_preview_table",
            rename:true,
            items:[],
            header:[],
            leftColumns:[],
            mergeColumns:[],
            nameValidationController:o.nameValidationController
        });
        this.mergeCard = BI.createWidget({
            type:"bi.card",
            items:[{
                el: this.merge,
                cardName: this._constants.NORMAL
            }, {
                el:  {
                    type : "bi.center_adapt",
                    items : [{
                        type:"bi.label",
                        cls: "warning",
                        text: BI.i18nText("BI-Please_Set_Right")
                    }]
                },
                cardName: this._constants.ERROR
            }]
        })
        this.merge.on(BI.AnalysisETLMergePreviewTable.EVENT_RENAME, function () {
            self.fireEvent(BI.AnalysisETLMergePreviewTable.EVENT_RENAME, arguments)
        })
        BI.createWidget({
            type:"bi.vtape",
            element:this.element,
            lgap:20,
            rgap:20,
            items:[{
                el :{
                    type:"bi.vertical_adapt",
                    cls:"title",
                    height:40,
                    items:[{
                        type:"bi.label",
                        text:BI.i18nText("BI-Original_Data")
                    }]
                },
                height:40
            }, {
                el :{
                    type:"bi.horizontal_adapt",
                    height:200,
                    items:[{
                        el :{
                            type:"bi.vtape",
                            height:200,
                            items:[{
                                el: {
                                    type:"bi.center_adapt",
                                    items:[this.lefttable],
                                    height:35
                                },
                                height:35
                            }, this.left]
                        },
                        rgap:12.5
                    },{
                        el : {
                            type:"bi.vtape",
                            height:200,

                            items:[{
                                el: {
                                    type:"bi.center_adapt",
                                    items:[this.righttable],
                                    height:35
                                },
                                height:35
                            }, this.right]
                        },
                        lgap:12.5

                    }]
                },
                height:200
            }, {
                type:"bi.layout",
                height:10
            }, {
                el :{
                    type:"bi.vertical_adapt",
                    cls:"title",
                    height:40,
                    items:[{
                        type:"bi.label",
                        text:BI.i18nText("BI-Merge_Result")
                    }]
                },
                height:40
            }, {
                type:"bi.layout",
                height:20
            }, this.mergeCard, {
                type:"bi.layout",
                height:20
            }]
        })
    }
});

$.shortcut("bi.analysis_etl_merge_preview",BI.AnalysisETLMergeSheetPreview)