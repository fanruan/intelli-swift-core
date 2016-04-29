BI.AnalysisETLMergeSheetType = BI.inherit(BI.MVCWidget, {

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLMergeSheetType.superclass._defaultConfig.apply(this, arguments), {
            extraCls:"bi-analysis-etl-merge-type"
        })
    },

    _initController : function () {
        return BI.AnalysisETLMergeSheetTypeController;
    },

    _initModel : function () {
      return BI.MVCModel;
    },

    _initView : function () {
        var height = 80;
        var svg = BI.createWidget({
            type:"bi.svg",
            height: height,
            width: 1
        });
        svg.path("M0,0L0," + height);
        var self = this;
        this.join_type = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BI.JoinTypeGroup.JOIN_TYPE_GROUP, {
                type: "bi.join_type_button",
                height: 115,
                width: 72
            }),
            layouts: [{
                type: "bi.left",
                lgap: 5,
                vgap:10
            }]
        });
        this.union = BI.createWidget({
            type: "bi.join_type_button",
            height: 115,
            width: 72,
            iconCls: "union-icon",
            text: BI.i18nText("BI-Union"),
            value: ETLCst.ETL_UNION_STYLE
        })
        this.join_type.on(BI.ButtonGroup.EVENT_CHANGE, function(v){
            self.controller.setValue(v)
            self.fireEvent(BI.AnalysisETLMergeSheetType.EVENT_CHANGE, v)
        })
        this.union.on(BI.Controller.EVENT_CHANGE, function () {
            self.controller.setValue(self.union.getValue())
            self.fireEvent(BI.AnalysisETLMergeSheetType.EVENT_CHANGE, self.union.getValue())
        })
        BI.createWidget({
            type:"bi.vtape",
            element:this.element,
            items:[{
                el :{
                    type:"bi.left_right_vertical_adapt",
                    cls:"title",
                    llgap : 10,
                    items:{
                        left:[{
                            type:"bi.label",
                            text:BI.i18nText("BI-Merge_Type"),
                            cls:"label"
                        }]
                    }
                },
                height:40
            }, {
                type:"bi.left_right_vertical_adapt",
                scrollable : false,
                lrgap:5,
                llgap:5,
                rrgap:10,
                items:{
                    left:[this.join_type, svg],
                    right:[this.union]
                }
            }]
        })


    }

})

BI.AnalysisETLMergeSheetType.EVENT_CHANGE = "merge_change";

$.shortcut("bi.analysis_etl_merge_type",BI.AnalysisETLMergeSheetType)