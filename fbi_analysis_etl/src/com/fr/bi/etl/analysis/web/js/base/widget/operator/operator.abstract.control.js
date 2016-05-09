/**
 * Created by 小灰灰 on 2016/4/14.
 */
BI.AnalysisETLOperatorAbstractController = BI.inherit(BI.MVCController, {

    _construct : function (widget, model) {
        this.trigger = BI.Utils.triggerPreview()
    },
    //
    // hideOperatorPane : function (widget) {
    //     widget.center.hideOperatorPane()
    // },


    resetPointerPosition : function (widget) {
        widget.center.controller.resetPointerPosition()
    },

    populate : function (widget, model) {
        widget.center.populate(model.update(), BI.extend(this.options, {
            showContent:widget.options.showContent,
        }));
        //this.refreshPopData(this._editing ? widget.options.value.operatorType : ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL, widget, model);
    },

    refreshPopData : function (operatorType, widget, model){
        this.trigger(widget.center, model, operatorType, ETLCst.PREVIEW.NORMAL)
    },

    refreshPreviewData:function (tempModel, operatorType, widget, model) {
        this.trigger(widget.center, tempModel, operatorType, ETLCst.PREVIEW.NORMAL);
    }
    
})
BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE="preview_change";