/**
 * Created by 小灰灰 on 2016/4/5.
 */
BI.AnalysisETLOperatorAddColumnExprLastPeriodController = BI.inherit(BI.AnalysisETLOperatorAddColumnExprPeriodController, {
    _checkCanSave : function (widget, model) {
        BI.AnalysisETLOperatorAddColumnExprLastPeriodController.superclass._checkCanSave.apply(this, arguments);
    },


    populate : function (widget, model) {
        BI.AnalysisETLOperatorAddColumnExprLastPeriodController.superclass.populate.apply(this, arguments);
        widget.yearMonthSeason.populate(model.getDateNumberFields());
        widget.yearMonthSeason.setValue(model.get('year_month_season'));
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