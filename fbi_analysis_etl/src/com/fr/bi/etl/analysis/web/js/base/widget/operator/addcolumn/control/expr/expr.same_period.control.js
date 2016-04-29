/**
 * Created by 小灰灰 on 2016/4/5.
 */
BI.AnalysisETLOperatorAddColumnExprSamePeriodController = BI.inherit(BI.AnalysisETLOperatorAddColumnExprPeriodController, {
    _checkCanSave : function (widget, model) {
        BI.AnalysisETLOperatorAddColumnExprSamePeriodController.superclass._checkCanSave.apply(this, arguments);
    },


    populate : function (widget, model) {
        BI.AnalysisETLOperatorAddColumnExprSamePeriodController.superclass.populate.apply(this, arguments);
        widget.year.populate(model.getDateNumberFields());
        widget.year.setValue(model.get('year'));
        widget.monthSeason.populate(model.getDateNumberFields());
        widget.monthSeason.setValue(model.get('monthSeason'));
        this._populateLabel(widget, model);
    },

    _populateLabel : function (widget, model) {
        widget.labels.empty();
        BI.each(['a', 'b', 'c'], function (i, item) {
            widget.labels.addItem(
                BI.createWidget({
                    type : 'bi.label',
                    cls : 'detail-label',
                    textAlign : 'left',
                    text : item
                })
            )
        })
    }
})