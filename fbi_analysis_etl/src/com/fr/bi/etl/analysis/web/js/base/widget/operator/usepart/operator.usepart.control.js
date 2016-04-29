/**
 * Created by 小灰灰 on 2016/4/18.
 */
BI.AnalysisETLOperatorUsePartController = BI.inherit(BI.AnalysisETLOperatorAbstractController, {
    _init : function() {
        BI.AnalysisETLOperatorUsePartController.superclass._init.apply(this, arguments);
        this._editing = false;
    }
})