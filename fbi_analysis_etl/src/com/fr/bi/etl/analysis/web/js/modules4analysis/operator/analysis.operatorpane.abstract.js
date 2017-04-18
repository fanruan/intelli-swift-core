/**
 * Created by Young's on 2017/4/5.
 */
BI.AnalysisOperatorAbstractPane = BI.inherit(BI.Widget, {
    props: {
        extraCls: "bi-analysis-etl-operator-select-data",
        value: ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.FILTER,
        showContent: true
    },

    render: function () {
        var self = this, o = this.options;
        this.trigger = BI.Utils.triggerPreview();
        this.model = new BI.AnalysisOperatorAbstarctPaneModel({});
        this.center = BI.createWidget({
            type: "bi.analysis_operator_center",
            contentItem: {
                value: o.value,
                type: o.value.operatorType + ETLCst.ANALYSIS_TABLE_PANE
            }
        });

        this.center.on(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, function (v) {
            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, arguments)
        });
        this.center.on(BI.AnalysisTopPointerSavePane.EVENT_CANCEL, function () {
            self._populate();
            self.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CANCEL, arguments)
        });
        this.center.on(BI.AnalysisTopPointerSavePane.EVENT_INVALID, function () {
            self.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_INVALID, arguments)
        });
        this.center.on(BI.AnalysisTopPointerSavePane.EVENT_FIELD_VALID, function () {
            self.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_FIELD_VALID, arguments)
        });
        this.center.on(BI.AnalysisTopPointerSavePane.EVENT_SAVE, function (v) {
            self.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_SAVE, arguments)
        });
        this.center.on(BI.AnalysisOperatorTitle.EVENT_SAVE, function (v) {
            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_SAVE, arguments)
        });
        this.center.on(BI.Controller.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments)
        });
        this.center.on(BI.AnalysisOperatorCenter.DATA_CHANGE, function (model) {
            self.populate(model);
        });
        this.center.on(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, function (previewModel, operatorType) {
            self.refreshPreviewData(previewModel, operatorType)
        });

        BI.createWidget({
            type: "bi.border",
            element: this,
            items: {
                west: {
                    el: {
                        type: "bi.layout"
                    },
                    width: 20
                },
                south: {
                    el: {
                        type: "bi.layout"
                    },
                    height: 10
                },
                east: {
                    el: {
                        type: "bi.layout"
                    },
                    width: 20
                },
                center: {
                    el: this.center
                }
            }
        })
    },

    mounted: function(){
        //this.populate(this.options.model);
    },

    resetPointerPosition : function () {
        this.center.resetPointerPosition()
    },

    _populate: function(){
        this.center.populate(this.model.update(), BI.extend(this.options, BI.extend(this.options, {
            showContent:this.options.showContent
        })));
    },

    populate : function (m, options) {
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    },

    refreshPopData : function (operatorType){
        this.trigger(this.center, this.model, operatorType, ETLCst.PREVIEW.NORMAL)
    },

    refreshPreviewData:function (tempModel, operatorType) {
        this.trigger(this.center, tempModel, operatorType, ETLCst.PREVIEW.NORMAL);
    }
});
BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE="preview_change";
BI.AnalysisETLOperatorAbstractController.VALID_CHANGE = "valid_change"