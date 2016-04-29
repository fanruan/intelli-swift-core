BI.MergeHistory = BI.inherit(BI.MVCWidget,  {

    _defaultConfig: function () {
        return BI.extend(BI.MergeHistory.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-merge-history"
        })
    },

    _initController : function () {
        return BI.MergeHistoryController;
    },

    createButton : function (item) {
        var button = BI.createWidget(BI.extend(item, {
            type:"bi.button",
            cls:"single_operator",
            level:"ignore",
            width:110,
            height:30,
            value:item.id,
            text:item.text
        }))
        var self = this;
        button.on(BI.Controller.EVENT_CHANGE, function () {
            self.controller.showContentAndPreview(item.id);
        })
        return button;
    },

    
    _initView : function () {
        this.previewTable = BI.createWidget({
            type: "bi.analysis_etl_preview_table",
            cls: "bi-analysis-tab-table",
            header:[],
            items:[]
        })

        this.path = BI.createWidget({
            type: "bi.etl_branch_relation",
            hgap:40
        });
        this.contentTitle = BI.createWidget({
            type:"bi.label"
        })
        this.contentView = BI.createWidget({
            type:"bi.vertical",
            scrollx:false,
            scrolly:false
        })
        var self = this;
        BI.createWidget({
            element:this.element,
            type:"bi.htape",
            items:[{
                type:"bi.layout",
                width:20
            },{
                el:{
                    type:"bi.vtape",
                    items:[{
                        el :{
                            type:"bi.left_right_vertical_adapt",
                            cls:"title border",
                            llgap:10,
                            rrgap:10,
                            height:40,
                            items:{
                                left:[{
                                    type:"bi.label",
                                    text:BI.i18nText("BI-ETL_Merge_History")
                                }],
                                right:[{
                                    type:"bi.button",
                                    level:"ignore",
                                    height:30,
                                    text:BI.i18nText("BI-Close"),
                                    title:BI.i18nText("BI-Close"),
                                    handler : function () {
                                        self.fireEvent(BI.MergeHistory.CANCEL)
                                    }
                                }]
                            }
                        },
                        height:40
                    }, {
                        type:"bi.layout",
                        height:10
                    }, {
                        type:"bi.htape",
                        items:[{
                            type:"bi.vertical",
                            cls:"border",
                            items:[this.path],
                            width:300
                        }, {
                            type:"bi.layout",
                            width:10
                        }, {
                            el:{
                                type:"bi.vtape",
                                items:[{
                                    el :  {
                                        type:"bi.vtape",
                                        cls:"border",
                                        items:[{
                                            type:"bi.left_right_vertical_adapt",
                                            cls:"title content",
                                            llgap:10,
                                            items:{
                                                left:[this.contentTitle]
                                            },
                                            height:40
                                        }, {
                                            el:this.contentView
                                        }]
                                    },
                                    height:300
                                }, {
                                    type:"bi.layout",
                                    height:10
                                }, {
                                    el : {
                                        type:"bi.border",
                                        cls:"border",
                                        items:{
                                            north:{
                                                type:"bi.layout",
                                                height:10
                                            },
                                            center:this.previewTable,
                                            south:{
                                                type:"bi.layout",
                                                height:10
                                            },
                                            east:{
                                                type:"bi.layout",
                                                width:10
                                            },
                                            west:{
                                                type:"bi.layout",
                                                width:10
                                            },
                                        }
                                    }
                                }]
                            }
                        }]
                    }, {
                        type:"bi.layout",
                        height:10
                    }]
                }

            }, {
                type:"bi.layout",
                width:20
            }]

        })
        
    }
})
BI.MergeHistory.CANCEL="cancel_view";
$.shortcut("bi.analysis_etl_merge_history", BI.MergeHistory);