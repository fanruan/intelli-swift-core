BI.AnalysisETLOperatorAbstractPane = BI.inherit(BI.MVCWidget, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorAbstractPane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-operator-select-data",
            value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.FILTER,
            showContent:true
        })
    },

    _initController : function(){
        return BI.AnalysisETLOperatorAbstractController
    },

    _initView: function () {
        var o = this.options;
        this.center = BI.createWidget({
            type:"bi.analysis_etl_operator_center",
            contentItem : {
                value: o.value,
                type:o.value.operatorType + ETLCst.ANALYSIS_TABLE_PANE,
                model:o.model
            }
        })

        var self = this;

        this.center.on(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, function(v){
            self.controller.refreshPopData(ETLCst.ANALYSIS_TABLE_OPERATOR_KEY[v.getValue()].operatorType);
            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, arguments)
        })

        this.center.on(BI.TopPointerSavePane.EVENT_CANCEL, function(){
            self.controller.populate();
            self.fireEvent(BI.TopPointerSavePane.EVENT_CANCEL, arguments)
        })

        this.center.on(BI.TopPointerSavePane.EVENT_INVALID, function(){
            self.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, arguments)
        })

        this.center.on(BI.TopPointerSavePane.EVENT_FIELD_VALID, function(){
            self.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, arguments)
        })

        this.center.on(BI.TopPointerSavePane.EVENT_SAVE, function(v){
            self.fireEvent(BI.TopPointerSavePane.EVENT_SAVE, arguments)
        })

        this.center.on(BI.AnalysisOperatorTitle.EVENT_SAVE, function(v){
            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_SAVE, arguments)
        })

        this.center.on(BI.Controller.EVENT_CHANGE, function(v){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments)
        });

        this.center.on(BI.AnalysisETLOperatorCenter.DATA_CHANGE, function (model) {
            self.controller.refreshModel(model);
        })
        
        this.center.on(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, function (previewModel, operatorType) {
            self.controller.refreshPreviewData(previewModel, operatorType)
        })

        BI.createWidget({
            type : "bi.border",
            element : this.element,
            items :{
                west :{
                    el : {
                        type:"bi.layout"
                    },
                    width:20
                },

                south : {
                    el : {
                        type:"bi.layout"
                    },
                    height:10
                },
                east : {
                    el : {
                        type:"bi.layout"
                    },
                    width:20
                },
                center: {
                    el:this.center
                }
            }
        })
    }
 
})
