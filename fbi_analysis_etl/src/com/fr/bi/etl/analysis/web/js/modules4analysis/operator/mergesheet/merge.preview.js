/**
 * Created by windy on 2017/4/12.
 */
BI.AnalysisETLMergeSheetPreview = BI.inherit(BI.Widget, {

    _constants : {
        ERROR:"error",
        NORMAL:"normal"
    },

    props: {
        extraCls: "bi-analysis-etl-merge-preview"
    },

    resetLeftRight : function () {
        this.first = false;
    },

    render: function(){
        var self = this;
        this.model = new BI.AnalysisETLMergeSheetCommonModel({});
        return {
            type:"bi.vtape",
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
                                    items:[{
                                        type:"bi.label",
                                        ref: function(_ref){
                                            self.lefttable = _ref;
                                        }
                                    }],
                                    height:35
                                },
                                height:35
                            }, {
                                type:"bi.analysis_etl_merge_preview_table",
                                rename:false,
                                items:[],
                                header:[],
                                leftColumns:[],
                                mergeColumns:[],
                                ref: function(_ref){
                                    self.left = _ref;
                                }
                            }]
                        },
                        rgap:12.5
                    },{
                        el : {
                            type:"bi.vtape",
                            height:200,

                            items:[{
                                el: {
                                    type:"bi.center_adapt",
                                    items:[{
                                        type:"bi.label",
                                        ref: function(_ref){
                                            self.righttable = _ref;
                                        }
                                    }],
                                    height:35
                                },
                                height:35
                            }, {
                                type:"bi.analysis_etl_merge_preview_table",
                                rename:false,
                                items:[],
                                header:[],
                                leftColumns:[],
                                mergeColumns:[],
                                ref: function(_ref){
                                    self.right = _ref;
                                }
                            }]
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
            }, {
                type: "bi.tab",
                cardCreator: BI.bind(this._createTabs, this),
                ref: function(_ref){
                    self.mergeCard = _ref;
                }
            }, {
                type:"bi.layout",
                height:20
            }]
        }
    },

    _createTabs: function(v){
        var self = this, o = this.options;
        switch (v) {
            case this._constants.NORMAL:
                return BI.createWidget({
                    type:"bi.analysis_etl_merge_preview_table",
                    rename:true,
                    items:[],
                    header:[],
                    leftColumns:[],
                    mergeColumns:[],
                    nameValidationController:o.nameValidationController,
                    listeners: [{
                        eventName: BI.AnalysisETLMergePreviewTable.EVENT_RENAME,
                        action: function(){
                            self.fireEvent(BI.AnalysisETLMergePreviewTable.EVENT_RENAME, arguments);
                        }
                    }],
                    ref: function(_ref){
                        self.merge = _ref;
                    }
                });
            case this._constants.ERROR:
                return BI.createWidget({
                    type : "bi.center_adapt",
                    items : [{
                        type:"bi.label",
                        cls: "warning",
                        text: BI.i18nText("BI-Please_Set_Right")
                    }]
                });
        }
    },

    _populate : function () {
        var self = this;
        var left = this.model.get("left");
        if(this.first !== true) {
            if(BI.isNotNull(left)) {
                this.lefttable.setText(left.tableName)
                BI.Utils.buildData(left, this.left, function (left_data) {
                    left_data.push(BI.range(0, left[ETLCst.FIELDS].length))
                    left_data.push([])
                    self.left.populate.apply(self.left, left_data);
                });

            }
            var right = this.model.get("right")
            if(BI.isNotNull(right)) {
                this.righttable.setText(right.tableName)
                BI.Utils.buildData(right, this.right, function (right_data) {
                    right_data.push([])
                    right_data.push([])
                    self.right.populate.apply(self.right, right_data);
                });

            }
        }
        this.first = true;
        var merge = this.model.get("merge")
        if(BI.isNotNull(merge)) {
            this.mergeCard.setSelect(this._constants.NORMAL)
            BI.Utils.buildData(merge, this.merge, function (merge_data) {
                merge_data.push(merge["leftColumns"])
                merge_data.push(merge["mergeColumns"])
                self.merge.populate.apply(self.merge, merge_data);
            });
        } else {
            this.mergeCard.setSelect(this._constants.ERROR)
        }

    },

    populate: function(m, options){
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    }
});

BI.shortcut("bi.analysis_etl_merge_preview",BI.AnalysisETLMergeSheetPreview)