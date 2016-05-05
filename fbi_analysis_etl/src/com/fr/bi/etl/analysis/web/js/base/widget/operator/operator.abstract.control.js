/**
 * Created by 小灰灰 on 2016/4/14.
 */
BI.AnalysisETLOperatorAbstractController = BI.inherit(BI.MVCController, {
    hideOperatorPane : function (widget) {
        widget.center.hideOperatorPane()
    },

    populate : function (widget, model) {
        widget.center.populate(model.update(), BI.extend(this.options, {
            showContent:widget.options.showContent,
        }));
        this.refreshPopData(this._editing ? widget.options.value.operatorType : ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL, widget, model);
    },

    refreshPopData : function (operatorType, widget, model){
        BI.Utils.triggerPreview(widget.center, model, operatorType)
    },

    refreshPreviewData:function (tempModel, operatorType, widget, model) {
        BI.Utils.triggerPreview(widget.center, tempModel, operatorType);
    }
    
})
BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE="preview_change";