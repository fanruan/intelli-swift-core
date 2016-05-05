/**
 * Created by 小灰灰 on 2016/4/6.
 */
BI.AnalysisETLOperatorAddColumnExprLastPeriodPercentController = BI.inherit(BI.AnalysisETLOperatorAddColumnExprLastPeriodController, {
    _populateLabel : function (widget, model) {
        widget.firstDetail.empty();
        widget.firstDetail.addItem(
            BI.createWidget({
                type : 'bi.label',
                cls : 'detail-label',
                textAlign : 'left',
                text : BI.i18nText('BI-Calculate_Target_Each_Value', model.get('date') || '', model.get('field')||'')
            })
        )
        widget.secondDetail.empty();
        this._populateDownLabel(widget.secondDetail, model);
    }
})